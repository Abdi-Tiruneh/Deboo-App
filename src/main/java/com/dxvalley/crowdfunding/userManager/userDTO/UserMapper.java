package com.dxvalley.crowdfunding.userManager.userDTO;

import com.dxvalley.crowdfunding.userManager.user.Users;

public class UserMapper {

    public static UserResponse toUserResponse(Users user) {
        return UserResponse.builder().userId(user.getUserId()).username(user.getUsername()).fullName(user.getFullName())
                .address(user.getAddress())
                .email(user.getEmail())
                .verified(user.isVerified() ? "YES" : "NO")
                .userStatus(user.getUserStatus())
                .createdAt(user.getCreatedAt())
                .editedAt(user.getEditedAt())
                .avatarUrl(user.getAvatarUrl())
                .biography(user.getBiography())
                .contributions(user.getContributions())
                .amountSpentInBirr(user.getTotalAmountSpent())
                .build();
    }
}

