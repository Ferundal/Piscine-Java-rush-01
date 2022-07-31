package edu.school21.tanks.repositories;

import edu.school21.tanks.models.Player;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

public class PlayersRepository {
    private final JdbcTemplate template;

    private final String SQL_FIND_ALL = "SELECT * FROM players";

    private final String SQL_FIND_BY_ID = "SELECT * FROM players WHERE id = ?";

    private final String SQL_UPDATE_SHOTS = "UPDATE players SET shots = ? WHERE id = ?";

    private final String SQL_UPDATE_HITS = "UPDATE players SET hits = ? WHERE id = ?";

    private final String SQL_SAVE = "INSERT INTO players (id, shots, hits) VALUES (?, ?, ?)";

    private final String SQL_DELETE = "DELETE FROM players WHERE id = ?";

    private RowMapper<Player> userRowMapper = (rs, rowNum) ->
            new Player(rs.getLong("id"),
                    rs.getInt("shots"),
                    rs.getInt("hits"));

    public PlayersRepository(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    public Player findById(Long id) {
        List<Player> userList = template.query(SQL_FIND_BY_ID, userRowMapper, id);
        if (userList.isEmpty()) {
            return null;
        } else {
            return userList.get(0);
        }
    }


    public List<Player> findAll() {
        return template.query(SQL_FIND_ALL, userRowMapper);
    }

    public void save(Player entity) {
        template.update(SQL_SAVE, entity.getId(), entity.getShots(), entity.getHits());
    }

    public void updateShots(Player entity) {
        template.update(SQL_UPDATE_SHOTS, entity.getShots() + 1, entity.getId());
    }

    public void updateHits(Player entity) {
        template.update(SQL_UPDATE_HITS, entity.getHits() + 1, entity.getId());
    }

    public void delete(Long id) {
        template.update(SQL_DELETE, id);
    }
}
