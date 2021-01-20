package piero.aldinucci.apt.bookstore.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.HashSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

public class RepositoriesWithPostgresIT {

	private AuthorJPARepository authorRepository;
	private BookJPARepository bookRepository;
	private EntityManagerFactory emFactory;
	private EntityManager entityManager;
	private Author author;
	private Book book;
	
	@Before
	public void setUp() {
		HashMap<String, String> propertiesJPA = new HashMap<String, String>();
		propertiesJPA.put("javax.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/projectAPTTestDb");
		propertiesJPA.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
		propertiesJPA.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore",propertiesJPA);
		entityManager = emFactory.createEntityManager();
		
		authorRepository = new AuthorJPARepository(entityManager);
		bookRepository = new BookJPARepository(entityManager);
		
		populateDB();
	}
	
	@After
	public void tearDown() {
		entityManager.close();
		emFactory.close();
	}
	
	
	@Test
	public void test_save_author_with_bookSet_not_empty() {
		Book bookToSave = new Book(null,"A book",new HashSet<>());
		Author authorToSave = new Author(null, "new Author", new HashSet<>());
		
		entityManager.getTransaction().begin();
		Book savedBook = bookRepository.save(bookToSave);
		authorToSave.getBooks().add(savedBook);
		Author savedAuthor = authorRepository.save(authorToSave);
		entityManager.getTransaction().commit();
		
		EntityManager em = emFactory.createEntityManager();
		Author persistedAuthor = em.find(Author.class, savedAuthor.getId());
		Book persistedBook = em.find(Book.class, savedBook.getId());
		em.close();
		
		assertThat(persistedAuthor).usingRecursiveComparison().isEqualTo(savedAuthor);
		assertThat(persistedBook).usingRecursiveComparison().isEqualTo(savedBook);
	}
	
	@Test
	public void test_update_removing_circular_reference() {
		author.getBooks().remove(book);
		book.getAuthors().remove(author);
		
		entityManager.getTransaction().begin();
		authorRepository.update(author);
		bookRepository.update(book);
		entityManager.getTransaction().commit();
		entityManager.clear();
		
		EntityManager em = emFactory.createEntityManager();
		Author foundAuthor = em.find(Author.class, author.getId());
		Book foundBook = em.find(Book.class,book.getId());
		em.close();
		
		assertThat(foundAuthor.getBooks()).isEmpty();
		assertThat(foundBook.getAuthors()).isEmpty();
	}
	
	@Test
	public void test_findById_with_circular_references() {
		entityManager.getTransaction().begin();
		Author foundAuthor = authorRepository.findById(author.getId()).get();
		Book foundBook = bookRepository.findById(book.getId()).get();
		entityManager.getTransaction().commit();
		
		assertThat(foundAuthor).isSameAs(foundBook.getAuthors().iterator().next());
		assertThat(foundBook).isSameAs(foundAuthor.getBooks().iterator().next());
	}
	
	@Test
	public void test_deleting_all_entities_with_circular_references() {
		entityManager.getTransaction().begin();
		authorRepository.delete(author.getId());
		bookRepository.delete(book.getId());
		entityManager.getTransaction().commit();
		
		EntityManager em = emFactory.createEntityManager();
		assertThat(em.createQuery("from Author",Author.class).getResultList()).isEmpty();
		assertThat(em.createQuery("from Book",Book.class).getResultList()).isEmpty();
		em.close();
	}
	
	public void populateDB() {
		author = new Author(null, "test name", new HashSet<>());
		book = new Book(null, "test title", new HashSet<>());
		EntityManager em = emFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(author);
		em.persist(book);
		author.getBooks().add(book);
		book.getAuthors().add(author);
		em.getTransaction().commit();
		em.close();
	}
	
}
