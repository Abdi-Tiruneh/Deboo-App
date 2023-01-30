package com.dxvalley.crowdfunding.controllers;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dxvalley.crowdfunding.services.RewardService;
import com.dxvalley.crowdfunding.services.CampaignService;
import com.dxvalley.crowdfunding.models.Reward;
import com.dxvalley.crowdfunding.models.Campaign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rewards")
public class RewardController {
  private final RewardService rewardService;
  private final CampaignService campaignService;

  @GetMapping("/getByCampaignId/{campaignId}")
  ResponseEntity<?> getRewards(@PathVariable Long campaignId) {
    return new ResponseEntity<>(this.rewardService.findRewardsByCampaignId(campaignId), HttpStatus.OK);
  }

  @GetMapping("getRewardById/{rewardId}")
  ResponseEntity<?> getReward(@PathVariable Long rewardId) {
    Reward reward = rewardService.getRewardById(rewardId);
    if(reward == null){
      return new ResponseEntity<>("No Reward with this ID", HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(reward, HttpStatus.OK) ;
  }

  @PostMapping("add/{campaignId}")
  public ResponseEntity<?> addReward(@RequestBody Reward reward,@PathVariable Long campaignId) {
      Campaign Campaign = campaignService.getCampaignById(campaignId);
      reward.setCampaign(Campaign);
      rewardService.addReward(reward);

      return new ResponseEntity<>(this.rewardService.findRewardsByCampaignId(campaignId), HttpStatus.OK);
  }

  @PutMapping("edit/{rewardId}")
  ResponseEntity<?> editReward(@RequestBody Reward reward, @PathVariable Long rewardId) {

    Reward tempReward = this.rewardService.getRewardById(rewardId);

    if (tempReward == null){
      return new ResponseEntity<>("No reward with this Id", HttpStatus.OK);
    }
      
    tempReward.setTitle(reward.getTitle() != null ? reward.getTitle() : tempReward.getTitle());
    tempReward.setDescription(reward.getDescription() != null ? reward.getDescription() : tempReward.getDescription());
    tempReward.setAmountToCollect(reward.getAmountToCollect() != null ? reward.getAmountToCollect() : tempReward.getAmountToCollect());
    tempReward.setDeliveryTime(reward.getDeliveryTime() != null ? reward.getDeliveryTime() : tempReward.getDeliveryTime());

    rewardService.editReward(tempReward);
    // return new ResponseEntity<>(tempReward, HttpStatus.OK);
    return new ResponseEntity<>(this.rewardService.findRewardsByCampaignId(tempReward.getCampaign().getCampaignId()), HttpStatus.OK);
  }

  @DeleteMapping("delete/{rewardId}")
  ResponseEntity<?> deleteReward(@PathVariable Long rewardId) {
    Reward Reward = this.rewardService.getRewardById(rewardId);

    if(Reward == null) return new ResponseEntity<String>("Entry does not exist!", HttpStatus.BAD_REQUEST);

    rewardService.deleteReward(rewardId);

    ApiResponse response = new ApiResponse("success", "Reward Deleted successfully!");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}


@Getter
@Setter
@AllArgsConstructor
class RewardResponse {
  Reward Reward;
  String status;
}
