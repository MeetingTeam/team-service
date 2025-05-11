package meetingteam.teamservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import meetingteam.commonlibrary.dtos.PagedResponseDto;
import meetingteam.teamservice.dtos.Team.CreateTeamDto;
import meetingteam.teamservice.dtos.Team.ResTeamDto;
import meetingteam.teamservice.dtos.Team.UpdateTeamDto;
import meetingteam.teamservice.services.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<ResTeamDto> createTeam(
           @Valid @RequestBody CreateTeamDto teamDto){
        return ResponseEntity.ok(teamService.createTeam(teamDto));
    }

    @PatchMapping
    public ResponseEntity<Void> updateTeam(
            @Valid @RequestBody UpdateTeamDto teamDto){
        teamService.updateTeam(teamDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(
            @PathVariable("teamId") String teamId){
        teamService.deleteTeam(teamId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<PagedResponseDto<ResTeamDto>> getJoinedTeams(
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize
    ) {
        return ResponseEntity.ok(teamService.getJoinedTeams(pageNo, pageSize));
    }

    @GetMapping("/search/{searchName}")
    public ResponseEntity<List<ResTeamDto>> searchTeamsByName(
            @PathVariable String searchName) {
        return ResponseEntity.ok(teamService.searchByTeamName(searchName));
    }
}
