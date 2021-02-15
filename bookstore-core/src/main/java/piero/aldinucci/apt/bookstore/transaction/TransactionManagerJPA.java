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
 * Second version of JPA specific TransactionManager implementation Made with
 * TDD without any step skip.
 * 
 * @author Piero Aldinucci
 *
 */
public class TransactionManagerJPA implements TransactionManager {

	private RepositoriesJPAFactory repositoriesFactory;
	private EntityManagerFactory emFactory;

	/**
	 * 
	 * @param emFactory           needed to create EntityManagers for the
	 *                            repositories factory.
	 * @param repositoriesFactory used to create instances of entity repositories.
	 */
	@Inject
	public TransactionManagerJPA(EntityManagerFactory emFactory, RepositoriesJPAFactory repositoriesFactory) {
		this.emFactory = emFactory;
		this.repositoriesFactory = repositoriesFactory;

	}

	@Override
	public <R> R doInTransaction(TransactionCode<R> code) {
		EntityManager em = emFactory.createEntityManager();
		AuthorRepository authorRepository = repositoriesFactory.createAuthorRepository(em);
		BookRepository bookRepository = repositoriesFactory.createBookRepository(em);

		R result = null;
		try {
			em.getTransaction().begin();
			result = code.apply(authorRepository, bookRepository);
			em.getTransaction().commit();
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
			throw new BookstorePersistenceException("Error while executin transaction", e);
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		} finally {
			em.close();
		}
		return result;
	}

}
