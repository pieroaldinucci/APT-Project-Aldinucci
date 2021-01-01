package piero.aldinucci.apt.bookstore.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.*;

import java.util.HashSet;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.AdditionalAnswers.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import piero.aldinucci.apt.bookstore.model.Author;
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
		Author author = new Author(null, "First Author", new HashSet<>());
		Author returnedAuthor = new Author(3L, "First Author", new HashSet<>()); 
		when(authorRepository.save(isA(Author.class)))
			.thenReturn(returnedAuthor);
		
		assertThat(bookstoreManager.newAuthor(author)).isSameAs(returnedAuthor);
		verify(authorRepository).save(new Author(null,"First Author",new HashSet<>()));
	}

}
