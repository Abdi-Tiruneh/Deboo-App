package com.dxvalley.crowdfunding.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import com.dxvalley.crowdfunding.models.Campaign;
import org.springframework.data.jpa.repository.Query;

public interface CampaignRepository extends JpaRepository<Campaign, Long>{

    Optional<Campaign> findCampaignByCampaignId(Long campaignId);
    List<Campaign> findCampaignsByOwner(String owner);

    @Query("SELECT new Campaign(c.campaignId, c.title, c.shortDescription, c.city,c.imageUrl,c.goalAmount," +
            "c.campaignDuration,c.projectType, c.campaignStage)" +
            " from Campaign as c where" +
            " c.isEnabled = TRUE")
    List<Campaign> findAllCampaigns();
    @Query("SELECT new Campaign(c.campaignId, c.title, c.shortDescription, c.city,c.imageUrl,c.goalAmount," +
            "c.campaignDuration,c.projectType, c.campaignStage)" +
            " from Campaign as c where" +
            " c.campaignSubCategory.campaignCategory.campaignCategoryId = :categoryId")
    List<Campaign> findByCampaignByCategoryId(Long categoryId);

    @Query("SELECT new Campaign(c.campaignId, c.title, c.shortDescription, c.city,c.imageUrl,c.goalAmount," +
            "c.campaignDuration,c.projectType, c.campaignStage)" +
            " from Campaign as c where" +
            " c.campaignSubCategory.campaignSubCategoryId = :subCategoryId")
    List<Campaign> findByCampaignBySubCategoryId(Long subCategoryId);

    @Query(value = "select * " +
            "from campaign where document @@ to_tsquery(:searchValue)" +
            "ORDER BY ts_rank(document,plainto_tsquery(:searchValue)) desc;", nativeQuery = true)
    List<Campaign> searchForCampaigns(String searchValue);

}


