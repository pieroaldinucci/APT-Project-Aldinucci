package piero.aldinucci.apt.bookstore.repositories;

import java.util.List;
import java.util.Optional;

import piero.aldinucci.apt.bookstore.model.Book;

/**
 * Repository for Book class
 *
 */

public interface BookRepository {

	/**
	 * 
	 * @return the list that contains all the Book entities present in the database 
	 */
	public List<Book> findAll();

	/**
	 * 
	 * @param id primary key of the entity to find
	 * @return contains the found Book if it's present, otherwise it's empty.
	 */
	public Optional<Book> findById(long id);

	/**
	 * 
	 * @param book the entity that must be saved into the database. Its id field must be null.
	 * @return a copy of the saved entity with the assigned Id field.
	 */
	public Book save(Book book);

	/**
	 * 
	 * @param book the Book to be updated. Its id field must not be null.
	 */
	public void update(Book book);

	/**
	 * 
	 * @param id identifier and primary key of the entity to remove.
	 * @return contains the deleted Book entity if the operation is successful,
	 * it's empty otherwise
	 */
	public Optional<Book> delete(long id);
}
