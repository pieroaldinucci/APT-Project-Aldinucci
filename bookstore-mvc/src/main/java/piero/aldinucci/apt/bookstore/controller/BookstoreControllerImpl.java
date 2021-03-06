package piero.aldinucci.apt.bookstore.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.service.BookstoreManager;
import piero.aldinucci.apt.bookstore.view.AuthorView;
import piero.aldinucci.apt.bookstore.view.BookView;
import piero.aldinucci.apt.bookstore.view.ComposeBookView;

/**
 * 
 * @author Piero Aldinucci
 *
 */
public class BookstoreControllerImpl implements BookstoreController {

	private static final Logger LOGGER = LogManager.getLogger();
	
	@Inject
	private AuthorView authorView;
	
	@Inject
	private BookView bookView;
	
	@Inject
	private ComposeBookView composeBookView;
	
	private BookstoreManager manager;
	
	/**
	 * 
	 * @param manager the service layer that handles interaction with the underlying DBS
	 */
	@Inject
	public BookstoreControllerImpl(BookstoreManager manager) {
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

	@Override
	public void newAuthor(Author author) {
		authorView.authorAdded(manager.newAuthor(author));
	}

	@Override
	public void deleteBook(Book book) {
		try {
			manager.deleteBook(book.getId());
			bookView.bookRemoved(book);
		} catch (BookstorePersistenceException e) {
			LOGGER.error("Controller: Error while deleting book",e);
			bookView.showError("Error while deleting book", book);
			showAllBooks();
		} finally {
			showAllAuthors();
		}
	}

	@Override
	public void deleteAuthor(Author author) {
		try {
			manager.deleteAuthor(author.getId());
			authorView.authorRemoved(author);
		} catch (BookstorePersistenceException e) {
			LOGGER.error("Controller: Error while deleting author",e);
			authorView.showError("Error while deleting author", author);
			showAllAuthors();
		} finally {
			showAllBooks();
		}
	}


	@Override
	public void composeBook() {
		composeBookView.composeNewBook(manager.getAllAuthors());
	}

	@Override
	public void newBook(Book book) {
		bookView.bookAdded(manager.newBook(book));
	}
	
	/**
	 * 
	 */
	private void showAllAuthors() {
		authorView.showAllAuthors(manager.getAllAuthors());
	}
	
	/**
	 * 
	 */
	private void showAllBooks() {
		bookView.showAllBooks(manager.getAllBooks());
	}

	/**
	 * 
	 * @param composeBookView view to manage
	 */
	public void setComposeBookView(ComposeBookView composeBookView) {
		this.composeBookView = composeBookView;
	}

	/**
	 * 
	 * @param bookView  view to manage
	 */
	public void setBookView(BookView bookView) {
		this.bookView = bookView;
	}

	/**
	 * 
	 * @param authorView  view to manage
	 */
	public void setAuthorView(AuthorView authorView) {
		this.authorView = authorView;
	}
	
	/**
	 * 
	 * @return managed view
	 */
	public AuthorView getAuthorView() {
		return authorView;
	}
	
	/**
	 * 
	 * @return managed view
	 */
	public BookView getBookView() {
		return bookView;
	}
	
	/**
	 * 
	 * @return managed view
	 */
	public ComposeBookView getComposeBookView() {
		return composeBookView;
	}
}
