package piero.aldinucci.apt.bookstore.view;

import java.util.List;

import piero.aldinucci.apt.bookstore.model.Author;

public interface AuthorView {

	public void showAllAuthors(List<Author> authors);

	public void authorAdded(Author author);

	public void authorRemoved(Author author);

	public void showError(String message, Author author);
}
