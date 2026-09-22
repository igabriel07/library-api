package com.library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.exception.ResourceNotFoundException;
import com.library.model.Author;
import com.library.model.Book;
import com.library.model.Category;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;


@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void shouldGetBookById() {

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setIsbn("978-0132350884");
        book.setPublicationYear(2008);
        book.setTotalCopies(10);
        book.setAvailableCopies(8);
        Author author = new Author();
		author.setId(1L);

		Category category = new Category();
		category.setId(1L);

		book.setAuthor(author);
		book.setCategory(category);

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        BookResponse response = bookService.getBookById(1L);

        assertEquals(1L, response.getId());
        assertEquals("Clean Code", response.getTitle());
        assertEquals("978-0132350884", response.getIsbn());
        assertEquals(2008, response.getPublicationYear());
        assertEquals(10, response.getTotalCopies());
        assertEquals(8, response.getAvailableCopies());
    }

	@Test
	void shouldRejectBookWhenAvailableCopiesExceedTotalCopies() {

		BookRequest request = new BookRequest();
		request.setTitle("Clean Code");
		request.setIsbn("978-0132350884");
		request.setPublicationYear(2008);
		request.setTotalCopies(5);
		request.setAvailableCopies(10);
		request.setAuthorId(1L);
		request.setCategoryId(1L);

		assertThrows(IllegalArgumentException.class, () -> {
			bookService.createBook(request);
		});
	}

	@Test
	void shouldThrowExceptionWhenBookDoesNotExist() {

		when(bookRepository.findById(999L))
				.thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			bookService.getBookById(999L);
		});
	}

    @Test
	void shouldCreateBook() {

		BookRequest request = new BookRequest();
		request.setTitle("Clean Code");
		request.setIsbn("978-0132350884");
		request.setPublicationYear(2008);
		request.setTotalCopies(10);
		request.setAvailableCopies(8);
		request.setAuthorId(1L);
		request.setCategoryId(1L);

		Author author = new Author();
		author.setId(1L);

		Category category = new Category();
		category.setId(1L);

		when(authorRepository.findById(1L))
				.thenReturn(Optional.of(author));

		when(categoryRepository.findById(1L))
				.thenReturn(Optional.of(category));

		Book savedBook = new Book();
		savedBook.setId(1L);
		savedBook.setTitle("Clean Code");
		savedBook.setIsbn("978-0132350884");
		savedBook.setPublicationYear(2008);
		savedBook.setTotalCopies(10);
		savedBook.setAvailableCopies(8);
		savedBook.setAuthor(author);
		savedBook.setCategory(category);

		when(bookRepository.save(org.mockito.ArgumentMatchers.any(Book.class)))
				.thenReturn(savedBook);
		
		BookResponse response = bookService.createBook(request);

		assertEquals(1L, response.getId());
		assertEquals("Clean Code", response.getTitle());
		assertEquals("978-0132350884", response.getIsbn());
		assertEquals(2008, response.getPublicationYear());
		assertEquals(10, response.getTotalCopies());
		assertEquals(8, response.getAvailableCopies());
		assertEquals(1L, response.getAuthorId());
		assertEquals(1L, response.getCategoryId());

		verify(bookRepository).save(org.mockito.ArgumentMatchers.any(Book.class));
	}

	@Test
	void shouldThrowExceptionWhenAuthorDoesNotExist() {

		BookRequest request = new BookRequest();
		request.setTitle("Clean Code");
		request.setIsbn("978-0132350884");
		request.setPublicationYear(2008);
		request.setTotalCopies(10);
		request.setAvailableCopies(8);
		request.setAuthorId(999L);
		request.setCategoryId(1L);

		when(authorRepository.findById(999L))
				.thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			bookService.createBook(request);
		});
	}

	@Test
	void shouldThrowExceptionWhenCategoryDoesNotExist() {

		BookRequest request = new BookRequest();
		request.setTitle("Clean Code");
		request.setIsbn("978-0132350884");
		request.setPublicationYear(2008);
		request.setTotalCopies(10);
		request.setAvailableCopies(8);
		request.setAuthorId(1L);
		request.setCategoryId(999L);

		Author author = new Author();
		author.setId(1L);

		when(authorRepository.findById(1L))
				.thenReturn(Optional.of(author));

		when(categoryRepository.findById(999L))
				.thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			bookService.createBook(request);
		});
	}

	@Test
	void shouldUpdateBook() {

		BookRequest request = new BookRequest();
		request.setTitle("Clean Code - Updated");
		request.setIsbn("978-0132350884");
		request.setPublicationYear(2008);
		request.setTotalCopies(15);
		request.setAvailableCopies(12);
		request.setAuthorId(1L);
		request.setCategoryId(1L);

		Book book = new Book();
		book.setId(1L);
		book.setTitle("Clean Code");
		book.setIsbn("978-0132350884");
		book.setPublicationYear(2008);
		book.setTotalCopies(10);
		book.setAvailableCopies(8);

		Author author = new Author();
		author.setId(1L);

		Category category = new Category();
		category.setId(1L);

		book.setAuthor(author);
		book.setCategory(category);

		when(bookRepository.findById(1L))
				.thenReturn(Optional.of(book));

		when(authorRepository.findById(1L))
				.thenReturn(Optional.of(author));

		when(categoryRepository.findById(1L))
				.thenReturn(Optional.of(category));

		when(bookRepository.save(book))
				.thenReturn(book);

		BookResponse response = bookService.updateBook(1L, request);

		assertEquals(1L, response.getId());
		assertEquals("Clean Code - Updated", response.getTitle());
		assertEquals(15, response.getTotalCopies());
		assertEquals(12, response.getAvailableCopies());
		assertEquals(1L, response.getAuthorId());
		assertEquals(1L, response.getCategoryId());
	}

	@Test
	void shouldThrowExceptionWhenUpdatingBookThatDoesNotExist() {

		BookRequest request = new BookRequest();
		request.setTitle("Clean Code");
		request.setIsbn("978-0132350884");
		request.setPublicationYear(2008);
		request.setTotalCopies(10);
		request.setAvailableCopies(8);
		request.setAuthorId(1L);
		request.setCategoryId(1L);

		when(bookRepository.findById(999L))
				.thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			bookService.updateBook(999L, request);
		});
	}

	@Test
	void shouldRejectUpdatedBookWhenAvailableCopiesExceedTotalCopies() {

		BookRequest request = new BookRequest();
		request.setTitle("Clean Code");
		request.setIsbn("978-0132350884");
		request.setPublicationYear(2008);
		request.setTotalCopies(5);
		request.setAvailableCopies(10);
		request.setAuthorId(1L);
		request.setCategoryId(1L);

		Book book = new Book();
		book.setId(1L);

		when(bookRepository.findById(1L))
				.thenReturn(Optional.of(book));

		assertThrows(IllegalArgumentException.class, () -> {
			bookService.updateBook(1L, request);
		});
	}

	@Test
	void shouldDeleteBook() {

		Book book = new Book();
		book.setId(1L);

		when(bookRepository.findById(1L))
				.thenReturn(Optional.of(book));

		bookService.deleteBook(1L);

		verify(bookRepository).delete(book);
	}

	@Test
	void shouldThrowExceptionWhenDeletingBookThatDoesNotExist() {

		when(bookRepository.findById(999L))
				.thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			bookService.deleteBook(999L);
		});
	}
}