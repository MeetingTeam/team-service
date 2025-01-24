package meetingteam.teamservice.services;

import meetingteam.teamservice.dtos.Channel.CreateChannelDto;
import meetingteam.teamservice.dtos.Channel.ResChannelDto;
import meetingteam.teamservice.dtos.Channel.UpdateChannelDto;

public interface ChannelService {
    ResChannelDto createChannel(CreateChannelDto channelDto);
    void updateChannel(UpdateChannelDto channelDto);
    void deleteChannel(String channelId);
}
