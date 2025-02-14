package meetingteam.teamservice.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import meetingteam.teamservice.models.enums.TeamRole;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Data
@NoArgsConstructor
public class TeamMember {
	@Id @UuidGenerator
	private String id;

	@Column(nullable = false)
	private String userId;

	@ManyToOne(fetch=FetchType.LAZY)
	private Team team;

	@Enumerated(EnumType.STRING)
	private TeamRole role;

	public TeamMember(Team team, String userId, TeamRole role) {
		this.team = team;
		this.userId = userId;
		this.role = role;
	}
}
