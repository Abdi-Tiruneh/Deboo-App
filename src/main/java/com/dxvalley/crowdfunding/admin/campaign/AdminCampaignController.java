package com.dxvalley.crowdfunding.admin.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.CampaignOperationService;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignRetrievalService;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.ApprovalResponse;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.CampaignApprovalReq;
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
@RequestMapping("/admin/campaigns")
@RequiredArgsConstructor
public class AdminCampaignController {
    private final CampaignOperationService campaignOperationService;
    private final CampaignRetrievalService campaignRetrievalService;
    private final AdminCampaignService adminCampaignService;
    private final AdminCampaignApprovalService adminCampaignApprovalService;

    @GetMapping
    public ResponseEntity<List<CampaignDTO>> getCampaigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Pageable pageable = PaginationUtils.createPageableWithSorting(page, size, sortBy, sortDirection);
        return campaignRetrievalService.getCampaigns(pageable);
    }

    @GetMapping({"/{id}"})
    ResponseEntity<CampaignDTO> getCampaign(@PathVariable Long id) {
        return ResponseEntity.ok(campaignRetrievalService.getCampaignById(id));
    }
    @GetMapping({"/owner/{username}"})
    ResponseEntity<List<CampaignDTO>> getCampaignByOwner(@PathVariable String username) {
        return ResponseEntity.ok(campaignRetrievalService.getCampaignsByOwner(username));
    }

    @GetMapping({"/stage/{stage}"})
    ResponseEntity<List<CampaignDTO>> getCampaignsByStage(
            @PathVariable String stage,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = PaginationUtils.createPageableWithSorting(page, size, sortBy, sortDirection);
        return campaignRetrievalService.getCampaignsByStage(stage, pageable);
    }

    @GetMapping({"/category/{categoryId}"})
    ResponseEntity<List<CampaignDTO>> getCampaignsByCategory(
            @PathVariable Short categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = PaginationUtils.createPageableWithSorting(page, size, sortBy, sortDirection);
        return campaignRetrievalService.getCampaignByCategory(categoryId, pageable);
    }

    @GetMapping({"/subCategory/{subCategoryId}"})
    ResponseEntity<List<CampaignDTO>> getCampaignsBySubCategory(
            @PathVariable Short subCategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = PaginationUtils.createPageableWithSorting(page, size, sortBy, sortDirection);
        return campaignRetrievalService.getCampaignBySubCategory(subCategoryId, pageable);
    }

    @GetMapping({"/fundingType/{fundingTypeId}"})
    ResponseEntity<List<CampaignDTO>> getCampaignsByFundingType(
            @PathVariable Short fundingTypeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = PaginationUtils.createPageableWithSorting(page, size, sortBy, sortDirection);
        return campaignRetrievalService.getCampaignsByFundingType(fundingTypeId, pageable);
    }

    @GetMapping("/search")
    ResponseEntity<List<CampaignDTO>> searchCampaigns(@RequestParam String searchParam) {
        return ResponseEntity.ok(campaignRetrievalService.searchCampaigns(searchParam));
    }

    @PutMapping({"suspend-resume/{campaignId}"})
    ResponseEntity<?> suspendResumeCampaign(@PathVariable Long campaignId, @RequestParam String action) {
        CampaignDTO campaignDTO;
        if (action.equalsIgnoreCase("SUSPEND")) {
            campaignDTO = adminCampaignService.suspendCampaign(campaignId);
            return ResponseEntity.ok(campaignDTO);
        } else if (action.equalsIgnoreCase("RESUME")) {
            campaignDTO = adminCampaignService.resumeCampaign(campaignId);
            return ResponseEntity.ok(campaignDTO);
        } else {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid action. Action should be either 'SUSPEND' or 'RESUME'.");
        }
    }

    @GetMapping({"/approvals/{campaignId}"})
    public ResponseEntity<ApprovalResponse> getCampaignApprovalByCampaignId(@PathVariable Long campaignId) {
        return ResponseEntity.ok(adminCampaignApprovalService.getCampaignApprovalByCampaignId(campaignId));
    }

    @PostMapping({"/approvals"})
    public ResponseEntity<String> approveCampaign(@ModelAttribute @Valid CampaignApprovalReq campaignApprovalReq) {
        return adminCampaignApprovalService.approveCampaign(campaignApprovalReq);
    }

    @DeleteMapping({"/{id}"})
    ResponseEntity<ApiResponse> deleteCampaign(@PathVariable Long id) {
        return campaignOperationService.deleteCampaign(id);
    }
}
