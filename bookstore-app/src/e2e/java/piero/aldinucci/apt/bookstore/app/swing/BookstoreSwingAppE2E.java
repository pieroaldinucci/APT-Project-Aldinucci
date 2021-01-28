package piero.aldinucci.apt.bookstore.app.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JFrame;
import javax.swing.JList;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JListFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;

@RunWith(GUITestRunner.class)
public class BookstoreSwingAppE2E extends AssertJSwingJUnitTestCase{

	private static final String BTN_DELETE_BOOK = "DeleteBook";
	private static final String BTN_DELETE_AUTHOR = "DeleteAuthor";
	private static final String FIXTURE_JDBC_USERNAME = "testUser";
	private static final String FIXTURE_JDBC_PASSWORD = "password";
	private static final String FIXTURE_TITLE_1 = "title 1";
	private static final String FIXTURE_TITLE_2 = "title 2";
	private static final String FIXTURE_TITLE_NEW = "new Book";
	private static final String FIXTURE_NAME_1 = "name 1";
	private static final String FIXTURE_NAME_2 = "name 2";
	private static final String FIXTURE_NAME_3 = "name 3";
	private static final String FIXTURE_NAME_NEW = "new Name";
	
	private FrameFixture window;
	private EntityManagerFactory emFactory;
	private List<Author> authors;
	private List<Book> books;
	
	@Override
	protected void onSetUp() throws Exception {
		HashMap<String, String> propertiesJPA = new HashMap<>();
		propertiesJPA.put("javax.persistence.jdbc.user", FIXTURE_JDBC_USERNAME);
		propertiesJPA.put("javax.persistence.jdbc.password", FIXTURE_JDBC_PASSWORD);
		propertiesJPA.put("javax.persistence.schema-generation.database.action", "drop-and-create");
		propertiesJPA.put("javax.persistence.jdbc.url","jdbc:postgresql://localhost:5432/projectAPTTestDb");
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore.app",propertiesJPA);
		populateDatabase();
		
		application("piero.aldinucci.apt.bookstore.app.swing.BookstoreSwingApp").withArgs(
				"-u="+FIXTURE_JDBC_USERNAME,
				"-p="+FIXTURE_JDBC_PASSWORD,
				"--postgres-host=localhost",
				"--db-name=projectAPTTestDb",
				"--postgres-port=5432").start();
		
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {

			@Override
			protected boolean isMatching(JFrame frame) {
				return frame.getTitle().equals("Bookstore View") && frame.isShowing();
			}
		}).using(robot()).focus();
	}
	
	@Override
	protected void onTearDown() {
		emFactory.close();
		window.cleanUp();
	}
	

	private void populateDatabase() {
		Author author1 = new Author(null,FIXTURE_NAME_1,new HashSet<>());
		Author author2 = new Author(null,FIXTURE_NAME_2,new HashSet<>());
		Author author3 = new Author(null,FIXTURE_NAME_3,new HashSet<>());
		Book book1 = new Book(null,FIXTURE_TITLE_1,new HashSet<>());
		Book book2 = new Book(null,FIXTURE_TITLE_2,new HashSet<>());
		
		authors = Arrays.asList(author1,author2,author3);
		books = Arrays.asList(book1, book2);
		EntityManager em = emFactory.createEntityManager();
		em.getTransaction().begin();
		authors.forEach(a -> em.persist(a));
		books.forEach(b -> em.persist(b));
		author1.getBooks().add(book1);
		author2.getBooks().add(book1);
		book1.getAuthors().add(author1);
		book1.getAuthors().add(author2);
		em.getTransaction().commit();
		em.close();
	}

	@Test
	@GUITest
	public void test_on_start_all_entities_are_shown() {
		window.tabbedPane().selectTab("Authors");
		
		assertThat(window.list().contents())
			.anySatisfy(a -> assertThat(a).contains(FIXTURE_NAME_1))
			.anySatisfy(a -> assertThat(a).contains(FIXTURE_NAME_2))
			.anySatisfy(a -> assertThat(a).contains(FIXTURE_NAME_3));

		window.tabbedPane().selectTab("Books");
		
		assertThat(window.list().contents())
			.anySatisfy(b -> assertThat(b)
					.contains(FIXTURE_TITLE_1,FIXTURE_NAME_1,FIXTURE_NAME_2))
			.anySatisfy(b -> assertThat(b).contains(FIXTURE_TITLE_2)
					.doesNotContain(FIXTURE_NAME_1,FIXTURE_NAME_2,FIXTURE_NAME_3));
	}
	
	
	@Test
	@GUITest
	@SuppressWarnings("rawtypes")
	public void test_add_new_book_with_new_author() {
		window.tabbedPane().selectTab("Authors");
		window.textBox().enterText(FIXTURE_NAME_NEW);
		window.button(JButtonMatcher.withText("Add")).click();
		
		assertThat(window.list().contents()).anyMatch(s -> 
			s.contains(FIXTURE_NAME_NEW));
		
		window.tabbedPane().selectTab("Books");
		window.button(JButtonMatcher.withText("New")).click();
		DialogFixture dialog = window.dialog();
		dialog.textBox().enterText(FIXTURE_TITLE_NEW);
		dialog.list(new GenericTypeMatcher<JList>(JList.class) {

			@Override
			protected boolean isMatching(JList component) {
				return component.getAccessibleContext().getAccessibleChildrenCount() != 0;
			}
		}).selectItem(Pattern.compile(".*"+FIXTURE_NAME_NEW+".*"));
		dialog.button(JButtonMatcher.withText("<")).click();
		dialog.button(JButtonMatcher.withText("OK")).click();
		
		assertThat(window.list().contents()).anySatisfy(b ->
			assertThat(b).contains(FIXTURE_TITLE_NEW,FIXTURE_NAME_NEW));
	}
	
	@Test
	@GUITest
	public void test_delete_Author_success() {
		window.tabbedPane().selectTab("Authors");
		JListFixture listFixture = window.list();
		listFixture.selectItem(Pattern.compile(".*"+FIXTURE_NAME_2+".*"));
		
		window.button(BTN_DELETE_AUTHOR).click();
		
		assertThat(window.list().contents())
			.anyMatch(s -> s.contains(FIXTURE_NAME_1))
			.noneMatch(s -> s.contains(FIXTURE_NAME_2))
			.anyMatch(s -> s.contains(FIXTURE_NAME_3));
		
		window.tabbedPane().selectTab("Books");
		
		assertThat(window.list().contents())
			.anySatisfy(b -> assertThat(b).contains(FIXTURE_TITLE_1, FIXTURE_NAME_1))
			.noneMatch(s -> s.contains(FIXTURE_NAME_2));
	}
	
	@Test
	@GUITest
	public void test_delete_Author_error() {
		window.tabbedPane().selectTab("Authors");
		JListFixture listFixture = window.list();
		EntityManager em = emFactory.createEntityManager();
		em.getTransaction().begin();
		Author author = em.find(Author.class, authors.get(2).getId());
		em.remove(author);
		em.getTransaction().commit();
		em.close();
		
		listFixture.selectItem(Pattern.compile(".*"+FIXTURE_NAME_3+".*"));
		window.button(BTN_DELETE_AUTHOR).click();
		
		assertThat(window.label("AuthorErrorLabel").text()).contains(FIXTURE_NAME_3);
		assertThat(listFixture.contents()).noneSatisfy(a -> 
			assertThat(a).contains(FIXTURE_NAME_3));
	}
	
	
	@Test
	@GUITest
	public void test_delete_Book_success() {
		window.tabbedPane().selectTab("Books");
		JListFixture listFixture = window.list();
		listFixture.selectItem(Pattern.compile(".*"+FIXTURE_TITLE_1+".*"));
		
		window.button(BTN_DELETE_BOOK).click();
		
		assertThat(listFixture.contents())
			.noneMatch(s -> s.contains(FIXTURE_TITLE_1))
			.anyMatch(s -> s.contains(FIXTURE_TITLE_2));
	}
	
	@Test
	@GUITest
	public void test_delete_Book_error() {
		window.tabbedPane().selectTab("Books");
		JListFixture listFixture = window.list("BookJList");
		EntityManager em = emFactory.createEntityManager();
		em.getTransaction().begin();
		Book book = em.find(Book.class, books.get(1).getId());
		em.remove(book);
		em.getTransaction().commit();
		em.close();
		
		listFixture.selectItem(Pattern.compile(".*"+FIXTURE_TITLE_2+".*"));
		window.button(BTN_DELETE_BOOK).click();
		
		assertThat(window.label("BookErrorLabel").text()).contains(FIXTURE_TITLE_2);
		assertThat(listFixture.contents()).noneMatch(s -> s.contains(FIXTURE_TITLE_2));
	}
}
