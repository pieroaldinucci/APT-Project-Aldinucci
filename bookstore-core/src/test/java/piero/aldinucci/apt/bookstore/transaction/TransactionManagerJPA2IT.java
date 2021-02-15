package piero.aldinucci.apt.bookstore.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.HashSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;
import piero.aldinucci.apt.bookstore.repositories.factory.RepositoriesJPAFactory;

public class TransactionManagerJPA2IT {

	private static final String FIXTURE_TITLE_1 = "title 1";
	private static final String FIXTURE_NAME_1 = "test name";

	@Mock
	private RepositoriesJPAFactory repositoriesFactory;

	@Mock
	private AuthorRepository authorRepository;

	@Mock
	private BookRepository bookRepository;

	@Captor
	private ArgumentCaptor<EntityManager> emCaptor;

	private TransactionManagerJPA transactionManager;
	private EntityManagerFactory emFactory;

	@Before
	public void setUp() {
		openMocks(this);

		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore.test");
		transactionManager = new TransactionManagerJPA(emFactory, repositoriesFactory);
		
		when(repositoriesFactory.createAuthorRepository(emCaptor.capture())).thenReturn(authorRepository);
		when(repositoriesFactory.createBookRepository(emCaptor.capture())).thenReturn(bookRepository);
	}

	@After
	public void tearDown() {
		emFactory.close();
	}

	@Test
	public void test_repositories_creation() {
		transactionManager.doInTransaction((authorR, bookR) -> {
			return null;
		});

		assertThat(emCaptor.getAllValues()).hasSize(2);
		assertThat(emCaptor.getAllValues().get(0)).isSameAs(emCaptor.getAllValues().get(1));
		assertThat(emCaptor.getValue()).isInstanceOf(EntityManager.class);
		verify(repositoriesFactory).createAuthorRepository(emCaptor.getValue());
		verify(repositoriesFactory).createBookRepository(emCaptor.getValue());
	}

	@Test
	public void test_call_code_as_expected() {
		Author author = new Author(1L, FIXTURE_NAME_1, new HashSet<>());
		when(authorRepository.save(isA(Author.class))).thenReturn(author);

		Author returnedObj = transactionManager.doInTransaction((authorR, bookR) -> {
			bookR.update(new Book(null, FIXTURE_TITLE_1, new HashSet<>()));
			return authorR.save(newAuthor(FIXTURE_NAME_1));
		});

		assertThat(returnedObj).isSameAs(author);
		InOrder inOrder = inOrder(authorRepository, bookRepository);
		inOrder.verify(bookRepository).update(new Book(null, FIXTURE_TITLE_1, new HashSet<>()));
		inOrder.verify(authorRepository).save(newAuthor(FIXTURE_NAME_1));
	}

	@Test
	public void test_transaction_successful() {
		
		Author author = transactionManager.doInTransaction((authorR,bookR) -> {
			EntityManager em = emCaptor.getValue();
			return em.merge(newAuthor(FIXTURE_NAME_1));
		});
		
		EntityManager em2 = emFactory.createEntityManager();
		Author foundAuthor = em2.createQuery("from Author",Author.class).getSingleResult();
		assertThat(foundAuthor).usingRecursiveComparison().isEqualTo(author);
		em2.close();
	}
	
	
	public void test_entityManager_is_closed_successfully() {
		transactionManager.doInTransaction((authorR,bookR) -> {
			return null;
		});
		
		EntityManager entityManager = emCaptor.getValue();
		assertThat(entityManager.getTransaction().isActive()).isFalse();
		assertThat(entityManager.isOpen()).isFalse();
	}
	
	@Test
	public void test_transaction_with_Persistence_Exception_closes_connection() {
		PersistenceException persistenceException = new PersistenceException();
		when(authorRepository.save(any())).thenThrow(persistenceException);
		
		assertThatThrownBy(() ->
		transactionManager.doInTransaction((authorR,bookR) -> authorR.save(newAuthor(FIXTURE_NAME_1)))
				).isInstanceOf(BookstorePersistenceException.class)
		.hasMessage("Error while executin transaction")
		.hasCause(persistenceException);
		
		EntityManager em = emCaptor.getValue();
		assertThat(em.getTransaction().isActive()).isFalse();
		assertThat(em.isOpen()).isFalse();
	}
	
	@Test
	public void test_transaction_with_general_Exception_closes_connection() {
		RuntimeException runtimeException = new RuntimeException();
		when(authorRepository.save(any())).thenThrow(runtimeException);
		
		assertThatThrownBy(() ->
		transactionManager.doInTransaction((authorR,bookR) -> authorR.save(newAuthor(FIXTURE_NAME_1))))
			.isSameAs(runtimeException);
		
		EntityManager em = emCaptor.getValue();
		assertThat(em.getTransaction().isActive()).isFalse();
		assertThat(em.isOpen()).isFalse();
	}

	private Author newAuthor(String name) {
		return new Author(null, name, new HashSet<>());
	}

}
