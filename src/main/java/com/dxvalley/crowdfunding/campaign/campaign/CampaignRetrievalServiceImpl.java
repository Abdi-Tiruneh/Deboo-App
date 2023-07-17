package com.dxvalley.crowdfunding.campaign.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignMapper;
import com.dxvalley.crowdfunding.campaign.campaignCollaborator.CollaboratorService;
import com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto.CollaboratorResponse;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.PromotionService;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionResponse;
import com.dxvalley.crowdfunding.campaign.campaignReward.RewardService;
import com.dxvalley.crowdfunding.campaign.campaignReward.dto.RewardResponse;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.paymentManager.payment.PaymentRetrievalService;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentResponse;
import com.dxvalley.crowdfunding.utils.CurrentLoggedInUser;
import com.dxvalley.crowdfunding.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignRetrievalServiceImpl implements CampaignRetrievalService {
    private final CampaignRepository campaignRepository;
    private final CollaboratorService collaboratorService;
    private final RewardService rewardService;
    private final PromotionService promotionService;
    private final PaymentRetrievalService paymentRetrievalService;
    private final CurrentLoggedInUser currentLoggedInUser;
    private final CampaignMapper campaignMapper;
    private final CampaignUtils campaignUtils;

    private final List<CampaignStage> campaignStages = List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED);

    @Override
    public ResponseEntity<List<CampaignDTO>> getCampaigns(Pageable pageable) {
        Page<Campaign> campaignPage = campaignRepository.findByCampaignStageIn(campaignStages, pageable);
        if (campaignPage.isEmpty())
            throw new ResourceNotFoundException("Currently, there is no campaign.");

        return buildResponse(campaignPage);
    }

    @Override
    public ResponseEntity<List<CampaignDTO>> getCampaignsByStage(String stage, Pageable pageable) {
        CampaignStage campaignStage = CampaignStage.lookup(stage);
        Page<Campaign> campaignPage = campaignRepository.findByCampaignStage(campaignStage, pageable);
        if (campaignPage.isEmpty())
            throw new ResourceNotFoundException("There is no campaign at the " + campaignStage + " stage.");

        return buildResponse(campaignPage);
    }

    @Override
    public ResponseEntity<List<CampaignDTO>> getCampaignByCategory(Short categoryId, Pageable pageable) {
        Page<Campaign> campaignPage = campaignRepository.findByCampaignSubCategoryCampaignCategoryIdAndCampaignStageIn(categoryId, campaignStages, pageable);
        if (campaignPage.isEmpty())
            throw new ResourceNotFoundException("There is no campaign available for this category.");

        return buildResponse(campaignPage);
    }

    public ResponseEntity<List<CampaignDTO>> getCampaignBySubCategory(Short subCategoryId, Pageable pageable) {
        Page<Campaign> campaignPage = campaignRepository.findByCampaignSubCategoryIdAndCampaignStageIn(subCategoryId, campaignStages, pageable);
        if (campaignPage.isEmpty())
            throw new ResourceNotFoundException("There is no campaign available for this sub-category.");

        return buildResponse(campaignPage);
    }

    public ResponseEntity<List<CampaignDTO>> getCampaignsByFundingType(Short fundingTypeId, Pageable pageable) {
        Page<Campaign> campaignPage = campaignRepository.findByFundingTypeIdAndCampaignStageIn(fundingTypeId, campaignStages, pageable);
        if (campaignPage.isEmpty())
            throw new ResourceNotFoundException("There are no campaigns available for this funding type.");

        return buildResponse(campaignPage);
    }

    @Override
    public List<CampaignDTO> getCampaignsByOwner() {
        String username = currentLoggedInUser.getUserName();
        List<Campaign> campaigns = campaignUtils.getCampaignsByUserUsername(username);
        return buildResponse(campaigns);
    }

    @Override
    public List<CampaignDTO> getCampaignsByOwner(String username) {
        List<Campaign> campaigns = campaignUtils.getCampaignsByUserUsername(username);
        return buildResponse(campaigns);
    }

    public List<CampaignDTO> searchCampaigns(String searchParam) {
        String[] searchParamArray = searchParam.trim().split("\\W+");
        String searchPattern = String.join("|", searchParamArray);
        List<Campaign> campaigns = campaignRepository.searchForCampaigns(searchPattern);
        if (campaigns.isEmpty())
            throw new ResourceNotFoundException("No campaigns found with this search parameter.");

        return campaigns.stream().map(campaignMapper::toDTO).toList();
    }

    @Override
    public CampaignDTO getCampaignById(Long id) {
        Campaign campaign = campaignUtils.getCampaignById(id);
        CampaignDTO campaignDTO = campaignMapper.toDTOById(campaign);
        setAdditionalDetails(campaignDTO, id);
        return campaignDTO;
    }

    private void setAdditionalDetails(CampaignDTO campaignDTO, Long id) {
        List<CollaboratorResponse> collaborators = collaboratorService.getCollaboratorByCampaignId(id);
        List<RewardResponse> rewards = rewardService.getByCampaign(id);
        List<PromotionResponse> promotions = promotionService.getPromotionByCampaign(id);
        List<PaymentResponse> contributors = paymentRetrievalService.getPaymentByCampaignId(id);
        campaignDTO.setCollaborators(collaborators);
        campaignDTO.setRewards(rewards);
        campaignDTO.setPromotions(promotions);
        campaignDTO.setContributors(contributors);
        campaignDTO.setNumberOfBackers(contributors.size());
    }

    private ResponseEntity<List<CampaignDTO>> buildResponse(Page<Campaign> campaignPage) {
        List<CampaignDTO> campaignDTOList = campaignPage.stream()
                .map(campaignMapper::toDTO)
                .toList();

        HttpHeaders responseHeaders = PaginationUtils.setPaginationHeaders(campaignPage);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(campaignDTOList);
    }

    private List<CampaignDTO> buildResponse(List<Campaign> campaigns) {
        return campaigns.stream()
                .map(campaignMapper::toDTO)
                .toList();
    }

}