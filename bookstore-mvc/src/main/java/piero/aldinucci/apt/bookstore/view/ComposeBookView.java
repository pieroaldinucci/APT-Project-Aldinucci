package piero.aldinucci.apt.bookstore.view;

import java.util.List;

import piero.aldinucci.apt.bookstore.model.Author;

/**
 * This view is used to create new Book objects, using the available authors.
 * @author Piero Aldinucci
 *
 */
public interface ComposeBookView {

	/**
	 * Initializa and show the view
	 * @param authors List of available authors
	 */
	public void composeNewBook(List<Author> authors);
}
