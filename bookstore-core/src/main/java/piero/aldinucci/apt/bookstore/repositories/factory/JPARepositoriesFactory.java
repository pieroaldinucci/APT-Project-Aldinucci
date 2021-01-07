package piero.aldinucci.apt.bookstore.repositories.factory;

import javax.persistence.EntityManager;

import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;

public interface JPARepositoriesFactory {
	public AuthorRepository getAuthorRepository(EntityManager entityManager);
	public BookRepository getBookRepository(EntityManager entityManager);
}
