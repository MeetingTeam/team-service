package meetingteam.teamservice.repositories;

import meetingteam.teamservice.models.Team;
import meetingteam.teamservice.models.TeamMember;
import meetingteam.teamservice.models.enums.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember,String> {
	boolean existsByTeamAndUserId(Team team, String userId);

	@Query("select count(tm) from TeamMember tm where tm.userId=?1 and " +
			"(?2 is null or tm.team.id=?2) and "+
			"(?3 is null or tm.team.id in (select c.team.id from Channel c where c.id=?3))")
	int existsByUserIdAndTeamIdAndChannelId(String userId, String teamId, String channelId);
	
	@Query("select tm from TeamMember tm where tm.team.id=?1 and tm.userId=?2")
	TeamMember findByTeamIdAndUserId(String teamId, String userId);

	@Query("select tm.userId from TeamMember tm where tm.team.id=?1")
	List<String> findUserIdsByTeamId(String teamId);
	
	@Query("select tm.role from TeamMember tm where tm.userId=?1 and tm.team.id=?2")
	TeamRole getRoleByUserIdAndTeamId(String userId, String teamId);

	List<TeamMember> findByTeam(Team team);

	@Modifying
	@Transactional
	@Query("delete from TeamMember tm where tm.team.id=?1")
	void deleteByTeamId(String teamId);
}
