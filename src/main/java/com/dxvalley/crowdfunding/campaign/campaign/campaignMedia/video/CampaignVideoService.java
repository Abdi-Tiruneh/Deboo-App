package com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.video;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
@RequiredArgsConstructor
public class CampaignVideoService {
    private final CampaignVideoRepository campaignVideoRepository;
    private final CampaignUtils campaignUtils;
    private final DateTimeFormatter dateTimeFormatter;

    public CampaignVideo addVideo(Long campaignId, String campaignVideoUrl) {
        Campaign campaign = this.campaignUtils.getCampaignById(campaignId);
        CampaignVideo campaignVideo = this.saveCampaignVideo(campaignVideoUrl);
        this.updateCampaignWithVideo(campaign, campaignVideo);
        return campaignVideo;
    }

    public CampaignVideo addVideo(Campaign campaign, String campaignVideoUrl) {
        CampaignVideo campaignVideo = this.saveCampaignVideo(campaignVideoUrl);
        this.updateCampaignWithVideo(campaign, campaignVideo);
        return campaignVideo;
    }

    public CampaignVideo saveCampaignVideo(String videoUrl) {
        CampaignVideo campaignVideo = CampaignVideo.builder().videoUrl(videoUrl).isPrimary(true).build();
        return (CampaignVideo) this.campaignVideoRepository.save(campaignVideo);
    }

    private void updateCampaignWithVideo(Campaign campaign, CampaignVideo campaignVideo) {
        campaign.setVideo(campaignVideo);
        campaign.setEditedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        this.campaignUtils.saveCampaign(campaign);
    }

    public ResponseEntity<ApiResponse> deleteCampaignVideo(Long mediaId, Long campaignId) {
        Campaign campaign = campaignUtils.getCampaignById(campaignId);
        campaignUtils.validateCampaignStage(campaign, CampaignStage.INITIAL, "Campaign cannot be edited unless it is in the initial stage");

        CampaignVideo video = campaign.getVideo();
        if (video != null && video.getVideoId().equals(mediaId)) {
            campaign.setVideo(null);
            campaignUtils.saveCampaign(campaign);
            campaignVideoRepository.deleteById(mediaId);
            return ApiResponse.success("Video deleted successfully.");
        }

        throw new ResourceNotFoundException("Campaign video is not found");
    }
}
