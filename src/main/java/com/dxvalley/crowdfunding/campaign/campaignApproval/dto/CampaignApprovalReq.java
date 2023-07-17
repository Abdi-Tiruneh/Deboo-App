package com.dxvalley.crowdfunding.campaign.campaignApproval.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class CampaignApprovalReq {
    @NotNull(message = "Campaign ID is required")
    private Long campaignId;
    @NotBlank(message = "Approved by field is required")
    private String approvedBy;
    @NotNull(message = "Approval status is required")
    @Pattern(regexp = "(?i)^(ACCEPTED|REJECTED|RESUBMIT)$", message = "Approval status must be either accepted, rejected, or resubmit.")
    private String approvalStatus;
    @NotBlank(message = "Reason is required")
    private String reason;
    private Double commissionRate;
    private List<MultipartFile> files;
}
