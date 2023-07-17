package com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.file;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.image.CampaignImage;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.exception.customException.BadRequestException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.fileUploadManager.FileUploadService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CampaignFileService {
    private final CampaignFileRepository campaignFileRepository;
    private final FileUploadService fileUploadService;
    private final DateTimeFormatter dateTimeFormatter;
    private final CampaignUtils campaignUtils;

    public List<CampaignFile> addFiles(Campaign campaign, List<MultipartFile> multipartFiles) {
        List<CampaignFile> campaignFiles = this.saveCampaignFile(multipartFiles);
        this.updateCampaignWithFile(campaign, campaignFiles);
        return campaignFiles;
    }

    public List<CampaignFile> saveCampaignFile(List<MultipartFile> campaignFiles) {
        if (campaignFiles != null && !campaignFiles.isEmpty()) {
            List<CampaignFile> campaignFileList = new ArrayList();
            Iterator iterator = campaignFiles.iterator();

            while (iterator.hasNext()) {
                MultipartFile file = (MultipartFile) iterator.next();
                CampaignFile campaignFile = this.createCampaignFile(file);
                campaignFileList.add(campaignFile);
            }

            return this.campaignFileRepository.saveAll(campaignFileList);
        } else {
            throw new BadRequestException("Invalid request. Please provide a file.");
        }
    }

    private CampaignFile createCampaignFile(MultipartFile file) {
        String fileUrl = this.fileUploadService.uploadFile(file);
        CampaignFile campaignFile = new CampaignFile();
        campaignFile.setFileUrl(fileUrl);
        campaignFile.setFileName(file.getOriginalFilename());
        campaignFile.setFileType(file.getContentType());
        campaignFile.setCreatedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        return campaignFile;
    }

    private void updateCampaignWithFile(Campaign campaign, List<CampaignFile> campaignFileList) {
        campaign.setFiles(campaignFileList);
        campaign.setEditedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        this.campaignUtils.saveCampaign(campaign);
    }
    public ResponseEntity<ApiResponse> deleteCampaignFile(Long mediaId, Long campaignId) {
        Campaign campaign = campaignUtils.getCampaignById(campaignId);
        campaignUtils.validateCampaignStage(campaign, CampaignStage.INITIAL, "Campaign cannot be edited unless it is in the initial stage");
        List<CampaignFile> campaignFiles = campaign.getFiles();

        Optional<CampaignFile> optionalCampaignFile = campaignFiles.stream()
                .filter(file -> file.getFileId().equals(mediaId))
                .findFirst();

        if (optionalCampaignFile.isPresent()) {
            campaign.getFiles().remove(optionalCampaignFile.get());
            campaignUtils.saveCampaign(campaign);
            return ApiResponse.success("File deleted successfully.");
        } else
            throw new ResourceNotFoundException("Campaign File is not found");
    }

    public CampaignFileService(final CampaignFileRepository campaignFileRepository, final FileUploadService fileUploadService,
                               final DateTimeFormatter dateTimeFormatter, final CampaignUtils campaignUtils) {
        this.campaignFileRepository = campaignFileRepository;
        this.fileUploadService = fileUploadService;
        this.dateTimeFormatter = dateTimeFormatter;
        this.campaignUtils = campaignUtils;
    }
}