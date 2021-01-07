package piero.aldinucci.apt.bookstore.view.swing;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(GUITestRunner.class)
public class BookstoreSwingFrameIT extends AssertJSwingJUnitTestCase{
	
	private AuthorSwingView authorView;
	private BookSwingView bookView;
	private FrameFixture window;
	private BookstoreSwingFrame frame;
	
	@Override
	protected void onSetUp() throws Exception {
		
		GuiActionRunner.execute(() -> {
			authorView = new AuthorSwingView(null);
			bookView = new BookSwingView(null);
			frame = new BookstoreSwingFrame(authorView, bookView);
			return frame;
		});

		window = new FrameFixture(robot(), frame);
		window.show();
	}
	
	@Test
	@GUITest
	public void test_Components() {		
		window.tabbedPane().selectTab("Books");
		window.list("BookJList").requireItemCount(0);
		window.button(JButtonMatcher.withText("New Book")).requireEnabled();
		window.button(JButtonMatcher.withText("Delete Book")).requireDisabled();
		
		window.tabbedPane().selectTab("Authors");
		window.list("AuthorList").requireItemCount(0);
		window.textBox().enterText("Someone");
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
	}

}
