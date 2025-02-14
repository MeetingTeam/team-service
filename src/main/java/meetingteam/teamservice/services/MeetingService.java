package meetingteam.teamservice.services;

public interface MeetingService {
    void deleteMeetingsByChannelId(String channelId);
    void deleteMessagesByTeamId(String teamId);
}
