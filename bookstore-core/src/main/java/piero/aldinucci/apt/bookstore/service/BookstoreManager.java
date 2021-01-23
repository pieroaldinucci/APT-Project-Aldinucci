package piero.aldinucci.apt.bookstore.service;

import java.util.List;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

/**
 * This is the service layer.
 *
 */

public interface BookstoreManager {
	/**
	 * 
	 * @param author Author to add in the database
	 * @return a copy of the Author, outside the persistence context, saved in the database.  
	 */
	public Author newAuthor(Author author);

	/**
	 * 
	 * @param book Book to add in the database
	 * @return a copy of the Book, outside the persistence context, saved in the database.
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

	/**
	 * 
	 * @param author the entity to be updated in the database.
	 */
	public void update(Author author);

	/**
	 * 
	 * @param book the entity to be updated in the database.
	 */
	public void update(Book book);
}
