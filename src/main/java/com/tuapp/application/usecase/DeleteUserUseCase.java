package com.tuapp.application.usecase;

import com.tuapp.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteUserUseCase {

    private final UserRepository userRepository;

    public DeleteUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void execute(Long userId, boolean deleteDiagnostics) {
        userRepository.deleteById(userId, deleteDiagnostics);
    }
}