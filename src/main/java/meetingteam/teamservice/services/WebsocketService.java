package meetingteam.teamservice.services;

import java.util.List;

import meetingteam.teamservice.dtos.Channel.ResChannelDto;
import meetingteam.teamservice.dtos.Team.ResTeamDto;
import meetingteam.teamservice.dtos.TeamMember.ResTeamMemberDto;

public interface WebsocketService {
          void addOrUpdateTeamToTeam(String destTeamId, ResTeamDto teamDto);
          void addOrUpdateTeamToUser(String destUserId, ResTeamDto teamDto);
          void deleteTeam(String destUserId, String delTeamId);
          void addOrUpdateChannel(String destTeamId, ResChannelDto channelDto);
          void deleteChannel(String destTeamId, String delChannelId);
          void addTeamMembers(String destTeamId, List<ResTeamMemberDto> memberDtos);
          void deleteMember(String destTeamId, String delUserId);
}
