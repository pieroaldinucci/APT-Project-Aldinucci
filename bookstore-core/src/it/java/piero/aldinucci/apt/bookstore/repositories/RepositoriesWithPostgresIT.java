package piero.aldinucci.apt.bookstore.repositories;

import static org.assertj.core.api.Assertions.assertThat;

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

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

public class RepositoriesWithPostgresIT {

	private AuthorJPARepository authorRep;
	private BookJPARepository bookRep;
	private EntityManagerFactory emFactory;
	private EntityManager entityManager;
	
	@Before
	public void setUp() {
		HashMap<String, String> propertiesJPA = new HashMap<String, String>();
		propertiesJPA.put("javax.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/projectAPTTestDb");
		propertiesJPA.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
		propertiesJPA.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore",propertiesJPA);
		entityManager = emFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		entityManager.createQuery("from Author",Author.class).getResultStream()
			.forEach(e -> entityManager.remove(e));
		entityManager.createQuery("from Book",Book.class).getResultStream()
			.forEach(e -> entityManager.remove(e));
		entityManager.getTransaction().commit();
		entityManager.clear();
		
		authorRep = new AuthorJPARepository(entityManager);
		bookRep = new BookJPARepository(entityManager);
	}
	
	@After
	public void tearDown() {
		entityManager.close();
		emFactory.close();
	}
	
	@Test
	public void test_findAll_should_return_list_with_all_authors_in_presistence_context() {
		Author author1 = persistAuthor("Person1");
		Author author2 = persistAuthor("Person2");
		
		entityManager.getTransaction().begin();
		List<Author> authors = authorRep.findAll();
		entityManager.getTransaction().commit();
		
		assertThat(authors).containsExactly(author1,author2);
		assertThat(authors.get(0)).usingRecursiveComparison().isEqualTo(author1);
		assertThat(authors.get(1)).usingRecursiveComparison().isEqualTo(author2);
		authors.stream().forEach(a -> assertThat(entityManager.contains(a)).isTrue());
	}
	
	@Test
	public void test_findById_when_id_exist_should_return_an_author_inside_presistance_context() {
		Author pAuthor = persistAuthor("test name");
		
		Optional<Author> found = authorRep.findById(pAuthor.getId());
		
		assertThat(found).isNotEmpty();
		assertThat(found.get()).usingRecursiveComparison().isEqualTo(pAuthor);
		assertThat(entityManager.contains(found.get())).isTrue();
	}
	
	@Test
	public void test_save_author_with_book() {
		Book bookToSave = new Book(null,"A book",new HashSet<>());
		Author authorToSave = new Author(null, "new Author", new HashSet<>());
		
		entityManager.getTransaction().begin();
		Book savedBook = bookRep.save(bookToSave);
		authorToSave.getBooks().add(savedBook);
		Author savedAuthor = authorRep.save(authorToSave);
		entityManager.getTransaction().commit();
		
		EntityManager em2 = emFactory.createEntityManager();
		Author persistedAuthor = em2.find(Author.class, savedAuthor.getId());
		assertThat(persistedAuthor).usingRecursiveComparison().isEqualTo(savedAuthor);
		em2.close();
	}
	
	@Test
	public void test_update_author_with_different_name_and_books() {
		Book book1 = persistBook("first book");
		Book book2 = persistBook("second book");
		Author persistedAuthor = new Author (null,"name to edit", new HashSet<>());
		persistedAuthor.getBooks().add(book1);
		entityManager.getTransaction().begin();
		entityManager.persist(persistedAuthor);
		entityManager.getTransaction().commit();
		entityManager.clear();
		
		
		Author modifiedAuthor = new Author(persistedAuthor.getId(), "modified name", new HashSet<>());
		modifiedAuthor.getBooks().add(book2);
		
		entityManager.getTransaction().begin();
		authorRep.update(modifiedAuthor);
		entityManager.getTransaction().commit();
		entityManager.clear();
		
		Author found = entityManager.find(Author.class, persistedAuthor.getId());
		assertThat(found).usingRecursiveComparison().isEqualTo(modifiedAuthor);
		assertThat(found).usingRecursiveComparison().isNotEqualTo(persistedAuthor);
	}
	
	@Test
	public void test_delete_author_success() {
		Author authorToDelete = persistAuthor("Author to be deleted");
		
		entityManager.getTransaction().begin();
		authorRep.delete(authorToDelete.getId());
		entityManager.getTransaction().commit();
		
		assertThat(entityManager.find(Author.class,authorToDelete.getId()))
			.isNull();
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
