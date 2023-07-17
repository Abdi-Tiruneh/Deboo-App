package com.dxvalley.crowdfunding.admin.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaignApproval.ApprovalStatus;
import com.dxvalley.crowdfunding.campaign.campaignApproval.CampaignApproval;
import com.dxvalley.crowdfunding.campaign.campaignApproval.CampaignApprovalRepository;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.ApprovalResponse;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.CampaignApprovalMapper;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.CampaignApprovalReq;
import com.dxvalley.crowdfunding.campaign.campaignApproval.file.ApprovalFile;
import com.dxvalley.crowdfunding.campaign.campaignApproval.file.ApprovalFileService;
import com.dxvalley.crowdfunding.exception.customException.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.userManager.user.UserUtils;
import com.dxvalley.crowdfunding.userManager.user.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminCampaignApprovalServiceImpl implements AdminCampaignApprovalService {
    private final CampaignApprovalRepository campaignApprovalRepository;
    private final CampaignUtils campaignUtils;
    private final UserUtils userUtils;
    private final DateTimeFormatter dateTimeFormatter;
    private final ApprovalFileService approvalFileService;

    @Override
    public ApprovalResponse getCampaignApprovalByCampaignId(Long campaignId) {
        CampaignApproval campaignApproval = campaignApprovalRepository.findByCampaignId(campaignId).orElseThrow(() -> {
            return new ResourceNotFoundException("There is no approval information for this campaign.");
        });
        return CampaignApprovalMapper.toAdminApprovalResponse(campaignApproval);
    }

    @Transactional
    @Override
    public ResponseEntity<String> approveCampaign(CampaignApprovalReq campaignApprovalReq) {
        Long id = campaignApprovalReq.getCampaignId();
        checkExistingApproval(id);
        Campaign campaign = campaignUtils.getCampaignByIdAndStage(id, CampaignStage.PENDING);

        if (campaignApprovalReq.getApprovalStatus().equalsIgnoreCase("RESUBMIT")) {
            resubmitCampaign(campaign);
            return ResponseEntity.ok("Campaign is returned to the initial stage for resubmission successfully.");
        }

        CampaignApproval campaignApproval = createCampaignApproval(campaign, campaignApprovalReq);
        campaignApprovalRepository.save(campaignApproval);
        processCampaignApproval(campaign, campaignApprovalReq);
        return ResponseEntity.ok("Campaign " + campaignApprovalReq.getApprovalStatus().toLowerCase() + " Successfully");
    }

    private void checkExistingApproval(Long campaignId) {
        Optional<CampaignApproval> existingApproval = campaignApprovalRepository.findByCampaignId(campaignId);
        if (existingApproval.isPresent())
            throw new ResourceAlreadyExistsException("This campaign has already been " + existingApproval.get().getApprovalStatus());
    }

    private CampaignApproval createCampaignApproval(Campaign campaign, CampaignApprovalReq campaignApprovalReq) {
        CampaignApproval campaignApproval = new CampaignApproval();
        campaignApproval.setCampaign(campaign);
        campaignApproval.setApprovedBy(campaignApprovalReq.getApprovedBy());
        campaignApproval.setApprovalStatus(ApprovalStatus.lookup(campaignApprovalReq.getApprovalStatus()));
        campaignApproval.setReason(campaignApprovalReq.getReason());
        campaignApproval.setCommissionRate(campaignApprovalReq.getCommissionRate());
        campaignApproval.setApprovedAt(LocalDateTime.now().format(dateTimeFormatter));
        if (campaignApprovalReq.getFiles() != null && !campaignApprovalReq.getFiles().isEmpty()) {
            List<ApprovalFile> approvalFiles = approvalFileService.saveApprovalFile(campaignApprovalReq.getFiles());
            campaignApproval.setApprovalFiles(approvalFiles);
        }

        return campaignApproval;
    }

    private void processCampaignApproval(Campaign campaign, CampaignApprovalReq campaignApprovalReq) {
        if (ApprovalStatus.ACCEPTED.name().equalsIgnoreCase(campaignApprovalReq.getApprovalStatus()))
            acceptCampaign(campaign);
        else
            rejectCampaign(campaign);
    }

    private void acceptCampaign(Campaign campaign) {
        configureCampaignForAcceptance(campaign);
        incrementUserTotalCampaigns(campaign.getUser().getUsername());
    }

    private void rejectCampaign(Campaign campaign) {
        campaign.setEnabled(false);
        campaign.setCampaignStage(CampaignStage.REJECTED);
        campaign.setApprovedAt(LocalDateTime.now().format(dateTimeFormatter));
        campaignUtils.saveCampaign(campaign);
    }

    private void resubmitCampaign(Campaign campaign) {
        campaign.setEnabled(false);
        campaign.setCampaignStage(CampaignStage.INITIAL);
        campaignUtils.saveCampaign(campaign);
    }

    private void configureCampaignForAcceptance(Campaign campaign) {
        campaign.setEnabled(true);
        campaign.setCampaignStage(CampaignStage.FUNDING);
        campaign.setApprovedAt(LocalDateTime.now().format(dateTimeFormatter));
        campaign.setCompletedAt(LocalDateTime.now().plusDays((long) campaign.getCampaignDuration()).format(dateTimeFormatter));
        campaignUtils.saveCampaign(campaign);
    }

    private void incrementUserTotalCampaigns(String username) {
        Users user = userUtils.utilGetUserByUsername(username);
        user.setTotalCampaigns((short) (user.getTotalCampaigns() + 1));
        userUtils.saveUser(user);
    }

}

