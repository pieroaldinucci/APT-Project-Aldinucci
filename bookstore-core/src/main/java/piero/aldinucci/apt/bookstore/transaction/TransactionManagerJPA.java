package piero.aldinucci.apt.bookstore.transaction;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.repositories.AuthorJPARepository;
import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookJPARepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;

public class TransactionManagerJPA implements TransactionManager {

	private EntityManagerFactory emFactory;

	public TransactionManagerJPA(EntityManagerFactory emFactory) {
		this.emFactory = emFactory;
	}

	@Override
	public <R> R doInTransaction(TransactionCode<R> code) {
		EntityManager entityManager = emFactory.createEntityManager();
		AuthorRepository authorRepository = createAuthorRepository(entityManager);
		BookRepository bookRepository = createBookRepository(entityManager);
		
		R result = null;
		try {
			entityManager.getTransaction().begin();
			result = code.apply(authorRepository, bookRepository);
			entityManager.getTransaction().commit();
		} catch (RuntimeException e) {
			throw new BookstorePersistenceException("Error while committing transaction",e);
		} finally {
			entityManager.close();
		}
		
		return result;
	}

	protected BookRepository createBookRepository(EntityManager entityManager) {
		return new BookJPARepository(entityManager);
	}

	protected AuthorRepository createAuthorRepository(EntityManager entityManager) {
		return new AuthorJPARepository(entityManager);
	}

}
