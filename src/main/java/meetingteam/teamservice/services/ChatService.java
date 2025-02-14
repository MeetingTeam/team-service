package meetingteam.teamservice.services;

public interface ChatService {
    void deleteMessagesByChannelId(String channelId);
    void deleteMessagesByTeamId(String teamId);
}
