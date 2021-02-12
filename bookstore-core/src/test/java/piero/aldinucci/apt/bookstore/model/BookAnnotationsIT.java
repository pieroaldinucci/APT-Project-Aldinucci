package piero.aldinucci.apt.bookstore.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BookAnnotationsIT {
	EntityManagerFactory emFactory;
	
	@Before
	public void setUp() {
		String postgresUrl = "jdbc:postgresql://localhost:"
				+System.getProperty("postgres.test.port","5432")
				+"/projectAPTTestDb";
		Map<String,String> properties = new HashMap<>();
		properties.put("javax.persistence.jdbc.url",postgresUrl);
		
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore.test",properties);
	}
	
	@After
	public void tearDown() {
		emFactory.close();
	}
	
	@Test
	public void test_generatedID() {
		Book book = new Book(null, "a Title", new HashSet<>());
		EntityManager entityManager = emFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(book);
		entityManager.getTransaction().commit();
		entityManager.close();
		
		assertThat(book.getId()).isNotNull();
	}
	
	@Test
	public void test_Eager_Initialization() {
		Book book = new Book(null, "a Title", new HashSet<>());
		EntityManager entityManager = emFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(book);
		entityManager.getTransaction().commit();
		entityManager.clear();
		
		Book retrievedBook = entityManager.find(Book.class, book.getId());
		entityManager.close();
		
		assertThatCode(() -> {
			assertThat(retrievedBook).usingRecursiveComparison().isEqualTo(book);
		}).doesNotThrowAnyException();
	}
}
