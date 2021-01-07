package piero.aldinucci.apt.bookstore.transaction.module;

import com.google.inject.AbstractModule;

import piero.aldinucci.apt.bookstore.repositories.AuthorJPARepository;
import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookJPARepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;

public class TansactionJPAGuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AuthorRepository.class).to(AuthorJPARepository.class);
		bind(BookRepository.class).to(BookJPARepository.class);
	}
}
