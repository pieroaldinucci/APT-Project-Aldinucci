package piero.aldinucci.apt.bookstore.model;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

public class FakeAuthorTest {

	@Test
	public void test() {
		Author author = new Author(3L, "a name", new HashSet<Book>());

		assertEquals(author.getName(), "a name");
	}

}
