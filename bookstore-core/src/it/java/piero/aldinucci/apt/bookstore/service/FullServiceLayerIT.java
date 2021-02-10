package piero.aldinucci.apt.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.repositories.factory.RepositoriesJPAFactoryImpl;
import piero.aldinucci.apt.bookstore.transaction.TransactionManagerJPA;

public class FullServiceLayerIT {

	private BookstoreManagerImpl bookstoreManager;

	private EntityManagerFactory emFactory;
	private List<Book> fixtureBooks;
	private List<Author> fixtureAuthors;

	@Before
	public void setUp() {
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore.test");

		bookstoreManager = new BookstoreManagerImpl(
				new TransactionManagerJPA(emFactory, new RepositoriesJPAFactoryImpl()));

		populateDB();
	}

	@After
	public void tearDown() {
		emFactory.close();
	}

	@Test
	public void test_getAll_authors() {
		List<Author> authors = bookstoreManager.getAllAuthors();

		assertThat(authors).usingRecursiveFieldByFieldElementComparator().isEqualTo(authors);
	}

	@Test
	public void test_getAll_books() {
		List<Book> books = bookstoreManager.getAllBooks();

		assertThat(books).usingRecursiveFieldByFieldElementComparator().isEqualTo(books);
	}

	@Test
	public void test_delete_author_success() {
		bookstoreManager.deleteAuthor(fixtureAuthors.get(0).getId());

		EntityManager em = emFactory.createEntityManager();
		Author author = em.createQuery("from Author", Author.class).getSingleResult();
		List<Book> bookList = em.createQuery("from Book", Book.class).getResultList();
		em.close();

		assertThat(author).isEqualTo(fixtureAuthors.get(1));
		assertThat(bookList.get(0).getAuthors()).isEmpty();
		assertThat(bookList.get(1).getAuthors()).containsExactly(author);
	}

	@Test
	public void test_delete_author_when_author_is_missing() {
		assertThatThrownBy(() -> bookstoreManager.deleteAuthor(27))
			.isInstanceOf(BookstorePersistenceException.class);

		EntityManager em = emFactory.createEntityManager();
		List<Author> authorList = em.createQuery("from Author", Author.class).getResultList();
		List<Book> bookList = em.createQuery("from Book", Book.class).getResultList();
		em.close();
		assertThat(authorList).usingFieldByFieldElementComparator().isEqualTo(fixtureAuthors);
		assertThat(bookList).usingFieldByFieldElementComparator().isEqualTo(fixtureBooks);
	}

	@Test
	public void test_delete_book_success() {
		bookstoreManager.deleteBook(fixtureBooks.get(1).getId());

		EntityManager em = emFactory.createEntityManager();
		List<Author> authorList = em.createQuery("from Author", Author.class).getResultList();
		Book book = em.createQuery("from Book", Book.class).getSingleResult();
		em.close();

		assertThat(book).isEqualTo(fixtureBooks.get(0));
		assertThat(authorList.get(0).getBooks()).containsExactly(book);
		assertThat(authorList.get(1).getBooks()).isEmpty();
	}

	@Test
	public void test_delete_book_when_is_missing() {

		assertThatThrownBy(() -> bookstoreManager.deleteBook(17))
			.isInstanceOf(BookstorePersistenceException.class);

		EntityManager em = emFactory.createEntityManager();
		List<Author> authorList = em.createQuery("from Author", Author.class).getResultList();
		List<Book> bookList = em.createQuery("from Book", Book.class).getResultList();
		em.close();
		assertThat(authorList).usingFieldByFieldElementComparator().isEqualTo(fixtureAuthors);
		assertThat(bookList).usingFieldByFieldElementComparator().isEqualTo(fixtureBooks);
	}

	@Test
	public void test_adding_new_book_with_author_should_update_the_author_as_well() {
		Book book = new Book(null, "title 3", new HashSet<>());
		book.getAuthors().add(fixtureAuthors.get(1));

		book = bookstoreManager.newBook(book);

		EntityManager em = emFactory.createEntityManager();
		Author author = em.find(Author.class, fixtureAuthors.get(1).getId());
		em.close();

		assertThat(author.getBooks()).contains(book);
	}

	private void populateDB() {
		Author author1 = new Author(null, "name 1", new HashSet<>());
		Author author2 = new Author(null, "name 2", new HashSet<>());
		Book book1 = new Book(null, "title 1", new HashSet<>());
		Book book2 = new Book(null, "title 2", new HashSet<>());

		fixtureAuthors = new LinkedList<>();
		fixtureAuthors.add(author1);
		fixtureAuthors.add(author2);
		fixtureBooks = new LinkedList<>();
		fixtureBooks.add(book1);
		fixtureBooks.add(book2);

		EntityManager em = emFactory.createEntityManager();
		em.getTransaction().begin();
		fixtureAuthors.stream().forEach(a -> em.persist(a));
		fixtureBooks.stream().forEach(b -> em.persist(b));
		author1.getBooks().add(book1);
		book1.getAuthors().add(author1);
		author2.getBooks().add(book2);
		book2.getAuthors().add(author2);
		author1.getBooks().add(book2);
		book2.getAuthors().add(author1);
		em.getTransaction().commit();
		em.close();
	}
}
