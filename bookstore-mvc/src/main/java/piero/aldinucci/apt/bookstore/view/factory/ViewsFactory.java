package piero.aldinucci.apt.bookstore.view.factory;

import piero.aldinucci.apt.bookstore.controller.BookstoreController;
import piero.aldinucci.apt.bookstore.view.AuthorView;
import piero.aldinucci.apt.bookstore.view.BookView;
import piero.aldinucci.apt.bookstore.view.ComposeBookView;

public interface ViewsFactory {

	public AuthorView createAuthorView(BookstoreController controller);

	public BookView createBookView(BookstoreController controller);

	public ComposeBookView createComposeBookView(BookstoreController controller);
}
