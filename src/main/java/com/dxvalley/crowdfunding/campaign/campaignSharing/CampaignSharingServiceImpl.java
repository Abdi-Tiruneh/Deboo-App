package com.dxvalley.crowdfunding.campaign.campaignSharing;

import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaignSharing.dto.CampaignShareCountResponse;
import com.dxvalley.crowdfunding.campaign.campaignSharing.dto.CampaignShareMapper;
import com.dxvalley.crowdfunding.campaign.campaignSharing.dto.CampaignShareResponse;
import com.dxvalley.crowdfunding.campaign.campaignSharing.dto.CampaignSharingReq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignSharingServiceImpl implements CampaignSharingService {
    private final CampaignSharingRepository campaignSharingRepository;
    private final DateTimeFormatter dateTimeFormatter;
    private final CampaignUtils campaignUtils;

    // Retrieves the share counts for a campaign based on the campaign ID.
    public CampaignShareCountResponse getByCampaignId(Long campaignId) {
        List<CampaignSharing> campaignShares = this.campaignSharingRepository.findByCampaignId(campaignId);
        CampaignShareCountResponse campaignShareCountResponse = new CampaignShareCountResponse();
        campaignShares.stream().map(CampaignSharing::getSharingPlatform).map(SharingPlatform::valueOf).forEach((sharingPlatform) -> {
            int shareCount = this.calculateShareCount(campaignShares, sharingPlatform);
            this.setShareCount(campaignShareCountResponse, sharingPlatform, shareCount);
        });
        return campaignShareCountResponse;
    }

    private int calculateShareCount(List<CampaignSharing> campaignShares, SharingPlatform sharingPlatform) {
        return campaignShares.stream().filter((campaignSharing) -> {
            return campaignSharing.getSharingPlatform().equals(sharingPlatform.name());
        }).mapToInt(CampaignSharing::getShareCount).sum();
    }

    private void setShareCount(CampaignShareCountResponse campaignShareCountResponse, SharingPlatform sharingPlatform, int shareCount) {
        switch (sharingPlatform) {
            case FACEBOOK:
                campaignShareCountResponse.setFacebookShares(shareCount);
                break;
            case TIKTOK:
                campaignShareCountResponse.setTiktokShares(shareCount);
                break;
            case TWITTER:
                campaignShareCountResponse.setTwitterShares(shareCount);
                break;
            case TELEGRAM:
                campaignShareCountResponse.setTelegramShares(shareCount);
                break;
            case WHATSAPP:
                campaignShareCountResponse.setWhatsappShares(shareCount);
                break;
            case LINKEDIN:
                campaignShareCountResponse.setLinkedinShares(shareCount);
                break;
            case INSTAGRAM:
                campaignShareCountResponse.setInstagramShares(shareCount);
                break;
            default:
                campaignShareCountResponse.setOtherShares(shareCount);
        }

    }

    @Transactional
    public CampaignShareResponse addShareCampaign(CampaignSharingReq campaignSharingReq) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CampaignSharing existingSharing = this.findExistingSharing(campaignSharingReq, username);
        CampaignSharing newSharing;
        if (existingSharing != null) {
            existingSharing.setShareCount(existingSharing.getShareCount() + 1);
            existingSharing.setSharingTime(LocalDateTime.now().format(this.dateTimeFormatter));
            newSharing = (CampaignSharing)this.campaignSharingRepository.save(existingSharing);
            return CampaignShareMapper.toCampaignShareResponse(newSharing);
        } else {
            newSharing = this.createNewSharing(campaignSharingReq, username);
            return CampaignShareMapper.toCampaignShareResponse(newSharing);
        }
    }

    private CampaignSharing findExistingSharing(CampaignSharingReq campaignSharingReq, String username) {
        return !username.equals("anonymousUser") ? this.campaignSharingRepository.findByCampaignIdAndUsernameAndSharingPlatform(campaignSharingReq.getCampaignId(), username, SharingPlatform.lookup(campaignSharingReq.getSharingPlatform())) : null;
    }

    private CampaignSharing createNewSharing(CampaignSharingReq campaignSharingReq, String username) {
        CampaignSharing newSharing = new CampaignSharing();
        newSharing.setCampaign(this.campaignUtils.getCampaignById(campaignSharingReq.getCampaignId()));
        newSharing.setUsername(username);
        newSharing.setSharingPlatform(SharingPlatform.lookup(campaignSharingReq.getSharingPlatform()));
        newSharing.setSharingTime(LocalDateTime.now().format(this.dateTimeFormatter));
        newSharing.setShareCount(1);
        return newSharing;
    }
}
