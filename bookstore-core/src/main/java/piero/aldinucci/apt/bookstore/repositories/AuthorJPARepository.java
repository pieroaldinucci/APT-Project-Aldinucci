package piero.aldinucci.apt.bookstore.repositories;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;

public class AuthorJPARepository implements AuthorRepository{

	private EntityManager entityManager;

	public AuthorJPARepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Author> findAll() {
		return entityManager.createQuery("from Author",Author.class).getResultList();
	}

	@Override
	public Optional<Author> findById(long id) {
		return Optional.ofNullable(entityManager.find(Author.class,id));
	}

	@Override
	public Author save(Author author) {
		if (author.getId() != null)
			throw new IllegalArgumentException("id of a new Author should be null");
		return entityManager.merge(author);
	}

	@Override
	public void update(Author author) {
		Author oldAuthor = entityManager.find(Author.class, author.getId());
		if (oldAuthor == null)
			throw new BookstorePersistenceException("Cannot find author to update with id: "+author.getId());
		entityManager.merge(author);
	}

	@Override
	public void delete(long id) {
		Author author = entityManager.find(Author.class, id);
		if (author != null)
			entityManager.remove(author);
	}

}
