package com.example.library_management_system.repository;

import com.example.library_management_system.entity.Book;
import com.example.library_management_system.entity.BorrowingHistory;
import com.example.library_management_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingHistoryRepository extends JpaRepository<BorrowingHistory, Long> {
    List<BorrowingHistory> findByUser(User user);

    List<BorrowingHistory> findAll();
    Optional<BorrowingHistory> findTopByBookAndReturnedAtIsNullOrderByBorrowedAtDesc(Book book);
}
