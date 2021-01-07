package piero.aldinucci.apt.bookstore.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.repositories.factory.RepositoriesJPAFactoryImpl;


public class TransactionManagerJPAIT {

	private TransactionManager transactionManager;
	private EntityManagerFactory emFactory;

	@Before
	public void setUp() {
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore");
		transactionManager = new TransactionManagerJPA(emFactory,new RepositoriesJPAFactoryImpl());
		
		EntityManager entityManager = emFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		entityManager.createQuery("from Author",Author.class).getResultStream()
			.forEach(e -> entityManager.remove(e));
		entityManager.createQuery("from Book",Book.class).getResultStream()
			.forEach(e -> entityManager.remove(e));
		entityManager.getTransaction().commit();
		entityManager.close();
	}
	
	@After
	public void tearDown() {
		emFactory.close();
	}
	
	@Test
	public void test_doInTransaction_should_save_author_to_db_in_transaction() {
		Author author = new Author(null, "First Author", new HashSet<>());
		
		Author returnedAuthor = transactionManager.doInTransaction((authorR, bookR) -> authorR.save(author));
		
		EntityManager em = emFactory.createEntityManager();
		Author savedAuthor = em.find(Author.class, returnedAuthor.getId());
		em.close();

		assertThat(savedAuthor.getName()).isEqualTo("First Author");
		assertThat(savedAuthor).usingRecursiveComparison().isEqualTo(returnedAuthor);
	}
	
	@Test
	public void test_doInTransaction_should_read_book_from_db() {
		Book book = new Book(null, "A Title", new HashSet<>());
		EntityManager em = emFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(book);
		em.getTransaction().commit();
		em.close();

		Optional<Book> returnedBook = transactionManager.doInTransaction((authorR, bookR) -> 
			bookR.findById(book.getId()));
		
		assertThat(book).usingRecursiveComparison().isEqualTo(returnedBook.get());
	}
	
	@Test
	public void test_doInTransaction_should_throw_and_rollback_when_RuntimeException_occur() {
		Author author = new Author(null, "First Author", new HashSet<>());
		Book book = new Book(null, "a Book", new HashSet<>());
		author.getBooks().add(book);
		
		assertThatThrownBy(() -> transactionManager.doInTransaction((authorR, bookR) ->
				authorR.save(author)))
			.isExactlyInstanceOf(BookstorePersistenceException.class)
			.hasMessage("Error while committing transaction")
			.getCause().isInstanceOf(PersistenceException.class);
		
		EntityManager em = emFactory.createEntityManager();
		List<Author> authors = em.createQuery("from Author",Author.class).getResultList();
		em.close();
		
		assertThat(authors).isEmpty();
	}

}
