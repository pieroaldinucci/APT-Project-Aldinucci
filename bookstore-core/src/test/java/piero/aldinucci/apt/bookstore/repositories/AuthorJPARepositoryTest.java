package piero.aldinucci.apt.bookstore.repositories;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

public class AuthorJPARepositoryTest {

	private AuthorJPARepository repository;
	private EntityManagerFactory emFactory;
	private EntityManager entityManager;
	
	@Before
	public void setUp() {
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore");
		entityManager = emFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		entityManager.createQuery("from Author",Author.class).getResultStream()
			.forEach(e -> entityManager.remove(e));
		entityManager.createQuery("from Book",Book.class).getResultStream()
			.forEach(e -> entityManager.remove(e));
		entityManager.getTransaction().commit();
		entityManager.clear();
		
		repository = new AuthorJPARepository(entityManager);
	}
	
	@After
	public void tearDown() {
		entityManager.close();
		emFactory.close();
	}
	
	@Test
	public void test_findAll_with_no_entries_should_return_empty_list() {
		List<Author> authors = repository.findAll();
		
		assertThat(authors).isEmpty();
	}

	@Test
	public void test_findAll_should_return_list_with_all_entities_in_presistence_context() {
		Author author1 = persistAuthor("Person1");
		Author author2 = persistAuthor("Person2");
		
		entityManager.getTransaction().begin();
		List<Author> authors = repository.findAll();
		entityManager.getTransaction().commit();
		
		assertThat(authors).containsExactly(author1,author2);
		assertThat(authors.get(0)).usingRecursiveComparison().isEqualTo(author1);
		assertThat(authors.get(1)).usingRecursiveComparison().isEqualTo(author2);
		authors.stream().forEach(a -> assertThat(entityManager.contains(a)).isTrue());
	}
	
	@Test
	public void test_findById_when_id_doesnt_exist_should_be_an_empty_Optional() {
		Optional<Author> author = repository.findById(2L);
		
		assertThat(author).isEmpty();
	}
	
	@Test
	public void test_findById_when_id_exist_should_return_an_object_inside_presistance_context() {
		Author pAuthor = persistAuthor("test name");
		
		Optional<Author> found = repository.findById(pAuthor.getId());
		
		assertThat(found).isNotEmpty();
		assertThat(found.get()).usingRecursiveComparison().isEqualTo(pAuthor);
		assertThat(entityManager.contains(found.get())).isTrue();
	}
	
	@Test
	public void test_save_author_should_return_an_object_in_persistance_context_with_updated_id() {
		Author authorToSave = new Author(null, "new Author", new HashSet<>());
		
		entityManager.getTransaction().begin();
		Author savedAuthor = repository.save(authorToSave);
		entityManager.getTransaction().commit();
		
		assertThat(savedAuthor).isNotNull();
		assertThat(entityManager.contains(savedAuthor)).isTrue();
		
		EntityManager em2 = emFactory.createEntityManager();
		Author persistedAuthor = em2.find(Author.class, savedAuthor.getId());
		assertThat(persistedAuthor).usingRecursiveComparison().isEqualTo(savedAuthor);
		em2.close();
		
		assertThat(savedAuthor).isNotSameAs(authorToSave);
		
//		assertThatThrownBy(() -> {
//			entityManager.getTransaction().begin();
//			savedAuthor.getBooks().add(new Book());
//			entityManager.getTransaction().commit();
//		}).isInstanceOf(RollbackException.class);
	}
	
	@Test
	public void test_save_new_author_with_an_id_should_throw_exception_and_not_persist() {
		Author newAuthor = new Author(1L, "author name", new HashSet<>());
		
		entityManager.getTransaction().begin();
		assertThatThrownBy(() -> repository.save(newAuthor))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("id of a new Author should be null");
		entityManager.getTransaction().commit();
		
		assertThat(entityManager.createQuery("from Author",Author.class).getResultList()).isEmpty();
	}

	@Test
	public void test_update_author_with_existing_id() {
		Author persistedAuthor = persistAuthor("name to edit");
		Author modifiedAuthor = new Author(persistedAuthor.getId(), "modified name", new HashSet<>());
		
		entityManager.getTransaction().begin();
		repository.update(modifiedAuthor);
		entityManager.getTransaction().commit();
		
		EntityManager em2 = emFactory.createEntityManager();
		Author found = em2.find(Author.class, persistedAuthor.getId());
		assertThat(found).usingRecursiveComparison().isEqualTo(modifiedAuthor);
//		assertThat(modifiedAuthor).usingRecursiveComparison().isEqualTo(persistedAuthor);
//		assertThat(entityManager.contains(modifiedAuthor)).isFalse();
		em2.close();
	}
	
	@Test
	public void test_update_with_non_existant_id_should_throw_and_not_persist() {
		Author modifiedAuthor = new Author(1L, "modified name", new HashSet<>());
		
		entityManager.getTransaction().begin();
		assertThatThrownBy(() -> repository.update(modifiedAuthor))
			.isExactlyInstanceOf(BookstorePersistenceException.class)
			.hasMessage("Cannot find author to update with id: 1");
		entityManager.getTransaction().commit();
		
		assertThat(entityManager.createQuery("from Author",Author.class).getResultList()).isEmpty();
	}
	
	@Test
	public void test_delete_author_success() {
		Author authorToDelete = persistAuthor("Author to be deleted");
		
		entityManager.getTransaction().begin();
		repository.delete(authorToDelete.getId());
		entityManager.getTransaction().commit();
		
		assertThat(entityManager.find(Author.class,authorToDelete.getId()))
			.isNull();
	}
	
	@Test
	public void test_delete_author_when_not_present_should_not_use_entityManager_to_remove_it() {
		assertThatCode(() -> {
			entityManager.getTransaction().begin();
			repository.delete(2L);
			entityManager.getTransaction().commit();
		}).doesNotThrowAnyException();
	}

	
	private Author persistAuthor (String name) {
		EntityManager em2 = emFactory.createEntityManager();
		Author author = new Author(null, name, new HashSet<>());
		em2.getTransaction().begin();
		em2.persist(author);
		em2.getTransaction().commit();
		em2.close();
		return author;
	}
	
}
