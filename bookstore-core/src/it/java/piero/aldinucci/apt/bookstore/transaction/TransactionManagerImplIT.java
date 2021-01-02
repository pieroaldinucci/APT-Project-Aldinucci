package piero.aldinucci.apt.bookstore.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;

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

public class TransactionManagerImplIT {

	private TransactionManagerJPA transactionManager;
	private EntityManagerFactory emFactory;

	@Before
	public void setUp() {
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore");
		transactionManager = spy(new TransactionManagerJPA(emFactory));
		
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
	public void test_doInTransaction_should_save_author_to_db() {
		Author author = new Author(null, "First Author", new HashSet<>());
		Author returnedAuthor = transactionManager.doInTransaction((authorR, bookR) -> authorR.save(author));
		
		EntityManager em = emFactory.createEntityManager();
		Author savedAuthor = em.find(Author.class, returnedAuthor.getId());
		em.close();

		assertThat(savedAuthor.getName()).isEqualTo("First Author");
		assertThat(savedAuthor).usingRecursiveComparison().isEqualTo(returnedAuthor);
	}
	
	@Test
	public void test_doInTransaction_should_save_book_to_db() {
		Book book = new Book(null, "first book", new HashSet<>());
		Book returnedBook = transactionManager.doInTransaction((authorR, bookR) -> bookR.save(book));
		
		EntityManager em = emFactory.createEntityManager();
		Book savedBook = em.find(Book.class, returnedBook.getId());
		em.close();
		
		assertThat(savedBook.getTitle()).isEqualTo("first book");
		assertThat(savedBook).usingRecursiveComparison().isEqualTo(returnedBook);
	}
	
	@Test
	public void test_doInTransaction_should_read_author_from_db() {
		Author author = new Author(null, "First Author", new HashSet<>());
		EntityManager em = emFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(author);
		em.getTransaction().commit();
		em.close();

		Optional<Author> returnedAuthor = transactionManager.doInTransaction((authorR, bookR) -> 
			authorR.findById(author.getId()));
		
		assertThat(author).usingRecursiveComparison().isEqualTo(returnedAuthor.get());
	}
	
	@Test
	public void test_doInTransaction_findall_authors_from_db() {
		Author author = new Author(null, "First Author", new HashSet<>());
		Author author2 = new Author(null, "Second Author", new HashSet<>());
		Book book = new Book(null, "Libbro", new HashSet<Author>());
		author.getBooks().add(book);
		book.getAuthors().add(author);
		EntityManager em = emFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(author);
		em.persist(author2);
		em.persist(book);
		em.getTransaction().commit();
		em.clear();
		
		List<Author> authors = transactionManager.doInTransaction((authorR, bookR) ->
			authorR.findAll());
		
		assertThat(authors).containsExactly(author,author2);
		assertThat(authors.get(0)).usingRecursiveComparison().isEqualTo(author).isNotSameAs(author);
		assertThat(authors.get(1)).usingRecursiveComparison().isEqualTo(author2).isNotSameAs(author2);
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
