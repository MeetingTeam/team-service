package meetingteam.teamservice.repositories;

import meetingteam.teamservice.models.Team;
import meetingteam.teamservice.models.TeamRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRequestRepository extends JpaRepository<TeamRequest, String> {
    Boolean existsBySenderIdAndTeam(String senderId, Team team);

    @Query("select tr from TeamRequest tr where tr.team.id=?1 and tr.isAccepted is null ")
    List<TeamRequest> getTeamRequestMessages(String teamId);

    @Query("select tr from TeamRequest tr where tr.senderId=?1 and tr.team is not null")
    List<TeamRequest> getSentRequestMessages(String senderId);
}
