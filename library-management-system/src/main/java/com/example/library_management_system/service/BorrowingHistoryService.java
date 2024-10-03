package com.example.library_management_system.service;

import com.example.library_management_system.entity.BorrowingHistory;
import com.example.library_management_system.entity.User;
import com.example.library_management_system.repository.BorrowingHistoryRepository;
import com.example.library_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowingHistoryService {

    private final UserRepository userRepository;
    private final BorrowingHistoryRepository borrowingHistoryRepository;

    @Autowired
    public BorrowingHistoryService(UserRepository userRepository, BorrowingHistoryRepository borrowingHistoryRepository) {
        this.userRepository = userRepository;
        this.borrowingHistoryRepository = borrowingHistoryRepository;
    }

    public List<BorrowingHistory> getBorrowingHistoryByUserId(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return borrowingHistoryRepository.findByUser(userOpt.get());
        }
        return new ArrayList<>();  // Return an empty list if the user is not found
    }

    public List<BorrowingHistory> getAllBorrowingHistory() {
        return borrowingHistoryRepository.findAll();
    }
}