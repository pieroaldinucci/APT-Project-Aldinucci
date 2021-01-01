package piero.aldinucci.apt.bookstore.service;

import java.util.List;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.transaction.TransactionManager;

public class BookstoreManagerImpl implements BookstoreManager {

	public BookstoreManagerImpl(TransactionManager transactionManager) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Book newBook(Book book) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Author newAuthor(Author author) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Author author) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Book book) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Author> getAllAuthors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Book> getAllBooks() {
		// TODO Auto-generated method stub
		return null;
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
