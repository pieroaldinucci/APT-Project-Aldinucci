package piero.aldinucci.apt.bookstore.view.swing;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;

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

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

@RunWith(GUITestRunner.class)
public class NewBookDialogImplTest extends AssertJSwingJUnitTestCase {

	private static final String BUTTON_REMOVE_AUTHOR = "buttonRemoveAuthor";
	private static final String BUTTON_ADD_AUTHOR = "buttonAddAuthor";
	private static final String TITLE_TEXT_FIELD = "titleTextField";
	private static final String AUTHOR_JLIST = "AvailableAuthors";
	private static final String BOOK_AUTHOR_JLIST = "BookAuthors";

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
		dialogFixture.label(JLabelMatcher.withText("Book's"));
		dialogFixture.label(JLabelMatcher.withText("authors"));
		dialogFixture.list(AUTHOR_JLIST).requireNoSelection();
		assertThat(dialogFixture.list(BOOK_AUTHOR_JLIST).contents()).isEmpty();
		dialogFixture.button(BUTTON_ADD_AUTHOR).requireDisabled();
		dialogFixture.button(BUTTON_REMOVE_AUTHOR).requireDisabled();
	}

	@Test
	@GUITest
	public void test_Dialog_window_cannot_be_closed_from_frame() {
		dialogFixture.close();
		dialogFixture.requireVisible();
	}

	@Test
	@GUITest
	public void test_available_authors_list_correctly_painted_by_model() {
		Author author1 = new Author(2L, "test name", null);
		Author author2 = new Author(3L, "another name", null);

		GuiActionRunner.execute(() -> {
			bookDialog.getModelAvailableAuthors().addElement(author1);
			bookDialog.getModelAvailableAuthors().addElement(author2);
		});

		assertThat(dialogFixture.list(AUTHOR_JLIST).contents()).containsExactly(author1.toString(), author2.toString());
	}

	@Test
	@GUITest
	public void test_book_authors_list_correctly_painted_by_model() {
		Author author1 = new Author(2L, "test name", null);
		Author author2 = new Author(3L, "another name", null);

		GuiActionRunner.execute(() -> {
			bookDialog.getModelBookAuthors().addElement(author1);
			bookDialog.getModelBookAuthors().addElement(author2);
		});

		assertThat(dialogFixture.list(BOOK_AUTHOR_JLIST).contents()).containsExactly(author1.toString(),
				author2.toString());
	}

	@Test
	@GUITest
	public void test_setAuthorList_should_clear_and_update_the_available_authors_list() {
		Author author1 = new Author(2L, "test name", null);
		Author author2 = new Author(3L, "another name", null);

		GuiActionRunner.execute(() -> bookDialog.setAuthorList(Arrays.asList(author1, author2)));

		assertThat(bookDialog.getModelAvailableAuthors().toArray()).containsExactly(author1, author2);

		GuiActionRunner.execute(() -> bookDialog.setAuthorList(Arrays.asList(author2)));

		assertThat(bookDialog.getModelAvailableAuthors().toArray()).containsExactly(author2);
	}
	
	@Test
	public void test_pressing_cancel_button_will_not_return_value_and_Dialog_will_hide_and_clear() {
		GuiActionRunner.execute(() -> {
			bookDialog.getModelAvailableAuthors().addElement(new Author(2L, "database author", new HashSet<>()));
			bookDialog.getModelBookAuthors().addElement(new Author(5L, "book author", new HashSet<>()));
		});
		
		dialogFixture.textBox(TITLE_TEXT_FIELD).enterText("Title");
		dialogFixture.button(JButtonMatcher.withText("Cancel")).click();
		
		dialogFixture.requireNotVisible();
		assertThat(bookDialog.getReturnValue()).isEmpty();
		
		dialogFixture.show();
		
		dialogFixture.textBox(TITLE_TEXT_FIELD).requireEmpty();
		dialogFixture.list(AUTHOR_JLIST).requireItemCount(0);
		dialogFixture.list(BOOK_AUTHOR_JLIST).requireItemCount(0);
	}
}
