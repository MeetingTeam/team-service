package meetingteam.teamservice.dtos.Team;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

@Data
public class UpdateTeamDto{
    @NotBlank @UUID
    private String id;

    private String teamName;

    private String urlIcon;

    private Boolean autoAddMember;
}
