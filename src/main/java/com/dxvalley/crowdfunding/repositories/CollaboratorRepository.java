package com.dxvalley.crowdfunding.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dxvalley.crowdfunding.models.Collaborator;

import java.util.List;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {
    Collaborator findCollaboratorByCollaboratorId(Long CollaboratorId);
    List<Collaborator> findAllCollaboratorByCampaignCampaignId(Long campaignId);
}