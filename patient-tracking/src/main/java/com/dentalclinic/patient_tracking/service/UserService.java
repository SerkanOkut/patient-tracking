package com.dentalclinic.patient_tracking.service;

import com.dentalclinic.patient_tracking.dto.UserDTO;
import com.dentalclinic.patient_tracking.entity.User;
import com.dentalclinic.patient_tracking.enums.Role;
import com.dentalclinic.patient_tracking.exception.DuplicateException;
import com.dentalclinic.patient_tracking.exception.NotFoundException;
import com.dentalclinic.patient_tracking.repository.UserRepository;
import com.dentalclinic.patient_tracking.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserDTO registerUser(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateException("Email zaten kullanılıyor");
        }

        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole() != null ? dto.getRole() : Role.PATIENT)
                .isActive(true)
                .build();

        userRepository.save(user);
        return mapToDTO(user);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Email veya şifre hatalı");
        }

        return jwtUtil.generateToken(user);
    }

    @Transactional
    public UserDTO updateUser(Long userId, UserDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setActive(dto.isActive());

        userRepository.save(user);
        return mapToDTO(user);
    }

    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı"));
        return mapToDTO(user);
    }

    public List<UserDTO> listUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
