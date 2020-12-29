package piero.aldinucci.apt.bookstore.transaction;

import java.util.function.BiFunction;

import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;

@FunctionalInterface
public interface TransactionCode<R> extends BiFunction<AuthorRepository, BookRepository, R>{

}
