package com.example.library_management_system.controller;

import com.example.library_management_system.entity.Book;
import com.example.library_management_system.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Book> getAllBooks(){
        return bookService.getAllBooks();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Book>> getFilteredBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Boolean available) {
        List<Book> filteredBooks = bookService.getFilteredBooks(title, author, available);
        return ResponseEntity.ok(filteredBooks);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Book>> getAllBooksPainated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.getAllBooksPaginated(pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id){
        Book book = bookService.getBookById(id);
        return book != null ? ResponseEntity.ok(book) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        Book createdBook = bookService.addBook(book);
        return ResponseEntity.ok(createdBook);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{bookId}/borrow/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> borrowBook(@PathVariable Long bookId, @PathVariable Long userId) {

        Book book = bookService.borrowBook(bookId, userId);

        if (book == null) {
            // If the book is not found, return a 404 response
            System.out.println("Book not found with id: " + bookId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Book not found with id: " + bookId);
        }

        // Fetch the updated state of the book after trying to borrow it
        if (book.getBorrowedBy() != null && !book.isAvailable()) {
            // Check if the current user is the one who borrowed the book
            if (book.getBorrowedBy().getId().equals(userId)) {
                // Return success if the current user is the borrower
                System.out.println("Book successfully borrowed by user: " + userId);
                return ResponseEntity.ok("Book successfully borrowed by user: " + userId);
            } else {
                // Return 409 conflict if another user has already borrowed the book
                System.out.println("Book is already borrowed by user: " + book.getBorrowedBy().getId());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Book is already borrowed by user: " + book.getBorrowedBy().getId());
            }
        }

        // If the book is now available, return a success message
        System.out.println("Book successfully borrowed by user: " + userId);
        return ResponseEntity.ok("Book successfully borrowed by user: " + userId);
    }

    @PutMapping("/{bookId}/return/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> returnBook(@PathVariable Long bookId, @PathVariable Long userId) {
        try {
            // Call the service to return the book
            boolean isReturned = bookService.returnBook(bookId, userId);

            if (isReturned) {
                return ResponseEntity.ok("Book successfully returned.");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Failed to return the book. It may not be borrowed by this user, or it has already been returned.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while trying to return the book. Please try again.");
        }
    }
}
