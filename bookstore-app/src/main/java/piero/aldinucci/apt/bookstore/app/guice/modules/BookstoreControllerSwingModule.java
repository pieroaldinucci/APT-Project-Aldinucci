package piero.aldinucci.apt.bookstore.app.guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import piero.aldinucci.apt.bookstore.controller.BookstoreControllerImpl;
import piero.aldinucci.apt.bookstore.service.BookstoreManager;
import piero.aldinucci.apt.bookstore.view.AuthorView;
import piero.aldinucci.apt.bookstore.view.BookView;
import piero.aldinucci.apt.bookstore.view.ComposeBookView;
import piero.aldinucci.apt.bookstore.view.factory.ViewsFactory;
import piero.aldinucci.apt.bookstore.view.swing.AuthorSwingView;
import piero.aldinucci.apt.bookstore.view.swing.BookSwingView;
import piero.aldinucci.apt.bookstore.view.swing.ComposeBookSwingView;

/**
 * This module is used to create an instance of BookstoreControllerImpl
 * and to automatically resolve its circular dependencies.
 * 
 * @author Piero Aldinucci
 *
 */
public class BookstoreControllerSwingModule extends AbstractModule{
	
	private AbstractModule serviceLayerModule;

	/**
	 * 
	 * @param serviceLayerModule a module that can create an instance of BookStoreManager
	 */
	public BookstoreControllerSwingModule(AbstractModule serviceLayerModule) {
		super();
		this.serviceLayerModule = serviceLayerModule;
	}
	
	@Override
	protected void configure() {
		install(new FactoryModuleBuilder()
			.implement(AuthorView.class, AuthorSwingView.class)
			.implement(BookView.class, BookSwingView.class)
			.implement(ComposeBookView.class, ComposeBookSwingView.class)
			.build(ViewsFactory.class));
	}
	
	@Provides
	BookstoreControllerImpl getController(ViewsFactory viewsFactory) {
		Injector injector = Guice.createInjector(serviceLayerModule);
		BookstoreManager bookstoreManager = injector.getInstance(BookstoreManager.class);
		
		BookstoreControllerImpl controller = new BookstoreControllerImpl(bookstoreManager);
		controller.setAuthorView(viewsFactory.createAuthorView(controller));
		controller.setBookView(viewsFactory.createBookView(controller));
		controller.setComposeBookView(viewsFactory.createComposeBookView(controller));
		return controller;
	}
}
