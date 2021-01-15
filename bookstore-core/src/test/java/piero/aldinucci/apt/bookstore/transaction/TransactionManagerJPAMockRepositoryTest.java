package piero.aldinucci.apt.bookstore.transaction;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;
import piero.aldinucci.apt.bookstore.repositories.factory.RepositoriesJPAFactory;


public class TransactionManagerJPAMockRepositoryTest {

	@Mock
	private RepositoriesJPAFactory factory;
	
	@Mock
	private AuthorRepository authorRepository;
	
	@Mock
	private BookRepository bookRepository;
	
	private TransactionManagerJPA transactionManager;
	private EntityManagerFactory emFactory;

	@Before
	public void setUp() {
		openMocks(this);
		
		HashMap<String, String> propertiesJPA = new HashMap<String, String>();
		propertiesJPA.put("javax.persistence.jdbc.url", "jdbc:hsqldb:mem:unit-testing-jpa");
		propertiesJPA.put("javax.persistence.jdbc.driver", "org.hsqldb.jdbcDriver");
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore",propertiesJPA);
		transactionManager = new TransactionManagerJPA(emFactory,factory);
		
		when(factory.createAuthorRepository(any())).thenReturn(authorRepository);
		when(factory.createBookRepository(any())).thenReturn(bookRepository);
	}
	
	@After
	public void tearDown() {
		emFactory.close();
	}
	
	@Test
	public void test_call_to_transactioncode() {
		ArgumentCaptor<Author> authorCaptor = ArgumentCaptor.forClass(Author.class);
		ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
		Author author = new Author (3L,"test",null); 
		when(authorRepository.save(isA(Author.class))).thenReturn(author);
		
		Author savedAuthor = transactionManager.doInTransaction((authorR,bookR) -> {
			bookR.update(new Book(4L,"title",null));
			return authorR.save(new Author(2L,"name",null));
		});
				
		InOrder inOrder = inOrder(authorRepository,bookRepository);
		inOrder.verify(bookRepository).update(bookCaptor.capture());
		inOrder.verify(authorRepository).save(authorCaptor.capture());
		verifyNoMoreInteractions(authorRepository);
		verifyNoMoreInteractions(bookRepository);
		
		assertThat(authorCaptor.getValue()).usingRecursiveComparison()
			.isEqualTo(new Author(2L,"name",null));
		assertThat(bookCaptor.getValue()).usingRecursiveComparison()
			.isEqualTo(new Book(4L,"title",null));
		assertThat(author).isSameAs(savedAuthor);
		assertThat(transactionManager.getEntityManager().isOpen()).isFalse();
	}
	
	@Test
	public void test_PersistenceException_should_be_rethrown_as_BookstorePersistanceException() {
		doThrow(PersistenceException.class).when(authorRepository).delete(anyLong());
		
		assertThatThrownBy(() -> {
			transactionManager.doInTransaction((authorR,bookR) -> {
				authorR.delete(1L);
				return null;
			});
		}).isExactlyInstanceOf(BookstorePersistenceException.class)
			.hasMessage("Error while committing transaction")
			.getCause().isInstanceOf(PersistenceException.class);
		
		assertThat(transactionManager.getEntityManager().isOpen()).isFalse();
	}
	
	@Test
	public void test_non_presistence_related_RuntimeExceptions_should_not_be_rethrown() {
		IllegalArgumentException iaException = new IllegalArgumentException();
		doThrow(iaException).when(authorRepository).delete(anyLong());
		
		assertThatThrownBy(() -> {
			transactionManager.doInTransaction((authorR,bookR) -> {
				authorR.delete(1L);
				return null;
			});
		}).isSameAs(iaException);
	}

}
