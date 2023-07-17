package com.dxvalley.crowdfunding.campaign.campaignLike;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CampaignLikeReq {
    @NotNull
    private Long campaignId;
}
