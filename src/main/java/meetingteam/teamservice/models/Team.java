package meetingteam.teamservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {
	@Id
	@UuidGenerator
	private String id;

	private String teamName;

	private String urlIcon;

	private Boolean autoAddMember;

	@OneToMany(mappedBy="team", fetch=FetchType.LAZY)
	@Cascade(CascadeType.ALL)
	private List<TeamMember> members;

	@OneToMany(mappedBy="team", fetch=FetchType.LAZY)
	@Cascade(CascadeType.ALL)
	private List<Channel> channels;
}
