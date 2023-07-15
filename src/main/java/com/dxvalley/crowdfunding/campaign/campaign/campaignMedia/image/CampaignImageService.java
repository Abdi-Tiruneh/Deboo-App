package com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.image;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.fileUploadManager.FileUploadService;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CampaignImageService {
    private final CampaignImageRepository campaignImageRepository;
    private final FileUploadService fileUploadService;
    private final CampaignUtils campaignUtils;
    private final DateTimeFormatter dateTimeFormatter;

    public CampaignImage addImage(Long campaignId, MultipartFile multipartFile) {
        Campaign campaign = this.campaignUtils.getCampaignById(campaignId);
        boolean imageExist = this.isImageExist(campaign);
        CampaignImage campaignImage = this.saveCampaignImage(multipartFile, imageExist);
        this.updateCampaignWithImage(campaign, campaignImage);
        return campaignImage;
    }

    public CampaignImage addImage(Campaign campaign, MultipartFile multipartFile) {
        boolean imageExist = this.isImageExist(campaign);
        CampaignImage campaignImage = this.saveCampaignImage(multipartFile, imageExist);
        this.updateCampaignWithImage(campaign, campaignImage);
        return campaignImage;
    }

    private void updateCampaignWithImage(Campaign campaign, CampaignImage campaignImage) {
        campaign.addImage(campaignImage);
        campaign.setEditedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        this.campaignUtils.saveCampaign(campaign);
    }

    private boolean isImageExist(Campaign campaign) {
        return !campaign.getImages().isEmpty();
    }

    public CampaignImage saveCampaignImage(MultipartFile multipartFile, boolean imageExist) {
        String imageUrl = this.fileUploadService.uploadFile(multipartFile);
        CampaignImage campaignImage = this.buildCampaignImage(imageUrl, imageExist);
        return (CampaignImage)this.campaignImageRepository.save(campaignImage);
    }

    private CampaignImage buildCampaignImage(String imageUrl, boolean imageExist) {
        return CampaignImage.builder().imageUrl(imageUrl).isPrimary(!imageExist).build();
    }

    public ResponseEntity<ApiResponse> deleteCampaignImage(Long mediaId, Long campaignId) {
        Campaign campaign = campaignUtils.getCampaignById(campaignId);
        campaignUtils.validateCampaignStage(campaign, CampaignStage.INITIAL, "Campaign cannot be edited unless it is in the initial stage");
        List<CampaignImage> campaignImages = campaign.getImages();
        boolean fileFound = false;

        for (Iterator<CampaignImage> iterator = campaignImages.iterator(); iterator.hasNext(); ) {
            CampaignImage campaignFile = iterator.next();
            if (campaignFile.getImageId().equals(mediaId)) {
                iterator.remove();
                campaign.setImages(campaignImages);
                campaignUtils.saveCampaign(campaign);
                campaignImageRepository.deleteById(mediaId);
                fileFound = true;
                break;
            }
        }

        if (fileFound)
            return ApiResponse.success("Image deleted successfully.");
        else
            throw new ResourceNotFoundException("Campaign Image is not found");
    }

}

