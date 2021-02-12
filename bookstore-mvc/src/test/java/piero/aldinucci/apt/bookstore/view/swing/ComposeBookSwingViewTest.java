package piero.aldinucci.apt.bookstore.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.*;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import piero.aldinucci.apt.bookstore.controller.BookstoreController;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

@RunWith(GUITestRunner.class)
public class ComposeBookSwingViewTest extends AssertJSwingJUnitTestCase {

	private static final String FIXTURE_TITLE_1 = "Book!";
	private static final String FIXTURE_NAME_1 = "test name";
	private static final String FIXTURE_NAME_2 = "another name";
	private static final String FIXTURE_NAME_3 = "author name";
	private static final String BUTTON_REMOVE_AUTHOR = "buttonRemoveAuthor";
	private static final String BUTTON_ADD_AUTHOR = "buttonAddAuthor";
	private static final String TITLE_TEXT_FIELD = "titleTextField";
	private static final String AUTHOR_JLIST = "AvailableAuthors";
	private static final String BOOK_AUTHOR_JLIST = "BookAuthors";
	private static final int WAIT_TIME = 5;

	private ComposeBookSwingView composeBookView;
	private DialogFixture dialogFixture;
	
	@Mock
	private BookstoreController controller;

	@Override
	protected void onSetUp() throws Exception {
		openMocks(this);
		
		GuiActionRunner.execute(() -> {
			composeBookView = new ComposeBookSwingView(controller);
		});

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
	@GUITest
	public void test_Dialog_window_is_hidden_but_not_disposed_when_closed() {
		dialogFixture.close();
		dialogFixture.requireNotVisible();
	}

	@Test
	@GUITest
	public void test_available_authors_list_correctly_painted_by_model() {
		Author author1 = new Author(2L, FIXTURE_NAME_1, null);
		Author author2 = new Author(3L, FIXTURE_NAME_2, null);

		GuiActionRunner.execute(() -> {
			composeBookView.getModelAvailableAuthors().addElement(author1);
			composeBookView.getModelAvailableAuthors().addElement(author2);
		});

		assertThat(dialogFixture.list(AUTHOR_JLIST).contents())
			.containsExactly(author1.toString(), author2.toString());
	}

	@Test
	@GUITest
	public void test_book_authors_list_correctly_painted_by_model() {
		Author author1 = new Author(2L, FIXTURE_NAME_1, null);
		Author author2 = new Author(3L, FIXTURE_NAME_2, null);

		GuiActionRunner.execute(() -> {
			composeBookView.getModelBookAuthors().addElement(author1);
			composeBookView.getModelBookAuthors().addElement(author2);
		});

		assertThat(dialogFixture.list(BOOK_AUTHOR_JLIST).contents())
			.containsExactly(author1.toString(),author2.toString());
	}

	@Test
	@GUITest
	public void test_composeNewBook_should_make_the_dialog_visible() {
		composeBookView.setModal(false);
		composeBookView.setVisible(false);
		dialogFixture.requireNotVisible();
		
		composeBookView.composeNewBook(Lists.emptyList());
		
		await().atMost(WAIT_TIME,TimeUnit.SECONDS).untilAsserted(() -> 
			dialogFixture.requireVisible());
	}
	
	@Test
	@GUITest
	public void test_composeNewBook_should_clear_all_components_and_update_the_available_authors_list() {
		dialogFixture.textBox().enterText("Not Empty");
		GuiActionRunner.execute(() -> {
			composeBookView.getModelBookAuthors().addElement(new Author(5L,"Isaac",new HashSet<>()));
			composeBookView.getModelBookAuthors().addElement(new Author(3L,"Clarke",new HashSet<>()));
		});
		Author author1 = new Author(2L, FIXTURE_NAME_1, null);
		Author author2 = new Author(3L, FIXTURE_NAME_2, null);

		composeBookView.composeNewBook(Arrays.asList(author1, author2));

		await().atMost(WAIT_TIME,TimeUnit.SECONDS).untilAsserted(() -> {
			assertThat(composeBookView.getModelBookAuthors().toArray()).isEmpty();
			assertThat(composeBookView.getModelAvailableAuthors().toArray())
				.containsExactly(author1, author2);
			dialogFixture.textBox().requireEmpty();
			dialogFixture.button(JButtonMatcher.withText("OK")).requireDisabled();
		});
	}
	
	@Test
	@GUITest
	public void test_writing_title_should_enable_or_disable_OK_button() {
		JButtonFixture okButton = dialogFixture.button(JButtonMatcher.withText("OK"));
		dialogFixture.textBox(TITLE_TEXT_FIELD).enterText("A title");
		
		okButton.requireEnabled();
		
		dialogFixture.textBox(TITLE_TEXT_FIELD).deleteText();
		okButton.requireDisabled();
		
		dialogFixture.textBox(TITLE_TEXT_FIELD).enterText("   ");
		okButton.requireDisabled();
	}

	@Test
	public void test_pressing_cancel_button_will_not_call_the_controller_and_hide_Dialog() {
		dialogFixture.button(JButtonMatcher.withText("Cancel")).click();

		dialogFixture.requireNotVisible();
		verifyNoInteractions(controller);
	}

	@Test
	@GUITest
	public void test_buttonAddAuthor_should_be_disabled_when_no_Existing_author_is_selected() {
		GuiActionRunner.execute(() -> composeBookView.getModelAvailableAuthors()
				.addElement(new Author(2L, FIXTURE_NAME_1, new HashSet<Book>())));
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
			composeBookView.getModelBookAuthors().addElement(
					new Author(2L, FIXTURE_NAME_1, new HashSet<Book>()));
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
		Author author = new Author(2L, FIXTURE_NAME_1, new HashSet<Book>());
		Author author2 = new Author(3L, FIXTURE_NAME_2, new HashSet<Book>());
		Author author3 = new Author(8L, FIXTURE_NAME_3, new HashSet<Book>());
		GuiActionRunner.execute(() -> {
			composeBookView.getModelBookAuthors().addElement(author);
			composeBookView.getModelAvailableAuthors().addElement(author3);
			composeBookView.getModelAvailableAuthors().addElement(author2);
		});

		dialogFixture.list(AUTHOR_JLIST).selectItem(1);
		dialogFixture.button(BUTTON_ADD_AUTHOR).click();

		await().atMost(WAIT_TIME,TimeUnit.SECONDS).untilAsserted(() ->{
			assertThat(composeBookView.getModelBookAuthors().toArray())
				.containsExactlyInAnyOrder(author, author2);
			assertThat(composeBookView.getModelAvailableAuthors().toArray())
				.containsExactly(author3);
		});
	}

	@Test
	@GUITest
	public void test_buttonRemoveAuthor_should_move_selected_author_from_left_to_right() {
		Author author = new Author(2L, FIXTURE_NAME_1, new HashSet<Book>());
		Author author2 = new Author(3L, FIXTURE_NAME_2, new HashSet<Book>());
		Author author3 = new Author(8L, FIXTURE_NAME_3, new HashSet<Book>());
		GuiActionRunner.execute(() -> {
			composeBookView.getModelBookAuthors().addElement(author);
			composeBookView.getModelBookAuthors().addElement(author2);
			composeBookView.getModelAvailableAuthors().addElement(author3);
		});

		dialogFixture.list(BOOK_AUTHOR_JLIST).selectItem(0);
		dialogFixture.button(BUTTON_REMOVE_AUTHOR).click();

		await().atMost(WAIT_TIME,TimeUnit.SECONDS).untilAsserted(() ->{
			assertThat(composeBookView.getModelBookAuthors().toArray()).containsExactly(author2);
			assertThat(composeBookView.getModelAvailableAuthors().toArray())
				.containsExactlyInAnyOrder(author, author3);
		});
	}

	@Test
	@GUITest
	public void test_OKbutton_should_hide_dialog_create_a_book_and_call_controller() {
		Author author = new Author(2L, FIXTURE_NAME_1, new HashSet<Book>());
		Author author2 = new Author(5L, FIXTURE_NAME_2, new HashSet<Book>());
		HashSet<Author> authors = new HashSet<>();
		authors.add(author);
		authors.add(author2);
		GuiActionRunner.execute(() -> {
			composeBookView.getModelBookAuthors().addElement(author);
			composeBookView.getModelBookAuthors().addElement(author2);
		});

		dialogFixture.textBox(TITLE_TEXT_FIELD).enterText(FIXTURE_TITLE_1);
		dialogFixture.button(JButtonMatcher.withText("OK")).click();

		await().atMost(WAIT_TIME,TimeUnit.SECONDS).untilAsserted(() ->
			dialogFixture.requireNotVisible());
		ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
		verify(controller).newBook(bookCaptor.capture());
		assertThat(bookCaptor.getValue()).usingRecursiveComparison()
			.isEqualTo(new Book(null, FIXTURE_TITLE_1, authors));
	}

}
