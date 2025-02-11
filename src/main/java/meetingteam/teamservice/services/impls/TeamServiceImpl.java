package meetingteam.teamservice.services.impls;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import meetingteam.commonlibrary.dtos.PagedResponseDto;
import meetingteam.commonlibrary.dtos.Pagination;
import meetingteam.commonlibrary.exceptions.BadRequestException;
import meetingteam.commonlibrary.utils.AuthUtil;
import meetingteam.commonlibrary.utils.FileUtil;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Value("${s3.url}")
    private String s3BaseUrl;

    @Transactional
    public ResTeamDto createTeam(CreateTeamDto teamDto) {
        String userId= AuthUtil.getUserId();
        Team team=modelMapper.map(teamDto, Team.class);
        team.setAutoAddMember(false);

        List<TeamMember> members= new ArrayList();
        members.add(new TeamMember(team, userId, TeamRole.LEADER));
        team.setMembers(members);

        var generalChannel= Channel.builder()
                .channelName("General")
                .description("The genneral chat channel")
                .team(team)
                .type(ChannelType.CHAT_CHANNEL)
                .build();
        team.setChannels(List.of(generalChannel));

        var savedTeam=teamRepo.save(team);
        return modelMapper.map(savedTeam, ResTeamDto.class);
    }

    public void updateTeam(UpdateTeamDto teamDto) {
        var team= teamRepo.findById(teamDto.getId())
                .orElseThrow(()->new BadRequestException("Team not found"));

        TeamRole role= teamMemberRepo.getRoleByUserIdAndTeamId(
                AuthUtil.getUserId(), team.getId());
        TeamRoleUtil.checkLEADERRole(role);

        if(teamDto.getTeamName()!=null)
            team.setTeamName(teamDto.getTeamName());
        if(teamDto.getAutoAddMember()!=null)
            team.setAutoAddMember(teamDto.getAutoAddMember());
        if(teamDto.getUrlIcon()!=null){
            if(teamDto.getUrlIcon().startsWith(s3BaseUrl))
                throw new BadRequestException("Invalid UrlIcon");
            var newImageName=teamDto.getUrlIcon().substring(s3BaseUrl.length());
            if(!FileUtil.isImageUrl(newImageName))
                throw new BadRequestException("Url Icon is not an image url");
            fileService.deleteFile(team.getUrlIcon());
            team.setUrlIcon(teamDto.getUrlIcon());
        }

        teamRepo.save(team);

        var resTeamDto= modelMapper.map(team, ResTeamDto.class);
        rabbitmqService.sendToTeam(team.getId(), WebsocketTopics.AddOrUpdateTeam, resTeamDto);
    }

    public PagedResponseDto<ResTeamDto> getJoinedTeams(Integer pageNo, Integer pageSize){
        String userId= AuthUtil.getUserId();
        var pageRequest= PageRequest.of(pageNo, pageSize);

        List<String> teamIds = teamRepo.getTeamIdsByUserId(userId);
        Page<Team> teamsPage=teamRepo.getTeamsWithChannels(teamIds, pageRequest);

        var pagination= new Pagination(pageNo, teamsPage.getTotalPages(), teamsPage.getTotalElements());
        return new PagedResponseDto(teamConverter.toDtos(teamsPage.getContent()), pagination);
    }
}
