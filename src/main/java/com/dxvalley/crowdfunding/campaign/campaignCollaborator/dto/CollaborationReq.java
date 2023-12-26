package com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CollaborationRequest {
    @NotBlank
    private String collaboratorFullName;

    @NotBlank(message = "An email for the collaborator must be provided.")
    @Email
    private String collaboratorEmail;

    @NotNull(message = "Campaign ID must be provided.")
    private Long campaignId;
}