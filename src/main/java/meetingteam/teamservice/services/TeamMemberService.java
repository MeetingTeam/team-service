package meetingteam.teamservice.services;

import meetingteam.teamservice.dtos.TeamMember.ResTeamMemberDto;

import java.util.List;

public interface TeamMemberService {
    void addFriendsToTeam(List<String> friendIds, String teamId);
    void leaveTeam(String teamId);
    void kickMember(String teamId, String memberId);
    List<ResTeamMemberDto> getMembersOfTeam(String teamId);
}
