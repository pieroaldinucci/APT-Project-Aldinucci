package piero.aldinucci.apt.bookstore.controller;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

public interface BookstoreController {
	public void allAuthors();
	public void allBooks();
//	public void newBook(Book book);
	public void newAuthor(Author author);
	public void deleteBook(Book book);
	public void deleteAuthor(Author author);
	public void composeBook();
	public void saveComposedBook();
}
