package piero.aldinucci.apt.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.repositories.factory.RepositoriesJPAFactoryImpl;
import piero.aldinucci.apt.bookstore.transaction.TransactionManagerJPA;

public class FullServiceLayerIT {
	
	private BookstoreManagerImpl bookstoreManager;

	private EntityManagerFactory emFactory;
	private List<Book> books;
	private List<Author> authors;
	
	@Before
	public void setUp() {
		HashMap<String, String> propertiesJPA = new HashMap<String, String>();
		propertiesJPA.put("javax.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/projectAPTTestDb");
		propertiesJPA.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
		propertiesJPA.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore",propertiesJPA);
		
		EntityManager entityManager = emFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.createQuery("from Author",Author.class).getResultStream()
			.forEach(e -> entityManager.remove(e));
		entityManager.createQuery("from Book",Book.class).getResultStream()
			.forEach(e -> entityManager.remove(e));
		entityManager.getTransaction().commit();
		entityManager.close();
		
		bookstoreManager = new BookstoreManagerImpl(new TransactionManagerJPA(
				emFactory,new RepositoriesJPAFactoryImpl()));
		
		populateDB();
	}
	
	@After
	public void tearDown() {
		emFactory.close();
	}
	
	@Test
	public void test_getAll_authors() {
		List<Author> authors = bookstoreManager.getAllAuthors();
	
		assertThat(authors).usingRecursiveFieldByFieldElementComparator()
			.isEqualTo(authors);
	}
	
	@Test
	public void test_getAll_books() {
		List<Book> books = bookstoreManager.getAllBooks();
	
		assertThat(books).usingRecursiveFieldByFieldElementComparator()
			.isEqualTo(books);
	}
	
	@Test
	public void test_delete_author() {
		bookstoreManager.delete(authors.get(0));

		EntityManager em = emFactory.createEntityManager();
		Author author = em.createQuery("from Author", Author.class).getSingleResult();
		List<Book> bookList = em.createQuery("from Book", Book.class).getResultList();
		em.close();
		
		assertThat(author).isEqualTo(authors.get(1));
		assertThat(bookList.get(0).getAuthors()).isEmpty();
		assertThat(bookList.get(1).getAuthors()).containsExactly(author);
	}
	
	@Test
	public void test_delete_book() {
		bookstoreManager.delete(books.get(1));

		EntityManager em = emFactory.createEntityManager();
		List<Author> authorList = em.createQuery("from Author", Author.class).getResultList();
		Book book = em.createQuery("from Book", Book.class).getSingleResult();
		em.close();
		
		assertThat(book).isEqualTo(books.get(0));
		assertThat(authorList.get(0).getBooks()).containsExactly(book);
		assertThat(authorList.get(1).getBooks()).isEmpty();
	}
	
	@Test
	public void test_adding_new_book_with_author_should_update_the_author_as_well() {
		Book book = new Book(null,"title 3",new HashSet<>());
		book.getAuthors().add(authors.get(1));
		
		book = bookstoreManager.newBook(book);
		
		EntityManager em = emFactory.createEntityManager();
		Author author = em.find(Author.class, authors.get(1).getId()); 
		em.close();
		
		assertThat(author.getBooks()).contains(book);
	}
	
	
	private void populateDB() {
		Author author1 = new Author(null,"name 1",new HashSet<>());
		Author author2 = new Author(null,"name 2",new HashSet<>());
		Book book1 = new Book(null,"title 1",new HashSet<>());
		Book book2 = new Book(null,"title 2",new HashSet<>());
		
		authors = new LinkedList<>();
		authors.add(author1);
		authors.add(author2);
		books = new LinkedList<>();
		books.add(book1);
		books.add(book2);

		EntityManager em = emFactory.createEntityManager();
		em.getTransaction().begin();
		authors.stream().forEach(a -> em.persist(a));
		books.stream().forEach(b -> em.persist(b));
		author1.getBooks().add(book1);
		book1.getAuthors().add(author1);
		author2.getBooks().add(book2);
		book2.getAuthors().add(author2);
		author1.getBooks().add(book2);
		book2.getAuthors().add(author1);
		em.getTransaction().commit();
		em.close();
	}
}
