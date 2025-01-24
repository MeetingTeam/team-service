package meetingteam.teamservice.services;

import meetingteam.teamservice.dtos.User.ResUserDto;
import meetingteam.teamservice.models.TeamMember;

import java.util.List;

public interface UserService {
    List<ResUserDto> getUsersByIds(List<TeamMember> members);
}
