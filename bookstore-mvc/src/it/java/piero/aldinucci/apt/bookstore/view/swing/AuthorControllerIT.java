package piero.aldinucci.apt.bookstore.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFrame;

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


@RunWith(GUITestRunner.class)
public class AuthorControllerIT extends AssertJSwingJUnitTestCase{
	
	@Mock
	private BookstoreManager manager;
		
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
			
			JFrame bookFrame = new JFrame();
			bookFrame.add(authorView);
			return bookFrame;
		});
		
		window = new FrameFixture(robot(), frame);
		window.show();
		
	}
	
	private void allAuthorsAndBooksSetup() {
		Author author1 = new Author(1L, "Arthur", new HashSet<>());
		Author author2 = new Author(2L, "Isaac", new HashSet<>());
		Author author3 = new Author(3L, "Newton", new HashSet<>());

		authors = Arrays.asList(author1,author2,author3);
		
		when(manager.getAllAuthors()).thenReturn(authors);
	}
	
	@Test
	@GUITest
	public void test_add_new_Author() {
		GuiActionRunner.execute(() -> controller.allAuthors());
		when(manager.newAuthor(isA(Author.class))).thenAnswer(invocation -> {
			Author author = invocation.getArgument(0, Author.class);
			author.setId(4L);
			return author;
		});
		
		window.textBox().enterText("Test a1");
		window.button(JButtonMatcher.withText("Add")).click();
		
		Author author = new Author(4L, "Test a1", new HashSet<>());
		assertThat(window.list().item(3).value()).isEqualTo(author.toString());
	}

	@Test
	@GUITest
	public void test_delete_author_success() {
		GuiActionRunner.execute(() -> controller.allAuthors());

		window.list().selectItem(0);
		window.button("DeleteAuthor").click();
		
		assertThat(window.list().valueAt(0)).isEqualTo(authors.get(1).toString());
		assertThat(window.list().valueAt(1)).isEqualTo(authors.get(2).toString());
	}
	
	@Test
	@GUITest
	public void test_delete_author_error() {
		GuiActionRunner.execute(() -> controller.allAuthors());
		doThrow(new BookstorePersistenceException()).when(manager).delete(isA(Author.class));
		assertThat(window.label("AuthorErrorLabel").text()).isBlank();
		
		window.list().selectItem(0);
		window.button("DeleteAuthor").click();
		
		window.list().requireItemCount(3);
		assertThat(window.label("AuthorErrorLabel").text()).isNotBlank();
	}
	
}
