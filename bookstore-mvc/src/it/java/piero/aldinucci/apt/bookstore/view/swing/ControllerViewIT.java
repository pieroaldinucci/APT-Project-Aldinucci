package piero.aldinucci.apt.bookstore.view.swing;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.MockitoAnnotations.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import piero.aldinucci.apt.bookstore.controller.BookstoreControllerImpl;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.service.BookstoreManager;

@RunWith(GUITestRunner.class)
public class ControllerViewIT extends AssertJSwingJUnitTestCase{
	
	@Mock
	private BookstoreManager manager;
	
	private BookstoreControllerImpl controller;
	private ComposeBookSwingView bookComposer;
	private AuthorSwingView authorView;
	private BookSwingView bookView;
	private FrameFixture window;
	private BookstoreSwingFrame frame;
	
	@Override
	protected void onSetUp() throws Exception {
		openMocks(this);
		
		
		GuiActionRunner.execute(() -> {
			authorView = new AuthorSwingView();
			bookView = new BookSwingView();
			bookComposer = new ComposeBookSwingView();
			controller = new BookstoreControllerImpl(authorView, bookView, bookComposer, manager);
			authorView.setController(controller);
			bookView.setController(controller);
			frame = new BookstoreSwingFrame(authorView, bookView);
			return frame;
		});

		window = new FrameFixture(robot(), frame);
		window.show();
		
		
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
		book3.getAuthors().add(author1);
		
		List<Author> authors = Arrays.asList(author1,author2,author3);
		List<Book> books = Arrays.asList(book1,book2,book3);
		when(manager.getAllAuthors()).thenReturn(authors);
		when(manager.getAllBooks()).thenReturn(books);
	}
	
	
	@Test
	@GUITest
	public void test() {
		GuiActionRunner.execute(() -> {
			controller.allAuthors();
			controller.allBooks();
		});
		
		Author author = new Author(5L,"something",new HashSet<>());
		when(manager.newAuthor(isA(Author.class))).thenReturn(author);
		
		window.list().requireItemCount(3);
		window.textBox().enterText("name");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().requireItemCount(4).valueAt(3)).isEqualTo(author.toString());
		
		window.tabbedPane().selectTab("Books");
		window.list().requireItemCount(3);
		
	}
}
