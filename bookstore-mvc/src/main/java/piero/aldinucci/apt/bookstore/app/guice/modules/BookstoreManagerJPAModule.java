package piero.aldinucci.apt.bookstore.app.guice.modules;

import javax.persistence.EntityManagerFactory;

import com.google.inject.AbstractModule;

import piero.aldinucci.apt.bookstore.repositories.factory.RepositoriesJPAFactory;
import piero.aldinucci.apt.bookstore.repositories.factory.RepositoriesJPAFactoryImpl;
import piero.aldinucci.apt.bookstore.service.BookstoreManager;
import piero.aldinucci.apt.bookstore.service.BookstoreManagerImpl;
import piero.aldinucci.apt.bookstore.transaction.TransactionManager;
import piero.aldinucci.apt.bookstore.transaction.TransactionManagerJPA;

/**
 * This module is used to get an instance of BookstoreManagerImpl
 * 
 * @author Piero Aldinucci
 *
 */
public class BookstoreManagerJPAModule extends AbstractModule{
	
	private EntityManagerFactory emFactory;

	/**
	 * 
	 * @param emFactory needed to create an instance of BookstoreManagerImpl
	 */
	public BookstoreManagerJPAModule(EntityManagerFactory emFactory) {
		super();
		this.emFactory = emFactory;
	}
	
	@Override
	protected void configure() {
		bind(TransactionManager.class).to(TransactionManagerJPA.class);
		bind(EntityManagerFactory.class).toInstance(emFactory);
		bind(RepositoriesJPAFactory.class).to(RepositoriesJPAFactoryImpl.class);
		bind(BookstoreManager.class).to(BookstoreManagerImpl.class);
	}
}
