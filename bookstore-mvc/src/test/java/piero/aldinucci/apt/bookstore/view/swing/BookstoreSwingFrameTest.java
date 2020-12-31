package piero.aldinucci.apt.bookstore.view.swing;

import javax.swing.JPanel;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTabbedPaneFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GUITestRunner.class)
public class BookstoreSwingFrameTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private BookstoreSwingFrame frame;
	private JPanel authorPanel;
	private JPanel bookPanel;

	@Override
	protected void onSetUp() throws Exception {
		GuiActionRunner.execute(() -> {
			authorPanel = new JPanel();
			authorPanel.setName("authorPanel");
			bookPanel = new JPanel();
			bookPanel.setName("bookPanel");
			frame = new BookstoreSwingFrame(authorPanel, bookPanel);
			return frame;
		});

		window = new FrameFixture(robot(), frame);
		window.show();
	}

	@Test
	public void test_Components() {
		JTabbedPaneFixture tabbedPane = window.tabbedPane("MainPane");
		tabbedPane.requireTabTitles("Authors", "Books");

		window.panel("authorPanel").requireVisible();

		tabbedPane.selectTab(1);

		window.panel("bookPanel").requireVisible();
	}
}
