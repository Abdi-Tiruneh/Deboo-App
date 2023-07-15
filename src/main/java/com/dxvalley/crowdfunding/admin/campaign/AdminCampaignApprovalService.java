package com.dxvalley.crowdfunding.admin.campaign;

import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.ApprovalResponse;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.CampaignApprovalReq;
import org.springframework.http.ResponseEntity;

public interface AdminCampaignApprovalService {
    ResponseEntity<String> approveCampaign(CampaignApprovalReq campaignApprovalReq);
    ApprovalResponse getCampaignApprovalByCampaignId(Long campaignId);
}
