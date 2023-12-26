package com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CollaborationReq {
    @NotBlank
    private String collaboratorFullName;

    @NotBlank
    @Email
    private String collaboratorEmail;

    @NotNull
    private Long campaignId;
}