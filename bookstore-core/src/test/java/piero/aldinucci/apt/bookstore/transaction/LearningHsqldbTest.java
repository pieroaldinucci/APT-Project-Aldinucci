package piero.aldinucci.apt.bookstore.transaction;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LearningHsqldbTest{
	
	private EntityManagerFactory emFactory;
	private EntityManager entityManager; 

	@Before
	public void setUp() {
		HashMap<String, String> propertiesJPA = new HashMap<String, String>();
		propertiesJPA.put("javax.persistence.jdbc.url", "jdbc:hsqldb:mem:unit-testing-jpa");
		propertiesJPA.put("javax.persistence.jdbc.driver", "org.hsqldb.jdbcDriver");
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore",propertiesJPA);
	}
	
	@After
	public void tearDown() {
		emFactory.close();
	}
	
	@Test
	public void test() {
		entityManager = emFactory.createEntityManager();
	}
	
}
