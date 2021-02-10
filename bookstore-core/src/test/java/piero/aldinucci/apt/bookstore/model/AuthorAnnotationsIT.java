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

public class AuthorAnnotationsIT {
	
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
		Author author = new Author(null, "an Author", new HashSet<>());
		EntityManager entityManager = emFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(author);
		entityManager.getTransaction().commit();
		entityManager.close();
		
		assertThat(author.getId()).isNotNull();
	}
	
	@Test
	public void test_Eager_Initialization() {
		Author author = new Author(null, "an Author", new HashSet<>());
		EntityManager entityManager = emFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(author);
		entityManager.getTransaction().commit();
		entityManager.close();
		
		entityManager = emFactory.createEntityManager();
		Author retrievedAuthor = entityManager.find(Author.class, author.getId());
		entityManager.close();
		
		assertThatCode(() -> {
			assertThat(retrievedAuthor).usingRecursiveComparison().isEqualTo(author);
		}).doesNotThrowAnyException();
	}

}
