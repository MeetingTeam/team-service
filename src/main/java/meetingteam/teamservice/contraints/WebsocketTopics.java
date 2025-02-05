package meetingteam.teamservice.contraints;

public class WebsocketTopics {
    public static final String AddOrUpdateTeam="team:add-or-update";
    public static final String DeleteTeam="team:delete";

    public static final String AddOrUpdateChannel="channel:add-or-update";

    public static final String AddTeamMembers="team-member:add-many";
    public static final String DeleteMember= "team-member:delete";

    public static final String NewTeamRequest= "team-request:create";
    public static final String RejectTeamRequest= "team-request:reject";
}
