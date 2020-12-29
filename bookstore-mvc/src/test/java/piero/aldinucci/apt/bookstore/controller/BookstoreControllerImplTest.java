package piero.aldinucci.apt.bookstore.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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

public class BookstoreControllerImplTest {
	
	@Mock
	AuthorView authorView;
	
	@Mock
	BookView bookView;
	
	@Mock
	BookstoreManager manager;
	
	public BookstoreControllerImpl controller;
	
	@Before
	public void setUp() {
		openMocks(this);
		controller = new BookstoreControllerImpl(authorView, bookView, manager); 
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
		List<Book> books= Arrays.asList(new Book(), new Book(1L,"title",null));
		when(manager.getAllBooks()).thenReturn(books);

		controller.allBooks();
		
		verify(bookView).showAllBooks(books);
	}
	
	@Test
	public void test_newAuthor() {
		Author author = new Author(null, "An Author", new HashSet<>());
		Author authorAdded = new Author(2L, "An Author", new HashSet<>());
		when(manager.newAuthor(isA(Author.class))).thenReturn(authorAdded);
		
		controller.newAuthor(author);
		
		verify(manager).newAuthor(author);
		verify(authorView).authorAdded(authorAdded);
	}
	
	@Test
	public void test_newBook() {
		Book bookToAdd = new Book(null,"A book",new HashSet<>());
		Book bookAdded = new Book(1L,"A book",new HashSet<>());
		when(manager.newBook(isA(Book.class))).thenReturn(bookAdded);
		
		controller.newBook(bookToAdd);
		
		verify(manager).newBook(bookToAdd);
		verify(bookView).bookAdded(bookAdded);
	}
	
	@Test
	public void test_deleteBook_successful() {
		Book book = new Book(1L, "test book", new HashSet<>());
		
		controller.deleteBook(book);
		
		InOrder inOrder = inOrder(manager,bookView);
		inOrder.verify(manager).delete(book);
		inOrder.verify(bookView).bookRemoved(book);
	}
	
	@Test
	public void test_deleteBook_when_book_doesnt_exist() {
		Book book = new Book(1L, "test book", new HashSet<>());
		doThrow(BookstorePersistenceException.class)
			.when(manager).delete(isA(Book.class));
		
		assertThatCode(() -> controller.deleteBook(book))
			.doesNotThrowAnyException();
		
		verify(manager).delete(book);
		verify(bookView).showError("Error while deleting book", book);
		verifyNoMoreInteractions(manager);
	}
	
	@Test
	public void test_deleteAuthor() {
		Author author = new Author(3L, "test author", new HashSet<>());
		
		controller.deleteAuthor(author);
		
		InOrder inOrder = inOrder(manager,authorView);
		inOrder.verify(manager).delete(author);
		inOrder.verify(authorView).authorRemoved(author);
	}
	
	@Test
	public void test_deleteAuthor_when_author_doesnt_exists() {
		doThrow(BookstorePersistenceException.class)
			.when(manager).delete(isA(Author.class));
		Author author = new Author(3L, "not existant", null);
		
		assertThatCode(() -> controller.deleteAuthor(author))
			.doesNotThrowAnyException();
		
		InOrder inOrder = inOrder(manager,authorView);
		inOrder.verify(manager).delete(author);
		inOrder.verify(authorView).showError("Error while deleting author", author);
		verifyNoMoreInteractions(manager);
	}
	
	@Test
	public void test_composeBook() {
		List<Author> authors = Arrays.asList(new Author(3L,"test name",new HashSet<Book>()));
		when(manager.getAllAuthors()).thenReturn(authors);
		
		controller.composeBook();
		
		verify(bookView).showCreateBook(authors);
	}

}
