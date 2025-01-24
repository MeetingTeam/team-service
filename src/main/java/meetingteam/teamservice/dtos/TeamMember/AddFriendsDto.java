package meetingteam.teamservice.dtos.TeamMember;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

import java.util.List;

@Data
public class AddFriendsDto {
    @NotNull
    private List<String> friendIds;

    @NotBlank
    @UUID
    private String teamId;
}
