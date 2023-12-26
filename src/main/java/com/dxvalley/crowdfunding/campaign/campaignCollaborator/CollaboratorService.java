package com.dxvalley.crowdfunding.campaign.campaignCollaborator;

import com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto.CollaborationReq;
import com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto.CollaboratorResponse;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CollaboratorService {
    CollaboratorResponse getCollaboratorById(Long collaboratorId);

    List<CollaboratorResponse> getCollaboratorByCampaignId(Long campaignId);

    CollaboratorResponse sendInvitation(CollaborationReq collaborationReq);

    ResponseEntity<ApiResponse> respondToCollaborationInvitation(Long collaboratorId, boolean accepted);

    ResponseEntity<ApiResponse> deleteCollaborator(Long collaboratorId);
}

