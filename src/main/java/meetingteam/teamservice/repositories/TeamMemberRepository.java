package meetingteam.teamservice.repositories;

import meetingteam.teamservice.models.Team;
import meetingteam.teamservice.models.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember,String> {
	boolean existsByTeamAndUserId(Team team, String userId);
	
	@Query("select count(tm) from TeamMember tm where tm.userId=?1 and "
			+ "tm.team.id in (select c.team.id from Channel c where c.id=?2)")
	int existsByChannelIdAndU(String userId, String channelId);
	
	@Query("select tm from TeamMember tm where tm.team.id=?1 and tm.userId=?2")
	TeamMember findByTeamIdAndUserId(String teamId, String userId);

	@Query("select tm.userId from TeamMember tm where tm.team.id=?1")
	List<String> findUsersByTeamId(String teamId);
	
	@Query("select tm.role from TeamMember tm where tm.userId=?1 and tm.team.id=?2")
	String getRoleByUserIdAndTeamId(String userId, String teamId);
}
