package com.dxvalley.crowdfunding.campaign.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CampaignRetrievalService {
    CampaignDTO getCampaignById(Long id);
    ResponseEntity<List<CampaignDTO>> getCampaigns(Pageable pageable);
    ResponseEntity<List<CampaignDTO>> getCampaignsByStage(String campaignStage,Pageable pageable);
    ResponseEntity<List<CampaignDTO>> getCampaignByCategory(Short categoryId,Pageable pageable);
    ResponseEntity<List<CampaignDTO>> getCampaignBySubCategory(Short subCategoryId,Pageable pageable);
    ResponseEntity<List<CampaignDTO>> getCampaignsByFundingType(Short fundingTypeId,Pageable pageable);
    List<CampaignDTO> getCampaignsByOwner(String username);
    List<CampaignDTO> getCampaignsByOwner();
    List<CampaignDTO> searchCampaigns(String searchParam);

}