package piero.aldinucci.apt.bookstore.view.swing;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import javax.swing.JDialog;

import org.assertj.core.util.Lists;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JListFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

@RunWith(GUITestRunner.class)
public class ComposeBookSwingViewTest extends AssertJSwingJUnitTestCase {

	private static final String BUTTON_REMOVE_AUTHOR = "buttonRemoveAuthor";
	private static final String BUTTON_ADD_AUTHOR = "buttonAddAuthor";
	private static final String TITLE_TEXT_FIELD = "titleTextField";
	private static final String AUTHOR_JLIST = "AvailableAuthors";
	private static final String BOOK_AUTHOR_JLIST = "BookAuthors";

	private ComposeBookSwingView composeBookView;
	private DialogFixture dialogFixture;

	@Override
	protected void onSetUp() throws Exception {
		GuiActionRunner.execute(() -> composeBookView = new ComposeBookSwingView());

		dialogFixture = new DialogFixture(robot(), composeBookView);
		dialogFixture.show();
	}

	@After
	public void cleanUP() {
		dialogFixture.cleanUp();
	}

	@Test
	@GUITest
	public void test_swing_components() {
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
	public void test_variables_initial_state() {
		assertThat(composeBookView.getBook()).isNull();
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
			composeBookView.getModelAvailableAuthors().addElement(author1);
			composeBookView.getModelAvailableAuthors().addElement(author2);
		});

		assertThat(dialogFixture.list(AUTHOR_JLIST).contents()).containsExactly(author1.toString(), author2.toString());
	}

	@Test
	@GUITest
	public void test_book_authors_list_correctly_painted_by_model() {
		Author author1 = new Author(2L, "test name", null);
		Author author2 = new Author(3L, "another name", null);

		GuiActionRunner.execute(() -> {
			composeBookView.getModelBookAuthors().addElement(author1);
			composeBookView.getModelBookAuthors().addElement(author2);
		});

		assertThat(dialogFixture.list(BOOK_AUTHOR_JLIST).contents()).containsExactly(author1.toString(),
				author2.toString());
	}

	@Test
	@GUITest
	public void test_showAuthorList_should_make_the_dialog_visible() {
		composeBookView.setModal(false);
		composeBookView.setVisible(false);
		dialogFixture.requireNotVisible();
		
		GuiActionRunner.execute(() -> composeBookView.showAuthorList(Lists.emptyList()));
		
		dialogFixture.requireVisible();
	}
	
	@Test
	@GUITest
	public void test_showAuthorList_should_clear_and_update_the_available_authors_list() {
		assertThat(composeBookView.getBook()).isNull();
		
		GuiActionRunner.execute(() -> {
			composeBookView.getModelBookAuthors().addElement(new Author(5L,"Isaac",new HashSet<>()));
			composeBookView.getModelBookAuthors().addElement(new Author(3L,"Clarke",new HashSet<>()));
		});
		Author author1 = new Author(2L, "test name", null);
		Author author2 = new Author(3L, "another name", null);

		GuiActionRunner.execute(() -> composeBookView.showAuthorList(Arrays.asList(author1, author2)));

		assertThat(composeBookView.getModelBookAuthors().toArray()).isEmpty();
		assertThat(composeBookView.getModelAvailableAuthors().toArray()).containsExactly(author1, author2);
	}
	
	@Test
	@GUITest
	public void test_writing_title_should_enable_or_disable_OK_button() {
		dialogFixture.textBox(TITLE_TEXT_FIELD).enterText("A title");
		dialogFixture.button(JButtonMatcher.withText("OK")).requireEnabled();
		
		dialogFixture.textBox(TITLE_TEXT_FIELD).deleteText();
		dialogFixture.button(JButtonMatcher.withText("OK")).requireDisabled();
		
		dialogFixture.textBox(TITLE_TEXT_FIELD).enterText("   ");
		dialogFixture.button(JButtonMatcher.withText("OK")).requireDisabled();
	}

	@Test
	public void test_pressing_cancel_button_will_return_empty_optional_and_hide_Dialog() {
		GuiActionRunner.execute(() -> {
			composeBookView.getModelAvailableAuthors().addElement(new Author(2L, "database author", new HashSet<>()));
			composeBookView.getModelBookAuthors().addElement(new Author(5L, "book author", new HashSet<>()));
		});
		
		dialogFixture.textBox(TITLE_TEXT_FIELD).enterText("Title");
		dialogFixture.button(JButtonMatcher.withText("Cancel")).click();

		dialogFixture.requireNotVisible();
		assertThat(composeBookView.getBook()).isEmpty();
	}

	@Test
	@GUITest
	public void test_buttonAddAuthor_should_be_disabled_when_no_Existing_author_is_selected() {
		GuiActionRunner.execute(() -> composeBookView.getModelAvailableAuthors()
				.addElement(new Author(2L, "author name", new HashSet<Book>())));
		JListFixture list = dialogFixture.list(AUTHOR_JLIST);
		JButtonFixture button = dialogFixture.button(BUTTON_ADD_AUTHOR);
		list.selectItem(0);

		button.requireEnabled();

		list.clearSelection();

		button.requireDisabled();
	}

	@Test
	@GUITest
	public void test_buttonRemoveAuthor_should_be_disabled_when_no_Book_author_is_selected() {
		GuiActionRunner.execute(() -> {
			composeBookView.getModelBookAuthors().addElement(new Author(2L, "author name", new HashSet<Book>()));
		});
		JListFixture list = dialogFixture.list(BOOK_AUTHOR_JLIST);
		JButtonFixture button = dialogFixture.button(BUTTON_REMOVE_AUTHOR);
		list.selectItem(0);

		button.requireEnabled();

		list.clearSelection();

		button.requireDisabled();
	}

	@Test
	@GUITest
	public void test_buttonAddAuthor_should_move_selected_author_from_right_to_left() {
		Author author = new Author(2L, "author name", new HashSet<Book>());
		Author author2 = new Author(3L, "another name", new HashSet<Book>());
		Author author3 = new Author(8L, "test name", new HashSet<Book>());
		GuiActionRunner.execute(() -> {
			composeBookView.getModelBookAuthors().addElement(author);
			composeBookView.getModelAvailableAuthors().addElement(author3);
			composeBookView.getModelAvailableAuthors().addElement(author2);
		});

		dialogFixture.list(AUTHOR_JLIST).selectItem(1);
		dialogFixture.button(BUTTON_ADD_AUTHOR).click();

		assertThat(composeBookView.getModelBookAuthors().toArray()).containsExactlyInAnyOrder(author, author2);
		assertThat(composeBookView.getModelAvailableAuthors().toArray()).containsExactly(author3);
	}

	@Test
	@GUITest
	public void test_buttonRemoveAuthor_should_move_selected_author_from_left_to_right() {
		Author author = new Author(2L, "author name", new HashSet<Book>());
		Author author2 = new Author(3L, "another name", new HashSet<Book>());
		Author author3 = new Author(8L, "test name", new HashSet<Book>());
		GuiActionRunner.execute(() -> {
			composeBookView.getModelBookAuthors().addElement(author);
			composeBookView.getModelBookAuthors().addElement(author2);
			composeBookView.getModelAvailableAuthors().addElement(author3);
		});

		dialogFixture.list(BOOK_AUTHOR_JLIST).selectItem(0);
		dialogFixture.button(BUTTON_REMOVE_AUTHOR).click();

		assertThat(composeBookView.getModelBookAuthors().toArray()).containsExactly(author2);
		// right now we don't require any sorting
		assertThat(composeBookView.getModelAvailableAuthors().toArray()).containsExactlyInAnyOrder(author, author3);
	}

	@Test
	@GUITest
	public void test_OKbutton_should_hide_dialog_and_create_a_book_with_multiple_authors() {
		Author author = new Author(2L, "author name", new HashSet<Book>());
		Author author2 = new Author(5L, "another name", new HashSet<Book>());
		HashSet<Author> authors = new HashSet<>();
		authors.add(author);
		authors.add(author2);
		GuiActionRunner.execute(() -> {
			composeBookView.getModelBookAuthors().addElement(author);
			composeBookView.getModelBookAuthors().addElement(author2);
		});

		dialogFixture.textBox(TITLE_TEXT_FIELD).enterText("Book!");
		dialogFixture.button(JButtonMatcher.withText("OK")).click();

		assertThat(composeBookView.getBook()).isNotEmpty();
		assertThat(composeBookView.getBook().get()).usingRecursiveComparison()
				.isEqualTo(new Book(null, "Book!", authors));
		dialogFixture.requireNotVisible();
	}

}
