package meetingteam.teamservice.dtos.TeamRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AcceptRequestDto {
    @NotBlank
    private String requestId;

    @NotNull
    private Boolean isAccepted;
}
