package piero.aldinucci.apt.bookstore.repositories;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

public class AutoFactoryLearningTest {

//	@Inject
//	Provider<AuthorRepository> authorRepProvider;
	
	private AuthorJPARepository repository;
	private EntityManagerFactory emFactory;
	private EntityManager entityManager;
	
	@Before
	public void setUp() {
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore");
//		entityManager = emFactory.createEntityManager();
		
//		entityManager.getTransaction().begin();
//		entityManager.createQuery("from Author",Author.class).getResultStream()
//			.forEach(e -> entityManager.remove(e));
//		entityManager.createQuery("from Book",Book.class).getResultStream()
//			.forEach(e -> entityManager.remove(e));
//		entityManager.getTransaction().commit();
//		entityManager.clear();
		
//		repository = new AuthorJPARepository(entityManager);
	}
	
	@After
	public void tearDown() {
		entityManager.close();
		emFactory.close();
	}
	
	private static interface AuthorRepositoryFactory {
		AuthorRepository create(EntityManager entityManager);
	}
	
	@Test
	public void test() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(AuthorRepository.class).to(AuthorJPARepository.class);
				install(new FactoryModuleBuilder()
						.implement(EntityManager.class, EntityManager.class)
//						.implement(AuthorRepository.class, AuthorJPARepository.class)
						.build(AuthorRepositoryFactory.class));
			}
			
			@Provides
			EntityManager getEM() {
				entityManager = emFactory.createEntityManager(); 
				return entityManager;
			}
		};
		
		Injector injector = Guice.createInjector(module);
		repository = (AuthorJPARepository)injector.getInstance(AuthorRepository.class);
		
		assertThat(repository).isNotNull();
	}

}
