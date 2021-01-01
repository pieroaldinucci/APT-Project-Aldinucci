package piero.aldinucci.apt.bookstore.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.AdditionalAnswers.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;
import piero.aldinucci.apt.bookstore.transaction.TransactionCode;
import piero.aldinucci.apt.bookstore.transaction.TransactionManager;

public class BookstoreManagerImplTest {

	@Mock
	AuthorRepository authorRepository;

	@Mock
	BookRepository bookRepository;

	@Mock
	TransactionManager transactionManager;

	BookstoreManagerImpl bookstoreManager;

	@Before
	public void setUp() {
		openMocks(this);

		when(transactionManager.doInTransaction(any()))
				.thenAnswer(answer((TransactionCode<?> code) -> code.apply(authorRepository, bookRepository)));

		bookstoreManager = new BookstoreManagerImpl(transactionManager);
	}

	@Test
	public void test_newAuthor_should_generate_return_Author_with_db_id() {
		Author author = new Author(1L, "First Author", new HashSet<>());
		Author returnedAuthor = new Author(3L, "First Author", new HashSet<>());
		when(authorRepository.save(isA(Author.class))).thenReturn(returnedAuthor);

		assertThat(bookstoreManager.newAuthor(author)).isSameAs(returnedAuthor);
		verify(authorRepository).save(new Author(null, "First Author", new HashSet<>()));
	}

	@Test
	public void test_newAuthor_with_not_empty_book_Set_should_throw() {
		Author author = new Author(null, "First Author", new HashSet<>());
		Book book = new Book(null, "A book", new HashSet<>());
		author.getBooks().add(book);

		assertThatThrownBy(() -> bookstoreManager.newAuthor(author)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("New authors should have an empty Book Set");
		verifyNoInteractions(transactionManager);
	}

	@Test
	public void test_newBook_should_create_new_book_and_set_the_id() {
		Book book = new Book(2L, "A great book", new HashSet<>());
		Book returnedBook = new Book(1L, "A great book", new HashSet<>());
		when(bookRepository.save(isA(Book.class))).thenReturn(returnedBook);

		assertThat(bookstoreManager.newBook(book)).isSameAs(returnedBook);
		verify(bookRepository).save(new Book(null, "A great book", new HashSet<>()));
	}

	@Test
	public void test_addBook_should_update_all_authors_in_its_Set() {
		Book book = new Book(null, "3 hands book", null);
		Author author1 = new Author(1L, "First author", new HashSet<>());
		Author author2 = new Author(5L, "Second author", new HashSet<>());
		Book returnedBook = new Book(1L, "4 hands book", new LinkedHashSet<>());
		returnedBook.getAuthors().add(author1);
		returnedBook.getAuthors().add(author2);
		when(bookRepository.save(isA(Book.class))).thenReturn(returnedBook);

		assertThat(bookstoreManager.newBook(book)).isSameAs(returnedBook);

		InOrder inOrder = Mockito.inOrder(authorRepository, bookRepository);
		inOrder.verify(bookRepository).save(book);
		inOrder.verify(authorRepository).update(author1);
		inOrder.verify(authorRepository).update(author2);
		assertThat(author1.getBooks()).containsExactly(returnedBook);
		assertThat(author2.getBooks()).containsExactly(returnedBook);
	}

	@Test
	public void test_getAllAuthors() {
		Author author1 = new Author(2L, "Test Author", new HashSet<>());
		Author author2 = new Author(10L, "2nd Test Author", new HashSet<>());
		when(authorRepository.findAll()).thenReturn(Arrays.asList(author1, author2));

		List<Author> authors = bookstoreManager.getAllAuthors();

		assertThat(authors).containsExactly(author1, author2);
		verify(authorRepository).findAll();
		verifyNoMoreInteractions(authorRepository);
		verifyNoInteractions(bookRepository);
	}

	@Test
	public void test_getAllBooks() {
		Book book1 = new Book(1L, "First book", new HashSet<>());
		Book book2 = new Book(5L, "Second book", new HashSet<>());
		when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

		List<Book> books = bookstoreManager.getAllBooks();

		assertThat(books).containsExactly(book1, book2);
		verify(bookRepository).findAll();
		verifyNoMoreInteractions(bookRepository);
		verifyNoInteractions(authorRepository);
	}

	@Test
	public void test_delete_author_when_author_not_present() {
		Author author = new Author(1L, "author to delete", new HashSet<>());
		when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookstoreManager.delete(author))				
				.isExactlyInstanceOf(BookstorePersistenceException.class)
				.hasMessage("Could not find author with id: 1");

		verify(authorRepository).findById(1L);
		verifyNoMoreInteractions(authorRepository);
		verifyNoInteractions(bookRepository);
	}

}
