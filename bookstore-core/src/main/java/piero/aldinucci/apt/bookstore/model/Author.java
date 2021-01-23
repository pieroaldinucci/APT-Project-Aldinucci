package piero.aldinucci.apt.bookstore.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 * 
 *
 */

@Entity
public class Author {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Book> books;

	/**
	 * Constructor without argument, needed by JPA Entity.
	 */
	public Author() {
	}

	/**
	 * 
	 * @param id The identifier and primary key of the model. 
	 * @param name Descriptive name of the Author
	 * @param books Collection of all books wrote by the author.
	 */
	public Author(Long id, String name, Set<Book> books) {
		this.id = id;
		this.name = name;
		this.books = books;
	}
	
	/**
	 * 
	 * @return identifier of the entity
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * 
	 * @param id identifier of the entity
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 
	 * @return descriptive name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name descriptive name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return Collection of Books wrote by the author
	 */
	public Set<Book> getBooks() {
		return books;
	}

	/**
	 * 
	 * @param books Collection of Books wrote by the author
	 */
	public void setBooks(Set<Book> books) {
		this.books = books;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Author other = (Author) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else 
			if (!id.equals(other.id))
				return false;
		return true;
	}

	@Override
	public String toString() {
		return "Author [Id=" + id + ", name=" + name + "]";
	}

}
