package piero.aldinucci.apt.bookstore.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.Arrays;
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
	public void test_showError() {
		Book book = new Book(3L, "Heavy book", new HashSet<Author>());
		
		GuiActionRunner.execute(() -> {
			bookView.showError("A random error occured on", book);
		});
		
		bookPanel.label(ERROR_LABEL).requireText("A random error occured on: "+book);
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
	
	@Test
	@GUITest
	public void test_showAllBooks_should_replace_the_Jlist_content() {
		Book book1 = new Book(2L, "second Book", new HashSet<Author>());
		Book book2 = new Book(5L, "fifth book", new HashSet<Author>());
		
		GuiActionRunner.execute(() -> bookView.showAllBooks(Arrays.asList(book1,book2)));
		
		assertThat(bookView.getBookModelList().toArray()).containsExactly(book1,book2);
		
		Book book3 = new Book(3L,"third book",new HashSet<Author>());
		
		GuiActionRunner.execute(() -> bookView.showAllBooks(Arrays.asList(book3,book2)));			
		
		assertThat(bookView.getBookModelList().toArray()).containsExactly(book3,book2);
	}
	
	@Test
	@GUITest
	public void test_bookRemoved_successful_should_also_clear_error_label() {
		Book book1 = new Book(2L, "second Book", new HashSet<Author>());
		Book book2 = new Book(5L, "fifth book", new HashSet<Author>());
		DefaultListModel<Book> model = bookView.getBookModelList();
		GuiActionRunner.execute(() -> {
			model.addElement(book1);
			model.addElement(book2);
		});
		
		GuiActionRunner.execute(() -> bookView.bookRemoved(book1));
		
		assertThat(model.toArray()).containsExactly(book2);
		bookPanel.label(ERROR_LABEL).requireText(" ");
	}
	
//	@Test
//	public void test_createBookView_should_delegate_to_dialog_and_controller_if_book_was_composed() {
//		Author author1 = new Author(1L, "Mario", null);
//		Author author2 = new Author(3L, "Luigi", null);
//		List<Author> authorList = Arrays.asList(author1, author2); 
//		Book book = new Book(4L, "new book", new HashSet<Author>());
//		when(newBookDialog.getReturnValue()).thenReturn(Optional.of(book));
//		ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
//		
//		bookView.showCreateBook(authorList);
//		
//		verify(newBookDialog).setAuthorList(authorList);
//		verify(newBookDialog).setVisible(true); //Is this correct? This method is not defined in this project 
//		verify(controller).newBook(bookCaptor.capture());
//		assertThat(bookCaptor.getValue()).isSameAs(book);
//		verifyNoMoreInteractions(controller);
//	}
//	
//	@Test
//	public void test_createBookView_should_not_call_controller_if_no_Book_is_Returned() {
//		when(newBookDialog.getReturnValue()).thenReturn(Optional.empty());
//		
//		bookView.showCreateBook(Lists.emptyList());
//		
//		verify(newBookDialog).setAuthorList(Lists.emptyList());
//		verify(newBookDialog).setVisible(true);
//		verifyNoInteractions(controller);
//	}
	
}
