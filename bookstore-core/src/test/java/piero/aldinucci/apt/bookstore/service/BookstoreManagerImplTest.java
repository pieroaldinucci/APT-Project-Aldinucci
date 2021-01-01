package piero.aldinucci.apt.bookstore.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.AdditionalAnswers.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

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
	public void test() {
		fail("Not yet implemented");
	}

}
