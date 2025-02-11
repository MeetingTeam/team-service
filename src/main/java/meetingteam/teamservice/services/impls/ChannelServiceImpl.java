package meetingteam.teamservice.services.impls;

import lombok.RequiredArgsConstructor;
import meetingteam.commonlibrary.exceptions.BadRequestException;
import meetingteam.commonlibrary.utils.AuthUtil;
import meetingteam.teamservice.contraints.WebsocketTopics;
import meetingteam.teamservice.dtos.Channel.CreateChannelDto;
import meetingteam.teamservice.dtos.Channel.ResChannelDto;
import meetingteam.teamservice.dtos.Channel.UpdateChannelDto;
import meetingteam.teamservice.models.Channel;
import meetingteam.teamservice.models.enums.ChannelType;
import meetingteam.teamservice.models.enums.TeamRole;
import meetingteam.teamservice.repositories.ChannelRepository;
import meetingteam.teamservice.repositories.TeamMemberRepository;
import meetingteam.teamservice.repositories.TeamRepository;
import meetingteam.teamservice.services.ChannelService;
import meetingteam.teamservice.services.ChatService;
import meetingteam.teamservice.services.MeetingService;
import meetingteam.teamservice.services.RabbitmqService;
import meetingteam.teamservice.utils.TeamRoleUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepo;
    private final TeamMemberRepository teamMemberRepo;
    private final TeamRepository teamRepo;
    private final RabbitmqService rabbitmqService;
    private final MeetingService meetingService;
    private final ChatService chatService;
    private final ModelMapper modelMapper;

    public void createChannel(CreateChannelDto channelDto) {
        TeamRole role= teamMemberRepo.getRoleByUserIdAndTeamId(
                AuthUtil.getUserId(), channelDto.getTeamId());
        TeamRoleUtil.checkLEADERRole(role);

        var channel= modelMapper.map(channelDto, Channel.class);
        channel.setTeam(teamRepo.getById(channelDto.getTeamId()));
        var savedChannel=channelRepo.save(channel);

        var resChannelDto= modelMapper.map(savedChannel, ResChannelDto.class);
        rabbitmqService.sendToTeam(channelDto.getTeamId(), WebsocketTopics.AddOrUpdateChannel, resChannelDto);
    }

    public void updateChannel(UpdateChannelDto channelDto) {
        var channel= channelRepo.findById(channelDto.getId())
                .orElseThrow(()-> new BadRequestException("Channel not found"));

        TeamRole role= teamMemberRepo.getRoleByUserIdAndTeamId(
                AuthUtil.getUserId(), channel.getTeam().getId());
        TeamRoleUtil.checkLEADERRole(role);

        if(channelDto.getChannelName()!=null)
            channel.setChannelName(channelDto.getChannelName());
        if(channelDto.getDescription()!=null)
            channel.setDescription(channelDto.getDescription());
        channelRepo.save(channel);

        var resChannelDto= modelMapper.map(channel, ResChannelDto.class);
        rabbitmqService.sendToTeam(channel.getTeam().getId(), WebsocketTopics.AddOrUpdateChannel, resChannelDto);
    }

    public void deleteChannel(String channelId) {
        var channel= channelRepo.findById(channelId)
                .orElseThrow(()-> new BadRequestException("Channel not found"));
        TeamRole role= teamMemberRepo.getRoleByUserIdAndTeamId(
                AuthUtil.getUserId(), channel.getTeam().getId());
        TeamRoleUtil.checkLEADERRole(role);

        if(channel.getType()== ChannelType.VOICE_CHANNEL) {
            meetingService.deleteMeetingsByChannelId(channel.getId());
        }
        else{
            chatService.deleteMessagesByChannelId(channelId);
        }

        channelRepo.delete(channel);
        rabbitmqService.sendToTeam(channel.getTeam().getId(), WebsocketTopics.DeleteChannel, channel.getId());
    }
}
