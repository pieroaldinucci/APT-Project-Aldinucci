package piero.aldinucci.apt.bookstore.view.swing;

import static org.assertj.core.api.Assertions.*;

import javax.swing.JFrame;

import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GUITestRunner.class)
public class AuthorSwingViewTest extends AssertJSwingJUnitTestCase{
	
	private static final String DELETE_AUTHOR_BUTTON = "DeleteAuthor";
	private static final String ADD_AUTHOR_BUTTON = "AddAuthor";
	private static final String NAME_TEXT_FIELD = "NameTextField";
	private static final String AUTHOR_LIST = "AuthorList";
	private JPanelFixture authorPanel;
	private AuthorSwingView authorView;
	private JFrame frame;
	

	@Override
	protected void onSetUp() throws Exception {
		GuiActionRunner.execute(() -> {
			frame = new JFrame();
			authorView = new AuthorSwingView();
			frame.add(authorView);
			return frame;
		});
		
		authorPanel = new JPanelFixture(robot(), authorView);
		new FrameFixture(robot(), frame).show();
	}

	@Test
	public void test_Components() {
		authorPanel.label(JLabelMatcher.withText("Name"));
		authorPanel.label("ErrorLabel").requireText(" ");
		authorPanel.textBox(NAME_TEXT_FIELD).requireEnabled().requireEmpty();
		assertThat(authorPanel.list(AUTHOR_LIST).contents()).isEmpty();
		assertThat(authorPanel.button(ADD_AUTHOR_BUTTON).requireDisabled().text()).isEqualTo("Add");
		assertThat(authorPanel.button(DELETE_AUTHOR_BUTTON).requireDisabled().text()).isEqualTo("Delete");
	}
	
	@Test
	public void test_editing_text_field_should_enable_and_disable_add_button() {
		JTextComponentFixture textBox = authorPanel.textBox(NAME_TEXT_FIELD);
		textBox.enterText("Something");
		JButtonFixture button = authorPanel.button(ADD_AUTHOR_BUTTON);
		button.requireEnabled();
		textBox.deleteText();
		button.requireDisabled();
		textBox.enterText(" ");
		button.requireDisabled();
	}
}
