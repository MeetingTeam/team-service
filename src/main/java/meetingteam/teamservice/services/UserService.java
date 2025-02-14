package meetingteam.teamservice.services;

import meetingteam.teamservice.dtos.TeamMember.ResTeamMemberDto;
import meetingteam.teamservice.dtos.User.ResUserDto;
import meetingteam.teamservice.models.TeamMember;

import java.util.List;

public interface UserService {
    List<ResUserDto> getUsersByIds(List<String> userIds);
    List<ResTeamMemberDto> fetchUsersData(List<String> userIds, List<TeamMember> members);
}
