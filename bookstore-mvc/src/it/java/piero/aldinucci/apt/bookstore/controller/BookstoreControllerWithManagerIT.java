package piero.aldinucci.apt.bookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;

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
import piero.aldinucci.apt.bookstore.view.factory.ViewsFactory;

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
	public void test_newBook() {
		Book book = new Book(1L, "title", new HashSet<>());
		Author author = new Author(2L, "name 1", new HashSet<>());
		book.getAuthors().add(author);
		when(bookRepository.save(isA(Book.class))).thenReturn(book);
		
		controller.newBook(book);
		
		verify(bookRepository).save(book);
		verify(authorRepository).update(author);
		verifyNoMoreInteractions(bookRepository);
		verifyNoMoreInteractions(authorRepository);
		assertThat(author.getBooks()).containsExactly(book);
	}

}
