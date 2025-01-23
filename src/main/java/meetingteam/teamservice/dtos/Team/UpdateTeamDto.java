package meetingteam.teamservice.dtos.Team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

@Data
public class UpdateTeamDto extends CreateTeamDto {
    @NotBlank
    @UUID
    private String id;

    private String iconFilename;

    @NotNull
    private Boolean autoAddMember;
}
