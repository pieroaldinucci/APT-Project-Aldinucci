package piero.aldinucci.apt.bookstore.repositories;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import piero.aldinucci.apt.bookstore.model.Author;

/**
 * JPA specific implementation of AuthorRepository
 *
 */

public class AuthorJPARepository implements AuthorRepository{

	private EntityManager entityManager;

	/**
	 * 
	 * @param entityManager the interface used to interact wit the persistence context.
	 */
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
		if (author.getId() == null)
			throw new IllegalArgumentException("Cannot update an author with null id");
		if (entityManager.find(Author.class, author.getId()) != null) 
				entityManager.merge(author);
	}

	@Override
	public Optional<Author> delete(long id) {
		Author author = entityManager.find(Author.class, id);
		if (author != null) 
			entityManager.remove(author);
		return Optional.ofNullable(author);
	}

}
