package meetingteam.teamservice.dtos.TeamMember;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import meetingteam.teamservice.dtos.User.ResUserDto;
import meetingteam.teamservice.models.enums.TeamRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResTeamMemberDto {
	private ResUserDto user;
	private TeamRole role;
}
