package com.dxvalley.crowdfunding.campaign.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.file.CampaignFileService;
import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.image.CampaignImageService;
import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.video.CampaignVideoService;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignAddReq;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignUpdateReq;
import com.dxvalley.crowdfunding.campaign.campaignLike.CampaignLikeReq;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import com.dxvalley.crowdfunding.utils.PaginationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/campaigns")
public class CampaignController {
    private final CampaignOperationService campaignOperationService;
    private final CampaignRetrievalService campaignRetrievalService;
    private final CampaignImageService campaignImageService;
    private final CampaignVideoService campaignVideoService;
    private final CampaignFileService campaignFileService;

    @GetMapping({"/{id}"})
    ResponseEntity<CampaignDTO> getCampaign(@PathVariable Long id) {
        return ResponseEntity.ok(campaignRetrievalService.getCampaignById(id));
    }

    @GetMapping({"/me"})
    ResponseEntity<List<CampaignDTO>> getCampaignByOwner() {
        return ResponseEntity.ok(campaignRetrievalService.getCampaignsByOwner());
    }

    @GetMapping
    public ResponseEntity<List<CampaignDTO>> getCampaigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Pageable pageable = PaginationUtils.createPageableWithSorting(page, size, sortBy, sortDirection);
        return campaignRetrievalService.getCampaigns(pageable);
    }

    @GetMapping({"/stage/{stage}"})
    ResponseEntity<List<CampaignDTO>> getCampaignsByStage(
            @PathVariable String stage,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = PaginationUtils.createPageableWithSorting(page, size, sortBy, sortDirection);
        return campaignRetrievalService.getCampaignsByStage(stage, pageable);
    }

    @GetMapping({"/category/{categoryId}"})
    ResponseEntity<List<CampaignDTO>> getCampaignsByCategory(
            @PathVariable Short categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = PaginationUtils.createPageableWithSorting(page, size, sortBy, sortDirection);
        return campaignRetrievalService.getCampaignByCategory(categoryId, pageable);
    }

    @GetMapping({"/subCategory/{subCategoryId}"})
    ResponseEntity<List<CampaignDTO>> getCampaignsBySubCategory(
            @PathVariable Short subCategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = PaginationUtils.createPageableWithSorting(page, size, sortBy, sortDirection);
        return campaignRetrievalService.getCampaignBySubCategory(subCategoryId, pageable);
    }

    @GetMapping({"/fundingType/{fundingTypeId}"})
    ResponseEntity<List<CampaignDTO>> getCampaignsByFundingType(
            @PathVariable Short fundingTypeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = PaginationUtils.createPageableWithSorting(page, size, sortBy, sortDirection);
        return campaignRetrievalService.getCampaignsByFundingType(fundingTypeId, pageable);
    }

    @GetMapping("/search")
    ResponseEntity<List<CampaignDTO>> searchCampaigns(@RequestParam String searchParam) {
        return ResponseEntity.ok(campaignRetrievalService.searchCampaigns(searchParam));
    }

    @PostMapping
    public ResponseEntity<Campaign> addCampaign(@RequestBody @Valid CampaignAddReq campaignAddRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(campaignOperationService.addCampaign(campaignAddRequestDto));
    }

    @PutMapping({"/{id}"})
    ResponseEntity<CampaignDTO> editCampaign(@PathVariable Long id, @ModelAttribute CampaignUpdateReq campaignUpdateReq) {
        return ResponseEntity.ok(campaignOperationService.editCampaign(id, campaignUpdateReq));
    }

    @PutMapping({"submit-withdraw/{id}"})
    ResponseEntity<?> submitWithdrawCampaign(@PathVariable Long id, @RequestParam String action) {
        CampaignDTO campaignDTO;
        if (action.equalsIgnoreCase("SUBMIT")) {
            campaignDTO = campaignOperationService.submitCampaign(id);
            return ResponseEntity.ok(campaignDTO);
        } else if (action.equalsIgnoreCase("WITHDRAW")) {
            campaignDTO = campaignOperationService.withdrawCampaign(id);
            return ResponseEntity.ok(campaignDTO);
        } else {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid action. Action should be either 'SUBMIT' or 'WITHDRAW'.");
        }
    }

    @PutMapping({"pause-resume/{id}"})
    ResponseEntity<?> pauseResumeCampaign(@PathVariable Long id, @RequestParam String action) {
        CampaignDTO campaignDTO;
        if (action.equalsIgnoreCase("PAUSE")) {
            campaignDTO = campaignOperationService.pauseCampaign(id);
            return ResponseEntity.ok(campaignDTO);
        } else if (action.equalsIgnoreCase("RESUME")) {
            campaignDTO = campaignOperationService.resumeCampaign(id);
            return ResponseEntity.ok(campaignDTO);
        } else {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid action. Action should be either 'PAUSE' or 'RESUME'.");
        }
    }

    @PostMapping({"/like"})
    public ResponseEntity<ApiResponse> likeCampaign(@RequestBody @Valid CampaignLikeReq campaignLikeReq) {
        return campaignOperationService.likeCampaign(campaignLikeReq);
    }

    @DeleteMapping({"/{id}"})
    ResponseEntity<ApiResponse> deleteCampaign(@PathVariable Long id) {
        return campaignOperationService.deleteCampaign(id);
    }

    @DeleteMapping("/{id}/media/{mediaId}")
    ResponseEntity<ApiResponse> deleteCampaignMedia(@PathVariable Long mediaId, @PathVariable Long id, @RequestParam String mediaType) {
        String lowercaseMediaType = mediaType.toLowerCase();

        if (lowercaseMediaType.equals("video"))
            return campaignVideoService.deleteCampaignVideo(mediaId, id);
        if (lowercaseMediaType.equals("image"))
            return campaignImageService.deleteCampaignImage(mediaId, id);
        if (lowercaseMediaType.equals("file"))
            return campaignFileService.deleteCampaignFile(mediaId, id);

        return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid mediaType. Allowed values are 'video', 'image', or 'file'.");
    }

}
