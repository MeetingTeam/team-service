package meetingteam.teamservice.services.impls;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import meetingteam.commonlibrary.exceptions.BadRequestException;
import meetingteam.commonlibrary.utils.AuthUtil;
import meetingteam.teamservice.contraints.WebsocketTopics;
import meetingteam.teamservice.dtos.Team.ResTeamDto;
import meetingteam.teamservice.dtos.TeamMember.ResTeamMemberDto;
import meetingteam.teamservice.dtos.User.ResUserDto;
import meetingteam.teamservice.models.Team;
import meetingteam.teamservice.models.TeamMember;
import meetingteam.teamservice.models.enums.TeamRole;
import meetingteam.teamservice.repositories.TeamMemberRepository;
import meetingteam.teamservice.repositories.TeamRepository;
import meetingteam.teamservice.services.RabbitmqService;
import meetingteam.teamservice.services.TeamMemberService;
import meetingteam.teamservice.services.UserService;
import meetingteam.teamservice.utils.TeamRoleUtil;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {
    private final TeamMemberRepository teamMemberRepo;
    private final TeamRepository teamRepo;
    private final UserService userService;
    private final RabbitmqService rabbitmqService;
    private final ModelMapper modelMapper;

    @Transactional
    public void addFriendsToTeam(List<String> friendIds, String teamId) {
        String userId= AuthUtil.getUserId();
        if(!teamMemberRepo.existsByTeamAndUserId(teamRepo.getById(teamId), userId))
            throw new AccessDeniedException("You do not have permissions to add new members to this team");

        var members=new ArrayList<TeamMember>();
        for(String friendId: friendIds) {
            TeamMember tm=teamMemberRepo.findByTeamIdAndUserId(teamId, friendId);
            if(tm==null) tm=new TeamMember(teamRepo.getById(teamId),friendId, TeamRole.MEMBER);
            else if(tm.getRole()==TeamRole.LEAVE) tm.setRole(TeamRole.MEMBER);
            members.add(tm);
        }
        teamMemberRepo.saveAll(members);

        List<TeamMember> savedMembers=teamMemberRepo.saveAll(members);
        List<ResTeamMemberDto> memberDtos= savedMembers.stream()
                        .map(member->modelMapper.map(member, ResTeamMemberDto.class))
                        .toList();
        rabbitmqService.sendToTeam(teamId, WebsocketTopics.AddTeamMembers, memberDtos);

        Team team=teamRepo.getTeamWithChannels(teamRepo.getById(teamId));
        var teamDto= modelMapper.map(team, ResTeamDto.class);
        for(String friendId: friendIds){
            rabbitmqService.sendToUser(friendId, WebsocketTopics.AddOrUpdateTeam, teamDto);
        }
    }

    @Transactional
    public void leaveTeam(String teamId) {
        String userId=AuthUtil.getUserId();

        TeamMember tm=teamMemberRepo.findByTeamIdAndUserId(teamId, userId);

        if(tm.getRole()==TeamRole.LEADER){
            List<TeamMember> members= teamMemberRepo.findByTeam(teamRepo.getById(teamId));
            TeamMember newLeader=null;
            for(TeamMember member: members){
                if(!member.getUserId().equals(userId)&&
                        member.getRole()==TeamRole.MEMBER){
                    newLeader=member;
                    break;
                }
            }
            if(newLeader==null) throw new BadRequestException("Can't find alternative member for the leader position");

            tm.setRole(TeamRole.LEAVE);
            newLeader.setRole(TeamRole.LEADER);
            teamMemberRepo.saveAll(List.of(tm, newLeader));
        }
        else{
            tm.setRole(TeamRole.LEAVE);
            teamMemberRepo.save(tm);
        }

        rabbitmqService.sendToTeam(teamId, WebsocketTopics.DeleteMember, tm.getId());
    }

    public void kickMember(String teamId, String memberId) {
        String userId=AuthUtil.getUserId();

        TeamRole role=teamMemberRepo.getRoleByUserIdAndTeamId(userId, teamId);
        TeamRoleUtil.checkLEADERRole(role);

        TeamMember kickedMember=teamMemberRepo.findByTeamIdAndUserId(teamId,memberId);
        kickedMember.setRole(TeamRole.LEAVE);
        teamMemberRepo.save(kickedMember);

        rabbitmqService.sendToTeam(teamId, WebsocketTopics.DeleteMember, kickedMember.getId());
        rabbitmqService.sendToUser(kickedMember.getUserId(), WebsocketTopics.DeleteTeam, teamId);
    }

    public List<ResTeamMemberDto> getMembersOfTeam(String teamId) {
        var members= teamMemberRepo.findByTeam(teamRepo.getById(teamId));
        var userIds=members.stream().map(member->member.getUserId()).toList();
        List<ResUserDto> userDtos=userService.getUsersByIds(userIds);

        var userDtosMap= new HashMap<String,ResUserDto>();
        userDtos.forEach(userDto->userDtosMap.put(userDto.getId(), userDto));

        var resMemberDtos=new ArrayList<ResTeamMemberDto>();
        for(var member: members){
            var resMemberDto= new ResTeamMemberDto(
                    modelMapper.map(userDtosMap.get(member.getUserId()), ResUserDto.class),
                    member.getRole()
            );
            resMemberDtos.add(resMemberDto);
        }
        return resMemberDtos;
    }

    public boolean isMemberOfTeam(String userId, String teamId, String channelId){
        if(teamId==null&&channelId==null)
            throw new BadRequestException("Neither team id nor channel id is not null");
        return teamMemberRepo.existsByUserIdAndTeamIdAndChannelId(userId, teamId, channelId)>0;
    }
}
