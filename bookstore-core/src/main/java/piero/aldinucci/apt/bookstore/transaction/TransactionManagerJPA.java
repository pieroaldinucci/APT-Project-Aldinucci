package piero.aldinucci.apt.bookstore.transaction;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import com.google.inject.Inject;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;
import piero.aldinucci.apt.bookstore.repositories.factory.RepositoriesJPAFactory;

/**
 * JPA specific implementation of TransactionManager
 * 
 * @author Piero Aldinucci
 *
 */
public class TransactionManagerJPA implements TransactionManager {

	private EntityManagerFactory emFactory;
	private RepositoriesJPAFactory repositoryFactory;

	/**
	 * 
	 * @param emFactory           needed to create EntityManagers for the
	 *                            repositories factory.
	 * @param repositoriesFactory used to create instances of entity repositories.
	 */
	@Inject
	public TransactionManagerJPA(EntityManagerFactory emFactory, RepositoriesJPAFactory repositoriesFactory) {
		this.emFactory = emFactory;
		this.repositoryFactory = repositoriesFactory;
	}

	@Override
	public <R> R doInTransaction(TransactionCode<R> code) {
		EntityManager entityManager = emFactory.createEntityManager();
		AuthorRepository authorRepository = repositoryFactory.createAuthorRepository(entityManager);
		BookRepository bookRepository = repositoryFactory.createBookRepository(entityManager);
		
		R result = null;
		try {
			entityManager.getTransaction().begin();
			result = code.apply(authorRepository, bookRepository);
			entityManager.getTransaction().commit();
		} catch (PersistenceException e) {
			entityManager.getTransaction().rollback();
			throw new BookstorePersistenceException("Error while executing transaction", e);
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw e;
		} finally {
			entityManager.close();
		}

		return result;
	}
}
