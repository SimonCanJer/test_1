package com.intuit.test.model.dao.api;


import com.intuit.test.model.dao.api.entities.IPlayer;
import reactor.core.publisher.Flux;

/**
 * Dao interface declaring functionality of any potential Dao
 */
public interface IDao {

    /**
     * Reads and sends all players list {@link Flux} used in order to support asynchronous read}
     * @return Flux which porvides readen data
     */
    Flux<IPlayer> allPlayers();

    /**
     * Provides a concrete player or null, when not found
     * @param id - string id of player
     * @return {@link IPlayer}
     */
    IPlayer get(String id);
}
