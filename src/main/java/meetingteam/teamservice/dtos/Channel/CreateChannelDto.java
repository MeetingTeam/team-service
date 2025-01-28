package meetingteam.teamservice.dtos.Channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import meetingteam.teamservice.models.enums.ChannelType;
import org.hibernate.validator.constraints.UUID;

@Data
public class CreateChannelDto {
    @NotBlank @UUID
    private String teamId;

    @NotBlank
    private String channelName;

    private String description;

    @NotNull
    private ChannelType type;
}
