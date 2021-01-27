package piero.aldinucci.apt.bookstore.app.swing;

import java.awt.EventQueue;
import java.util.HashMap;
import java.util.concurrent.Callable;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import piero.aldinucci.apt.bookstore.app.guice.modules.BookstoreControllerSwingModule;
import piero.aldinucci.apt.bookstore.app.guice.modules.BookstoreManagerJPAModule;
import piero.aldinucci.apt.bookstore.controller.BookstoreControllerImpl;
import piero.aldinucci.apt.bookstore.view.swing.AuthorSwingView;
import piero.aldinucci.apt.bookstore.view.swing.BookSwingView;
import piero.aldinucci.apt.bookstore.view.swing.BookstoreSwingFrame;

/**
 * 
 * @author Piero Aldinucci
 *
 */
@Command(mixinStandardHelpOptions = true)
public class BookstoreSwingApp implements Callable<Void> {

	private static final Logger LOGGER = LogManager.getLogger();
	
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
	
	@Option(names = {"-c", "--create" }, description = { "Create database tables if not present" })
	private boolean createDb;

	private EntityManagerFactory emFactory;

	/**
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		new CommandLine(new BookstoreSwingApp()).execute(args);
	}
	
	/**
	 * 
	 * @return an instance of the persistence context interface
	 */
	private EntityManagerFactory getEntityManagerFactory() {
		String propertyJdbcUrl = "jdbc:postgresql://" + host +
				":" + port + "/" + databaseName;
		
		HashMap<String, String> propertiesJPA = new HashMap<>();
		propertiesJPA.put("javax.persistence.jdbc.url", propertyJdbcUrl);
		
		if (userName != null)
			propertiesJPA.put("javax.persistence.jdbc.user", userName);
		
		if(password != null) 
			propertiesJPA.put("javax.persistence.jdbc.password", password);
		
		if (createDb)
			propertiesJPA.put("javax.persistence.schema-generation.database.action", "create");
		
		return Persistence.createEntityManagerFactory("apt.project.bookstore", propertiesJPA);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {			
			try {
				emFactory = getEntityManagerFactory();
				Injector injector = Guice.createInjector(
						new BookstoreControllerSwingModule(
						new BookstoreManagerJPAModule(emFactory)));
				
				BookstoreControllerImpl controller = injector
						.getInstance(BookstoreControllerImpl.class);
				BookstoreSwingFrame frame = new BookstoreSwingFrame(
						(AuthorSwingView)controller.getAuthorView(),
						(BookSwingView)controller.getBookView());
				controller.allAuthors();
				controller.allBooks();
				frame.setVisible(true);
			} catch (Exception e) {
				LOGGER.fatal(e);
				emFactory.close();
			}
		});
		return null;
	}

}
