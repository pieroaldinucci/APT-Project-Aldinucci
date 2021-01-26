package piero.aldinucci.apt.bookstore.app.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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

	private static final String FIXTURE_JDBC_USERNAME = "testUser";
	private static final String FIXTURE_JDBC_PASSWORD = "password";
	private static final String TITLE_FIXTURE_3 = "title 3";
	private static final String TITLE_FIXTURE_2 = "title 2";
	private static final String TITLE_FIXTURE_1 = "title 1";
	private static final String NAME_FIXTURE_3 = "name 3";
	private static final String NAME_FIXTURE_2 = "name 2";
	private static final String NAME_FIXTURE_1 = "name 1";
	private FrameFixture window;
	private EntityManagerFactory emFactory;
	private LinkedList<Author> authors;
	private LinkedList<Book> books;
	
	@Override
	protected void onSetUp() throws Exception {
		
		HashMap<String, String> propertiesJPA = new HashMap<>();
		propertiesJPA.put("javax.persistence.jdbc.user", FIXTURE_JDBC_USERNAME);
		propertiesJPA.put("javax.persistence.jdbc.password", FIXTURE_JDBC_PASSWORD);
		propertiesJPA.put("javax.persistence.schema-generation.database.action", "drop-and-create");
		emFactory = Persistence.createEntityManagerFactory("apt.project.bookstore",propertiesJPA);
		
		populateDatabase();
		
		application("piero.aldinucci.apt.bookstore.app.swing.BookstoreSwingApp")
			.withArgs("-u="+FIXTURE_JDBC_USERNAME,
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
		Author author1 = new Author(null,NAME_FIXTURE_1,new HashSet<>());
		Author author2 = new Author(null,NAME_FIXTURE_2,new HashSet<>());
		Author author3 = new Author(null,NAME_FIXTURE_3,new HashSet<>());
		Book book1 = new Book(null,TITLE_FIXTURE_1,new HashSet<>());
		Book book2 = new Book(null,TITLE_FIXTURE_2,new HashSet<>());
		Book book3 = new Book(null,TITLE_FIXTURE_3,new HashSet<>());
		
		authors = new LinkedList<>();
		authors.add(author1);
		authors.add(author2);
		authors.add(author3);
		books = new LinkedList<>();
		books.add(book1);
		books.add(book2);
		books.add(book3);

		EntityManager em = emFactory.createEntityManager();
		em.getTransaction().begin();
		authors.stream().forEach(a -> em.persist(a));
		books.stream().forEach(b -> em.persist(b));
		author1.getBooks().add(book1);
		book1.getAuthors().add(author1);
		author2.getBooks().add(book2);
		book2.getAuthors().add(author2);
		author1.getBooks().add(book2);
		book2.getAuthors().add(author1);
		em.getTransaction().commit();
		em.close();
	}

	@Test
	@GUITest
	public void test_on_start_all_entities_are_shown() {
		window.tabbedPane().selectTab("Authors");
		
		assertThat(window.list().contents())
			.anySatisfy(a -> assertThat(a).contains(NAME_FIXTURE_1))
			.anySatisfy(a -> assertThat(a).contains(NAME_FIXTURE_2))
			.anySatisfy(a -> assertThat(a).contains(NAME_FIXTURE_3));

		window.tabbedPane().selectTab("Books");
		
		assertThat(window.list().contents())
			.anySatisfy(b -> assertThat(b).contains(TITLE_FIXTURE_1,NAME_FIXTURE_1))
			.anySatisfy(b -> assertThat(b).contains(TITLE_FIXTURE_2
					,NAME_FIXTURE_1, NAME_FIXTURE_2))
			.anySatisfy(b -> assertThat(b).contains(TITLE_FIXTURE_3));
	}
	
	
	@Test
	@GUITest
	@SuppressWarnings("rawtypes")
	public void test_add_new_book_with_new_author() {
		window.tabbedPane().selectTab("Authors");
		window.textBox().enterText("new Author");
		window.button(JButtonMatcher.withText("Add")).click();
		
		assertThat(window.list().contents()).anySatisfy(a -> assertThat(a).contains("new Author"));
		
		window.tabbedPane().selectTab("Books");
		window.button(JButtonMatcher.withText("New")).click();
		DialogFixture dialog = window.dialog();
		dialog.textBox().enterText("new Book");
		dialog.list(new GenericTypeMatcher<JList>(JList.class) {

			@Override
			protected boolean isMatching(JList component) {
				return component.getAccessibleContext().getAccessibleChildrenCount() != 0;
			}
		}).selectItem(Pattern.compile(".*new Author.*"));
		dialog.button(JButtonMatcher.withText("<")).click();
		dialog.button(JButtonMatcher.withText("OK")).click();
		
		assertThat(window.list().contents()).anySatisfy(b -> assertThat(b).contains("new Book","new Author"));
	}
	
	@Test
	@GUITest
	public void test_delete_Author_success() {
		window.tabbedPane().selectTab("Authors");
		JListFixture listFixture = window.list();
		listFixture.selectItem(Pattern.compile(".*"+NAME_FIXTURE_2+".*"));
		
		window.button("DeleteAuthor").click();
		
		assertThat(window.list().contents())
			.anySatisfy(a -> assertThat(a).contains(NAME_FIXTURE_1))
			.noneSatisfy(a -> assertThat(a).contains(NAME_FIXTURE_2))
			.anySatisfy(a -> assertThat(a).contains(NAME_FIXTURE_3));
		
		window.tabbedPane().selectTab("Books");
		
		assertThat(window.list().contents())
			.anySatisfy(b -> assertThat(b).contains(TITLE_FIXTURE_1, NAME_FIXTURE_1))
			.anySatisfy(b -> assertThat(b).contains(TITLE_FIXTURE_2, NAME_FIXTURE_1))
			.anySatisfy(b -> assertThat(b).contains(TITLE_FIXTURE_3))
			.noneSatisfy(a -> assertThat(a).contains(NAME_FIXTURE_2));
	}
	
	@Test
	@GUITest
	public void test_delete_Author_error() {
		window.tabbedPane().selectTab("Authors");
		JListFixture listFixture = window.list();
		listFixture.selectItem(Pattern.compile(".*"+NAME_FIXTURE_3+".*"));
		
		EntityManager em = emFactory.createEntityManager();
		em.getTransaction().begin();
		Author author = em.find(Author.class, authors.get(2).getId());
		em.remove(author);
		em.getTransaction().commit();
		em.close();
		
		window.button("DeleteAuthor").click();
		
		assertThat(window.label("AuthorErrorLabel").text()).contains(NAME_FIXTURE_3);
		assertThat(listFixture.contents()).noneSatisfy(a -> 
			assertThat(a).contains(NAME_FIXTURE_3));
	}
	
	
	@Test
	@GUITest
	public void test_delete_Book_success() {
		window.tabbedPane().selectTab("Books");
		JListFixture listFixture = window.list();
		listFixture.selectItem(Pattern.compile(".*"+TITLE_FIXTURE_1+".*"));
		
		window.button("DeleteBook").click();
		
		assertThat(window.list().contents())
			.noneSatisfy(a -> assertThat(a).contains(TITLE_FIXTURE_1))
			.anySatisfy(a -> assertThat(a).contains(TITLE_FIXTURE_2))
			.anySatisfy(a -> assertThat(a).contains(TITLE_FIXTURE_3));
	}
	
	@Test
	@GUITest
	public void test_delete_Book_error() {
		window.tabbedPane().selectTab("Books");
		JListFixture listFixture = window.list("BookJList");
		listFixture.selectItem(Pattern.compile(".*"+TITLE_FIXTURE_3+".*"));
		
		EntityManager em = emFactory.createEntityManager();
		em.getTransaction().begin();
		Book book = em.find(Book.class, books.get(2).getId());
		em.remove(book);
		em.getTransaction().commit();
		em.close();
		
		window.button("DeleteBook").click();
		
		assertThat(window.label("BookErrorLabel").text()).contains(TITLE_FIXTURE_3);
		assertThat(listFixture.contents()).noneSatisfy(b -> 
			assertThat(b).contains(TITLE_FIXTURE_3));
	}
}
