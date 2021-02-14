package piero.aldinucci.apt.bookstore.app.swing.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.List;

import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ViewSteps {

	private static final String FIXTURE_NEW_NAME = "new author";
	private static final String FIXTURE_NEW_TITLE = "new book";
	private FrameFixture frameFixture;

	@After
	public void tearDown() {
		if (frameFixture != null)
			frameFixture.cleanUp();
	}

	
	@Given("The application starts")
	public void the_application_starts() {
		application("piero.aldinucci.apt.bookstore.app.swing.BookstoreSwingApp").withArgs("-u=testUser", "-p=password",
				"--postgres-host=localhost", "--db-name=projectAPTTestDb", "--postgres-port=5432").start();

		frameFixture = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {

			@Override
			protected boolean isMatching(JFrame component) {
				return "Bookstore View".equals(component.getTitle()) && component.isShowing();
			}
		}).using(BasicRobot.robotWithCurrentAwtHierarchy()).focus();
	}

	@Given("The {string} view is shown")
	public void the_view_is_shown(String paneName) {
		frameFixture.tabbedPane().selectTab(paneName);
	}

	@Then("The list contains elements with the following values")
	public void the_list_contains_elements_with_the_following_values(List<List<String>> values) {
		values.forEach(v -> assertThat(frameFixture.list().contents())
				.anySatisfy(s -> assertThat(s).contains(v.get(0), v.get(1))));
	}

	@When("The user click the {string} button")
	public void the_user_click_the_button(String button) {
		frameFixture.button(JButtonMatcher.withText(button).andShowing()).click();
	}
	
	@When("The user enter {string} in the text field")
	public void the_user_enter_in_the_text_field(String string) {
		frameFixture.textBox().enterText(string);
	}

	@Given("The user provides the name of the new author")
	public void the_user_provides_the_name_of_the_new_author() {
		frameFixture.textBox().enterText(FIXTURE_NEW_NAME);
	}

	@Then("The list constains the new author")
	public void the_list_constains_the_new_author() {
		assertThat(frameFixture.list().contents())
			.anyMatch(s -> s.contains(FIXTURE_NEW_NAME));
	}
	
	@Then("The Compose book view is shown")
	public void the_compose_book_view_is_shown() {
		frameFixture.dialog().requireVisible();
	}
	
	@When("The user provides the title for the book")
	public void the_user_provides_the_title_for_the_book() {
		frameFixture.dialog().textBox().enterText(FIXTURE_NEW_TITLE);
	}

	@When("The user choose an author from the list of authors")
	public void the_user_choose_an_author_from_the_list_of_authors() {
		frameFixture.dialog().list("AvailableAuthors").selectItem(0);
	}
	
	@Then("The new book and its authors is shown in the Book View")
	public void the_new_book_and_its_authors_is_shown_in_the_book_view() {
		assertThat((frameFixture.list().contents()))
			.anySatisfy(s -> assertThat(s).contains(FIXTURE_NEW_TITLE,DatabaseSteps.FIXTURE_NAME_1));
	}

	@When("The user click the {string} dialog button")
	public void the_user_click_the_dialog_button(String string) {
		frameFixture.dialog().button(JButtonMatcher.withText(string)).click();
	}

	@When("The user select the first item from the list")
	public void the_user_select_the_first_item_from_the_list() {
		frameFixture.list().selectItem(0);
	}

	@Then("The first book will be removed from the list of books")
	public void the_first_book_will_be_removed_from_the_list_of_books() {
		assertThat(frameFixture.list().contents())
			.noneSatisfy(s -> assertThat(s).contains(DatabaseSteps.FIXTURE_TITLE_1));
	}
	
	@Then("The first author will be removed from the list")
	public void the_first_author_will_be_removed_from_the_list() {
		assertThat(frameFixture.list().contents())
			.noneSatisfy(s -> assertThat(s).contains(DatabaseSteps.FIXTURE_NAME_1));
	}	
}
