package piero.aldinucci.apt.bookstore.view.swing;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.MockitoAnnotations.*;

import java.util.HashSet;

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
import piero.aldinucci.apt.bookstore.model.Book;

@RunWith(GUITestRunner.class)
public class BookSwingViewTest extends AssertJSwingJUnitTestCase{

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
	public void test_components() {
		bookPanel.list(BOOK_J_LIST).requireNoSelection();
		bookPanel.button(NEW_BOOK_BUTTON).requireEnabled();
		bookPanel.button(DELETE_BOOK_BUTTON).requireDisabled();
		bookPanel.label(ERROR_LABEL).requireText("");
	}
	
	@Test
	@GUITest
	public void test_delete_button_should_be_enabled_only_when_book_is_selected() {
		Book book1 = new Book(3L,"some Book",new HashSet<>());
		GuiActionRunner.execute(() -> bookView.getBookModelList().addElement(book1));
		
		JListFixture bookList = bookPanel.list(BOOK_J_LIST);
		bookList.selectItem(0);
		bookList.requireSelection(0); //select can fail if the jlist is not visible due to small size
		bookPanel.button(DELETE_BOOK_BUTTON).requireEnabled();
		bookList.clearSelection();
		bookPanel.button(DELETE_BOOK_BUTTON).requireDisabled();
	}
}
