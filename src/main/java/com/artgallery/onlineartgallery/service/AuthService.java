package com.artgallery.onlineartgallery.service;

import com.artgallery.onlineartgallery.dto.AuthResponse;
import com.artgallery.onlineartgallery.dto.LoginRequest;
import com.artgallery.onlineartgallery.dto.UserResponse;
import com.artgallery.onlineartgallery.exception.ForbiddenException;
import com.artgallery.onlineartgallery.exception.UnauthorizedException;
import com.artgallery.onlineartgallery.model.AppUser;
import com.artgallery.onlineartgallery.model.UserRole;
import com.artgallery.onlineartgallery.repository.AppUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public static final String SESSION_USER_ID = "userId";

    private final AppUserRepository appUserRepository;

    public AuthService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AuthResponse login(LoginRequest request, HttpSession session) {
        AppUser user = appUserRepository.findByEmailIgnoreCase(request.email())
                .filter(existingUser -> existingUser.getPassword().equals(request.password()))
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        session.setAttribute(SESSION_USER_ID, user.getId());
        return new AuthResponse("Login successful", toResponse(user));
    }

    public AuthResponse currentUser(HttpSession session) {
        AppUser user = getCurrentUser(session);
        return new AuthResponse("Authenticated user loaded", toResponse(user));
    }

    public AuthResponse logout(HttpSession session) {
        session.invalidate();
        return new AuthResponse("Logged out successfully", null);
    }

    public AppUser requireUser(HttpSession session) {
        return getCurrentUser(session);
    }

    public AppUser requireRole(HttpSession session, UserRole role) {
        AppUser user = getCurrentUser(session);
        if (user.getRole() != role) {
            throw new ForbiddenException("This action requires " + role.name().toLowerCase() + " access");
        }
        return user;
    }

    private AppUser getCurrentUser(HttpSession session) {
        Object userId = session.getAttribute(SESSION_USER_ID);
        if (!(userId instanceof Long id)) {
            throw new UnauthorizedException("Please log in to continue");
        }

        return appUserRepository.findById(id)
                .orElseThrow(() -> new UnauthorizedException("Session user was not found"));
    }

    private UserResponse toResponse(AppUser user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
