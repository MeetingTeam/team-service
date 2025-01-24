package meetingteam.teamservice.utils;

import meetingteam.teamservice.models.enums.TeamRole;
import org.springframework.security.access.AccessDeniedException;

public class TeamRoleUtil {
    public static void checkJoinedTeam(){

    }
    public static void checkLEADERRole(TeamRole role){
        if(role==null || (role!=TeamRole.LEADER)){
            throw new AccessDeniedException("Only leader of this team have permission to do this task");
        }
    }
}
