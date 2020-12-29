package piero.aldinucci.apt.bookstore.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.service.BookstoreManager;
import piero.aldinucci.apt.bookstore.view.AuthorView;
import piero.aldinucci.apt.bookstore.view.BookView;

public class BookstoreControllerImpl implements BookstoreController {

	private AuthorView authorView;
	private BookView bookView;
	private BookstoreManager manager;
	private static final Logger LOGGER = LogManager.getLogger();

	public BookstoreControllerImpl(AuthorView authorView, BookView bookView, BookstoreManager manager) {
		this.authorView = authorView;
		this.bookView = bookView;
		this.manager = manager;
	}

	@Override
	public void allAuthors() {
		authorView.showAllAuthors(manager.getAllAuthors());

	}

	@Override
	public void allBooks() {
		bookView.showAllBooks(manager.getAllBooks());

	}

	@Override
	public void newBook(Book book) {
		bookView.bookAdded(manager.newBook(book));
	}

	@Override
	public void newAuthor(Author author) {
		authorView.authorAdded(manager.newAuthor(author));
	}

	@Override
	public void deleteBook(Book book) {
		try {
			manager.delete(book);
			bookView.bookRemoved(book);
		} catch (BookstorePersistenceException e) {
			LOGGER.error("Controller: Error while deleting book",e);
			bookView.showError("Error while deleting book", book);
		}
	}

	@Override
	public void deleteAuthor(Author author) {
		try {
			manager.delete(author);
			authorView.authorRemoved(author);
		} catch (BookstorePersistenceException e) {
			LOGGER.error("Controller: Error while deleting author",e);
			authorView.showError("Error while deleting author", author);
		}
	}

	@Override
	public void composeBook() {
		bookView.showCreateBook(manager.getAllAuthors());
	}

}
