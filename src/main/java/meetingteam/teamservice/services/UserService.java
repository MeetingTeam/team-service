package meetingteam.teamservice.services;

import meetingteam.teamservice.dtos.User.ResUserDto;

import java.util.List;

public interface UserService {
    List<ResUserDto> getUsersByIds(List<String> userIds);
}
