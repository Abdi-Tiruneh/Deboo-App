package com.dxvalley.crowdfunding.campaign.campaignCollaborator;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "Collaborator")
@SQLDelete(sql = "UPDATE Collaborator SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Collaborator {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private boolean accepted;

    @Column(nullable = false)
    private String collaboratorEmail;

    @Column(nullable = false)
    private String collaboratorFullName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaignId", nullable = false)
    private Campaign campaign;

    @Column(nullable = false)
    private String invitationSentAt;

    private String invitationExpiredAt;

    private String respondedAt;

    @JsonIgnore
    private boolean deleted;
}





