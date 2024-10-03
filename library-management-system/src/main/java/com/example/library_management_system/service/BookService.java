package com.example.library_management_system.service;

import com.example.library_management_system.entity.Book;
import com.example.library_management_system.entity.BorrowingHistory;
import com.example.library_management_system.entity.User;
import com.example.library_management_system.repository.BookRepository;
import com.example.library_management_system.repository.BorrowingHistoryRepository;
import com.example.library_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowingHistoryRepository borrowingHistoryRepository;


    public BookService(BookRepository bookRepository, UserRepository userRepository, BorrowingHistoryRepository borrowingHistoryRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.borrowingHistoryRepository = borrowingHistoryRepository;
    }

    public List<Book> getFilteredBooks(String title, String author, Boolean available) {
        return bookRepository.findByFilter(title, author, available);
    }

    public Page<Book> getAllBooksPaginated(Pageable pageable){
        return bookRepository.findAll(pageable);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public Book borrowBook(Long bookId, Long userId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();

            // Check if the book is available
            if (!book.isAvailable()) {
                System.out.println("Book is already borrowed by user: " + book.getBorrowedBy().getId());
                return book;  // Return the book object to indicate it's already borrowed
            }

            // Fetch the user by userId
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Set the user as the borrower of the book
                book.setBorrowedBy(user);
                book.setAvailable(false);  // Mark as unavailable (borrowed)
                book.setBorrowedAt(LocalDateTime.now());  // Set the borrowed_at timestamp

                // Save the updated book
                bookRepository.save(book);

                // Create a BorrowingHistory record
                BorrowingHistory history = new BorrowingHistory();
                history.setBook(book);
                history.setUser(user);
                history.setBorrowedAt(LocalDateTime.now());  // Set the borrow date to now
                borrowingHistoryRepository.save(history);  // Save the borrowing history

                System.out.println("Book successfully borrowed by user: " + userId);
                return book;
            } else {
                System.out.println("User not found with id: " + userId);
                return null;
            }
        } else {
            System.out.println("Book not found with id: " + bookId);
            return null;
        }
    }
    public boolean returnBook(Long bookId, Long userId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();

            // Check if the book is already available (not borrowed)
            if (book.isAvailable()) {
                System.out.println("Book is not borrowed.");
                return false;  // Book is already available (not borrowed)
            }

            // Check if the current user is the one who borrowed the book
            if (book.getBorrowedBy() == null || !book.getBorrowedBy().getId().equals(userId)) {
                System.out.println("This book is not borrowed by the user with id: " + userId);
                return false;  // Return false if the user trying to return the book is not the one who borrowed it
            }

            // Set the book as available again
            book.setAvailable(true);
            book.setReturnedAt(LocalDateTime.now());  // Set the returned_at timestamp in the book

            // Find the borrowing history where the book is borrowed and not yet returned
            Optional<BorrowingHistory> historyOpt = borrowingHistoryRepository.findTopByBookAndReturnedAtIsNullOrderByBorrowedAtDesc(book);
            if (historyOpt.isPresent()) {
                BorrowingHistory history = historyOpt.get();
                history.setReturnedAt(LocalDateTime.now());  // Update returned_at only when the book is returned
                borrowingHistoryRepository.save(history);  // Save the updated history
            }

            // Clear the user who borrowed the book
            book.setBorrowedBy(null);

            // Save the updated book entity
            bookRepository.save(book);

            System.out.println("Book successfully returned.");
            return true;  // Return true indicating the book was returned successfully
        } else {
            System.out.println("Book not found with id: " + bookId);
            return false;  // Book not found
        }
    }
}
