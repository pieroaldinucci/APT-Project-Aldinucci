package piero.aldinucci.apt.bookstore.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import piero.aldinucci.apt.bookstore.model.Author;
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
	void test_allAuthors() {
		List<Author> authors = Arrays.asList(new Author());
		when(manager.getAllAuthors()).thenReturn(authors);

		controller.allAuthors();
		
		verify(authorView).showAllAuthors(authors);
	}

}
