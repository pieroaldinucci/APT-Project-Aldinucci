package piero.aldinucci.apt.bookstore.view;

import java.util.List;

import piero.aldinucci.apt.bookstore.model.Author;

public interface ComposeBookView {

	public void composeNewBook(List<Author> authors);
//	public void modifyBook(Book book, List<Author> authors);
}
