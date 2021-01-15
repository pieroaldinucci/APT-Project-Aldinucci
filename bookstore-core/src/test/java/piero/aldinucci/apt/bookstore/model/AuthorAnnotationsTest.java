package piero.aldinucci.apt.bookstore.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.HashMap;
import java.util.HashSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AuthorAnnotationsTest {
	
	EntityManagerFactory emFactory;
	
	@Before
	public void setUp() {
		HashMap<String, String> propertiesJPA = new HashMap<String, String>();
		propertiesJPA.put("javax.persistence.jdbc.url", "jdbc:hsqldb:mem:unit-testing-jpa");
		propertiesJPA.put("javax.persistence.jdbc.driver", "org.hsqldb.jdbcDriver");
		propertiesJPA.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore",propertiesJPA);
		
		EntityManager entityManager = emFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.createQuery("from Author", Author.class).getResultStream()
			.forEach(a -> entityManager.remove(a));
		entityManager.getTransaction().commit();
		entityManager.close();
	}
	
	@After
	public void tearDown() {
		emFactory.close();
	}

	@Test
	public void test_generatedID_and_Eager_Initialization() {
		Author author = new Author(null, "an Author", new HashSet<>());
		EntityManager entityManager = emFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(author);
		entityManager.getTransaction().commit();
		entityManager.close();
		
		assertThat(author.getId()).isNotNull();
		
		entityManager = emFactory.createEntityManager();
		Author retrievedAuthor = entityManager.find(Author.class, author.getId());
		entityManager.close();
		
		assertThatCode(() -> {
			assertThat(retrievedAuthor).usingRecursiveComparison().isEqualTo(author);
		}).doesNotThrowAnyException();
	}

}
