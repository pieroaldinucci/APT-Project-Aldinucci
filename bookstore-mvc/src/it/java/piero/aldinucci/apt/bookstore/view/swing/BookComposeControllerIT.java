package piero.aldinucci.apt.bookstore.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

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
import piero.aldinucci.apt.bookstore.view.AuthorView;

@RunWith(GUITestRunner.class)
public class BookComposeControllerIT extends AssertJSwingJUnitTestCase {

	private static final String ERROR_LABEL_BOOK = "BookErrorLabel";
	private static final String BTN_DELETE_BOOK = "DeleteBook";
	private static final String FIXTURE_TITLE_1 = "Title 1";
	private static final String FIXTURE_TITLE_2 = "Title 2";
	private static final String FIXTURE_TITLE_NEW = "New Title";
	private static final String FIXTURE_NAME_1 = "Name 1";
	private static final String FIXTURE_NAME_2 = "Name 2";

	@Mock
	private BookstoreManager manager;

	@Mock
	private AuthorView authorView;

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
			controller.setAuthorView(authorView);

			dialog = new DialogFixture(robot(), composeBookView);
			JFrame bookFrame = new JFrame();
			bookFrame.add(bookView);
			return bookFrame;
		});

		window = new FrameFixture(robot(), frame);
		window.show();

	}

	private void allAuthorsAndBooksSetup() {
		Author author1 = new Author(1L, FIXTURE_NAME_1, new HashSet<>());
		Author author2 = new Author(2L, FIXTURE_NAME_2, new HashSet<>());
		Book book1 = new Book(11L, FIXTURE_TITLE_1, new HashSet<>());
		Book book2 = new Book(12L, FIXTURE_TITLE_2, new HashSet<>());

		author1.getBooks().add(book1);
		author2.getBooks().add(book2);
		book1.getAuthors().add(author1);
		book2.getAuthors().add(author2);

		authors = Arrays.asList(author1, author2);
		books = Arrays.asList(book1, book2);

		when(manager.getAllAuthors()).thenReturn(authors);
		when(manager.getAllBooks()).thenReturn(books);
	}

	@Test
	@GUITest
	public void test_Book_list_contains_all_entries() {
		GuiActionRunner.execute(() -> controller.allBooks());
		
		assertThat(window.list().contents())
			.anySatisfy(e ->  assertThat(e).contains(FIXTURE_TITLE_1,FIXTURE_NAME_1))
			.anySatisfy(e ->  assertThat(e).contains(FIXTURE_TITLE_2,FIXTURE_NAME_2));
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
		window.list().requireItemCount(2);
		verify(manager, times(0)).newBook(isA(Book.class));
	}

	@Test
	@GUITest
	public void test_compose_new_Book() {
		GuiActionRunner.execute(() -> controller.allBooks());
		when(manager.newBook(isA(Book.class))).thenAnswer(invocation -> 
			invocation.getArgument(0, Book.class));

		window.button(JButtonMatcher.withText("New")).click();
		dialog.textBox("titleTextField").enterText(FIXTURE_TITLE_NEW);
		dialog.list("AvailableAuthors")
			.selectItem(Pattern.compile(".*"+FIXTURE_NAME_2+".*"));
		dialog.button(JButtonMatcher.withText("<")).click();
		dialog.button(JButtonMatcher.withText("OK")).click();

		assertThat(window.list().contents()).anySatisfy(b -> 
			assertThat(b).contains(FIXTURE_TITLE_NEW,FIXTURE_NAME_2));
	}

	@Test
	@GUITest
	public void test_delete_book_success() {
		doNothing().when(manager).deleteBook(anyLong());
		GuiActionRunner.execute(() -> controller.allBooks());

		window.list().selectItem(Pattern.compile(".*"+FIXTURE_TITLE_2+".*"));
		window.button(BTN_DELETE_BOOK).click();

		assertThat(window.list().contents())
			.noneMatch(s -> s.contains(FIXTURE_TITLE_2));
	}

	@Test
	@GUITest
	public void test_delete_book_error() {
		GuiActionRunner.execute(() -> controller.allBooks());
		doThrow(new BookstorePersistenceException()).when(manager).deleteBook(anyLong());
		
		assertThat(window.label(ERROR_LABEL_BOOK).text()).isBlank();

		window.list().selectItem(Pattern.compile(".*"+FIXTURE_TITLE_1+".*"));
		window.button(BTN_DELETE_BOOK).click();

		assertThat(window.label(ERROR_LABEL_BOOK).text()).contains(FIXTURE_TITLE_1);
	}

}
