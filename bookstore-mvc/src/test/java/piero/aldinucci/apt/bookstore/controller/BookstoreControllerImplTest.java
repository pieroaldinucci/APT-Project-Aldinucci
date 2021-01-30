package piero.aldinucci.apt.bookstore.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.service.BookstoreManager;
import piero.aldinucci.apt.bookstore.view.AuthorView;
import piero.aldinucci.apt.bookstore.view.BookView;
import piero.aldinucci.apt.bookstore.view.ComposeBookView;

public class BookstoreControllerImplTest {
	
	private static final String FIXTURE_NAME_2 = "Author 2";
	private static final String FIXTURE_NAME_1 = "An Author";
	private static final String FIXTURE_TITLE_1 = "A book";
	private static final String FIXTURE_TITLE_2 = "title 2";

	@Mock
	AuthorView authorView;
	
	@Mock
	BookView bookView;
	
	@Mock
	BookstoreManager manager;
	
	@Mock
	ComposeBookView composeBookView;
	
	public BookstoreControllerImpl controller;
	
	@Before
	public void setUp() {
		openMocks(this);
		controller = new BookstoreControllerImpl(manager);
		controller.setAuthorView(authorView);
		controller.setBookView(bookView);
		controller.setComposeBookView(composeBookView);
	}
	
	@Test
	public void test_allAuthors() {
		List<Author> authors = Arrays.asList(new Author());
		when(manager.getAllAuthors()).thenReturn(authors);

		controller.allAuthors();
		
		verify(authorView).showAllAuthors(authors);
	}
	
	@Test
	public void test_allBooks() {
		List<Book> books= Arrays.asList(new Book(), new Book(1L,FIXTURE_TITLE_1,null));
		when(manager.getAllBooks()).thenReturn(books);

		controller.allBooks();
		
		verify(bookView).showAllBooks(books);
	}
	
	@Test
	public void test_newBook() {
		Book bookToAdd = new Book(null,FIXTURE_TITLE_1,new HashSet<>());
		Book bookAdded = new Book(1L,FIXTURE_TITLE_1,new HashSet<>());
		when(manager.newBook(isA(Book.class))).thenReturn(bookAdded);
		
		controller.newBook(bookToAdd);
		
		InOrder inOrder = inOrder(manager,bookView);
		inOrder.verify(manager).newBook(bookToAdd);
		inOrder.verify(bookView).bookAdded(bookAdded);
		verifyNoMoreInteractions(manager);
	}
	
	@Test
	public void test_newAuthor() {
		Author author = new Author(null, FIXTURE_NAME_1, new HashSet<>());
		Author authorAdded = new Author(2L, FIXTURE_NAME_1, new HashSet<>());
		when(manager.newAuthor(isA(Author.class))).thenReturn(authorAdded);
		
		controller.newAuthor(author);
		
		InOrder inOrder = inOrder(manager,authorView);
		inOrder.verify(manager).newAuthor(author);
		inOrder.verify(authorView).authorAdded(authorAdded);
		verifyNoMoreInteractions(manager);
	}
	
	/*
	 * Every time we delete an Book we have to update the authors as well. 
	 */
	@Test
	public void test_deleteBook_successful() {
		Book book = new Book(1L, FIXTURE_TITLE_1, new HashSet<>());
		List<Author> authors = Lists.emptyList();
		when(manager.getAllAuthors()).thenReturn(authors);
		
		controller.deleteBook(book);
		
		InOrder inOrder = inOrder(manager,bookView, authorView);
		inOrder.verify(manager).deleteBook(1L);
		inOrder.verify(bookView).bookRemoved(book);
		inOrder.verify(manager).getAllAuthors();
		inOrder.verify(authorView).showAllAuthors(authors);
		verifyNoMoreInteractions(manager);
	}
	
	@Test
	public void test_deleteBook_when_book_doesnt_exist() {
		Book book = new Book(1L, FIXTURE_TITLE_1, new HashSet<>());
		doThrow(BookstorePersistenceException.class)
			.when(manager).deleteBook(anyLong());
		List<Book> books = Arrays.asList(new Book(4L, FIXTURE_TITLE_2,new HashSet<>()));
		when(manager.getAllBooks()).thenReturn(books);
		List<Author> authors = Lists.emptyList();
		when(manager.getAllAuthors()).thenReturn(authors);
		
		assertThatCode(() -> controller.deleteBook(book))
			.doesNotThrowAnyException();
		
		InOrder inOrder = inOrder(manager,bookView, authorView);
		inOrder.verify(manager).deleteBook(1L);
		inOrder.verify(bookView).showError("Error while deleting book", book);
		inOrder.verify(manager).getAllBooks();
		inOrder.verify(bookView).showAllBooks(books);
		inOrder.verify(manager).getAllAuthors();
		inOrder.verify(authorView).showAllAuthors(authors);
		verifyNoMoreInteractions(manager);
	}
	
	@Test
	public void test_deleteAuthor() {
		Author author = new Author(3L, FIXTURE_NAME_1, new HashSet<>());
		Book book = new Book(2L,FIXTURE_TITLE_1, new HashSet<>());
		List<Book> books = Lists.list(book);
		when(manager.getAllBooks()).thenReturn(books);
		
		controller.deleteAuthor(author);
		
		InOrder inOrder = inOrder(manager,authorView, bookView);
		inOrder.verify(manager).deleteAuthor(3L);
		inOrder.verify(authorView).authorRemoved(author);
		inOrder.verify(manager).getAllBooks();
		inOrder.verify(bookView).showAllBooks(books);
		verifyNoMoreInteractions(manager);
	}
	
	@Test
	public void test_deleteAuthor_when_author_doesnt_exists() {
		doThrow(BookstorePersistenceException.class)
			.when(manager).deleteAuthor(anyLong());
		List<Author> authors = Arrays.asList(new Author(1L,FIXTURE_NAME_1,new HashSet<>()));
		when(manager.getAllAuthors()).thenReturn(authors);
		List<Book> books = Lists.emptyList();
		when(manager.getAllBooks()).thenReturn(books);
		Author nonExistantAuthor = new Author(3L, FIXTURE_NAME_2, null);
		
		assertThatCode(() -> controller.deleteAuthor(nonExistantAuthor))
			.doesNotThrowAnyException();
		
		InOrder inOrder = inOrder(manager,authorView, bookView);
		inOrder.verify(manager).deleteAuthor(3L);
		inOrder.verify(authorView).showError("Error while deleting author", nonExistantAuthor);
		inOrder.verify(manager).getAllAuthors();
		inOrder.verify(authorView).showAllAuthors(authors);
		inOrder.verify(manager).getAllBooks();
		inOrder.verify(bookView).showAllBooks(books);
		verifyNoMoreInteractions(manager);
	}
	
	@Test
	public void test_ComposeBook() {
		List<Author> authors = Arrays.asList(new Author(3L,FIXTURE_NAME_1,new HashSet<Book>()));
		when(manager.getAllAuthors()).thenReturn(authors);
		
		controller.composeBook();
		
		verify(composeBookView).composeNewBook(authors);
	}
	
	/* I find it simpler and cleaner to just add tests for
	 * getters rather than add specific exclusions and suppressions
	 */
	@Test
	public void test_getAuthorView() {
		assertThat(controller.getAuthorView()).isSameAs(authorView);
	}
	
	@Test
	public void test_getBookView() {
		assertThat(controller.getBookView()).isSameAs(bookView);
	}
	
	@Test
	public void test_getComposeBookView() {
		assertThat(controller.getComposeBookView()).isSameAs(composeBookView);
	}

}
