package piero.aldinucci.apt.bookstore.repositories;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Book;

public class BookJPARepository implements BookRepository{

	private EntityManager entityManager;

	public BookJPARepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Book> findAll() {
		return entityManager.createQuery("from Book",Book.class).getResultList();
	}

	@Override
	public Optional<Book> findById(long id) {
		return Optional.ofNullable(entityManager.find(Book.class, id));
	}

	@Override
	public Book save(Book book) {
		if (book.getId() != null)
			throw new IllegalArgumentException("id of a new Book should be null");
		return entityManager.merge(book);
	}

	@Override
	public void update(Book book) {
		Book oldBook = entityManager.find(Book.class, book.getId());
		if (oldBook == null)
			throw new BookstorePersistenceException("Cannot find book to update with id: "+book.getId());
		entityManager.merge(book);
	}

	@Override
	public Book delete(long id) {
		Book book = entityManager.find(Book.class, id);
		if (book != null)
			entityManager.remove(book);
		return book;
	}

}
