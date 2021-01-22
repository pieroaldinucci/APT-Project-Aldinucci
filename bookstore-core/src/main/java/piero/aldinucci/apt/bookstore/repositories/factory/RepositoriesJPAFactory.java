package piero.aldinucci.apt.bookstore.repositories.factory;

import javax.persistence.EntityManager;

import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;

/**
 * Used to create repository instances by the TransactionManagerJPA.
 *
 */
public interface RepositoriesJPAFactory {

	/**
	 * 
	 * @param entityManager interface to interact with the persistence context.
	 * @return an instance of AuthorRepository
	 */
	public AuthorRepository createAuthorRepository(EntityManager entityManager);

	/**
	 * 
	 * @param entityManager interface to interact with the persistence context.
	 * @return an instance of BookRepository
	 */
	public BookRepository createBookRepository(EntityManager entityManager);
}
