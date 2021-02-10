package piero.aldinucci.apt.bookstore.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.HashSet;

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
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore.test");
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
