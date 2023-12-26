package com.dxvalley.crowdfunding.campaign.campaignCollaborator;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto.CollaborationReq;
import com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto.CollaboratorResponse;
import com.dxvalley.crowdfunding.exception.customException.BadRequestException;
import com.dxvalley.crowdfunding.exception.customException.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.messageManager.email.EmailBuilder;
import com.dxvalley.crowdfunding.messageManager.email.EmailServiceImpl;
import com.dxvalley.crowdfunding.userManager.user.UserUtils;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CollaboratorServiceImpl implements CollaboratorService {
    private final CollaboratorRepository collaboratorRepository;
    private final UserUtils userUtils;
    private final EmailServiceImpl emailService;
    private final DateTimeFormatter dateTimeFormatter;
    private final CampaignUtils campaignUtils;
    @Value("${appUrl.collaborator}")
    private String invitationLink;

    public List<CollaboratorResponse> getCollaboratorByCampaignId(Long campaignId) {
        List<Collaborator> collaborators = collaboratorRepository.findCollaboratorsByCampaignIdAndAccepted(campaignId, true);
        return collaborators.stream().map(CollaboratorResponse::toCollResponse).toList();
    }

    public CollaboratorResponse getCollaboratorById(Long id) {
        return CollaboratorResponse.toCollResponse(utilGetCollaboratorById(id));
    }


    public CollaboratorResponse sendInvitation(CollaborationReq collaborationReq) {
        Long campaignId = collaborationReq.getCampaignId();
        String collaboratorEmail = collaborationReq.getCollaboratorEmail();
        String collaboratorFullName = collaborationReq.getCollaboratorFullName();

        Campaign campaign = campaignUtils.getCampaignById(campaignId);

        Collaborator collaborator = collaboratorRepository.findByCampaignIdAndCollaboratorEmail(campaignId, collaboratorEmail)
                .orElseGet(() -> {
                    return Collaborator.builder()
                            .collaboratorEmail(collaboratorEmail)
                            .collaboratorFullName(collaboratorFullName)
                            .campaign(campaign)
                            .build();
                });

        LocalDateTime invitationSentAt = LocalDateTime.now();
        LocalDateTime expiredAt = invitationSentAt.plusDays(7L);

        collaborator.setInvitationSentAt(invitationSentAt.format(dateTimeFormatter));
        collaborator.setInvitationExpiredAt(expiredAt.format(dateTimeFormatter));
        collaborator.setCollaboratorFullName(collaboratorFullName);
        collaborator = collaboratorRepository.save(collaborator);

        String inviterFullName = campaign.getUser().getFullName();
        String invitationDetailLink = invitationLink + collaborator.getId() + "/campaign/" + campaignId;

        emailService.send(collaboratorEmail, EmailBuilder.buildCollaborationInvitationEmail(collaboratorFullName, inviterFullName, campaign.getTitle(), invitationDetailLink), "Asking for Collaboration");

        return CollaboratorResponse.toCollResponse(collaborator);
    }


    public ResponseEntity<ApiResponse> respondToCollaborationInvitation(Long collaboratorId, boolean accepted) {
        Collaborator collaborator = utilGetCollaboratorById(collaboratorId);

        if (collaborator.getRespondedAt() != null) {
            String responseStatus = accepted ? "Accepted" : "Rejected";
            throw new ResourceAlreadyExistsException("You have already " + responseStatus + " this collaboration invitation");
        }

        LocalDateTime expiredAt = LocalDateTime.parse(collaborator.getInvitationExpiredAt(), dateTimeFormatter);
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("This invitation has already expired.");
        } else {
            collaborator.setRespondedAt(LocalDateTime.now().format(dateTimeFormatter));
            collaborator.setAccepted(accepted);
            collaboratorRepository.save(collaborator);
            return accepted ? ApiResponse.success("Hooray! You have successfully become a collaborator.") :
                    ApiResponse.success("Collaboration was successfully rejected.");
        }
    }

    public ResponseEntity<ApiResponse> deleteCollaborator(Long CollaboratorId) {
        utilGetCollaboratorById(CollaboratorId);
        collaboratorRepository.deleteById(CollaboratorId);
        return ApiResponse.success("Collaborator successfully deleted");
    }

    public Collaborator utilGetCollaboratorById(Long CollaboratorId) {
        return collaboratorRepository.findById(CollaboratorId).orElseThrow(() -> {
            throw new ResourceNotFoundException("There is no Collaborator with this Id");
        });
    }
}
