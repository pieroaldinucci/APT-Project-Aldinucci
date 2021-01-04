package piero.aldinucci.apt.bookstore.view;

import java.util.List;
import java.util.Optional;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

public interface ComposeBookView {

	public void composeNewBook(List<Author> authors);
//	public void modifyBook(Book book, List<Author> authors);
}
