package piero.aldinucci.apt.bookstore.repositories;

import java.util.List;
import java.util.Optional;

import piero.aldinucci.apt.bookstore.model.Book;

public interface BookRepository {
	
	public List<Book> findAll();
	public Optional<Book> findById(long id);
	public Book save (Book book);
	public void update (Book book);
	public Book delete (long id);
}
