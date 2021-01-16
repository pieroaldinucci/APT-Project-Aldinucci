package piero.aldinucci.apt.bookstore.repositories.factory;

import javax.persistence.EntityManager;

import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;

public interface RepositoriesJPAFactory {

	public AuthorRepository createAuthorRepository(EntityManager entityManager);

	public BookRepository createBookRepository(EntityManager entityManager);
}
