package piero.aldinucci.apt.bookstore.repositories.factory;

import javax.persistence.EntityManager;

import piero.aldinucci.apt.bookstore.repositories.AuthorJPARepository;
import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookJPARepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;

public class RepositoriesJPAFactoryImpl implements RepositoriesJPAFactory {

	@Override
	public AuthorRepository createAuthorRepository(EntityManager entityManager) {
		return new AuthorJPARepository(entityManager);
	}

	@Override
	public BookRepository createBookRepository(EntityManager entityManager) {
		return new BookJPARepository(entityManager);
	}

}
