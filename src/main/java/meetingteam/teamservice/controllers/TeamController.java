package meetingteam.teamservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import meetingteam.teamservice.dtos.Team.CreateTeamDto;
import meetingteam.teamservice.dtos.Team.ResTeamDto;
import meetingteam.teamservice.dtos.Team.UpdateTeamDto;
import meetingteam.teamservice.services.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<Void> createTeam(
           @Valid @RequestBody CreateTeamDto teamDto){
        teamService.createTeam(teamDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<String> updateTeam(
            @Valid @RequestBody UpdateTeamDto teamDto){
        return ResponseEntity.ok(teamService.updateTeam(teamDto));
    }

    @GetMapping
    public ResponseEntity<List<ResTeamDto>> getJoinedTeams() {
        return ResponseEntity.ok(teamService.getJoinedTeams());
    }
}
