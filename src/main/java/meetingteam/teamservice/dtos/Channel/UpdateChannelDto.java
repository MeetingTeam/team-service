package meetingteam.teamservice.dtos.Channel;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

@Data
public class UpdateChannelDto {
    @NotBlank @UUID
    private String id;

    private String channelName;

    private String description;
}
