package piero.aldinucci.apt.bookstore.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import piero.aldinucci.apt.bookstore.controller.BookstoreControllerImpl;
import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.service.BookstoreManager;


@RunWith(GUITestRunner.class)
public class BookComposeControllerIT extends AssertJSwingJUnitTestCase{
	
	@Mock
	private BookstoreManager manager;
		
	private FrameFixture window;
	private DialogFixture dialog;
	private List<Author> authors;
	private List<Book> books;

	private BookstoreControllerImpl controller;
	
	@Override
	protected void onSetUp() throws Exception {
		openMocks(this);
		allAuthorsAndBooksSetup();
		
		JFrame frame = GuiActionRunner.execute(() -> {
			controller = new BookstoreControllerImpl(manager);
			BookSwingView bookView = new BookSwingView(controller);
			ComposeBookSwingView composeBookView = new ComposeBookSwingView(controller);
			controller.setBookView(bookView);
			controller.setComposeBookView(composeBookView);
			
			dialog = new DialogFixture(robot(),composeBookView);
			JFrame bookFrame = new JFrame();
			bookFrame.add(bookView);
			return bookFrame;
		});
		
		window = new FrameFixture(robot(), frame);
		window.show();
		
	}
	
	private void allAuthorsAndBooksSetup() {
		Author author1 = new Author(1L, "Arthur", new HashSet<>());
		Author author2 = new Author(2L, "Isaac", new HashSet<>());
		Author author3 = new Author(3L, "Newton", new HashSet<>());
		Book book1 = new Book(11L, "A book", new HashSet<>());
		Book book2 = new Book(12L, "Manual", new HashSet<>());
		Book book3 = new Book(13L, "Novel", new HashSet<>());
		
		author1.getBooks().add(book3);
		author2.getBooks().add(book2);
		author2.getBooks().add(book3);
		author3.getBooks().add(book1);
		author3.getBooks().add(book2);
		book1.getAuthors().add(author3);
		book2.getAuthors().add(author2);
		book2.getAuthors().add(author3);
		book3.getAuthors().add(author1);
		book3.getAuthors().add(author2);
		
		authors = Arrays.asList(author1,author2,author3);
		books = Arrays.asList(book1,book2,book3);
		
		when(manager.getAllAuthors()).thenReturn(authors);
		when(manager.getAllBooks()).thenReturn(books);
	}
	
	@Test
	@GUITest
	public void test_compose_Book_dialog_is_opened_and_closed_correctly() {
		GuiActionRunner.execute(() -> controller.allBooks());
		
		dialog.requireNotVisible();
		
		window.button("NewBook").click();
		
		dialog.requireVisible();
		dialog.button(JButtonMatcher.withText("Cancel")).click();
		
		dialog.requireNotVisible();
		window.list("BookJList").requireItemCount(3);
		verify(manager,times(0)).newBook(isA(Book.class));
	}

	@Test
	@GUITest
	public void test_compose_new_Book() {
		GuiActionRunner.execute(() -> controller.allBooks());
		when(manager.newBook(isA(Book.class))).thenAnswer(invocation -> {
			Book book = invocation.getArgument(0, Book.class);
			book.setId(14L);
			return book;
		});

		window.button(JButtonMatcher.withText("New")).click();

		dialog.textBox("titleTextField").enterText("Novel");
		dialog.list("AvailableAuthors").selectItem(1);
		dialog.button(JButtonMatcher.withText("<")).click();
		dialog.button(JButtonMatcher.withText("OK")).click();

		Book composedBook = new Book(14L, "Novel", new HashSet<>());
		assertThat(window.list().item(3).value()).isEqualTo(composedBook.toString() + "; Authors: Isaac");
	}
	
	@Test
	@GUITest
	public void test_delete_book_success() {
		GuiActionRunner.execute(() -> controller.allBooks());
		
		window.list().selectItem(1);
		window.button("DeleteBook").click();
		
		assertThat(window.list().valueAt(0)).isEqualTo(books.get(0).toString()+
				"; Authors: Newton");
		assertThat(window.list().valueAt(1)).isEqualTo(books.get(2).toString()+
				"; Authors: Arthur - Isaac");
	}

	@Test
	@GUITest
	public void test_delete_book_error() {
		GuiActionRunner.execute(() -> controller.allBooks());
		doThrow(new BookstorePersistenceException()).when(manager).deleteBook(anyLong());
		window.list().selectItem(0);
		assertThat(window.label("BookErrorLabel").text()).isBlank();
		
		window.button("DeleteBook").click();
		
		window.list().requireItemCount(3);
		assertThat(window.label("BookErrorLabel").text()).isNotBlank();
	}
	
}
