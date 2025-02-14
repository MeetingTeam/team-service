package meetingteam.teamservice.dtos.TeamRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTeamRequestDto {
    @NotBlank
    private String teamId;

    @NotNull
    private String content;
}
