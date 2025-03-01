package meetingteam.teamservice.dtos.TeamRequest;

import lombok.Data;
import meetingteam.teamservice.dtos.Team.RequestTeamDto;
import meetingteam.teamservice.dtos.Team.ResTeamDto;
import meetingteam.teamservice.dtos.User.ResUserDto;

import java.time.LocalDateTime;

@Data
public class ResTeamRequestDto {
    private String id;

    private ResUserDto sender;

    private RequestTeamDto team;

    private String content;

    private Boolean isAccepted;

    private LocalDateTime createdAt;
}
