package piero.aldinucci.apt.bookstore.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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

import piero.aldinucci.apt.bookstore.model.Book;

public class BookJPARepositoryTest {

	private BookJPARepository repository;
	private EntityManagerFactory emFactory;
	private EntityManager entityManager;
	
	@Before
	public void setUp() {
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore");
		entityManager = emFactory.createEntityManager();
		
		repository = new BookJPARepository(entityManager);
	}
	
	@After
	public void tearDown() {
		entityManager.close();
		emFactory.close();
	}
	
	@Test
	public void test_findAll_with_no_entries_should_return_empty_list() {
		List<Book> books= repository.findAll();
		
		assertThat(books).isEmpty();
	}
	
	@Test
	public void test_findAll_should_return_list_with_all_entities_in_presistence_context() {
		Book book1 = persistBook("Title1");
		Book book2 = persistBook("Title2");
		
		entityManager.getTransaction().begin();
		List<Book> books = repository.findAll();
		entityManager.getTransaction().commit();
		
		assertThat(books).containsExactly(book1,book2);
		assertThat(books.get(0)).usingRecursiveComparison().isEqualTo(book1);
		assertThat(books.get(1)).usingRecursiveComparison().isEqualTo(book2);
		books.stream().forEach(b -> assertThat(entityManager.contains(b)).isTrue());
	}
	
	@Test
	public void test_findById_when_id_doesnt_exist_should_be_an_empty_Optional() {
		Optional<Book> book = repository.findById(3L);
		
		assertThat(book).isEmpty();
	}
	
	@Test
	public void test_findById_when_id_exist_should_return_an_object_inside_presistance_context() {
		Book pBook = persistBook("test name");
		
		Optional<Book> found = repository.findById(pBook.getId());
		
		assertThat(found).isNotEmpty();
		assertThat(found.get()).usingRecursiveComparison().isEqualTo(pBook);
		assertThat(entityManager.contains(found.get())).isTrue();
	}
	
	@Test
	public void test_save_book_should_return_an_object_in_persistance_context_with_updated_id() {
		Book bookToSave = new Book(null, "new Book", new HashSet<>());
		EntityManager em2 = emFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		Book savedBook = repository.save(bookToSave);
		entityManager.getTransaction().commit();
		
		assertThat(savedBook).isNotNull();
		assertThat(entityManager.contains(savedBook)).isTrue();
		assertThat(entityManager.contains(bookToSave)).isFalse(); //enforced behaviour. In reality not needed
		Book persistedAuthor = em2.find(Book.class, savedBook.getId());
		assertThat(persistedAuthor).usingRecursiveComparison().isEqualTo(savedBook);
		assertThat(savedBook).isNotSameAs(bookToSave);
		
		em2.close();
	}
	
	@Test
	public void test_save_new_book_with_an_id_should_throw_exception_and_not_persist() {
		Book newBook = new Book(1L, "book title", new HashSet<>());
		
		entityManager.getTransaction().begin();
		assertThatThrownBy(() -> repository.save(newBook))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("id of a new Book should be null");
		entityManager.getTransaction().commit();
		
		assertThat(entityManager.createQuery("from Book",Book.class).getResultList()).isEmpty();
	}

	@Test
	public void test_update_book_with_existing_id() {
		Book persistedBook = persistBook("title to edit");
		Book modifiedBook = new Book(persistedBook.getId(), "modified title", new HashSet<>());
		
		entityManager.getTransaction().begin();
		repository.update(modifiedBook);
		entityManager.getTransaction().commit();
		
		EntityManager em2 = emFactory.createEntityManager();
		Book found = em2.find(Book.class, persistedBook.getId());
		assertThat(found).usingRecursiveComparison().isEqualTo(modifiedBook);
		assertThat(entityManager.contains(modifiedBook)).isFalse();
		em2.close();
	}
	
	@Test
	public void test_update_with_null_id_should_throw_IllegalArgumentException() {
		Book modifiedBook = new Book(null, "modified title", new HashSet<>());
		
		entityManager.getTransaction().begin();
		assertThatThrownBy(() -> repository.update(modifiedBook))
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.hasMessage("Cannot update a book with null id");
		entityManager.getTransaction().commit();
		
		assertThat(entityManager.createQuery("from Book",Book.class).getResultList()).isEmpty();
	}
	
	@Test
	public void test_update_with_non_existant_id_should_not_save_into_db() {
		Book nonExistantdBook = new Book(3L, "modified title", new HashSet<>());
		
		entityManager.getTransaction().begin();
		repository.update(nonExistantdBook);
		entityManager.getTransaction().commit();
		entityManager.clear();
		
		assertThat(entityManager.createQuery("from Book",Book.class).getResultList()).isEmpty();
	}
	
	@Test
	public void test_delete_book_success_should_return_Optional_of_the_book() {
		Book bookToDelete = persistBook("Book to be deleted");
		
		entityManager.getTransaction().begin();
		Optional<Book> deletedBook = repository.delete(bookToDelete.getId());
		entityManager.getTransaction().commit();
		
		assertThat(deletedBook).isEqualTo(Optional.of(bookToDelete));
		assertThat(entityManager.find(Book.class,bookToDelete.getId()))
			.isNull();
	}
	
	@Test
	public void test_delete_book_when_not_present_should_return_empty_optional_and_not_thrown_IllegalArgumentException() {
		assertThatCode(() -> {
			entityManager.getTransaction().begin();
			Optional<Book> book = repository.delete(2L);
			entityManager.getTransaction().commit();
			
			assertThat(book).isEmpty();
		}).doesNotThrowAnyException();
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
