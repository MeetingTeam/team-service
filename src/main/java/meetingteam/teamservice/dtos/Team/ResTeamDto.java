package meetingteam.teamservice.dtos.Team;

import lombok.Data;
import meetingteam.teamservice.dtos.Channel.ResChannelDto;

import java.util.List;

@Data
public class ResTeamDto {
    private String id;

    private String teamName;

    private String urlIcon;

    private Boolean autoAddMember=false;

    private List<ResChannelDto> channels;
}
