package meetingteam.teamservice.services;

public interface RabbitmqService {
    void sendToUser(String userId, String topic, Object payload);
    void sendToTeam(String teamId, String topic, Object payload);
    void sendToTeamPrivate(String teamId, String topic, Object payload);
}
