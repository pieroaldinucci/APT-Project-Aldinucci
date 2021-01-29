package piero.aldinucci.apt.bookstore.service;

import java.util.List;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

/**
 * Service layer.
 * Manages the access to the data layer.
 *
 */

public interface BookstoreManager {
	/**
	 * 
	 * Save an author with an empty collection of books into the database.
	 * 
	 * @param author Author to add in the database, it's book collection must be
	 *               empty
	 * @return a copy of the Author object saved in the database.
	 */
	public Author newAuthor(Author author);

	/**
	 * 
	 * @param book Book to add in the database
	 * @return a copy of the Book object saved in the database.
	 */
	public Book newBook(Book book);

	/**
	 * 
	 * @param id primary key of the Author to remove from the database
	 */
	public void deleteAuthor(long id);

	/**
	 * 
	 * @param id primary key of the Book to remove from the database
	 */
	public void deleteBook(long id);

	/**
	 * 
	 * @return A collection of all the authors present in the database.
	 */
	public List<Author> getAllAuthors();

	/**
	 * 
	 * @return A collection of all the books present in the database.
	 */
	public List<Book> getAllBooks();
}
