package tech.bts.cardgame.repository;

import org.springframework.stereotype.Repository;
import tech.bts.cardgame.model.Deck;
import tech.bts.cardgame.model.Game;
import tech.bts.cardgame.util.DataSourceUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GameRepositoryJdbc {

    private Map<Long, Game> gameMap;
    //private long nextId;

    public GameRepositoryJdbc(){
        gameMap = new HashMap<>();
        //nextId = 0;
    }

    public void create(ResultSet rs) throws SQLException {

        //TODO: double-check create method
        //game.setId(nextId);

        Game g = new Game(new Deck());
        g.setId(rs.getInt("id"));

        if(rs.getString("players") != null){
            String[] playersArray = rs.getString("players").split(",");
            for ( String player : playersArray) {
                g.join(player);
            }
        }

        g.setState(Game.State.valueOf(rs.getString("state"))); //TODO: necessary?

        gameMap.put(g.getId(), g);

        rs.close();
        DataSourceUtil.getDataSourceInPath().getConnection().createStatement().close();
        DataSourceUtil.getDataSourceInPath().getConnection().close();

        //nextId++;
    }

    public Game getById(long id) throws SQLException{

        ResultSet rs = DataSourceUtil.getDataSourceInPath().getConnection().createStatement().executeQuery("select * from games WHERE id = " + id);
        create(rs);
        return gameMap.get(id);
    }

    public List<Game> getAll() throws SQLException{

        ResultSet rs = DataSourceUtil.getDataSourceInPath().getConnection().createStatement().executeQuery("select * from games");
        while (rs.next()) {
            create(rs);
        }
        return new ArrayList<>(gameMap.values());
    }
}