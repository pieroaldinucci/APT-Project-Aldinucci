package piero.aldinucci.apt.bookstore.controller;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

/**
 * 
 * @author Piero Aldinucci
 *
 */
public interface BookstoreController {
	/**
	 * populate Author view
	 */
	public void allAuthors();

	/**
	 * populate Book view
	 */
	public void allBooks();

	/**
	 * Save an author and show it into the Author view
	 * @param author author to be added
	 */
	public void newAuthor(Author author);

	/**
	 * Save a book and show it into the Book view
	 * @param book book to be added
	 */
	public void newBook(Book book);

	/**
	 * Delete a book and remove it from the book view
	 * @param book book to be removed
	 */
	public void deleteBook(Book book);

	/**
	 * Delete an author and remove it from the Author view
	 * @param author author to be removed
	 */
	public void deleteAuthor(Author author);

	/**
	 * populate the ComposeBook view, used to create a new book.
	 */
	public void composeBook();
}
