package meetingteam.teamservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import meetingteam.teamservice.dtos.TeamMember.AddFriendsDto;
import meetingteam.teamservice.dtos.TeamMember.ResTeamMemberDto;
import meetingteam.teamservice.services.TeamMemberService;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team-member")
@RequiredArgsConstructor
public class TeamMemberController {
    private final TeamMemberService teamMemberService;

    @PostMapping("/add-friends")
    public ResponseEntity<Void> addFriendsToTeam(
            @RequestBody @Valid AddFriendsDto addFriendsDto){
        teamMemberService.addFriendsToTeam(addFriendsDto.getFriendIds(), addFriendsDto.getTeamId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/leave-team/{teamId}")
    public ResponseEntity<Void> leaveTeam(
            @PathVariable String teamId){
        teamMemberService.leaveTeam(teamId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/kick-member")
    public ResponseEntity<Void> kickMember(
            @RequestParam("teamId") @UUID String teamId,
            @RequestParam("memberId") String memberId
    ){
        teamMemberService.kickMember(teamId, memberId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<List<ResTeamMemberDto>> getMembersOfTeam(
            @PathVariable("teamId") String teamId
    ){
        return ResponseEntity.ok(teamMemberService.getMembersOfTeam(teamId));
    }

    @GetMapping("/private/is-member-of-team")
    public ResponseEntity<Boolean> isMemberOfTeam(
            @RequestParam("userId") String userId,
            @RequestParam("channelId") String channelId){
        return ResponseEntity.ok(teamMemberService.isMemberOfTeam(userId, channelId));
    }
}
