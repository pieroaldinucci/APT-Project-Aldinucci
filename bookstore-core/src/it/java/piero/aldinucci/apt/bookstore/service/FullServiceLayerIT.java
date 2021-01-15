package piero.aldinucci.apt.bookstore.service;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
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
import piero.aldinucci.apt.bookstore.transaction.TransactionManagerJPA;

public class FullServiceLayerIT {
	
	private BookstoreManagerImpl bookstoreManager;

	private EntityManagerFactory emFactory;

	private TransactionManagerJPA transactionManager;
	
	@Before
	public void setUp() {
		HashMap<String, String> propertiesJPA = new HashMap<String, String>();
		propertiesJPA.put("javax.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/projectAPTTestDb");
		propertiesJPA.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
		propertiesJPA.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore",propertiesJPA);
		transactionManager = new TransactionManagerJPA(emFactory,new RepositoriesJPAFactoryImpl());
		bookstoreManager = new BookstoreManagerImpl(transactionManager);
		
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
	public void test_Authors_operations() {
		Author author1 = bookstoreManager.newAuthor(new Author(null,"name 1",new HashSet<>()));
		Author author2 = bookstoreManager.newAuthor(new Author(null,"name 2",new HashSet<>()));

		List<Author> authors = bookstoreManager.getAllAuthors();
		
		assertThat(authors).usingRecursiveFieldByFieldElementComparator().containsExactly(author1,author2);
		
		author1.getBooks().add(new Book(1L,"title",new HashSet<>()));

		assertThatThrownBy(() -> bookstoreManager.update(author1))
			.isInstanceOf(BookstorePersistenceException.class);

		Book book = new Book(null,"title 2",new HashSet<>());
		book.getAuthors().add(author2);
		book = bookstoreManager.newBook(book);
		
		authors = bookstoreManager.getAllAuthors();
		
		assertThat(authors.get(0).getBooks()).isEmpty();
		assertThat(authors.get(1).getBooks().iterator().next()).isNotSameAs(book)
			.usingRecursiveComparison().isEqualTo(book);
	}
}
