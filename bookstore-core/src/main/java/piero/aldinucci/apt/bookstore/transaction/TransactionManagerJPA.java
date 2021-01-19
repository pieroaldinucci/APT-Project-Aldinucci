package piero.aldinucci.apt.bookstore.transaction;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import com.google.inject.Inject;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;
import piero.aldinucci.apt.bookstore.repositories.factory.RepositoriesJPAFactory;

public class TransactionManagerJPA implements TransactionManager {
	
	private EntityManagerFactory emFactory;
	private RepositoriesJPAFactory repositoryFactory;
	private EntityManager entityManager;

	@Inject
	public TransactionManagerJPA(EntityManagerFactory emFactory, RepositoriesJPAFactory repositoriesFactory) {
		this.emFactory = emFactory;
		this.repositoryFactory = repositoriesFactory;
	}

	@Override
	public <R> R doInTransaction(TransactionCode<R> code) {
		
		R result = null;
		try {
			entityManager = emFactory.createEntityManager();
			AuthorRepository authorRepository = repositoryFactory.createAuthorRepository(entityManager);
			BookRepository bookRepository = repositoryFactory.createBookRepository(entityManager);
			entityManager.getTransaction().begin();
			result = code.apply(authorRepository, bookRepository);
			entityManager.getTransaction().commit();
		} catch (PersistenceException e) {
			entityManager.getTransaction().rollback(); //temporary, is useless?
			throw new BookstorePersistenceException("Error while executing transaction",e);
		} finally {
			entityManager.close();
		}
		
		return result;
	}

	EntityManager getEntityManager() {
		return entityManager;
	}

}
