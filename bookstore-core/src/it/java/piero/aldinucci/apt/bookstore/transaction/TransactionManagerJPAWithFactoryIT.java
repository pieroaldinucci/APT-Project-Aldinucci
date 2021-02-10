package piero.aldinucci.apt.bookstore.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.repositories.factory.RepositoriesJPAFactoryImpl;


public class TransactionManagerJPAWithFactoryIT {

	private static final String FIXTURE_NAME_1 = "First Author";
	private static final String FIXTURE_TITLE_1 = "first book";
	
	private TransactionManager transactionManager;
	private EntityManagerFactory emFactory;

	@Before
	public void setUp() {
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore.test");
		transactionManager = new TransactionManagerJPA(emFactory,new RepositoriesJPAFactoryImpl());
	}
	
	@After
	public void tearDown() {
		emFactory.close();
	}
	
	@Test
	public void test_doInTransaction_should_save_and_update_entities_to_db_inside_transaction() {
		Author author = (new Author(null, FIXTURE_NAME_1, new HashSet<>()));
		EntityManager em = emFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(author);
		em.getTransaction().commit();
		em.close();
		
		Book book = transactionManager.doInTransaction((authorR, bookR) -> {
			Book b = new Book(null,FIXTURE_TITLE_1,new HashSet<>());
			b.getAuthors().add(author);
			b = bookR.save(b);
			author.getBooks().add(b);
			authorR.update(author);
			return b;
		});
		
		em = emFactory.createEntityManager();
		Author savedAuthor = em.createQuery("from Author",Author.class).getSingleResult();
		Book savedBook = em.createQuery("from Book", Book.class).getSingleResult();
		em.close();
		assertThat(savedAuthor).usingRecursiveComparison().isEqualTo(author);
		assertThat(savedBook).usingRecursiveComparison().isEqualTo(book);
	}
	
	@Test
	public void test_doInTransaction_should_catch_exception_when_commit_fails() {
		Author author = new Author(null, FIXTURE_NAME_1, new HashSet<>());
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
