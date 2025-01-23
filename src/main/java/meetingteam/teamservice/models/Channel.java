package meetingteam.teamservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import meetingteam.teamservice.models.enums.ChannelType;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Channel {
	@Id
	@UuidGenerator
	private String id;

	private String channelName;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Enumerated(EnumType.STRING)
	private ChannelType type;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="teamId")
	private Team team;
}
