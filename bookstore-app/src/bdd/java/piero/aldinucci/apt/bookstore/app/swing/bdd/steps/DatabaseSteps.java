package piero.aldinucci.apt.bookstore.app.swing.bdd.steps;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

public class DatabaseSteps {
	
	
	public static final String FIXTURE_TITLE_1 = "first title";
	public static final String FIXTURE_TITLE_2 = "second title";
	public static final String FIXTURE_NAME_2 = "second author";
	public static final String FIXTURE_NAME_1 = "first author";
	private EntityManagerFactory emFactory;

	@Before
	public void setUp() {
		Map<String, String> propertiesJpa = new HashMap<>();
		propertiesJpa.put("javax.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/projectAPTTestDb");
		propertiesJpa.put("javax.persistence.jdbc.user", "testUser");
		propertiesJpa.put("javax.persistence.jdbc.password", "password");
		propertiesJpa.put("javax.persistence.schema-generation.database.action", "drop-and-create");
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore.app", propertiesJpa);
	}

	@After
	public void tearDown() {
		emFactory.close();
	}
	
	@Given("The database contains the authors with the following values")
	public void the_database_contains_the_authors_with_the_following_values(List<String> values) {
		EntityManager em = emFactory.createEntityManager();
		em.getTransaction().begin();
		values.forEach(v -> em.persist(new Author(null, v, new HashSet<>())));
		em.getTransaction().commit();
		em.close();
	}
	
	@Given("The database contains a few authors and books")
	public void the_database_contains_a_few_authors_and_books() {
		EntityManager em = emFactory.createEntityManager();
	    em.getTransaction().begin();
	    Author author = new Author(null,FIXTURE_NAME_1,new HashSet<>());
	    Book book = new Book(null, FIXTURE_TITLE_1, new HashSet<>());
	    em.persist(author);
	    em.persist(book);
	    book.getAuthors().add(author);
	    author.getBooks().add(book);
	    author = new Author(null,FIXTURE_NAME_2,new HashSet<>());
	    book = new Book(null, FIXTURE_TITLE_2, new HashSet<>());
	    em.persist(author);
	    em.persist(book);
	    book.getAuthors().add(author);
	    author.getBooks().add(book);
	    em.getTransaction().commit();
	    em.close();
	}
		
}
