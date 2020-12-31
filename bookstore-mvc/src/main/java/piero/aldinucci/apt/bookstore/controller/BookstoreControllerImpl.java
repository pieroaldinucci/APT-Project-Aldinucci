package piero.aldinucci.apt.bookstore.controller;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.service.BookstoreManager;
import piero.aldinucci.apt.bookstore.view.AuthorView;
import piero.aldinucci.apt.bookstore.view.BookView;
import piero.aldinucci.apt.bookstore.view.ComposeBookView;

public class BookstoreControllerImpl implements BookstoreController {

	private AuthorView authorView;
	private BookView bookView;
	private BookstoreManager manager;
	private ComposeBookView composeBookView;
	private static final Logger LOGGER = LogManager.getLogger();

	public BookstoreControllerImpl(AuthorView authorView, BookView bookView, ComposeBookView composeBookView, BookstoreManager manager) {
		this.authorView = authorView;
		this.bookView = bookView;
		this.composeBookView = composeBookView;
		this.manager = manager;
	}

	@Override
	public void allAuthors() {
		showAllAuthors();

	}

	@Override
	public void allBooks() {
		showAllBooks();

	}


//	@Override
//	public void newBook(Book book) {
//		bookView.bookAdded(manager.newBook(book));
//	}

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
			showAllBooks();
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
			showAllAuthors();
		}
	}


	@Override
	public void composeBook() {
		composeBookView.showAuthorList(manager.getAllAuthors());
	}

	@Override
	public void saveComposedBook() {
		Optional<Book> optionalBook = composeBookView.getBook();
		if (optionalBook.isPresent())
			bookView.bookAdded(manager.newBook(optionalBook.get()));
	}
	
	private void showAllAuthors() {
		authorView.showAllAuthors(manager.getAllAuthors());
	}
	
	private void showAllBooks() {
		bookView.showAllBooks(manager.getAllBooks());
	}

}
