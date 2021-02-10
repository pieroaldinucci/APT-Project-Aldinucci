package piero.aldinucci.apt.bookstore.repositories.factory;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import piero.aldinucci.apt.bookstore.repositories.AuthorJPARepository;
import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookJPARepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;

public class RepositoriesJPAFactoryImplIT {

	
	private RepositoriesJPAFactoryImpl factory;
	private EntityManagerFactory emFactory;
	private EntityManager entityManager;
	
	@Before
	public void setUp() {
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore.test");
		entityManager = emFactory.createEntityManager();

		factory = new RepositoriesJPAFactoryImpl();
	}
	
	@After
	public void tearDown() {
		entityManager.close();
		emFactory.close();
	}
	
	@Test
	public void test_createAuthorRepository() {
		AuthorRepository authorRepository = factory.createAuthorRepository(entityManager);
		
		assertThat(authorRepository).isExactlyInstanceOf(AuthorJPARepository.class);
		assertThat(entityManager.isOpen()).isTrue();
	}
	
	@Test
	public void test_createBookRepository() {
		BookRepository bookRepository = factory.createBookRepository(entityManager);
		
		assertThat(bookRepository).isExactlyInstanceOf(BookJPARepository.class);
		assertThat(entityManager.isOpen()).isTrue();
	}

}
