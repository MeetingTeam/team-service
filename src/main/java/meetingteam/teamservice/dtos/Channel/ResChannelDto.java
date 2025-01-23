package meetingteam.teamservice.dtos.Channel;

import lombok.Data;
import meetingteam.teamservice.models.enums.ChannelType;

@Data
public class ResChannelDto {
    private String id;

    private String channelName;

    private String description;

    private ChannelType type;
}
