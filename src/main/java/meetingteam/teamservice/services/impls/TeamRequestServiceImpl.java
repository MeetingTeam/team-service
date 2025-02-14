package meetingteam.teamservice.services.impls;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import meetingteam.commonlibrary.exceptions.BadRequestException;
import meetingteam.commonlibrary.utils.AuthUtil;
import meetingteam.teamservice.contraints.WebsocketTopics;
import meetingteam.teamservice.dtos.Team.ResTeamDto;
import meetingteam.teamservice.dtos.TeamMember.ResTeamMemberDto;
import meetingteam.teamservice.dtos.TeamRequest.CreateTeamRequestDto;
import meetingteam.teamservice.dtos.TeamRequest.ResTeamRequestDto;
import meetingteam.teamservice.dtos.User.ResUserDto;
import meetingteam.teamservice.models.Team;
import meetingteam.teamservice.models.TeamMember;
import meetingteam.teamservice.models.TeamRequest;
import meetingteam.teamservice.models.enums.TeamRole;
import meetingteam.teamservice.repositories.TeamMemberRepository;
import meetingteam.teamservice.repositories.TeamRepository;
import meetingteam.teamservice.repositories.TeamRequestRepository;
import meetingteam.teamservice.services.TeamRequestService;
import meetingteam.teamservice.services.UserService;
import meetingteam.teamservice.services.WebsocketService;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TeamRequestServiceImpl implements TeamRequestService {
    private final TeamRequestRepository teamRequestRepo;
    private final TeamMemberRepository teamMemberRepo;
    private final TeamRepository teamRepo;
    private final UserService userService;
    private final WebsocketService websocketService;
    private final ModelMapper modelMapper;

    @Transactional
    public String requestToJoinTeam(CreateTeamRequestDto requestDto) {
        String userId= AuthUtil.getUserId();
        Team team=teamRepo.findById(requestDto.getTeamId()).orElseThrow(()->new BadRequestException("Team does not exists"));
        if(team.getAutoAddMember()) {
            var tm=teamMemberRepo.findByTeamIdAndUserId(team.getId(),userId);
            if(tm==null) tm=new TeamMember(team,userId,TeamRole.MEMBER);
            else tm.setRole(TeamRole.MEMBER);

            teamMemberRepo.save(tm);
            team=teamRepo.getTeamWithChannels(team);

            var memberDto= modelMapper.map(tm, ResTeamMemberDto.class);
            websocketService.addTeamMembers(team.getId(), List.of(memberDto));

            var teamDto= modelMapper.map(team, ResTeamDto.class);
            websocketService.addOrUpdateTeamToUser(userId, teamDto);
            return "You has been added to team '"+team.getTeamName()+"'";
        }
        else if(!teamRequestRepo.existsBySenderIdAndTeam(userId,team)) {
            var request= TeamRequest.builder()
                    .senderId(userId)
                    .team(team)
                    .content(requestDto.getContent())
                    .createdAt(LocalDateTime.now())
                    .build();
            teamRequestRepo.save(request);

            return "Request has been sent successfully";
        }
        return "Request has been sent before! Please wait for admin of the team accepts";
    }

    @Transactional
    public void acceptNewMember(String requestId, boolean isAccepted) {
        TeamRequest request= teamRequestRepo.findById(requestId).orElseThrow(()->new BadRequestException("Request does not exist"));

        var userTm = teamMemberRepo.findByTeamIdAndUserId(request.getTeam().getId(), AuthUtil.getUserId());
        if(userTm.getRole() != TeamRole.LEADER)
            throw new AccessDeniedException("You don't have permission to accept members");

        request.setIsAccepted(isAccepted);
        teamRequestRepo.save(request);

        if(isAccepted){
            var requesterTm=teamMemberRepo.findByTeamIdAndUserId(request.getTeam().getId(),request.getSenderId());
            if(requesterTm==null) requesterTm=new TeamMember(request.getTeam(),request.getSenderId(), TeamRole.MEMBER);
            else requesterTm.setRole(TeamRole.MEMBER);
            var savedMember=teamMemberRepo.save(requesterTm);

            var team=teamRepo.getTeamWithChannels(request.getTeam());
            var teamDto= modelMapper.map(team, ResTeamDto.class);
            
            websocketService.addOrUpdateTeamToUser(requesterTm.getUserId(),  teamDto);

            var memberDtos= userService.fetchUsersData(List.of(savedMember.getUserId()), List.of(savedMember));
            websocketService.addTeamMembers(team.getId(), memberDtos);
        }
    }

    @Override
    public void deleteTeamRequest(String requestId) {
        String userId= AuthUtil.getUserId();
        TeamRequest request= teamRequestRepo.findById(requestId).orElseThrow(()->new BadRequestException("Request does not exist"));
        if(!request.getSenderId().equals(userId))
            throw new AccessDeniedException("You don't have permission to delete request");
        teamRequestRepo.delete(request);
    }

    @Override
    public List<ResTeamRequestDto> getTeamRequests(String teamId) {
        var tm = teamMemberRepo.findByTeamIdAndUserId(teamId, AuthUtil.getUserId());
        if(tm.getRole() != TeamRole.LEADER)
            throw new AccessDeniedException("You do not have permission to get requests of this team");

        var requests= teamRequestRepo.getTeamRequestMessages(teamId);
        var senderIds= requests.stream().map(request->request.getSenderId()).toList();
        Map<String, ResUserDto> sendersMap= new HashMap<>();
        userService.getUsersByIds(senderIds).forEach(sender-> sendersMap.put(sender.getId(), sender));

        return requests.stream()
                .map(request-> {
                    var resRequest=modelMapper.map(request, ResTeamRequestDto.class);
                    resRequest.setSender(sendersMap.get(request.getSenderId()));
                    return resRequest;
                })
                .toList();
    }

    @Override
    public List<ResTeamRequestDto> getSendedRequests() {
        String userId= AuthUtil.getUserId();
        var requests= teamRequestRepo.getSentRequestMessages(userId);
        return requests.stream()
                .map(request-> modelMapper.map(request, ResTeamRequestDto.class))
                .toList();
    }
}
