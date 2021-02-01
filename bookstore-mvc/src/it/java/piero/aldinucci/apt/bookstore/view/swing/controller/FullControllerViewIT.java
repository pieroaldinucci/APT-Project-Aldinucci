package piero.aldinucci.apt.bookstore.view.swing.controller;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.Arrays;
import java.util.HashSet;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import piero.aldinucci.apt.bookstore.controller.BookstoreControllerImpl;
import piero.aldinucci.apt.bookstore.model.Author;
import piero.aldinucci.apt.bookstore.model.Book;
import piero.aldinucci.apt.bookstore.service.BookstoreManager;
import piero.aldinucci.apt.bookstore.view.AuthorView;
import piero.aldinucci.apt.bookstore.view.BookView;
import piero.aldinucci.apt.bookstore.view.ComposeBookView;
import piero.aldinucci.apt.bookstore.view.factory.ViewsFactory;
import piero.aldinucci.apt.bookstore.view.swing.AuthorSwingView;
import piero.aldinucci.apt.bookstore.view.swing.BookSwingView;
import piero.aldinucci.apt.bookstore.view.swing.BookstoreSwingFrame;
import piero.aldinucci.apt.bookstore.view.swing.ComposeBookSwingView;

@RunWith(GUITestRunner.class)
public class FullControllerViewIT extends AssertJSwingJUnitTestCase{

	@Mock
	private BookstoreManager manager;
	
	private FrameFixture window;
	private DialogFixture dialog;

	private BookstoreControllerImpl controller;

	private class IntegrationTestModule extends AbstractModule{
		
		@Override
		protected void configure() {
			bind(BookstoreManager.class).toInstance(manager);
			install(new FactoryModuleBuilder()
				.implement(AuthorView.class, AuthorSwingView.class)
				.implement(BookView.class, BookSwingView.class)
				.implement(ComposeBookView.class, ComposeBookSwingView.class)
				.build(ViewsFactory.class));
		}
		
		@Provides
		BookstoreControllerImpl getController(ViewsFactory viewsFactory) {
			BookstoreControllerImpl controller = new BookstoreControllerImpl(manager);
			controller.setAuthorView(viewsFactory.createAuthorView(controller));
			controller.setBookView(viewsFactory.createBookView(controller));
			controller.setComposeBookView(viewsFactory.createComposeBookView(controller));
			return controller;
		}
	}
	

	@Override
	protected void onSetUp() throws Exception {
		openMocks(this);
		Injector injector = Guice.createInjector(new IntegrationTestModule());
		
		GuiActionRunner.execute(() -> {
			controller = injector.getInstance(BookstoreControllerImpl.class);
			JFrame frame = new BookstoreSwingFrame((AuthorSwingView) controller.getAuthorView(),
					(BookSwingView) controller.getBookView());
			dialog = new DialogFixture(robot(), (ComposeBookSwingView) controller.getComposeBookView());
			window = new FrameFixture(robot(), frame);
			return frame;
		});
		
		window.show();
		allAuthorsAndBooksSetup();
	}

	private void allAuthorsAndBooksSetup() {
		Author author1 = new Author(1L, "Author 1", new HashSet<>());
		Book book1 = new Book(11L, "Book 2", new HashSet<>());
		Book book2 = new Book(13L, "Book 2", new HashSet<>());
		
		when(manager.getAllAuthors()).thenReturn(Arrays.asList(author1));
		when(manager.getAllBooks()).thenReturn(Arrays.asList(book1,book2));
		
		GuiActionRunner.execute(() -> {
			controller.allAuthors();
			controller.allBooks();
		});
	}

	
	@Test
	@GUITest
	public void test_navigate_UI_components() {
		window.tabbedPane().selectTab("Books");
		
		window.list("BookJList").requireItemCount(2);
		dialog.requireNotVisible();
		
		window.button(JButtonMatcher.withText("New")).click();
		
		dialog.requireVisible().list("AvailableAuthors").requireItemCount(1);
		
		dialog.button(JButtonMatcher.withText("Cancel")).click();
		
		dialog.requireNotVisible();

		window.tabbedPane().selectTab("Authors");
		
		window.list("AuthorList").requireItemCount(1);
	}
}
