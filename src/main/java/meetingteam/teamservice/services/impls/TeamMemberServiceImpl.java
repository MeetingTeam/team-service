package meetingteam.teamservice.services.impls;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import meetingteam.commonlibrary.exceptions.BadRequestException;
import meetingteam.commonlibrary.utils.AuthUtil;
import meetingteam.teamservice.dtos.TeamMember.ResTeamMemberDto;
import meetingteam.teamservice.dtos.User.ResUserDto;
import meetingteam.teamservice.models.TeamMember;
import meetingteam.teamservice.models.enums.TeamRole;
import meetingteam.teamservice.repositories.TeamMemberRepository;
import meetingteam.teamservice.repositories.TeamRepository;
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

//        List<TeamMember> savedTMs=teamMemberRepo.saveAll(members);
//        socketTemplate.sendTeam(teamId,"/updateMembers",tmConverter.convertToDTO(savedTMs));
//        Team team=teamRepo.getTeamWithMembers(teamId);
//        team=teamRepo.getTeamWithChannels(teamId);
//        for(String friendId: friendIds)
//            socketTemplate.sendUser(friendId,"/addTeam",
//                    teamConverter.convertTeamToDTO(team,team.getMembers(),team.getChannels()));
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
        //socketTemplate.sendTeam(teamId,"/updateMembers",List.of(tmConverter.convertToDTO(tm)));
    }

    public void kickMember(String teamId, String memberId) {
        String userId=AuthUtil.getUserId();

        TeamRole role=teamMemberRepo.getRoleByUserIdAndTeamId(userId, teamId);
        TeamRoleUtil.checkLEADERRole(role);

        TeamMember kickedMember=teamMemberRepo.findByTeamIdAndUserId(teamId,memberId);
        kickedMember.setRole(TeamRole.LEAVE);
        teamMemberRepo.save(kickedMember);
//            socketTemplate.sendUser(memberId,"/deleteTeam",teamId);
//            socketTemplate.sendTeam(teamId,"/updateMembers",List.of(tmConverter.convertToDTO(tm)));
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

    public boolean isMemberOfTeam(String userId, String channelId){
        return teamMemberRepo.existsByUserIdAndChannelId(userId, channelId)>0;
    }
}
