package piero.aldinucci.apt.bookstore.repositories.factory;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.repositories.AuthorJPARepository;
import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookJPARepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;

public class RepositoriesJPAFactoryImplIT {

	
	private RepositoriesJPAFactoryImpl factory;
	private EntityManagerFactory emFactory;
	
	@Before
	public void setUp() {
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore");
		factory = new RepositoriesJPAFactoryImpl();
		
		EntityManager entityManager = emFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.createQuery("from Author",Author.class).getResultStream()
			.forEach(a -> entityManager.remove(a));
		entityManager.createQuery("from Book",Book.class).getResultStream()
		.forEach(b -> entityManager.remove(b));
		entityManager.getTransaction().commit();
		entityManager.close();
	}
	
	@After
	public void tearDown() {
		emFactory.close();
	}
	
	@Test
	public void test_createAuthorRepository() {
		EntityManager entityManager = emFactory.createEntityManager();
		
		AuthorRepository authorRepository = factory.createAuthorRepository(entityManager);
		
		assertThat(authorRepository).isExactlyInstanceOf(AuthorJPARepository.class);
		
		entityManager.getTransaction().begin();
		Author author = authorRepository.save(new Author(null,"Author",new HashSet<>()));
		entityManager.getTransaction().commit();
		
		assertThat(entityManager.isOpen()).isTrue();
		assertThat(entityManager.contains(author)).isTrue();
		
		entityManager.close();
	}
	
	@Test
	public void test_createBookRepository() {
		EntityManager entityManager = emFactory.createEntityManager();
		
		BookRepository bookRepository = factory.createBookRepository(entityManager);
		
		assertThat(bookRepository).isExactlyInstanceOf(BookJPARepository.class);
		
		entityManager.getTransaction().begin();
		Book book = bookRepository.save(new Book(null,"Title",new HashSet<>()));
		entityManager.getTransaction().commit();
		
		assertThat(entityManager.isOpen()).isTrue();
		assertThat(entityManager.contains(book)).isTrue();
		
		entityManager.close();
	}

}
