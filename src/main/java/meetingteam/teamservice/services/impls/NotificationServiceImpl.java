package meetingteam.teamservice.services.impls;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import meetingteam.teamservice.dtos.Notification.MailDto;
import meetingteam.teamservice.dtos.Team.ResTeamDto;
import meetingteam.teamservice.dtos.TeamMember.ResTeamMemberDto;
import meetingteam.teamservice.models.Team;
import meetingteam.teamservice.models.TeamMember;
import meetingteam.teamservice.services.NotificationService;
import meetingteam.teamservice.services.RabbitmqService;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final RabbitmqService rabbitmqService;

    public void notifyJoinedTeam(Team team, List<TeamMember> members){
        var content = String.format(
            "You has been added to the team %s", 
            team.getTeamName()
        );
        
        var mailDto = new MailDto(
            "Join new team",
            content,
            members.stream().map(member -> member.getUserId()).toList()
        );

        rabbitmqService.sendEmailNotification(mailDto);
    }
}
