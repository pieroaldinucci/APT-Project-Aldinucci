package piero.aldinucci.apt.bookstore.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import piero.aldinucci.apt.bookstore.controller.BookstoreController;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

@RunWith(GUITestRunner.class)
public class AuthorSwingViewTest extends AssertJSwingJUnitTestCase {

	private static final int WAIT_TIME = 5;
	private static final String FIXTURE_TITLE_1 = "A book";
	private static final String FIXTURE_NAME_2 = "Name 2";
	private static final String FIXTURE_NAME_1 = "Author 1";
	private static final String AUTHOR_ERROR_LABEL = "AuthorErrorLabel";
	private static final String DELETE_AUTHOR_BUTTON = "DeleteAuthor";
	private static final String ADD_AUTHOR_BUTTON = "AddAuthor";
	private static final String NAME_TEXT_FIELD = "NameTextField";
	private static final String AUTHOR_LIST = "AuthorList";
	private JPanelFixture authorPanel;
	private AuthorSwingView authorView;
	private JFrame frame;
	
	@Mock
	BookstoreController controller;

	@Override
	protected void onSetUp() throws Exception {
		openMocks(this);
		
		GuiActionRunner.execute(() -> {
			frame = new JFrame();
			authorView = new AuthorSwingView(controller);
			frame.add(authorView);
			return frame;
		});

		authorPanel = new JPanelFixture(robot(), authorView);
		new FrameFixture(robot(), frame).show();
	}

	@Test
	public void test_Components() {
		authorPanel.label(JLabelMatcher.withText("Name"));
		assertThat(authorPanel.label(AUTHOR_ERROR_LABEL).requireText(" ").target().getForeground())
			.isEqualTo(Color.RED);
		authorPanel.textBox(NAME_TEXT_FIELD).requireEnabled().requireEmpty();
		assertThat(authorPanel.list(AUTHOR_LIST).contents()).isEmpty();
		assertThat(authorPanel.button(ADD_AUTHOR_BUTTON).requireDisabled().text()).isEqualTo("Add");
		assertThat(authorPanel.button(DELETE_AUTHOR_BUTTON).requireDisabled().text()).isEqualTo("Delete");
	}

	@Test
	@GUITest
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

	@Test
	@GUITest
	public void test_delete_button_enabled_only_when_item_list_is_selected() {
		DefaultListModel<Author> listModel = authorView.getAuthorListModel();
		GuiActionRunner.execute(() ->
			listModel.addElement(new Author(1L, FIXTURE_NAME_1, new HashSet<Book>())));

		authorPanel.list(AUTHOR_LIST).selectItem(0);
		authorPanel.button(DELETE_AUTHOR_BUTTON).requireEnabled();
		authorPanel.list(AUTHOR_LIST).clearSelection();
		authorPanel.button(DELETE_AUTHOR_BUTTON).requireDisabled();
	}

	@Test
	@GUITest
	public void test_changing_modelList_should_update_the_Jlist_correctly() {
		Author author1 = new Author(1L, FIXTURE_NAME_1, new HashSet<Book>());
		Author author2 = new Author(3L, FIXTURE_NAME_2, new HashSet<Book>());

		GuiActionRunner.execute(() ->{ 
			authorView.getAuthorListModel().addElement(author1);
			authorView.getAuthorListModel().addElement(author2);
		});

		assertThat(authorPanel.list(AUTHOR_LIST).contents())
			.containsExactly(author1.toString(), author2.toString());
	}
	
	@Test
	@GUITest
	public void test_showAllAuthors_should_replace_all_authors_into_the_list() {
		Author author1 = new Author(1L, FIXTURE_NAME_1, new HashSet<Book>());
		Author author2 = new Author(3L, FIXTURE_NAME_2, new HashSet<Book>());

		authorView.showAllAuthors(Arrays.asList(author1, author2));

		await().atMost(WAIT_TIME,TimeUnit.SECONDS).untilAsserted(() -> 
			assertThat(authorView.getAuthorListModel().toArray()).containsExactly(author1, author2));

		authorView.showAllAuthors(Arrays.asList(author2));

		await().atMost(WAIT_TIME,TimeUnit.SECONDS).untilAsserted(() -> 
			assertThat(authorView.getAuthorListModel().toArray()).containsExactly(author2));
	}
	
	@Test
	@GUITest
	public void test_authorAdded_should_clear_text_field_update_the_list_clear_error_and_disable_add_button() {
		authorPanel.textBox().enterText(FIXTURE_NAME_1);
		Author testAuthor = new Author(1L, FIXTURE_NAME_1, new HashSet<Book>());
		
		authorView.authorAdded(testAuthor);
		
		await().atMost(WAIT_TIME,TimeUnit.SECONDS).untilAsserted(() -> {
			assertThat(authorView.getAuthorListModel().toArray()).containsExactly(testAuthor);
			assertThat(authorPanel.label(AUTHOR_ERROR_LABEL).text()).isEmpty();
			authorPanel.textBox().requireEmpty();
			authorPanel.button(ADD_AUTHOR_BUTTON).requireDisabled();
		});
	}
	
	@Test
	@GUITest
	public void test_authorRemoved_should_update_the_list_and_clear_error() {
		Author testAuthor = new Author(1L, FIXTURE_NAME_1, null);
		Author testAuthor2 = new Author(4L, FIXTURE_NAME_2, null);
		GuiActionRunner.execute(() -> {
			authorView.getAuthorListModel().addElement(testAuthor);
			authorView.getAuthorListModel().addElement(testAuthor2);			
		});
		
		authorView.authorRemoved(testAuthor);
		
		await().atMost(WAIT_TIME,TimeUnit.SECONDS).untilAsserted(() -> {
			assertThat(authorView.getAuthorListModel().toArray()).containsExactly(testAuthor2);
			assertThat(authorPanel.label(AUTHOR_ERROR_LABEL).text()).isEmpty();
		});
	}
	
	@Test
	@GUITest
	public void text_showError_should_update_error_label() {
		Author testAuthor = new Author(2L, FIXTURE_NAME_1, new HashSet<Book>());
		testAuthor.getBooks().add(new Book(3L, FIXTURE_TITLE_1, null));
		
		authorView.showError("This is an Error Message!", testAuthor);
		
		await().atMost(WAIT_TIME,TimeUnit.SECONDS).untilAsserted(() -> 
			authorPanel.label(AUTHOR_ERROR_LABEL).requireText("This is an Error Message!: "+testAuthor));
	}
	
	@Test
	public void test_clicking_add_button_should_delegate_to_controller() {
		ArgumentCaptor<Author> author = ArgumentCaptor.forClass(Author.class);

		authorPanel.textBox(NAME_TEXT_FIELD).enterText(FIXTURE_NAME_2);
		authorPanel.button(ADD_AUTHOR_BUTTON).click();
		
		await().atMost(WAIT_TIME,TimeUnit.SECONDS).untilAsserted(() ->{
			verify(controller).newAuthor(author.capture());
			assertThat(author.getValue()).usingRecursiveComparison()
				.isEqualTo(new Author(null, FIXTURE_NAME_2, new HashSet<Book>()));
		});
		verifyNoMoreInteractions(controller);
	}
	
	@Test
	public void test_clicking_delete_button_should_delegate_to_controller() {
		Author testAuthor = new Author(2L, FIXTURE_NAME_1, null);
		Author testAuthor2 = new Author(3L, FIXTURE_NAME_2, null);
		GuiActionRunner.execute(() -> {
			authorView.getAuthorListModel().addElement(testAuthor);
			authorView.getAuthorListModel().addElement(testAuthor2);
		});
		
		authorPanel.list(AUTHOR_LIST).selectItem(0);
		authorPanel.button(DELETE_AUTHOR_BUTTON).click();
		
		await().atMost(WAIT_TIME,TimeUnit.SECONDS).untilAsserted(() ->
			verify(controller).deleteAuthor(testAuthor));
		verifyNoMoreInteractions(controller);
	}
}
