package meetingteam.teamservice.services.impls;

import lombok.RequiredArgsConstructor;
import meetingteam.commonlibrary.exceptions.BadRequestException;
import meetingteam.commonlibrary.utils.AuthUtil;
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
import meetingteam.teamservice.utils.TeamRoleUtil;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepo;
    private final TeamMemberRepository teamMemberRepo;
    private final TeamRepository teamRepo;
    private final ModelMapper modelMapper;

    public ResChannelDto createChannel(CreateChannelDto channelDto) {
        TeamRole role= teamMemberRepo.getRoleByUserIdAndTeamId(
                AuthUtil.getUserId(), channelDto.getTeamId());
        TeamRoleUtil.checkLEADERRole(role);

        var channel= modelMapper.map(channelDto, Channel.class);
        channel.setTeam(teamRepo.getById(channelDto.getTeamId()));
        var savedChannel=channelRepo.save(channel);

        return modelMapper.map(savedChannel, ResChannelDto.class);
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
    }

    public void deleteChannel(String channelId) {
        var channel= channelRepo.findById(channelId)
                .orElseThrow(()-> new BadRequestException("Channel not found"));
        TeamRole role= teamMemberRepo.getRoleByUserIdAndTeamId(
                AuthUtil.getUserId(), channel.getTeam().getId());
        TeamRoleUtil.checkLEADERRole(role);

        if(channel.getType()== ChannelType.VIDEOCALL_CHANNEL) {
            // delete Meetings
        }
        //else delete chat messages

        channelRepo.delete(channel);
    }
}
