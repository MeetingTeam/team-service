package meetingteam.teamservice.services.impls;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import meetingteam.commonlibrary.exceptions.BadRequestException;
import meetingteam.commonlibrary.utils.AuthUtil;
import meetingteam.teamservice.contraints.WebsocketTopics;
import meetingteam.teamservice.converters.TeamConverter;
import meetingteam.teamservice.dtos.Team.CreateTeamDto;
import meetingteam.teamservice.dtos.Team.ResTeamDto;
import meetingteam.teamservice.dtos.Team.UpdateTeamDto;
import meetingteam.teamservice.models.Channel;
import meetingteam.teamservice.models.Team;
import meetingteam.teamservice.models.TeamMember;
import meetingteam.teamservice.models.enums.ChannelType;
import meetingteam.teamservice.models.enums.TeamRole;
import meetingteam.teamservice.repositories.TeamMemberRepository;
import meetingteam.teamservice.repositories.TeamRepository;
import meetingteam.teamservice.services.FileService;
import meetingteam.teamservice.services.RabbitmqService;
import meetingteam.teamservice.services.TeamService;
import meetingteam.teamservice.services.UserService;
import meetingteam.teamservice.utils.TeamRoleUtil;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepo;
    private final TeamMemberRepository teamMemberRepo;
    private final FileService fileService;
    private final RabbitmqService rabbitmqService;
    private final ModelMapper modelMapper;
    private final TeamConverter teamConverter;

    @Transactional
    public void createTeam(CreateTeamDto teamDto) {
        if(teamDto.getMemberIds().isEmpty())
            throw new BadRequestException("You must add at least one member");

        String leaderId= AuthUtil.getUserId();
        Team team=modelMapper.map(teamDto, Team.class);
        team.setAutoAddMember(false);

        List<TeamMember> members= new ArrayList();
        members.add(new TeamMember(team, leaderId, TeamRole.LEADER));
        teamDto.getMemberIds().forEach(memberId->{
            members.add(new TeamMember(team, memberId, TeamRole.MEMBER));
        });
        team.setMembers(members);

        var generalChannel= Channel.builder()
                .channelName("General")
                .description("The genneral chat channel")
                .team(team)
                .type(ChannelType.CHAT_CHANNEl)
                .build();
        team.setChannels(List.of(generalChannel));

        var savedTeam=teamRepo.save(team);
        var resTeamDto= modelMapper.map(savedTeam, ResTeamDto.class);

        for(TeamMember member: members){
            rabbitmqService.sendToUser(member.getUserId(), WebsocketTopics.AddOrUpdateTeam, resTeamDto);
        }
    }

    public String updateTeam(UpdateTeamDto teamDto) {
        var team= teamRepo.findById(teamDto.getId())
                .orElseThrow(()->new BadRequestException("Team not found"));

        TeamRole role= teamMemberRepo.getRoleByUserIdAndTeamId(
                AuthUtil.getUserId(), team.getId());
        TeamRoleUtil.checkLEADERRole(role);

        if(teamDto.getTeamName()!=null)
            team.setTeamName(teamDto.getTeamName());
        if(teamDto.getAutoAddMember()!=null)
            team.setAutoAddMember(teamDto.getAutoAddMember());

        String preSignedUrl=null;
        if(teamDto.getIconFilename()!=null){
            preSignedUrl=fileService.generatePreSignedUrl(
                    teamDto.getIconFilename(),
                    team.getUrlIcon());
            team.setUrlIcon(preSignedUrl.split("\\?")[0]);
        }

        teamRepo.save(team);

        var resTeamDto= modelMapper.map(team, ResTeamDto.class);
        rabbitmqService.sendToTeam(team.getId(), WebsocketTopics.AddOrUpdateTeam, resTeamDto);

        return preSignedUrl;
    }

    public List<ResTeamDto> getJoinedTeams(){
        String userId= AuthUtil.getUserId();
        List<String> teamIds = teamRepo.getTeamIdsByUserId(userId);
        List<Team> teams=teamRepo.getTeamsWithChannels(teamIds);
        return teamConverter.toDtos(teams);
    }
}
