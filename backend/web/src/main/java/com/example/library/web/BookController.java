package com.example.library.web;

import com.example.library.models.Author;
import com.example.library.models.Book;
import com.example.library.service.AuthorService;
import com.example.library.service.BookService;
import com.example.library.web.exceptions.AuthorNotFoundException;
import com.example.library.web.exceptions.BookNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping({"/books", "/"})
public class BookController {
    private final BookService bookService;
    private final AuthorService authorService;

    public BookController(BookService bookService, AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = this.bookService.findAll();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/pagination")
    public Page<Book> getAllBooksWithPagination(Pageable pageable) {
        return this.bookService.findAllWithPagination(pageable);
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book, @RequestParam Long authorId) {
        Author author = this.authorService.findById(authorId).orElseThrow(() -> new AuthorNotFoundException(authorId));
        try {
            Book _book =
                    this.bookService.save(book.getName(), book.getCategory(), author, book.getCopies());
            return new ResponseEntity<>(book, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> findBookById(@PathVariable("id") long id) {
        try {
            Book book = this.bookService.findById(id).orElseThrow(() -> new BookNotFoundException(id));
            return new ResponseEntity<>(book, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<Book>> findBooksByCategory(@PathVariable("category") String category) {
        try {
            List<Book> books = this.bookService.findByCategory(category);
            return new ResponseEntity<>(books, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteBookById(@PathVariable("id") long id) {
        this.bookService.deleteBookById(id);
        if (this.bookService.findById(id).isEmpty()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/take/{id}")
    public ResponseEntity<Book> markBookAsTaken(@PathVariable("id") long id) {
        try {
            Book book = this.bookService.findById(id).orElseThrow(() -> new BookNotFoundException(id));
            book.setCopies(book.getCopies() - 1);
            this.bookService.saveBook(book);
            return new ResponseEntity<>(book, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Book> edit(@PathVariable Long id, @RequestBody Book book) {
        try {
            Book book1 = this.bookService.edit(id, book);
            return new ResponseEntity<>(book1, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
