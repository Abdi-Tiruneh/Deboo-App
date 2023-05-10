package com.dxvalley.crowdfunding.tokenManager;

import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.messageManager.email.EmailService;
import com.dxvalley.crowdfunding.messageManager.sms.SmsService;
import com.dxvalley.crowdfunding.user.UserRepository;
import com.dxvalley.crowdfunding.user.userRole.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SmsService smsService;
    @Autowired
    private DateTimeFormatter dateTimeFormatter;

    @Override
    public ConfirmationToken saveConfirmationToken(Users user, String token, int expirationTimeInMinutes) {
        try {
            ConfirmationToken confirmationToken = new ConfirmationToken();
            confirmationToken.setUser(user);
            confirmationToken.setToken(token);
            confirmationToken.setCreatedAt(LocalDateTime.now().format(dateTimeFormatter));
            confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(expirationTimeInMinutes).format(dateTimeFormatter));
            return confirmationTokenRepository.save(confirmationToken);
        } catch (RuntimeException ex) {
            log.error("Error saving confirmation token : {}", ex.getMessage());
            throw new RuntimeException("Error saving confirmation token", ex);
        }
    }

    @Override
    public ConfirmationToken getToken(String token) {
        return confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Token"));
    }

    @Override
    public void sendConfirmationToken(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with this username."));

        if (emailService.isValidEmail(username)) {
            String token = UUID.randomUUID().toString();
            String link = "http://10.1.177.121/verify/" + token;

            emailService.send(
                    username,
                    emailService.emailBuilderForUserConfirmation(user.getFullName(), link),
                    "Confirm your email");

            saveConfirmationToken(user, token, 30);
        } else {
            String code = String.format("%06d", new Random().nextInt(999999));
            smsService.sendOtp(user.getUsername(), code);
            saveConfirmationToken(user, code, 3);
        }
    }
}