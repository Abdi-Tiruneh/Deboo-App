package com.dxvalley.crowdfunding.campaign.campaign.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CampaignAddReq {
    @NotEmpty(message = "The campaign title is a required field.")
    private String title;
    @NotEmpty(message = "The campaign city is a required field.")
    private String city;
    @NotNull(message = "The funding type ID is a required field.")
    @Digits(integer = 10, fraction = 0, message = "The funding type ID must be a positive integer.")
    private Short fundingTypeId;
    @NotNull(message = " The Campaign Sub-category ID is a required field.")
    @Digits(integer = 10, fraction = 0, message = "The Campaign Sub-category ID must be a positive integer.")
    private Short campaignSubCategoryId;
}


