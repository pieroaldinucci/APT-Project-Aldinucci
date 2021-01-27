package piero.aldinucci.apt.bookstore.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.JFrame;

import org.assertj.core.util.Lists;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import piero.aldinucci.apt.bookstore.controller.BookstoreControllerImpl;
import piero.aldinucci.apt.bookstore.exceptions.BookstorePersistenceException;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.service.BookstoreManager;
import piero.aldinucci.apt.bookstore.view.BookView;
import piero.aldinucci.apt.bookstore.view.ComposeBookView;


@RunWith(GUITestRunner.class)
public class AuthorControllerIT extends AssertJSwingJUnitTestCase{
	
	private static final String ERROR_LABEL_AUTHOR = "AuthorErrorLabel";
	private static final String BTN_DELETE_AUTHOR = "DeleteAuthor";
	private static final String FIXTURE_NAME_1 = "name 1";
	private static final String FIXTURE_NAME_2 = "name 2";
	private static final String FIXTURE_NAME_3 = "name 3";

	@Mock
	private BookstoreManager manager;
	
	@Mock
	private BookView bookView;
	
	@Mock
	private ComposeBookView composeBookView;
		
	private FrameFixture window;
	private List<Author> authors;

	private BookstoreControllerImpl controller;
	
	@Override
	protected void onSetUp() throws Exception {
		openMocks(this);
		allAuthorsAndBooksSetup();
		
		JFrame frame = GuiActionRunner.execute(() -> {
			controller = new BookstoreControllerImpl(manager);
			AuthorSwingView authorView = new AuthorSwingView(controller);
			controller.setAuthorView(authorView);
			controller.setBookView(bookView);
			controller.setComposeBookView(composeBookView);
			
			JFrame bookFrame = new JFrame();
			bookFrame.add(authorView);
			return bookFrame;
		});
		
		window = new FrameFixture(robot(), frame);
		window.show();
		
	}
	
	
	private void allAuthorsAndBooksSetup() {
		Author author1 = new Author(1L, FIXTURE_NAME_1, new HashSet<>());
		Author author2 = new Author(2L, FIXTURE_NAME_2, new HashSet<>());
		Author author3 = new Author(3L, FIXTURE_NAME_3, new HashSet<>());

		authors = Lists.list(author1,author2,author3);
		
		when(manager.getAllAuthors()).thenReturn(authors);
		when(manager.getAllBooks()).thenReturn(Lists.emptyList());
	}
	
	@Test
	@GUITest
	public void test_all_authors() {
		GuiActionRunner.execute(() -> controller.allAuthors());
		
		assertThat(window.list().contents()).containsAll(
				authors.stream().map(a -> a.toString()).collect(Collectors.toList()));
	}
	
	@Test
	@GUITest
	public void test_add_new_Author() {
		GuiActionRunner.execute(() -> controller.allAuthors());
		when(manager.newAuthor(isA(Author.class))).thenAnswer(invocation ->
			invocation.getArgument(0, Author.class));
		
		window.textBox().enterText("new Author");
		window.button(JButtonMatcher.withText("Add")).click();
		
		assertThat(window.list().contents()).anyMatch(s -> s.contains("new Author"));
	}

	@Test
	@GUITest
	public void test_delete_author_success() {
		doNothing().when(manager).deleteAuthor(anyLong());
		GuiActionRunner.execute(() -> controller.allAuthors());

		window.list().selectItem(Pattern.compile(".*"+FIXTURE_NAME_1+".*"));
		window.button(BTN_DELETE_AUTHOR).click();
		
		assertThat(window.list().requireItemCount(2).contents())
			.noneMatch(s -> s.contains(FIXTURE_NAME_1));
	}
	
	@Test
	@GUITest
	public void test_delete_author_error() {
		GuiActionRunner.execute(() -> controller.allAuthors());
		doThrow(new BookstorePersistenceException()).when(manager).deleteAuthor(anyLong());

		assertThat(window.label(ERROR_LABEL_AUTHOR).text()).isBlank();
		
		window.list().selectItem(Pattern.compile(".*"+FIXTURE_NAME_2+".*"));
		window.button(BTN_DELETE_AUTHOR).click();
		
		assertThat(window.label(ERROR_LABEL_AUTHOR).text())
			.contains(FIXTURE_NAME_2);
	}
	
}
