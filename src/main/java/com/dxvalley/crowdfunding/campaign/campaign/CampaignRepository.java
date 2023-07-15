package com.dxvalley.crowdfunding.campaign.campaign;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    Optional<Campaign> findCampaignByIdAndCampaignStage(Long campaignId, CampaignStage campaignStage);

    List<Campaign> findByUserUsername(String username);

    Page<Campaign> findByCampaignStageIn(List<CampaignStage> campaignStages, Pageable pageable);

    Page<Campaign> findByCampaignStage(CampaignStage campaignStage, Pageable pageable);

    List<Campaign> findByCampaignStage(CampaignStage campaignStage);

    Page<Campaign> findByFundingTypeIdAndCampaignStageIn(Short fundingTypeId, List<CampaignStage> campaignStages, Pageable pageable);

    Page<Campaign> findByCampaignSubCategoryCampaignCategoryIdAndCampaignStageIn(Short categoryId, List<CampaignStage> campaignStages,Pageable pageable);

    Page<Campaign> findByCampaignSubCategoryIdAndCampaignStageIn(Short subCategoryId, List<CampaignStage> campaignStages, Pageable pageable);

    List<Campaign> findByBankAccountAccountNumber(String accountNumber);

    @Query(value = "SELECT * " +
            "FROM campaign WHERE document @@ to_tsquery(:searchValue) AND campaign_stage IN('FUNDING','COMPLETED')" +
            "ORDER BY ts_rank(document,plainto_tsquery(:searchValue)) DESC;", nativeQuery = true)
    List<Campaign> searchForCampaigns(String searchValue);

}
