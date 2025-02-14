package meetingteam.teamservice.dtos.Team;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTeamDto {
    @NotBlank
    private String teamName;
}
