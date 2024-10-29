package com.example.library_management_system.controller;

import com.example.library_management_system.entity.BorrowingHistory;
import com.example.library_management_system.service.BorrowingHistoryService;
import com.example.library_management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowing-history")
@CrossOrigin(origins = "http://localhost:3000")
public class BorrowingHistoryController {

    @Autowired
    private BorrowingHistoryService borrowingHistoryService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<BorrowingHistory>> getAllBorrowingHistory() {
        List<BorrowingHistory> historyList = borrowingHistoryService.getAllBorrowingHistory();

        if (historyList.isEmpty()) {
            return ResponseEntity.noContent().build();  // No content if history is empty
        } else {
            return ResponseEntity.ok(historyList);  // Return all borrowing history
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserBorrowingHistory(@PathVariable Long userId) {
        List<BorrowingHistory> history = borrowingHistoryService.getBorrowingHistoryByUserId(userId);
        if (history.isEmpty()) {
            return ResponseEntity.ok("No borrowing history found for the user with ID: " + userId);
        } else {
            return ResponseEntity.ok(history);
        }
    }
}
