package piero.aldinucci.apt.bookstore.view.swing;

import java.util.List;
import java.util.Optional;

import javax.swing.JDialog;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

public abstract class NewBookDialog extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public abstract Optional<Book> getReturnValue();
	public abstract void setAuthorList(List<Author> authors);

}
