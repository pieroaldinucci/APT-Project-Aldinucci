package piero.aldinucci.apt.bookstore.view.factory;

import piero.aldinucci.apt.bookstore.controller.BookstoreController;
import piero.aldinucci.apt.bookstore.view.AuthorView;
import piero.aldinucci.apt.bookstore.view.BookView;
import piero.aldinucci.apt.bookstore.view.ComposeBookView;

/**
 * 
 * @author Piero Aldinucci
 *
 */
public interface ViewsFactory {

	/**
	 * 
	 * @param controller controller of the view 
	 * @return an instance of view
	 */
	public AuthorView createAuthorView(BookstoreController controller);

	/**
	 * 
	 * @param controller controller of the view 
	 * @return an instance of view
	 */
	public BookView createBookView(BookstoreController controller);

	/**
	 * 
	 * @param controller controller of the view 
	 * @return an instance of view
	 */
	public ComposeBookView createComposeBookView(BookstoreController controller);
}
