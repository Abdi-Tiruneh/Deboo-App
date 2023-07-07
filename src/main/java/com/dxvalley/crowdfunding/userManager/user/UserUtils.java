package com.dxvalley.crowdfunding.userManager.user;

import com.dxvalley.crowdfunding.exception.customException.*;
import com.dxvalley.crowdfunding.messageManager.email.EmailService;
import com.dxvalley.crowdfunding.messageManager.sms.SmsService;
import com.dxvalley.crowdfunding.userManager.role.Role;
import com.dxvalley.crowdfunding.userManager.role.RoleService;
import com.dxvalley.crowdfunding.userManager.userDTO.UserRegistrationReq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserUtils {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final DateTimeFormatter dateTimeFormatter;
    private final EmailService emailService;
    private final SmsService smsService;

    public void validateUsernameAvailability(String username) {
        if (this.userRepository.findByUsername(username).isPresent()) {
            throw new ResourceAlreadyExistsException("There is already a user with " + username);
        }
    }

    public void validateUsername(String username) {
        if (!this.emailService.isValidEmail(username) && !this.smsService.isValidPhoneNumber(username)) {
            throw new BadRequestException("Username is neither a valid email nor a valid phone number.");
        }
    }

    public Users createUser(String username, UserRegistrationReq userRegistrationReq) {
        Role role = this.roleService.getRoleByName("USER");
        String encodedPassword = this.passwordEncoder.encode(userRegistrationReq.getPassword());
        return Users.builder().username(username).fullName(userRegistrationReq.getFullName()).password(encodedPassword).userStatus(UserStatus.ACTIVE).email(this.emailService.isValidEmail(username) ? username : null).role(role).createdAt(LocalDateTime.now().format(this.dateTimeFormatter)).build();
    }

    public Users utilGetUserByUserId(Long userId) {
        Users user = (Users)this.userRepository.findById(userId).orElseThrow(() -> {
            return new ResourceNotFoundException("There is no user with this Id");
        });
        return user;
    }

    public Users utilGetUserByUsername(String username) {
        Users user = (Users)this.userRepository.findByUsername(username).orElseThrow(() -> {
            return new ResourceNotFoundException("There is no user with this username.");
        });
        return user;
    }

    public Users saveUser(Users user) {
        return (Users)this.userRepository.save(user);
    }

    public void delete(String username) {
        Users user = this.utilGetUserByUsername(username);
        this.userRepository.delete(user);
    }

    public void verifyUser(Users user) {
        if (!user.isVerified()) {
            throw new ForbiddenException("User is not verified");
        }
    }

    public void verifyUserEmail(Users user) {
        if (user.getEmail() == null) {
            throw new BadRequestException("User email is required for future communication.");
        }
    }

    public void checkAccountActive(Users user) {
        if (user.getUserStatus() == UserStatus.BANNED) {
            throw new BannedUserException("The user account is currently banned.");
        }
    }
}
