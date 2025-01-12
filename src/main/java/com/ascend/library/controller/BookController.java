package com.ascend.library.controller;


import com.ascend.library.model.ApiResponse;
import com.ascend.library.model.Book;
import com.ascend.library.model.BookRequest;
import com.ascend.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    BookService bookService;

    @GetMapping
    public ResponseEntity<ApiResponse> getBooksByAuthor(@RequestParam String author) {
        List<Book> books = bookService.getBooksByAuthor(author);
        if (books.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse("error", "No books found for this author", null));
        }
        return ResponseEntity.ok(new ApiResponse("success", "Books found", books));
    }

    @PostMapping
    public ResponseEntity<ApiResponse>saveBook(@RequestBody @Validated BookRequest bookRequest) {
        Book book = new Book(bookRequest.getTitle(),bookRequest.getAuthor(),LocalDate.parse(bookRequest.getPublishedDate()));
        try {
            Book savedBook = bookService.saveBook(book);
            return ResponseEntity.ok(new ApiResponse("success", "Book saved successfully", savedBook));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage(), null));
        }
    }
}

