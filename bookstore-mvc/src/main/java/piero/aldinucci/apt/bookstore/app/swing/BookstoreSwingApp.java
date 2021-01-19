package piero.aldinucci.apt.bookstore.app.swing;

import java.awt.EventQueue;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.inject.Guice;
import com.google.inject.Injector;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import piero.aldinucci.apt.bookstore.app.guice.modules.BookstoreControllerSwingModule;
import piero.aldinucci.apt.bookstore.app.guice.modules.BookstoreManagerJPAModule;
import piero.aldinucci.apt.bookstore.controller.BookstoreControllerImpl;
import piero.aldinucci.apt.bookstore.repositories.factory.RepositoriesJPAFactoryImpl;
import piero.aldinucci.apt.bookstore.service.BookstoreManagerImpl;
import piero.aldinucci.apt.bookstore.transaction.TransactionManagerJPA;
import piero.aldinucci.apt.bookstore.view.swing.AuthorSwingView;
import piero.aldinucci.apt.bookstore.view.swing.BookSwingView;
import piero.aldinucci.apt.bookstore.view.swing.BookstoreSwingFrame;
import piero.aldinucci.apt.bookstore.view.swing.ComposeBookSwingView;

@Command(mixinStandardHelpOptions = true)
public class BookstoreSwingApp implements Callable<Void> {

	@Option(names = { "--postgres-host" }, description = { "Postgresql host address" })
	private String host = "localhost";

	@Option(names = { "--db-name" }, description = { "Database name" })
	private String databaseName = "projectAPTTestDb";

	@Option(names = { "--postgres-port" }, description = { "Postgresql host port" })
	private int port = 5432;

	@Option(names = { "--user" }, description = { "Postgresql username" })
	private String userName = "testUser";

	@Option(names = { "--password" }, description = { "Postgresql password" })
	private String password = "secret";

	private EntityManagerFactory emFactory;

	public static void main(String[] args) {
		new CommandLine(new BookstoreSwingApp()).execute(args);
	}

	private EntityManagerFactory getEntityManagerFactory() {
		String propertyJdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;

		HashMap<String, String> propertiesJPA = new HashMap<>();
		propertiesJPA.put("javax.persistence.jdbc.user", userName);
		propertiesJPA.put("javax.persistence.jdbc.password", password);
		propertiesJPA.put("javax.persistence.jdbc.url", propertyJdbcUrl);
		return Persistence.createEntityManagerFactory("apt.project.bookstore", propertiesJPA);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {			
			try {
				emFactory = getEntityManagerFactory();
				Injector injector = Guice.createInjector(new BookstoreControllerSwingModule(
						new BookstoreManagerJPAModule(emFactory)));
				
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

}
