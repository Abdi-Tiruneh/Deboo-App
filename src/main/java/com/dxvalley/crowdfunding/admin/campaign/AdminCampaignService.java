package com.dxvalley.crowdfunding.admin.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignRepository;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignMapper;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCampaignService {
    private final CampaignRepository campaignRepository;
    private final CampaignUtils campaignUtils;
    private final CampaignMapper campaignMapper;
    private final DateTimeFormatter dateTimeFormatter;

    public List<Campaign> getCampaigns() {
        List<Campaign> campaigns = this.campaignRepository.findAll(Sort.by(Sort.Direction.ASC, new String[]{"id"}));
        if (campaigns.isEmpty()) {
            throw new ResourceNotFoundException("Currently, There is no campaign.");
        } else {
            return campaigns;
        }
    }

    public CampaignDTO suspendCampaign(Long campaignId) {
        Campaign campaign = this.campaignUtils.getCampaignById(campaignId);
        this.campaignUtils.validateCampaignStage(campaign, CampaignStage.FUNDING, "Campaign cannot be suspended unless it is in the funding stage");
        campaign.setCampaignStage(CampaignStage.SUSPENDED);
        campaign.setSuspendedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        Campaign savedCampaign = this.campaignUtils.saveCampaign(campaign);
        return this.campaignMapper.toDTO(savedCampaign);
    }

    public CampaignDTO resumeCampaign(Long campaignId) {
        Campaign campaign = this.campaignUtils.getCampaignById(campaignId);
        this.campaignUtils.validateCampaignStage(campaign, CampaignStage.SUSPENDED, "Campaign cannot be resumed unless it is in the suspended stage");
        campaign.setCampaignStage(CampaignStage.FUNDING);
        campaign.setResumedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        Campaign savedCampaign = this.campaignUtils.saveCampaign(campaign);
        return this.campaignMapper.toDTO(savedCampaign);
    }

}