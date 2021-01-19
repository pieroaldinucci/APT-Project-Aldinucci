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
import org.mockito.ArgumentCaptor;
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

		InOrder inOrder = inOrder(authorRepository, bookRepository);
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
		when(authorRepository.delete(anyLong())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookstoreManager.deleteAuthor(1))				
				.isExactlyInstanceOf(IllegalArgumentException.class)
				.hasMessage("Could not find author with id: 1");

		verify(authorRepository).delete(1L);
		verifyNoMoreInteractions(authorRepository);
		verifyNoInteractions(bookRepository);
	}
	
	/**
	 * Question: Is legit to spy on HashSet? It should not be
	 * Then it's possible to check if the sets are being updated inOrder?
	 * 
	 * Using ArgumentCaptor can't be done because we'll get the
	 * final state of the objects, and not the state they were in when
	 * the method is called.
	 */
	@Test
	public void test_delete_author_when_book_set_not_empty() {
		Author author = new Author(1L, "author to delete", new LinkedHashSet<>());
		Book book1 = new Book(1L, "test book", new HashSet<>());
		Book book2 = new Book(3L, "third test book", new HashSet<>());
		author.getBooks().add(book1);
		author.getBooks().add(book2);
		book1.getAuthors().add(author);
		book2.getAuthors().add(author);
		when(authorRepository.delete(anyLong())).thenReturn(Optional.of(author));
//		when(authorRepository.delete(anyLong())).thenReturn(author);
		
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
				.isExactlyInstanceOf(IllegalArgumentException.class)
				.hasMessage("Could not find book with id: 2");

		verify(bookRepository).delete(2);
		verifyNoMoreInteractions(bookRepository);
		verifyNoInteractions(authorRepository);
	}
	
	@Test
	public void test_delete_book_when_its_set_is_not_empty() {
		Book book = new Book(1L, "book to delete", new LinkedHashSet<>());
		Author author1 = new Author(1L, "some guy", new HashSet<>());
		Author author2 = new Author(3L, "tizio", new HashSet<>());
		book.getAuthors().add(author1);
		book.getAuthors().add(author2);
		author1.getBooks().add(book);
		author2.getBooks().add(book);
		when(bookRepository.delete(anyLong())).thenReturn(Optional.of(book));
//		when(bookRepository.delete(anyLong())).thenReturn(book);
		
		bookstoreManager.deleteBook(1);
		
		InOrder inOrder = inOrder(authorRepository,bookRepository);
		inOrder.verify(bookRepository).delete(1);
		inOrder.verify(authorRepository).update(author1);
		inOrder.verify(authorRepository).update(author2);
		assertThat(author1.getBooks()).isEmpty();
		assertThat(author2.getBooks()).isEmpty();
	}
	
	@Test
	public void test_update_author_should_throw_if_not_present() {
		when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());
		Author author = new Author(1L, "not existant", null);
		
		assertThatThrownBy(() -> bookstoreManager.update(author))
			.isExactlyInstanceOf(BookstorePersistenceException.class)
			.hasMessage("Cannot find author to update with id: 1");
		
		verify(authorRepository).findById(1L);
		verifyNoMoreInteractions(authorRepository);
		verifyNoInteractions(bookRepository);
	}
	
	/**
	 * This is needed because the equals method only consider the id.
	 */
	@Test
	public void test_update_author_should_update_the_correct_object_considering_all_fields() {
		Author oldAuthor = new Author(3L, "old name", new HashSet<> ());
		Author updatedAuthor = new Author(3L, "Updated name", new HashSet<>());
		when(authorRepository.findById(anyLong())).thenReturn(Optional.of(oldAuthor));
		ArgumentCaptor<Author> capturedAuthor = ArgumentCaptor.forClass(Author.class);
		
		bookstoreManager.update(updatedAuthor);
		
		verify(authorRepository).update(capturedAuthor.capture());
		assertThat(capturedAuthor.getAllValues().get(0)).usingRecursiveComparison()
			.isEqualTo(updatedAuthor);
	}
	
	/**
	 * Again, is possible to check if Sets are updated before the repository itself is
	 * updated? (we should not assume that we're using a persistency unit)
	 */
	@Test
	public void test_update_author_should_cascade_to_its_books() {
		Author authorToUpdate = new Author(3L, "author to update",new LinkedHashSet<>());
		Author updatedAuthor = new Author(3L, "Updated name", new LinkedHashSet<>());
		Book book1 = new Book(3L, "test book 1", new HashSet<>(Arrays.asList(authorToUpdate)));
		Book book2 = new Book(5L, "test book 2", new HashSet<>(Arrays.asList(authorToUpdate)));
		Book book3 = new Book(11L, "test book 3", new HashSet<>());
		authorToUpdate.getBooks().add(book1);
		authorToUpdate.getBooks().add(book2);
		updatedAuthor.getBooks().add(book2);
		updatedAuthor.getBooks().add(book3);
		when(authorRepository.findById(anyLong())).thenReturn(Optional.of(authorToUpdate));
		
		bookstoreManager.update(updatedAuthor);
		
		assertThat(book1.getAuthors()).isEmpty();
		assertThat(book2.getAuthors().size()).isEqualTo(1);
		assertThat(book2.getAuthors().iterator().next())
			.usingRecursiveComparison().isEqualTo(updatedAuthor);
		assertThat(book3.getAuthors().size()).isEqualTo(1);
		assertThat(book3.getAuthors().iterator().next())
			.usingRecursiveComparison().isEqualTo(updatedAuthor);		
		InOrder inOrder = inOrder(authorRepository,bookRepository);
		inOrder.verify(authorRepository).findById(3L);
		inOrder.verify(authorRepository).update(updatedAuthor);
		inOrder.verify(bookRepository).update(book1);
		inOrder.verify(bookRepository).update(book2);
		inOrder.verify(bookRepository).update(book3);
		verifyNoMoreInteractions(authorRepository);
		verifyNoMoreInteractions(bookRepository);
	}
	
	
	@Test
	public void test_update_book_should_throw_if_not_present() {
		when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
		Book book = new Book(3L, "not existant", null);
		
		assertThatThrownBy(() -> bookstoreManager.update(book))
			.isExactlyInstanceOf(BookstorePersistenceException.class)
			.hasMessage("Cannot find book to update with id: 3");
		
		verify(bookRepository).findById(3L);
		verifyNoMoreInteractions(bookRepository);
		verifyNoInteractions(authorRepository);
	}
	
	/**
	 * This is needed because the equals method only consider the id.
	 */
	@Test
	public void test_update_book_should_update_the_correct_object_considering_all_fields() {
		Book oldBook = new Book(3L, "old title", new HashSet<> ());
		Book updatedBook = new Book(3L, "Updated title", new HashSet<>());
		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(oldBook));
		ArgumentCaptor<Book> capturedBook = ArgumentCaptor.forClass(Book.class);
		
		bookstoreManager.update(updatedBook);
		
		verify(bookRepository).update(capturedBook.capture());
		assertThat(capturedBook.getAllValues().get(0)).usingRecursiveComparison()
			.isEqualTo(updatedBook);
	}
	
	@Test
	public void test_update_book_should_cascade_to_its_authors() {
		Book bookToUpdate = new Book(3L, "book to update",new LinkedHashSet<>());
		Book updatedBook = new Book(3L, "Updated title", new LinkedHashSet<>());
		Author author1 = new Author(3L, "test author 1", new HashSet<>(Arrays.asList(bookToUpdate)));
		Author author2 = new Author(5L, "test author 2", new HashSet<>(Arrays.asList(bookToUpdate)));
		Author author3 = new Author(11L, "test author 3", new HashSet<>());
		bookToUpdate.getAuthors().add(author1);
		bookToUpdate.getAuthors().add(author2);
		updatedBook.getAuthors().add(author2);
		updatedBook.getAuthors().add(author3);
		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookToUpdate));
		
		bookstoreManager.update(updatedBook);
		
		assertThat(author1.getBooks()).isEmpty();
		assertThat(author2.getBooks().size()).isEqualTo(1);
		assertThat(author2.getBooks().iterator().next())
			.usingRecursiveComparison().isEqualTo(updatedBook);
		assertThat(author3.getBooks().size()).isEqualTo(1);
		assertThat(author3.getBooks().iterator().next())
			.usingRecursiveComparison().isEqualTo(updatedBook);		
		InOrder inOrder = inOrder(authorRepository,bookRepository);
		inOrder.verify(bookRepository).findById(3L);
		inOrder.verify(bookRepository).update(updatedBook);
		inOrder.verify(authorRepository).update(author1);
		inOrder.verify(authorRepository).update(author2);
		inOrder.verify(authorRepository).update(author3);
		verifyNoMoreInteractions(bookRepository);
		verifyNoMoreInteractions(authorRepository);
	}

}
