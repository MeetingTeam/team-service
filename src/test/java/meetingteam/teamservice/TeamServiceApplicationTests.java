package meetingteam.teamservice;

import meetingteam.teamservice.models.Team;
import meetingteam.teamservice.models.TeamMember;
import meetingteam.teamservice.services.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TeamServiceApplicationTests {

    @Autowired
    private TeamService teamService;

    @Test
    void contextLoads() {
        assertNotNull(teamService, "The teamService is not null");
    }
}
