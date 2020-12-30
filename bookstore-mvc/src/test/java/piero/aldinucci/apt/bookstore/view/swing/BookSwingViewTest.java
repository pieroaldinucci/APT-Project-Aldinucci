package piero.aldinucci.apt.bookstore.view.swing;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.*;

import java.util.HashSet;
import java.util.LinkedHashSet;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JListFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import piero.aldinucci.apt.bookstore.controller.BookstoreController;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

@RunWith(GUITestRunner.class)
public class BookSwingViewTest extends AssertJSwingJUnitTestCase {

	private static final String ERROR_LABEL = "ErrorLabel";
	private static final String NEW_BOOK_BUTTON = "NewBook";
	private static final String DELETE_BOOK_BUTTON = "DeleteBook";
	private static final String BOOK_J_LIST = "BookJList";
	private JPanelFixture bookPanel;
	private JFrame frame;
	private BookSwingView bookView;

	@Mock
	private BookstoreController controller;

	@Override
	protected void onSetUp() throws Exception {
		openMocks(this);

		GuiActionRunner.execute(() -> {
			frame = new JFrame();
			bookView = new BookSwingView();
			bookView.setController(controller);
			frame.add(bookView);
			return bookView;
		});

		bookPanel = new JPanelFixture(robot(), bookView);
		new FrameFixture(robot(), frame).show();
	}

	@Test
	@GUITest
	public void test_components() {
		bookPanel.list(BOOK_J_LIST).requireNoSelection();
		bookPanel.button(NEW_BOOK_BUTTON).requireEnabled();
		bookPanel.button(DELETE_BOOK_BUTTON).requireDisabled();
		bookPanel.label(ERROR_LABEL).requireText("");
	}

	@Test
	@GUITest
	public void test_delete_button_should_be_enabled_only_when_book_is_selected() {
		Book book1 = new Book(3L, "some Book", new HashSet<>());
		GuiActionRunner.execute(() -> bookView.getBookModelList().addElement(book1));

		JListFixture bookList = bookPanel.list(BOOK_J_LIST);
		bookList.selectItem(0);
		bookList.requireSelection(0); // select can fail if the jlist is not visible due to small size
		bookPanel.button(DELETE_BOOK_BUTTON).requireEnabled();
		bookList.clearSelection();
		bookPanel.button(DELETE_BOOK_BUTTON).requireDisabled();
	}

	@Test
	public void test_deleteBook_button_should_delegate_to_controller() {
		DefaultListModel<Book> bookListModel = bookView.getBookModelList();
		Book book1 = new Book(2L, "Test Title", new HashSet<>());
		Book book2 = new Book(5L, "Some book", new HashSet<>());
		GuiActionRunner.execute(() -> {
			bookListModel.addElement(book1);
			bookListModel.addElement(book2);
		});

		bookPanel.list(BOOK_J_LIST).selectItem(1);
		bookPanel.button(DELETE_BOOK_BUTTON).click();

		verify(controller).deleteBook(book2);
		verifyNoMoreInteractions(controller);
	}

	@Test
	public void test_newBook_button_should_delegate_to_controller() {
		bookPanel.button(NEW_BOOK_BUTTON).click();

		verify(controller).composeBook();
		verifyNoMoreInteractions(controller);
	}

	@Test
	@GUITest
	public void test_books_with_authors_should_be_shown_correctly_on_screen() {
		Author author1 = new Author(3L, "first author", new HashSet<Book>());
		Author author2 = new Author(9L, "second author", new HashSet<Book>());
		Book book = new Book(2L, "A weird book", new LinkedHashSet<>());
		book.getAuthors().add(author1);
		book.getAuthors().add(author2);
		author1.getBooks().add(book);
		author2.getBooks().add(book);

		GuiActionRunner.execute(() -> {
			bookView.getBookModelList().addElement(book);
		});

		assertThat(bookPanel.list(BOOK_J_LIST).contents())
				.containsExactly(book.toString() + "; Authors: first author - second author");
	}
	
	@Test
	@GUITest
	public void test_bookAdded_successful_should_also_clear_errors() {
		Author author = new Author(5L, "the author", new HashSet<>());
		Book book = new Book(2L, "A new book", new HashSet<>());
		book.getAuthors().add(author);
		author.getBooks().add(book);
		
		GuiActionRunner.execute(() -> {
			bookView.bookAdded(book);
		});
		
		assertThat(bookView.getBookModelList().toArray()).containsExactly(book);
		bookPanel.label(ERROR_LABEL).requireText(" ");
	}
}
