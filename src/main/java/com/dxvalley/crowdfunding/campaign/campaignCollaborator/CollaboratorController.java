package com.dxvalley.crowdfunding.campaign.campaignCollaborator;

import com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto.CollaborationReq;
import com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto.CollaboratorResponse;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/collaborators")
@RequiredArgsConstructor
public class CollaboratorController {
    private final CollaboratorService collaboratorService;

    @GetMapping({"/{id}"})
    public ResponseEntity<CollaboratorResponse> getCollaborator(@PathVariable Long id) {
        return ResponseEntity.ok(collaboratorService.getCollaboratorById(id));
    }

    @GetMapping({"/by-campaign/{campaignId}"})
    public ResponseEntity<List<CollaboratorResponse>> getUserCollaborators(@PathVariable Long campaignId) {
        return ResponseEntity.ok(collaboratorService.getCollaboratorByCampaignId(campaignId));
    }

    @PostMapping({"/invite"})
    public ResponseEntity<CollaboratorResponse> sendInvitation(@RequestBody @Valid CollaborationReq collaborationReq) {
        CollaboratorResponse collaboratorResponse = collaboratorService.sendInvitation(collaborationReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(collaboratorResponse);
    }

    @PutMapping({"/respond-to-invitation/{id}"})
    public ResponseEntity<ApiResponse> acceptInvitation(@PathVariable Long id, @RequestParam boolean accepted) {
        return collaboratorService.respondToCollaborationInvitation(id, accepted);
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<ApiResponse> deleteCollaborator(@PathVariable Long id) {
        return collaboratorService.deleteCollaborator(id);
    }

}
