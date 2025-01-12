package com.ascend.library.service;

import com.ascend.library.model.Book;
import com.ascend.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookService {
    @Autowired
    BookRepository bookRepository;

    @Autowired
    ValidationService validationService;

    public List<Book> getBooksByAuthor (String author){
        return bookRepository.findByAuthor(author);
    }

    public Book saveBook(Book book){
        LocalDate validateDate = validationService.validateDate(book.getPublishedDate());
        book.setPublishedDate(validateDate);
        return bookRepository.save(book);
    }
}
