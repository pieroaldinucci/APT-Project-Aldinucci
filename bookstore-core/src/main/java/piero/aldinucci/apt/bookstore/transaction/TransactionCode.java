package piero.aldinucci.apt.bookstore.transaction;

import java.util.function.BiFunction;

import piero.aldinucci.apt.bookstore.repositories.AuthorRepository;
import piero.aldinucci.apt.bookstore.repositories.BookRepository;

/**
 * 
 * @author Piero Aldinucci
 *
 * @param <R> The type of the result of TransactionCode
 */

@FunctionalInterface
public interface TransactionCode<R> extends BiFunction<AuthorRepository, BookRepository, R>{

}
