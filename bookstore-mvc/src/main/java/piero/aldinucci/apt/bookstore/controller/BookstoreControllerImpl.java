package piero.aldinucci.apt.bookstore.controller;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.service.BookstoreManager;
import piero.aldinucci.apt.bookstore.view.AuthorView;
import piero.aldinucci.apt.bookstore.view.BookView;

public class BookstoreControllerImpl implements BookstoreController {

	private AuthorView authorView;
	private BookView bookView;
	private BookstoreManager manager;

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
		manager.delete(book);
		bookView.bookRemoved(book);
	}

	@Override
	public void deleteAuthor(Author author) {
		// TODO Auto-generated method stub

	}

	@Override
	public void composeBook() {
		// TODO Auto-generated method stub

	}

}
