package piero.aldinucci.apt.bookstore.repositories;

import java.util.List;
import java.util.Optional;

import piero.aldinucci.apt.bookstore.model.Author;

public interface AuthorRepository {
	public List<Author> findAll();
	public Optional<Author> findById(long id);
	public Author save(Author author);
	public void update (Author author);
	public Optional<Author> delete (long id);
}
