package com.dxvalley.crowdfunding.security.service;

import com.dxvalley.crowdfunding.exception.customException.BannedUserException;
import com.dxvalley.crowdfunding.userManager.user.UserRepository;
import com.dxvalley.crowdfunding.userManager.user.UserStatus;
import com.dxvalley.crowdfunding.userManager.user.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final DateTimeFormatter dateTimeFormatter;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("There is no user with this username")
        );

        if (user.getUserStatus().equals(UserStatus.BANNED))
            throw new BannedUserException("Your account has been temporarily banned. Please admins for further assistance.");

        Collection<SimpleGrantedAuthority> authorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities
        );
    }

    public void updateLastLogin(String username){
        Optional<Users> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            Users adminUser = user.get();
            adminUser.setLastLogin(LocalDateTime.now().format(dateTimeFormatter));
            userRepository.save(adminUser);
        }
    }
}