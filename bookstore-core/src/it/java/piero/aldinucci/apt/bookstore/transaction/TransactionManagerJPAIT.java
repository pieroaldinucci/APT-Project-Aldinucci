package piero.aldinucci.apt.bookstore.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.repositories.AuthorJPARepository;
import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookJPARepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;
import piero.aldinucci.apt.bookstore.repositories.factory.RepositoriesJPAFactory;
import piero.aldinucci.apt.bookstore.repositories.factory.RepositoriesJPAFactoryImpl;


public class TransactionManagerJPAIT {

	private TransactionManager transactionManager;
	private EntityManagerFactory emFactory;

	@Before
	public void setUp() {
		HashMap<String, String> propertiesJPA = new HashMap<String, String>();
		propertiesJPA.put("javax.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/projectAPTTestDb");
		propertiesJPA.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
		propertiesJPA.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore",propertiesJPA);
		transactionManager = new TransactionManagerJPA(emFactory,new RepositoriesJPAFactoryImpl());
		
		EntityManager em = emFactory.createEntityManager();
		
		em.getTransaction().begin();
		em.createQuery("from Author",Author.class).getResultStream()
			.forEach(e -> em.remove(e));
		em.createQuery("from Book",Book.class).getResultStream()
			.forEach(e -> em.remove(e));
		em.getTransaction().commit();
		em.close();
	}
	
	@After
	public void tearDown() {
		emFactory.close();
	}
	
	@Test
	public void test_doInTransaction_should_save_entities_to_db_inside_transaction() {
		Author author = new Author(null, "First Author", new HashSet<>());
		
		Author returnedAuthor = transactionManager.doInTransaction((authorR, bookR) ->{
			Book book = new Book(null, "A book", new HashSet<>());
			book = bookR.save(book);
			author.getBooks().add(book);
			return authorR.save(author);
		});
		
		EntityManager em = emFactory.createEntityManager();
		Author savedAuthor = em.find(Author.class, returnedAuthor.getId());
		em.close();
		
		assertThat(savedAuthor.getName()).isEqualTo("First Author");
		assertThat(savedAuthor).usingRecursiveComparison().isEqualTo(returnedAuthor);
		
		Book savedBook = transactionManager.doInTransaction((authorR,bookR) ->
			bookR.findAll().get(0));
		
		assertThat(savedAuthor.getBooks()).usingRecursiveFieldByFieldElementComparator()
			.containsExactly(savedBook);
	}
	
	// This is specifically to check if the commit is inside the try catch
	@Test
	public void test_doInTransaction_should_throw_when_commit_fail() {
		Author author = new Author(null, "First Author", new HashSet<>());
		Book book = new Book(null, "a Book", new HashSet<>());
		author.getBooks().add(book);
		
		assertThatThrownBy(() -> transactionManager.doInTransaction((authorR, bookR) ->
				authorR.save(author)))
			.isExactlyInstanceOf(BookstorePersistenceException.class);
		
		EntityManager em = emFactory.createEntityManager();
		List<Author> authors = em.createQuery("from Author",Author.class).getResultList();
		em.close();
		
		assertThat(authors).isEmpty();
	}
	

}
