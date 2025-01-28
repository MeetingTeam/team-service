package meetingteam.teamservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import meetingteam.teamservice.dtos.TeamRequest.AcceptRequestDto;
import meetingteam.teamservice.dtos.TeamRequest.CreateTeamRequestDto;
import meetingteam.teamservice.dtos.TeamRequest.ResTeamRequestDto;
import meetingteam.teamservice.services.TeamRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team-request")
@RequiredArgsConstructor
public class TeamRequestController {
    private final TeamRequestService teamRequestService;

    @PostMapping
    public ResponseEntity<String> createTeamRequest(
            @RequestBody @Valid CreateTeamRequestDto requestDto){
        return ResponseEntity.ok(teamRequestService.requestToJoinTeam(requestDto));
    }

    @PostMapping("/accept")
    public ResponseEntity<Void> acceptNewMember(
            @RequestBody @Valid AcceptRequestDto acceptDto){
        teamRequestService.acceptNewMember(acceptDto.getRequestId(), acceptDto.getIsAccepted());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<ResTeamRequestDto>> getTeamRequestMessages(
            @PathVariable("teamId") String teamId){
        return ResponseEntity.ok(teamRequestService.getTeamRequestMessages(teamId));
    }

    @GetMapping
    public ResponseEntity<List<ResTeamRequestDto>> getTeamRequestMessages(){
        return ResponseEntity.ok(teamRequestService.getSendedRequestMessages());
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> deleteTeamRequest(
            @PathVariable("requestId") String requestId){
        teamRequestService.deleteTeamRequest(requestId);
        return ResponseEntity.ok().build();
    }
}
