package meetingteam.teamservice.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TeamRequest {
    @Id @UuidGenerator
    private String id;

    @Column(nullable = false)
    private String senderId;

    @ManyToOne
    @JoinColumn(name="teamId")
    private Team team;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Boolean isAccepted;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
