package com.ascend.library.controller;

import com.ascend.library.model.Book;
import com.ascend.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll(); // Clear database before each test
    }

    // GET /api/books Tests
    @Test
    public void shouldReturnBooksByAuthor() throws Exception {
        bookRepository.save(new Book("Book 1", "Author1", LocalDate.of(2023, 1, 1)));
        bookRepository.save(new Book("Book 2", "Author1", LocalDate.of(2022, 5, 5)));

        mockMvc.perform(get("/api/books?author=Author1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    public void shouldReturn404IfNoBooksForAuthor() throws Exception {
        mockMvc.perform(get("/api/books?author=Unknown Author"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("No books found for this author"));
    }

    @Test
    public void shouldReturnBadRequestForEmptyAuthorQueryParam() throws Exception {
        mockMvc.perform(get("/api/books?author="))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
    }

    // POST /api/books Tests
    @Test
    public void shouldSaveBookSuccessfully() throws Exception {
        String bookJson = """
            {
                "title": "New Book",
                "author": "Author1",
                "publishedDate": "2023-01-01"
            }
            """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Book saved successfully"));

        assertThat(bookRepository.findAll()).hasSize(1);
    }

    @Test
    public void shouldConvertBuddhistDateToGregorian() throws Exception {
        String bookJson = """
            {
                "title": "Book with Buddhist Date",
                "author": "Author1",
                "publishedDate": "2568-01-01"
            }
            """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.publishedDate").value("2025-01-01"));
    }

    @Test
    public void shouldReturnValidationErrorsForEmptyFields() throws Exception {
        String invalidBookJson = """
            {
                "title": "",
                "author": "",
                "publishedDate": "2023-01-01"
            }
            """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBookJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.title").value("Title is required"))
                .andExpect(jsonPath("$.data.author").value("Author is required"));
    }

    @Test
    public void shouldReturnErrorForFutureDate() throws Exception {
        String invalidDateBookJson = """
            {
                "title": "Future Book",
                "author": "Author1",
                "publishedDate": "3000-01-01"
            }
            """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDateBookJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Invalid date should be before or equal current date."));
    }


    @Test
    public void shouldReturnEmptyListIfDatabaseIsEmpty() throws Exception {
        mockMvc.perform(get("/api/books?author=Author1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("No books found for this author"));
    }

    @Test
    public void shouldHandleLargeNumberOfBooks() throws Exception {
        for (int i = 0; i < 1000; i++) {
            bookRepository.save(new Book("Book " + i, "Author1", LocalDate.of(2020, 1, 1)));
        }

        mockMvc.perform(get("/api/books?author=Author1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1000));
    }
}
