
package com.library.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.library.service.BookService;
import com.library.dto.BookResponse;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.doThrow;

import java.util.List;
import com.library.exception.ResourceNotFoundException;
import org.springframework.http.MediaType;
import com.library.dto.BookRequest;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void shouldGetAllBooks() throws Exception {

        when(bookService.getAllBooks())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldReturn404WhenBookDoesNotExist() throws Exception {

        when(bookService.getBookById(999L))
                .thenThrow(new ResourceNotFoundException("Book not found"));

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    
    @Test
    void shouldGetBookById() throws Exception {
    
        BookResponse response = new BookResponse(
                1L,
                "Clean Code",
                "978-0132350884",
                2008,
                10,
                8,
                1L,
                1L
        );
    
        when(bookService.getBookById(1L))
                .thenReturn(response);
    
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.isbn").value("978-0132350884"))
                .andExpect(jsonPath("$.availableCopies").value(8))
                .andExpect(jsonPath("$.authorId").value(1))
                .andExpect(jsonPath("$.categoryId").value(1));
    }

    @Test
    void shouldCreateBook() throws Exception {
    
        BookResponse response = new BookResponse(
                1L,
                "Clean Code",
                "978-0132350884",
                2008,
                10,
                8,
                1L,
                1L
        );
    
        when(bookService.createBook(org.mockito.ArgumentMatchers.any(BookRequest.class)))
                .thenReturn(response);
    
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "Clean Code",
                            "isbn": "978-0132350884",
                            "publicationYear": 2008,
                            "totalCopies": 10,
                            "availableCopies": 8,
                            "authorId": 1,
                            "categoryId": 1
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.availableCopies").value(8));
    }

    @Test
    void shouldReturn400WhenCreatingInvalidBook() throws Exception {

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "",
                            "isbn": "978-0132350884",
                            "publicationYear": 2008,
                            "totalCopies": -1,
                            "availableCopies": 0,
                            "authorId": 1,
                            "categoryId": 1
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenCreatingBookWithNonExistingAuthor() throws Exception {

        when(bookService.createBook(org.mockito.ArgumentMatchers.any(BookRequest.class)))
                .thenThrow(new ResourceNotFoundException("Author not found"));

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "Clean Code",
                            "isbn": "978-0132350884",
                            "publicationYear": 2008,
                            "totalCopies": 10,
                            "availableCopies": 8,
                            "authorId": 999,
                            "categoryId": 1
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Author not found"));
    }

    @Test
    void shouldReturn400WhenCreatingBookWithInvalidCopies() throws Exception {

        when(bookService.createBook(org.mockito.ArgumentMatchers.any(BookRequest.class)))
                .thenThrow(new IllegalArgumentException(
                        "Available copies cannot be greater than total copies"));

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "Clean Code",
                            "isbn": "978-0132350884",
                            "publicationYear": 2008,
                            "totalCopies": 5,
                            "availableCopies": 10,
                            "authorId": 1,
                            "categoryId": 1
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Available copies cannot be greater than total copies"));
    }

    @Test
    void shouldUpdateBook() throws Exception {

        BookResponse response = new BookResponse(
                1L,
                "Clean Code Updated",
                "978-0132350884",
                2008,
                15,
                12,
                1L,
                1L);

        when(bookService.updateBook(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any(BookRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "Clean Code Updated",
                            "isbn": "978-0132350884",
                            "publicationYear": 2008,
                            "totalCopies": 15,
                            "availableCopies": 12,
                            "authorId": 1,
                            "categoryId": 1
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code Updated"))
                .andExpect(jsonPath("$.totalCopies").value(15))
                .andExpect(jsonPath("$.availableCopies").value(12));
    }

    @Test
    void shouldReturn400WhenUpdatingInvalidBook() throws Exception {

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "",
                            "isbn": "978-0132350884",
                            "publicationYear": 2008,
                            "totalCopies": -1,
                            "availableCopies": 0,
                            "authorId": 1,
                            "categoryId": 1
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenUpdatingBookThatDoesNotExist() throws Exception {

        when(bookService.updateBook(
                org.mockito.ArgumentMatchers.eq(999L),
                org.mockito.ArgumentMatchers.any(BookRequest.class)))
                .thenThrow(new ResourceNotFoundException("Book not found"));

        mockMvc.perform(put("/api/books/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "Clean Code",
                            "isbn": "978-0132350884",
                            "publicationYear": 2008,
                            "totalCopies": 10,
                            "availableCopies": 8,
                            "authorId": 1,
                            "categoryId": 1
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    void shouldDeleteBook() throws Exception {

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        org.mockito.Mockito.verify(bookService)
                .deleteBook(1L);
    }

    @Test
    void shouldReturn404WhenDeletingBookThatDoesNotExist() throws Exception {
    
        doThrow(new ResourceNotFoundException("Book not found"))
                .when(bookService)
                .deleteBook(999L);
    
        mockMvc.perform(delete("/api/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }
}