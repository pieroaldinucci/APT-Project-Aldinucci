package piero.aldinucci.apt.bookstore.view.swing;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.MockitoAnnotations.*;

import javax.swing.JFrame;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import piero.aldinucci.apt.bookstore.controller.BookstoreController;

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
	public void test() {
		fail("Not yet implemented");
	}
}
