package com.intuit.test.rest.spring;

import com.intuit.test.model.dao.api.IDao;
import com.intuit.test.model.dao.api.entities.IPlayer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicLong;

@RestController()
@Slf4j
@RequestMapping(path = "${api.uri:/api}")
public class RestEndPoint {

    public static final String PLAYERS = "players";
    @Autowired
    IDao dao;

    @Autowired
    MeterRegistry micrometer;

    private AtomicLong counterAll= new AtomicLong();
    private AtomicLong counterConcrete= new AtomicLong();

    /**
     * init
     * intentioanlly in pack visible
     */
    @PostConstruct
    void init(){
        Gauge.builder(PLAYERS,counterAll::get).
                tag("retrieval","all").
                description("retrieve all query").
                register(micrometer);
        Gauge.builder("usercontroller.usercount",counterConcrete::get).
                tag("retrievel","concrete").
                description("retrieve concrete query").
                register(micrometer);

    }

    /**
     * The method is mapped to handle a get request for all players
     *
     * @return Either collection of {@link Player} when no error occured, or
     * InternalError when it has been occured
     * intentionly in pack visible
     */
    @Operation(summary = "returns all player", description = "Returns players array adhereing the WebFlux concept.Use related API to get cancel and backpressure options")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),

    })
    @GetMapping("${api.players.uri:/players}")
    ResponseEntity<Flux<IPlayer>> allPlayers() {
        log.debug("all players request");
        counterAll.incrementAndGet();
        try {
            return ResponseEntity.ok(dao.allPlayers());
        } catch (Exception e) {
            log.error("exception in all players {} ", e.getMessage());
            return ResponseEntity.internalServerError().build();

        }

    }

    /**
     * Get a particular player by id mapped
     *
     * @param playerId - identifier of player
     * @return ResponseEntity, which either contains the searched entity, whe found.
     * or the NotFound error code.
     * intentionly in pack visible
     */
    @Operation(summary = "Get am player by id", description = "Returns a player with id specified")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - The player is unknown")
    })
    @GetMapping("${api.players.uri:/players}/{playerId}")
    ResponseEntity<IPlayer> player(@PathVariable() String playerId) {

        log.debug("request for player with id {} ", playerId);
        counterConcrete.incrementAndGet();
        try {
            IPlayer player = dao.get(playerId);
            if (player == null) {
                log.debug("player with id {} is not found ",playerId);
                return ResponseEntity.notFound().build();
            }
            log.debug("player with id {} successfully retrieved ",playerId);
            return ResponseEntity.ok(player);
        } catch (Exception e) {
            log.error("excpetion in player retrieve {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}
