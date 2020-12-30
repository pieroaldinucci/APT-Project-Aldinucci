package piero.aldinucci.apt.bookstore.view.swing;

import static org.assertj.core.api.Assertions.*;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GUITestRunner.class)
public class NewBookDialogImplTest extends AssertJSwingJUnitTestCase{
	private static final String BUTTON_REMOVE_AUTHOR = "buttonRemoveAuthor";
	private static final String BUTTON_ADD_AUTHOR = "buttonAddAuthor";
	private static final String TITLE_TEXT_FIELD = "titleTextField";
	private static final String AUTHOR_JLIST = "BookAuthors";
	private static final String BOOK_AUTHOR_JLIST = "AvailableAuthors";
	private NewBookDialogImpl bookDialog;
	private DialogFixture dialogFixture;

	@Override
	protected void onSetUp() throws Exception {
		GuiActionRunner.execute(() -> bookDialog = new NewBookDialogImpl());
		
		dialogFixture = new DialogFixture(robot(), bookDialog);
		dialogFixture.show();		
	}
	
	@After
	public void cleanUP() {
		dialogFixture.cleanUp();
	}

	@Test
	@GUITest
	public void test_components() {
		dialogFixture.requireModal();
		dialogFixture.button(JButtonMatcher.withText("OK")).requireDisabled();
		dialogFixture.button(JButtonMatcher.withText("Cancel"));
		dialogFixture.textBox(TITLE_TEXT_FIELD).requireEmpty();
		dialogFixture.label(JLabelMatcher.withText("Title"));
		dialogFixture.label(JLabelMatcher.withText("Book"));
		dialogFixture.label(JLabelMatcher.withText("authors"));
		dialogFixture.list(AUTHOR_JLIST).requireNoSelection();
		assertThat(dialogFixture.list(BOOK_AUTHOR_JLIST).contents()).isEmpty();
		dialogFixture.button(BUTTON_ADD_AUTHOR).requireDisabled();
		dialogFixture.button(BUTTON_REMOVE_AUTHOR).requireDisabled();
	}
}
