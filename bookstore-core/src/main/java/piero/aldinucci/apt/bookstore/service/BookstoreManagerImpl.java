package piero.aldinucci.apt.bookstore.service;

import java.util.List;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.transaction.TransactionManager;

public class BookstoreManagerImpl implements BookstoreManager {

	private TransactionManager transactionManager;

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
	public void delete(Author author) {
		transactionManager.doInTransaction((authorR,bookR) -> {
			return authorR.findById(author.getId())
					.orElseThrow(() -> new BookstorePersistenceException("Could not find author with id: "+author.getId()));
		});
	}

	@Override
	public void delete(Book book) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Author> getAllAuthors() {
		return transactionManager.doInTransaction((authorR, bookR) -> authorR.findAll());
	}

	@Override
	public List<Book> getAllBooks() {
		return transactionManager.doInTransaction((authorR, bookR) -> bookR.findAll());
	}

	@Override
	public void update(Author author) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Book book) {
		// TODO Auto-generated method stub

	}

}
