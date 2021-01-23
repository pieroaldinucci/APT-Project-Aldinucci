package piero.aldinucci.apt.bookstore.repositories;

import java.util.List;
import java.util.Optional;

import piero.aldinucci.apt.bookstore.model.Author;

/**
 * 
 *
 */

public interface AuthorRepository {
	/**
	 * 
	 * @return a list of all Author entities present in the database
	 */
	public List<Author> findAll();

	/**
	 * 
	 * @param id primary key of the searched entity
	 * @return Contains the Author found by its respective id if it's present in the
	 *         database, it's empty otherwise.
	 */
	public Optional<Author> findById(long id);

	/**
	 * 
	 * @param author The author to save in the database. Its id field must be null.
	 * @return a copy of the author saved with the assigned id field.
	 */
	public Author save(Author author);

	/**
	 * 
	 * @param author The author that must be updated. Its Id field must not be null.
	 */
	public void update(Author author);

	
	/**
	 * 
	 * @param id the primary key of the author to remove from the database
	 * @return Contains the author that was successfully deleted, or is empty
	 * if the author could not be deleted. 
	 */
	public Optional<Author> delete(long id);
}
