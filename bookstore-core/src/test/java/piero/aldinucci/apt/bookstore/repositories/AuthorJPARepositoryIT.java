package piero.aldinucci.apt.bookstore.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

public class AuthorJPARepositoryIT {

	private static final String FIXTURE_NAME_1 = "test name 1";
	private static final String FIXTURE_NAME_2 = "Test name 2";
	private static final String FIXTURE_TITLE_1 = "Title 1";
	private AuthorJPARepository repository;
	private EntityManagerFactory emFactory;
	private EntityManager entityManager;
	
	@Before
	public void setUp() {
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore.test");
		entityManager = emFactory.createEntityManager();
		
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
		Author author1 = persistAuthor(FIXTURE_NAME_1);
		Author author2 = persistAuthor(FIXTURE_NAME_2);
		
		entityManager.getTransaction().begin();
		List<Author> authors = repository.findAll();
		entityManager.getTransaction().commit();
		
		assertThat(authors).containsExactly(author1,author2);
		assertThat(authors.get(0)).usingRecursiveComparison().isEqualTo(author1);
		assertThat(authors.get(1)).usingRecursiveComparison().isEqualTo(author2);
		authors.stream().forEach(a -> assertThat(entityManager.contains(a)).isTrue());
	}
	
	@Test
	public void test_findById_when_id_doesnt_exist_should_return_an_empty_Optional() {
		Optional<Author> author = repository.findById(2L);
		
		assertThat(author).isEmpty();
	}
	
	@Test
	public void test_findById_when_id_exist_should_return_an_object_inside_presistance_context() {
		Author pAuthor = persistAuthor(FIXTURE_NAME_1);
		
		Optional<Author> found = repository.findById(pAuthor.getId());
		
		assertThat(found).isNotEmpty();
		assertThat(found.get()).usingRecursiveComparison().isEqualTo(pAuthor);
		assertThat(entityManager.contains(found.get())).isTrue();
	}
	
	@Test
	public void test_save_author_should_return_an_object_in_persistance_context_with_updated_id() {
		Author authorToSave = new Author(null, FIXTURE_NAME_1, new HashSet<>());
		EntityManager em2 = emFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		Author savedAuthor = repository.save(authorToSave);
		entityManager.getTransaction().commit();
		
		assertThat(savedAuthor).isNotNull();
		assertThat(entityManager.contains(savedAuthor)).isTrue();
		assertThat(em2.find(Author.class, savedAuthor.getId()))
			.usingRecursiveComparison().isEqualTo(savedAuthor);
		assertThat(savedAuthor).isNotSameAs(authorToSave);
		em2.close();
	}
	
	@Test
	public void test_save_new_author_with_an_id_should_throw_exception_and_not_persist() {
		Author newAuthor = new Author(1L, FIXTURE_NAME_1, new HashSet<>());
		
		entityManager.getTransaction().begin();
		assertThatThrownBy(() -> repository.save(newAuthor))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("id of a new Author should be null");
		entityManager.getTransaction().commit();
		
		assertThat(entityManager.createQuery("from Author",Author.class).getResultList()).isEmpty();
	}

	@Test
	public void test_update_author_with_existing_id() {
		Author persistedAuthor = persistAuthor(FIXTURE_NAME_1);
		Author modifiedAuthor = new Author(persistedAuthor.getId(), FIXTURE_NAME_2, new HashSet<>());
		modifiedAuthor.getBooks().add(persistBook(FIXTURE_TITLE_1));
		EntityManager em2 = emFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		repository.update(modifiedAuthor);
		entityManager.getTransaction().commit();
		
		assertThat(em2.find(Author.class, persistedAuthor.getId()))
			.usingRecursiveComparison().isEqualTo(modifiedAuthor);
		assertThat(entityManager.contains(modifiedAuthor)).isFalse();
		em2.close();
	}
	
	@Test
	public void test_update_with_null_id_should_throw_IllegalArgumentException() {
		Author modifiedAuthor = new Author(null, FIXTURE_NAME_1, new HashSet<>());
		
		entityManager.getTransaction().begin();
		assertThatThrownBy(() -> repository.update(modifiedAuthor))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("Cannot update an author with null id");
		entityManager.getTransaction().commit();
		
		assertThat(entityManager.createQuery("from Author",Author.class).getResultList()).isEmpty();
	}
	
	@Test
	public void test_update_with_non_existant_id_should_not_save_into_db() {
		Author nonExistantAuthor = new Author(5L, FIXTURE_NAME_1, new HashSet<>());
		
		entityManager.getTransaction().begin();
		repository.update(nonExistantAuthor);
		entityManager.getTransaction().commit();
		entityManager.clear();
		
		assertThat(entityManager.createQuery("from Author",Author.class).getResultList()).isEmpty();
	}
	
	@Test
	public void test_delete_author_success() {
		Author authorToDelete = persistAuthor(FIXTURE_NAME_1);
		
		entityManager.getTransaction().begin();
		Optional<Author> deletedAuthor = repository.delete(authorToDelete.getId());
		entityManager.getTransaction().commit();
		
		assertThat(deletedAuthor).usingRecursiveComparison()
			.isEqualTo(Optional.of(authorToDelete));
		assertThat(entityManager.find(Author.class,authorToDelete.getId()))
			.isNull();
	}
	
	@Test
	public void test_delete_author_when_not_present_should_return_empy_optional() {
		entityManager.getTransaction().begin();
		Optional<Author> author = repository.delete(2L);
		entityManager.getTransaction().commit();
		
		assertThat(author).isEmpty();
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
	
	private Book persistBook (String title) {
		EntityManager em2 = emFactory.createEntityManager();
		Book book = new Book(null, title, new HashSet<>());
		em2.getTransaction().begin();
		em2.persist(book);
		em2.getTransaction().commit();
		em2.close();
		return book;
	}
}
