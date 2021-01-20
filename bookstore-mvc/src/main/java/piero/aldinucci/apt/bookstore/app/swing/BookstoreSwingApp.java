package piero.aldinucci.apt.bookstore.app.swing;

import java.awt.EventQueue;
import java.util.HashMap;
import java.util.concurrent.Callable;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.inject.Guice;
import com.google.inject.Injector;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import piero.aldinucci.apt.bookstore.app.guice.modules.BookstoreControllerSwingModule;
import piero.aldinucci.apt.bookstore.app.guice.modules.BookstoreManagerJPAModule;
import piero.aldinucci.apt.bookstore.controller.BookstoreControllerImpl;
import piero.aldinucci.apt.bookstore.view.swing.AuthorSwingView;
import piero.aldinucci.apt.bookstore.view.swing.BookSwingView;
import piero.aldinucci.apt.bookstore.view.swing.BookstoreSwingFrame;

@Command(mixinStandardHelpOptions = true)
public class BookstoreSwingApp implements Callable<Void> {

	@Option(names = { "--postgres-host" }, description = { "Postgresql host address" })
	private String host = "localhost";

	@Option(names = { "--db-name" }, description = { "Database name" })
	private String databaseName = "projectAPTTestDb";

	@Option(names = { "--postgres-port" }, description = { "Postgresql host port" })
	private int port = 5432;

	@Option(names = {"-u", "--user" }, description = { "Postgresql username" })
	private String userName;

	@Option(names = {"-p", "--password" }, description = { "Postgresql password" })
	private String password;
	
	@Option(names = {"-c", "--create" }, description = { "Create database if not present" })
	private boolean createDb;

	public static void main(String[] args) {
		new CommandLine(new BookstoreSwingApp()).execute(args);
	}

	private EntityManagerFactory getEntityManagerFactory() {
		String propertyJdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;

		HashMap<String, String> propertiesJPA = new HashMap<>();
		propertiesJPA.put("javax.persistence.jdbc.user", userName);
		propertiesJPA.put("javax.persistence.jdbc.password", password);
		propertiesJPA.put("javax.persistence.jdbc.url", propertyJdbcUrl);
		if (createDb)
			propertiesJPA.put("javax.persistence.schema-generation.database.action", "create");
		return Persistence.createEntityManagerFactory("apt.project.bookstore", propertiesJPA);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {			
			try {
				Injector injector = Guice.createInjector(new BookstoreControllerSwingModule(
						new BookstoreManagerJPAModule(getEntityManagerFactory())));
				
				BookstoreControllerImpl controller = injector.getInstance(BookstoreControllerImpl.class);
				BookstoreSwingFrame frame = new BookstoreSwingFrame(
						(AuthorSwingView)controller.getAuthorView(),
						(BookSwingView)controller.getBookView());
				controller.allAuthors();
				controller.allBooks();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return null;
	}
	
	//No Guice version
//	@Override
//	public Void call() throws Exception {
//		EventQueue.invokeLater(() -> {			
//			try {
//				emFactory = getEntityManagerFactory();
//				
//				BookstoreManagerImpl manager = new BookstoreManagerImpl(
//						new TransactionManagerJPA(emFactory, new RepositoriesJPAFactoryImpl()));
//				
//				BookstoreControllerImpl controller = new BookstoreControllerImpl(manager);
//				AuthorSwingView authorView = new AuthorSwingView(controller);
//				BookSwingView bookView = new BookSwingView(controller);
//				ComposeBookSwingView composeBook = new ComposeBookSwingView(controller);
//				controller.setAuthorView(authorView);
//				controller.setBookView(bookView);
//				controller.setComposeBookView(composeBook);
//				BookstoreSwingFrame frame = new BookstoreSwingFrame(authorView, bookView);
//				
//				controller.allAuthors();
//				controller.allBooks();
//				frame.setVisible(true);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
//		return null;
//	}

}
