package piero.aldinucci.apt.bookstore.service;

import java.util.List;

import com.google.inject.Inject;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.transaction.TransactionManager;

/**
 * 
 * @author Piero Aldinucci
 *
 */
public class BookstoreManagerImpl implements BookstoreManager {

	private TransactionManager transactionManager;

	/**
	 * 
	 * @param transactionManager it's used to perform CRUD operation within the database. 
	 */
	@Inject
	public BookstoreManagerImpl(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Override
	public Author newAuthor(Author author) {
		if (!author.getBooks().isEmpty())
			throw new IllegalArgumentException("New authors should have an empty Book Set");
		author.setId(null);
		return transactionManager.doInTransaction((authorR, bookR) -> authorR.save(author));
	}

	@Override
	public Book newBook(Book book) {
		book.setId(null);
		return transactionManager.doInTransaction((authorR, bookR) -> {
			Book returnBook = bookR.save(book);
			returnBook.getAuthors().stream().forEach(a -> {
				a.getBooks().add(returnBook);
				authorR.update(a);
			});
			return returnBook;
		});
	}

	@Override
	public void deleteAuthor(long id) {
		transactionManager.doInTransaction((authorR, bookR) -> {
			Author deleted = authorR.delete(id).orElseThrow(() -> 
				new BookstorePersistenceException("Could not find author with id: " + id));
			
			deleted.getBooks().stream().forEach(b -> {
				b.getAuthors().remove(deleted);
				bookR.update(b);
			});
			return null;
		});
	}

	@Override
	public void deleteBook(long id) {
		transactionManager.doInTransaction((authorR, bookR) -> {
			Book deleted = bookR.delete(id).orElseThrow(() -> 
				new BookstorePersistenceException("Could not find book with id: " + id));
			
			deleted.getAuthors().stream().forEach(a -> {
				a.getBooks().remove(deleted);
				authorR.update(a);
			});
			return null;
		});

	}

	@Override
	public List<Author> getAllAuthors() {
		return transactionManager.doInTransaction((authorR, bookR) -> authorR.findAll());
	}

	@Override
	public List<Book> getAllBooks() {
		return transactionManager.doInTransaction((authorR, bookR) -> bookR.findAll());
	}

}
