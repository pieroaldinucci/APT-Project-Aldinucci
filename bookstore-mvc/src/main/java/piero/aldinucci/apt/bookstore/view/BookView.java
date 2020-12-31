package piero.aldinucci.apt.bookstore.view;

import java.util.List;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

public interface BookView {
	public void showAllBooks(List<Book> books);
	public void bookAdded(Book book);
	public void bookRemoved(Book book);
	public void showError(String message, Book book);
}
