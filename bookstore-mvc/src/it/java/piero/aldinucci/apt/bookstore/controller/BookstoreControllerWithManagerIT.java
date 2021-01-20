package piero.aldinucci.apt.bookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.HashSet;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;
import piero.aldinucci.apt.bookstore.service.BookstoreManager;
import piero.aldinucci.apt.bookstore.service.BookstoreManagerImpl;
import piero.aldinucci.apt.bookstore.transaction.TransactionCode;
import piero.aldinucci.apt.bookstore.transaction.TransactionManager;
import piero.aldinucci.apt.bookstore.view.AuthorView;
import piero.aldinucci.apt.bookstore.view.BookView;
import piero.aldinucci.apt.bookstore.view.ComposeBookView;

/**
 * I'm on the edge about this test, it seems almost redundant.
 * If I can't find something more meaningful to test with it,
 * this class should be removed.
 *
 */

public class BookstoreControllerWithManagerIT {
	
	@Mock
	TransactionManager transactionManager;
	
	@Mock
	AuthorRepository authorRepository;
	
	@Mock
	BookRepository bookRepository;
	
	@Mock
	AuthorView authorView;
	
	@Mock
	BookView  bookView;
	
	@Mock
	ComposeBookView composeBookView;
	
	BookstoreControllerImpl controller;
	BookstoreManagerImpl serviceManager;
	
	@Before
	public void setUp() {
		openMocks(this);
		serviceManager = new BookstoreManagerImpl(transactionManager);
		
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(BookstoreManager.class).toInstance(serviceManager);
				bind(AuthorView.class).toInstance(authorView);
				bind(BookView.class).toInstance(bookView);
				bind(ComposeBookView.class).toInstance(composeBookView);
			}
		});
		
		when(transactionManager.doInTransaction(any()))
			.thenAnswer(answer((TransactionCode<?> code) -> code.apply(authorRepository, bookRepository)));
		
		controller = injector.getInstance(BookstoreControllerImpl.class);
	}
	
	@Test
	public void test_newAuthor() {
		Author authorToSave = new Author(null,"name 2",new HashSet<>()); 
		Author savedAuthor = new Author(2L, "name 1", new HashSet<>());
		when(authorRepository.save(isA(Author.class))).thenReturn(savedAuthor);
		
		controller.newAuthor(authorToSave);
		
		verify(authorView).authorAdded(savedAuthor);
		verify(authorRepository).save(authorToSave);
		verifyNoInteractions(bookRepository);
	}
	
	@Test
	public void test_newBook() {
		Book bookToSave = new Book(null, "title", new HashSet<>());
		Book savedBook = new Book(1L, "another title", new HashSet<>());
		Author author = new Author(2L, "name 1", new HashSet<>());
		savedBook.getAuthors().add(author);
		when(bookRepository.save(isA(Book.class))).thenReturn(savedBook);
		
		controller.newBook(bookToSave);
		
		verify(bookView).bookAdded(savedBook);
		verify(bookRepository).save(bookToSave);
		verify(authorRepository).update(author);
		assertThat(author.getBooks()).containsExactly(savedBook);
	}
	
	@Test
	public void test_deleteAuthor() {
		Author authorToDelete = new Author(2L,"name 3", new HashSet<>());
		Author authorFound = new Author(2L,"name 1", new HashSet<>());
		Book book = new Book(1L,"title",new HashSet<>());
		book.getAuthors().add(authorToDelete);
		authorFound.getBooks().add(book);
		when(authorRepository.delete(anyLong())).thenReturn(Optional.of(authorFound));
		
		controller.deleteAuthor(authorToDelete);
		
		verify(authorView).authorRemoved(authorToDelete);
		verify(authorRepository).delete(2L);
		verify(bookRepository).update(book);
		assertThat(book.getAuthors()).isEmpty();
	}
	
	@Test
	public void test_deleteBook() {
		Book bookToDelete = new Book(3L,"title 2", new HashSet<>());
		Book  bookFound = new Book(3L,"title 1", new HashSet<>());
		Author author = new Author(5L,"name",new HashSet<>());
		author.getBooks().add(bookToDelete);
		bookFound.getAuthors().add(author);
		when(bookRepository.delete(anyLong())).thenReturn(Optional.of(bookFound));
		
		controller.deleteBook(bookToDelete);
		
		verify(bookView).bookRemoved(bookToDelete);
		verify(bookRepository).delete(3L);
		verify(authorRepository).update(author);
		assertThat(author.getBooks()).isEmpty();
	}

}
