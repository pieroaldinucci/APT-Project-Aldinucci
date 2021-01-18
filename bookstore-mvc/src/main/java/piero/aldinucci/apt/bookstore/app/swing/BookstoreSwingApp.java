package piero.aldinucci.apt.bookstore.app.swing;

import java.awt.EventQueue;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.inject.Guice;
import com.google.inject.Injector;

import piero.aldinucci.apt.bookstore.app.guice.modules.BookstoreControllerSwingModule;
import piero.aldinucci.apt.bookstore.app.guice.modules.BookstoreManagerJPAModule;
import piero.aldinucci.apt.bookstore.controller.BookstoreControllerImpl;
import piero.aldinucci.apt.bookstore.view.swing.AuthorSwingView;
import piero.aldinucci.apt.bookstore.view.swing.BookSwingView;
import piero.aldinucci.apt.bookstore.view.swing.BookstoreSwingFrame;

public class BookstoreSwingApp{
	
	public static void main(String[] args){
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
	}

	private static EntityManagerFactory getEntityManagerFactory() {
		return Persistence.createEntityManagerFactory("apt.project.bookstore");
	}
}
