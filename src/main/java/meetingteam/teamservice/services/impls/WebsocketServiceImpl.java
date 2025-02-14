package meetingteam.teamservice.services.impls;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import meetingteam.teamservice.contraints.WebsocketTopics;
import meetingteam.teamservice.dtos.Channel.ResChannelDto;
import meetingteam.teamservice.dtos.Team.ResTeamDto;
import meetingteam.teamservice.dtos.TeamMember.ResTeamMemberDto;
import meetingteam.teamservice.services.RabbitmqService;
import meetingteam.teamservice.services.WebsocketService;

@Service
@RequiredArgsConstructor
public class WebsocketServiceImpl implements WebsocketService{
          private final RabbitmqService rabbitmqService;

          @Override
          public void addOrUpdateTeamToTeam(String destTeamId, ResTeamDto teamDto) {
                    rabbitmqService.sendToTeam(destTeamId, WebsocketTopics.AddOrUpdateTeam, teamDto);
          }

          @Override
          public void addOrUpdateTeamToUser(String destUserId, ResTeamDto teamDto) {
                    rabbitmqService.sendToUser(destUserId, WebsocketTopics.AddOrUpdateTeam, teamDto);
          }

          @Override
          public void deleteTeam(String destUserId, String delTeamId) {
                    rabbitmqService.sendToUser(destUserId, WebsocketTopics.DeleteTeam, delTeamId);
          }

          @Override
          public void addOrUpdateChannel(String destTeamId, ResChannelDto channelDto) {
                    rabbitmqService.sendToTeam(destTeamId, WebsocketTopics.AddOrUpdateChannel, channelDto);
          }

          @Override
          public void deleteChannel(String destTeamId, String delChannelId) {
                    rabbitmqService.sendToTeam(destTeamId, WebsocketTopics.DeleteChannel, delChannelId);
          }

          @Override
          public void addTeamMembers(String destTeamId, List<ResTeamMemberDto> memberDtos) {
                    rabbitmqService.sendToTeam(destTeamId, WebsocketTopics.AddTeamMembers, memberDtos);
          }

          @Override
          public void deleteMember(String destTeamId, String delUserId) {
                    rabbitmqService.sendToTeam(destTeamId, WebsocketTopics.DeleteMember, delUserId);
          }
}
