package meetingteam.teamservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Team {
	@Id
	@UuidGenerator
	private String id;

	private String teamName;

	private String urlIcon;

	private Boolean autoAddMember;

	@OneToMany(mappedBy="team", fetch=FetchType.LAZY)
	private List<TeamMember> members;

	@OneToMany(mappedBy="team", fetch=FetchType.LAZY)
	private List<Channel> channels;
}
