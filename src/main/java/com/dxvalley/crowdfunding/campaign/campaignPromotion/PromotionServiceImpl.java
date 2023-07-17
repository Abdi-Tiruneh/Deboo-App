package com.dxvalley.crowdfunding.campaign.campaignPromotion;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionMapper;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionReq;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionResponse;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionUpdateReq;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final CampaignUtils campaignUtils;
    private final DateTimeFormatter dateTimeFormatter;

    public PromotionResponse addPromotion(PromotionReq promotionReq) {
        Promotion promotion = new Promotion();
        Campaign campaign = this.campaignUtils.getCampaignById(promotionReq.getCampaignId());
        promotion.setPromotionLink(promotionReq.getPromotionLink());
        promotion.setDescription(promotionReq.getDescription());
        promotion.setCreatedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        promotion.setCampaign(campaign);
        Promotion savedPromotion = (Promotion) this.promotionRepository.save(promotion);
        return PromotionMapper.toPromotionRes(savedPromotion);
    }

    public PromotionResponse editPromotion(Long id, PromotionUpdateReq promotionUpdateReq) {
        Promotion promotion = this.utilGetPromotionById(id);
        promotion.setDescription(promotionUpdateReq.getDescription());
        promotion.setUpdatedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        Promotion savedPromotion = (Promotion) this.promotionRepository.save(promotion);
        return PromotionMapper.toPromotionRes(savedPromotion);
    }

    public PromotionResponse getPromotionById(Long promotionId) {
        return PromotionMapper.toPromotionRes(this.utilGetPromotionById(promotionId));
    }

    public List<PromotionResponse> getPromotionByCampaign(Long campaignId) {
        List<Promotion> promotions = this.promotionRepository.findPromotionByCampaignId(campaignId);
        return promotions.stream().map(PromotionMapper::toPromotionRes).toList();
    }

    public ResponseEntity<ApiResponse> deletePromotion(Long promotionId) {
        this.utilGetPromotionById(promotionId);
        this.promotionRepository.deleteById(promotionId);
        return ApiResponse.success("Deleted Successfully");
    }

    private Promotion utilGetPromotionById(Long promotionId) {
        return this.promotionRepository.findById(promotionId).orElseThrow(() -> {
            return new ResourceNotFoundException("Promotion Not Found");
        });
    }
}
