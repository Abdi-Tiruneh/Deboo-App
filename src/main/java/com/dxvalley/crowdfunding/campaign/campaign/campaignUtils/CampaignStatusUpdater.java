package com.dxvalley.crowdfunding.campaign.campaign.campaignUtils;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignRepository;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;


@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class CampaignStatusUpdater {
    private final CampaignRepository campaignRepository;
    private final DateTimeFormatter dateTimeFormatter;

    @Scheduled(
            fixedDelay = 300000L
    )
    @Async("asyncExecutor")
    public void updateCampaignStatus() {
        List<Campaign> campaigns = this.campaignRepository.findByCampaignStage(CampaignStage.FUNDING);
        LocalDateTime currentDateTime = LocalDateTime.now();
        Iterator iterator = campaigns.iterator();

        while(iterator.hasNext()) {
            Campaign campaign = (Campaign)iterator.next();
            LocalDateTime expiredAt = LocalDateTime.parse(campaign.getCompletedAt(), this.dateTimeFormatter);
            if (expiredAt.isBefore(currentDateTime)) {
                campaign.setCampaignStage(CampaignStage.COMPLETED);
            }
        }

        this.campaignRepository.saveAll(campaigns);
    }
}
