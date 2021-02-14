package piero.aldinucci.apt.bookstore.app.swing.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.awt.Frame;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import piero.aldinucci.apt.bookstore.model.Author;

public class AuthorViewPanelSteps {

	private EntityManagerFactory emFactory;

	private FrameFixture frameFixture;

	@Before
	public void setUp() {
		Map<String, String> propertiesJpa = new HashMap<>();
		propertiesJpa.put("javax.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/projectAPTTestDb");
		propertiesJpa.put("javax.persistence.jdbc.user", "testUser");
		propertiesJpa.put("javax.persistence.jdbc.password", "password");
		propertiesJpa.put("javax.persistence.schema-generation.database.action", "drop-and-create");
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore.app", propertiesJpa);
	}

	@After
	public void tearDown() {
		emFactory.close();
		if (frameFixture != null)
			frameFixture.cleanUp();
	}

	@Given("The database contains the authors with the following values")
	public void the_database_contains_the_authors_with_the_following_values(List<String> values) {
		EntityManager em = emFactory.createEntityManager();
		em.getTransaction().begin();
		values.forEach(v -> em.persist(new Author(null, v, new HashSet<>())));
		em.getTransaction().commit();
		em.close();
	}
	
	@Given("The application starts")
	public void the_application_starts() {
		application("piero.aldinucci.apt.bookstore.app.swing.BookstoreSwingApp").withArgs("-u=testUser", "-p=password",
				"--postgres-host=localhost", "--db-name=projectAPTTestDb", "--postgres-port=5432").start();

		frameFixture = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {

			@Override
			protected boolean isMatching(JFrame component) {
				return component.getTitle().equals("Bookstore View") && component.isShowing();
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
		frameFixture.button(JButtonMatcher.withText(button)).click();
	}
	
	@When("The user enter {string} in the text field")
	public void the_user_enter_in_the_text_field(String string) {
		frameFixture.textBox().enterText(string);
	}

}
