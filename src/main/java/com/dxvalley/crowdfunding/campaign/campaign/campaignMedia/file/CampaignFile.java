package com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.file;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long fileId;
    private String fileUrl;
    private String fileName;
    private String fileType;
    private String createdAt;

}
