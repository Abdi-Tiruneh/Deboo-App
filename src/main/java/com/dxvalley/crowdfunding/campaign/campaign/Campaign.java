package com.dxvalley.crowdfunding.campaign.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.file.CampaignFile;
import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.image.CampaignImage;
import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.video.CampaignVideo;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.CampaignBankAccount;
import com.dxvalley.crowdfunding.campaign.campaignFundingType.FundingType;
import com.dxvalley.crowdfunding.campaign.campaignSubCategory.CampaignSubCategory;
import com.dxvalley.crowdfunding.userManager.user.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String shortDescription;
    private String city;
    private double goalAmount;
    private String projectType;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignStage campaignStage;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String risks;
    private short campaignDuration;
    private double commissionRate;
    private double totalAmountCollected;
    private int numberOfBackers;
    private int numberOfLikes;
    private boolean enabled;
    private String createdAt;
    private String approvedAt;
    private String editedAt;
    private String editedBy;
    private String pausedAt;
    private String pausedBy;
    private String suspendedAt;
    private String suspendedBy;
    private String resumedAt;
    private String resumedBy;
    private String completedAt;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CampaignImage> images = new ArrayList();

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.EAGER)
    private CampaignVideo video;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CampaignFile> files = new ArrayList();

    @ManyToOne(optional = false)
    @JoinColumn(name = "funding_type_id", nullable = false)
    private FundingType fundingType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "campaign_subcategory_id", nullable = false)
    private CampaignSubCategory campaignSubCategory;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "bank_account_id")
    private CampaignBankAccount bankAccount;

    public void addImage(CampaignImage campaignImage) {
        this.images.add(campaignImage);
    }

    public void addFile(CampaignFile campaignFile) {
        this.files.add(campaignFile);
    }

    public void setFiles(List<CampaignFile> campaignFiles) {
        for (CampaignFile campaignFile : campaignFiles) {
            this.addFile(campaignFile);
        }
    }

}
