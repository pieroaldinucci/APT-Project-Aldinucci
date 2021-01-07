package piero.aldinucci.apt.bookstore.transaction;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.repositories.AuthorJPARepository;
import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookJPARepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;

public class TransactionManagerJPA implements TransactionManager {

	private EntityManagerFactory emFactory;
	private Module module;

	public TransactionManagerJPA(EntityManagerFactory emFactory) {
		this.emFactory = emFactory;
		this.module = module;
	}

	@Override
	public <R> R doInTransaction(TransactionCode<R> code) {
		EntityManager entityManager = emFactory.createEntityManager();
		Injector injector = Guice.createInjector(module);
		AuthorRepository authorRepository = injector.getInstance(AuthorRepository.class);
		BookRepository bookRepository = injector.getInstance(BookRepository.class);
//		AuthorRepository authorRepository = createAuthorRepository(entityManager);
//		BookRepository bookRepository = createBookRepository(entityManager);
		
		R result = null;
		try {
			entityManager.getTransaction().begin();
			result = code.apply(authorRepository, bookRepository);
			entityManager.getTransaction().commit();
		} catch (RuntimeException e) {
			throw new BookstorePersistenceException("Error while committing transaction",e);
		} finally {
			// how can we test that this is called?
			// Changing it into a field and create a get method would work, but it's even worth it?
			entityManager.close(); 
		}
		
		return result;
	}

//	protected BookRepository createBookRepository(EntityManager entityManager) {
//		return new BookJPARepository(entityManager);
//	}
//
//	protected AuthorRepository createAuthorRepository(EntityManager entityManager) {
//		return new AuthorJPARepository(entityManager);
//	}

}
