package piero.aldinucci.apt.bookstore.transaction;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;
import piero.aldinucci.apt.bookstore.repositories.factory.RepositoriesJPAFactory;

public class TransactionManagerJPA implements TransactionManager {
	
	private EntityManagerFactory emFactory;
	private RepositoriesJPAFactory repositoryFactory;

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
		} catch (RuntimeException e) {
			throw new BookstorePersistenceException("Error while committing transaction",e);
		} finally {
			// how can we test that this is called?
			// Changing it into a field and create a get method would work, but it's even worth it?
			// Is even needed to close the EntityManager in this situation?
			entityManager.close(); 
		}
		
		return result;
	}

}
