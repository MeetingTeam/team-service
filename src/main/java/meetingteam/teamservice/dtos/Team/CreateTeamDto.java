package meetingteam.teamservice.dtos.Team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

import java.util.List;

@Data
public class CreateTeamDto {
    @NotBlank
    private String teamName;

    @NotNull
    private List<String> memberIds;
}
