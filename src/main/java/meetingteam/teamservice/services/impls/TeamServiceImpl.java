package meetingteam.teamservice.services.impls;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import meetingteam.commonlibrary.exceptions.BadRequestException;
import meetingteam.commonlibrary.utils.AuthUtil;
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
import meetingteam.teamservice.services.TeamService;
import meetingteam.teamservice.services.UserService;
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
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final TeamConverter teamConverter;

    @Transactional
    public ResTeamDto createTeam(CreateTeamDto teamDto) {
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
        return teamConverter.toDto(savedTeam);
    }

    public void updateTeam(UpdateTeamDto teamDto) {
        var team= teamRepo.findById(teamDto.getId())
                .orElseThrow(()->new BadRequestException("Team not found"));
        team.setTeamName(teamDto.getTeamName());
        team.setAutoAddMember(teamDto.getAutoAddMember());

        if(teamDto.getIconFilename()!=null){
            var preSignedUrl=fileService.generatePreSignedUrl(
                    "teams/"+teamDto.getId(),
                    teamDto.getIconFilename(),
                    team.getUrlIcon());
            team.setUrlIcon(preSignedUrl.split("\\?")[0]);
        }

        teamRepo.save(team);
    }

    @Transactional
    public void addFriendsToTeam(List<String> friendIds, String teamId) {
        String userId= AuthUtil.getUserId();
        if(!teamMemberRepo.existsByTeamAndUserId(teamRepo.getById(teamId), userId))
            throw new AccessDeniedException("You do not have permissions to add new members to this team");

        var members=new ArrayList<TeamMember>();
        for(String friendId: friendIds) {
            TeamMember tm=teamMemberRepo.findByTeamIdAndUserId(teamId, friendId);
            if(tm==null) tm=new TeamMember(teamRepo.getById(teamId),friendId, TeamRole.MEMBER);
            else if(tm.getRole()==TeamRole.LEAVE) tm.setRole(TeamRole.LEAVE);
            members.add(tm);
        }

//        List<TeamMember> savedTMs=teamMemberRepo.saveAll(members);
//        socketTemplate.sendTeam(teamId,"/updateMembers",tmConverter.convertToDTO(savedTMs));
//        Team team=teamRepo.getTeamWithMembers(teamId);
//        team=teamRepo.getTeamWithChannels(teamId);
//        for(String friendId: friendIds)
//            socketTemplate.sendUser(friendId,"/addTeam",
//                    teamConverter.convertTeamToDTO(team,team.getMembers(),team.getChannels()));
    }

    public List<ResTeamDto> getJoinedTeams(){
        String userId= AuthUtil.getUserId();
        List<String> teamIds = teamRepo.getTeamIdsByUserId(userId);
        List<Team> teams=teamRepo.getTeamsWithChannels(teamIds);
        return teamConverter.toDtos(teams);
    }

//    public void leaveTeam(String teamId) {
//        String userId=infoChecking.getUserIdFromContext();
//        TeamMember tm=teamMemberRepo.findByTeamIdAndUserId(teamId, userId);
//        tm.setRole("LEAVE");
//        teamMemberRepo.save(tm);
//        socketTemplate.sendTeam(teamId,"/updateMembers",List.of(tmConverter.convertToDTO(tm)));
//    }
//    public void kickMember(String teamId, String memberId) {
//        User u=infoChecking.getUserFromContext();
//        String role=teamMemberRepo.getRoleByUserIdAndTeamId(u.getId(), teamId);
//        TeamMember tm=teamMemberRepo.findByTeamIdAndUserId(teamId,memberId);
//        if(role.equals("LEADER")) {
//            tm.setRole("LEAVE");
//            teamMemberRepo.save(tm);
//            socketTemplate.sendUser(memberId,"/deleteTeam",teamId);
//            socketTemplate.sendTeam(teamId,"/updateMembers",List.of(tmConverter.convertToDTO(tm)));
//        }
//        else throw new RequestException("You do not have permission to kick a member!Contact leader or deputies of your team for help!");
//    }
}
