package piero.aldinucci.apt.bookstore.service;

import java.util.HashSet;
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
		transactionManager.doInTransaction((authorR, bookR) -> {
			Author toDelete = authorR.findById(author.getId()).orElseThrow(
					() -> new BookstorePersistenceException("Could not find author with id: " + author.getId()));
			toDelete.getBooks().stream().forEach(b -> {
				b.getAuthors().remove(toDelete);
				bookR.update(b);
			});
			authorR.delete(toDelete.getId());
			return null;
		});
	}

	@Override
	public void delete(Book book) {
		transactionManager.doInTransaction((authorR, bookR) -> {
			Book toDelete = bookR.findById(book.getId()).orElseThrow(
					() -> new BookstorePersistenceException("Could not find book with id: " + book.getId()));
			toDelete.getAuthors().stream().forEach(a -> {
				a.getBooks().remove(toDelete);
				authorR.update(a);
			});
			bookR.delete(toDelete.getId());
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

	@Override
	public void update(Author author) {
		transactionManager.doInTransaction((authorR, bookR) -> {
			Author oldAuthor = authorR.findById(author.getId()).orElseThrow(() -> 
					new BookstorePersistenceException("Cannot find author to update with id: " + author.getId()));
			
			HashSet<Book> fullSet = new HashSet<>(oldAuthor.getBooks());
			fullSet.addAll(author.getBooks());
			fullSet.stream().forEach(b -> {
				b.getAuthors().remove(oldAuthor);
				if (author.getBooks().contains(b))
					b.getAuthors().add(author);
				bookR.update(b);
			});
			
			authorR.update(author);
			return null;
		});
	}

	@Override
	public void update(Book book) {
		transactionManager.doInTransaction((authorR, bookR) -> {
			Book oldBook = bookR.findById(book.getId()).orElseThrow(() -> 
					new BookstorePersistenceException("Cannot find book to update with id: " + book.getId()));
			
			HashSet<Author> fullSet = new HashSet<>(oldBook.getAuthors());
			fullSet.addAll(book.getAuthors());
			fullSet.stream().forEach(a -> {
				a.getBooks().remove(oldBook);
				if (book.getAuthors().contains(a))
					a.getBooks().add(book);
				authorR.update(a);
			});
			
			bookR.update(book);
			return null;
		});
	}

}
