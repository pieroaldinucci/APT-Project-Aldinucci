package piero.aldinucci.apt.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;
import piero.aldinucci.apt.bookstore.transaction.TransactionCode;
import piero.aldinucci.apt.bookstore.transaction.TransactionManager;

public class BookstoreManagerImplTest {


	private static final String FIXTURE_TITLE_1 = "A book";
	private static final String FIXTURE_TITLE_2 = "Another book";
	private static final String FIXTURE_NAME_1 = "First Author";
	private static final String FIXTURE_NAME_2 = "Second author";

	@Mock
	private AuthorRepository authorRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private TransactionManager transactionManager;

	private BookstoreManagerImpl bookstoreManager;

	@Before
	public void setUp() {
		openMocks(this);

		when(transactionManager.doInTransaction(any()))
				.thenAnswer(answer((TransactionCode<?> code) -> code.apply(authorRepository, bookRepository)));

		bookstoreManager = new BookstoreManagerImpl(transactionManager);
	}

	@Test
	public void test_newAuthor_should_generate_return_Author_with_db_id() {
		Author author = new Author(1L, FIXTURE_NAME_1, new HashSet<>());
		Author returnedAuthor = new Author(3L, FIXTURE_NAME_1, new HashSet<>());
		when(authorRepository.save(isA(Author.class))).thenReturn(returnedAuthor);

		Author newAuthor = bookstoreManager.newAuthor(author);
		
		assertThat(newAuthor).isSameAs(returnedAuthor);
		verify(authorRepository).save(new Author(null, FIXTURE_NAME_1, new HashSet<>()));
	}

	@Test
	public void test_newAuthor_with_not_empty_book_Set_should_throw() {
		Author author = new Author(null, FIXTURE_NAME_1, new HashSet<>());
		Book book = new Book(null, FIXTURE_TITLE_1, new HashSet<>());
		author.getBooks().add(book);

		assertThatThrownBy(() -> 
			bookstoreManager.newAuthor(author))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("New authors should have an empty Book Set");
		
		verifyNoInteractions(transactionManager);
	}

	@Test
	public void test_newBook_should_create_new_book_and_set_the_id() {
		Book book = new Book(2L, FIXTURE_TITLE_1, new HashSet<>());
		Book returnedBook = new Book(1L, FIXTURE_TITLE_1, new HashSet<>());
		when(bookRepository.save(isA(Book.class))).thenReturn(returnedBook);

		Book newBook = bookstoreManager.newBook(book);
		
		assertThat(newBook).isSameAs(returnedBook);
		verify(bookRepository).save(new Book(null, FIXTURE_TITLE_1, new HashSet<>()));
	}

	@Test
	public void test_newBook_should_update_all_authors_in_its_Set() {
		Book book = new Book(null, FIXTURE_TITLE_1, null);
		Author author1 = new Author(1L, FIXTURE_NAME_1, new HashSet<>());
		Author author2 = new Author(5L, FIXTURE_NAME_2, new HashSet<>());
		Book returnedBook = new Book(1L, FIXTURE_TITLE_2, new LinkedHashSet<>());
		returnedBook.getAuthors().add(author1);
		returnedBook.getAuthors().add(author2);
		when(bookRepository.save(isA(Book.class))).thenReturn(returnedBook);

		Book newBook = bookstoreManager.newBook(book);
		
		assertThat(newBook).isSameAs(returnedBook);
		InOrder inOrder = inOrder(authorRepository, bookRepository);
		inOrder.verify(bookRepository).save(book);
		inOrder.verify(authorRepository).update(author1);
		inOrder.verify(authorRepository).update(author2);
		assertThat(author1.getBooks()).containsExactly(returnedBook);
		assertThat(author2.getBooks()).containsExactly(returnedBook);
	}

	@Test
	public void test_getAllAuthors() {
		Author author1 = new Author(2L, FIXTURE_NAME_1, new HashSet<>());
		Author author2 = new Author(10L, FIXTURE_NAME_2, new HashSet<>());
		when(authorRepository.findAll()).thenReturn(Arrays.asList(author1, author2));

		List<Author> authors = bookstoreManager.getAllAuthors();

		assertThat(authors).containsExactly(author1, author2);
		verify(authorRepository).findAll();
		verifyNoMoreInteractions(authorRepository);
		verifyNoInteractions(bookRepository);
	}

	@Test
	public void test_getAllBooks() {
		Book book1 = new Book(1L, FIXTURE_TITLE_1, new HashSet<>());
		Book book2 = new Book(5L, FIXTURE_TITLE_2, new HashSet<>());
		when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

		List<Book> books = bookstoreManager.getAllBooks();

		assertThat(books).containsExactly(book1, book2);
		verify(bookRepository).findAll();
		verifyNoMoreInteractions(bookRepository);
		verifyNoInteractions(authorRepository);
	}

	@Test
	public void test_delete_author_when_author_not_present() {
		when(authorRepository.delete(anyLong())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookstoreManager.deleteAuthor(1))				
				.isExactlyInstanceOf(BookstorePersistenceException.class)
				.hasMessage("Could not find author with id: 1");

		verify(authorRepository).delete(1L);
		verifyNoMoreInteractions(authorRepository);
		verifyNoInteractions(bookRepository);
	}
	
	@Test
	public void test_delete_author_when_book_set_not_empty() {
		Author author = new Author(1L, FIXTURE_NAME_1, new LinkedHashSet<>());
		Book book1 = new Book(1L, FIXTURE_TITLE_1, new HashSet<>());
		Book book2 = new Book(3L, FIXTURE_TITLE_2, new HashSet<>());
		author.getBooks().add(book1);
		author.getBooks().add(book2);
		book1.getAuthors().add(author);
		book2.getAuthors().add(author);
		when(authorRepository.delete(anyLong())).thenReturn(Optional.of(author));
		
		bookstoreManager.deleteAuthor(1);
		
		InOrder inOrder = inOrder(authorRepository,bookRepository);
		inOrder.verify(authorRepository).delete(1);
		inOrder.verify(bookRepository).update(book1);
		inOrder.verify(bookRepository).update(book2);
		assertThat(book1.getAuthors()).isEmpty();
		assertThat(book2.getAuthors()).isEmpty();
	}
	
	@Test
	public void test_delete_book_when_its_not_present() {
		when(bookRepository.delete(anyLong())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookstoreManager.deleteBook(2L))				
				.isExactlyInstanceOf(BookstorePersistenceException.class)
				.hasMessage("Could not find book with id: 2");

		verify(bookRepository).delete(2);
		verifyNoMoreInteractions(bookRepository);
		verifyNoInteractions(authorRepository);
	}
	
	@Test
	public void test_delete_book_when_its_set_is_not_empty() {
		Book book = new Book(1L, FIXTURE_TITLE_1, new LinkedHashSet<>());
		Author author1 = new Author(1L, FIXTURE_NAME_1, new HashSet<>());
		Author author2 = new Author(3L, FIXTURE_NAME_2, new HashSet<>());
		book.getAuthors().add(author1);
		book.getAuthors().add(author2);
		author1.getBooks().add(book);
		author2.getBooks().add(book);
		when(bookRepository.delete(anyLong())).thenReturn(Optional.of(book));
		
		bookstoreManager.deleteBook(1);
		
		InOrder inOrder = inOrder(authorRepository,bookRepository);
		inOrder.verify(bookRepository).delete(1);
		inOrder.verify(authorRepository).update(author1);
		inOrder.verify(authorRepository).update(author2);
		assertThat(author1.getBooks()).isEmpty();
		assertThat(author2.getBooks()).isEmpty();
	}
	

}
