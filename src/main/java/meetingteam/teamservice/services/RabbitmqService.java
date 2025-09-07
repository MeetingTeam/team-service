package meetingteam.teamservice.services;

import meetingteam.teamservice.dtos.Notification.MailDto;

public interface RabbitmqService {
    void sendToUser(String userId, String topic, Object payload);
    void sendToTeam(String teamId, String topic, Object payload);
    void sendEmailNotification(MailDto mailDto);
}
