package meetingteam.teamservice.services;

import java.util.List;

import meetingteam.teamservice.models.Team;
import meetingteam.teamservice.models.TeamMember;

public interface NotificationService {
    void notifyJoinedTeam(Team team, List<TeamMember> members);
}
