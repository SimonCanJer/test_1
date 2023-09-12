package com.intuit.test.model.dao.dao_solr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intuit.test.model.dao.api.entities.IPlayer;
import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;

import java.time.Instant;
import java.util.Date;

/**
 * This class is a data transport object for Solr bean mechanism
 */

public class SolrPlayer implements IPlayer {

    //playerID,birthYear,birthMonth,birthDay,birthCountry,birthState,birthCity,deathYear,deathMonth,deathDay,deathCountry,deathState,deathCity,nameFirst,nameLast,nameGiven,weight,height,bats,throws,debut,finalGame,retroID,bbrefID


    @Getter
    @Field()
    private String playerID = "id_player";

    @Field()
    private long birthYear = 1957;

    @Field()
    private long birthMonth = 12;

    @Field()
    private long birthDay = 10;

    @Getter
    @Field()
    private String birthCountry = "USA";

    @Getter
    @Field()
    private String birthState = "FL";

    @Getter
    @Field()
    private String birthCity = "Maiami";


    @Field
    private long deathYear = -1;


    @Field
    private long deathMonth = -1;

    @Field
    private long deathDay = -1;

    @Getter
    @Field
    private String deathCountry;

    @Getter
    @Field
    private String deathState;

    @Field
    @Getter
    private String deathCity;

    @Getter
    @Field
    private String nameFirst = "Jarry";

    @Getter
    @Field
    private String nameLast = "Clark";

    @Getter
    @Field
    private String nameGiven = "Lightning";

    @Field
    private long weight = 100;

    @Field
    private long height = 200;

    @Getter
    @Field
    private String bats = "R";

    @Getter
    @JsonProperty("throws")
    @Field
    private String tthrows = "R";

    @Getter
    @Field()
    private Date debut = Date.from(Instant.now());

    @Getter
    @Field()
    private Date finalGame = Date.from(Instant.now());

    @Getter
    @Field
    private String retroID = "no";

    @Getter
    @Field
    private String bbrefID = "n";


    @Override
    public int getBirthYear() {
        return (int)birthYear;
    }

    @Override
    public int getBirthMonth() {
        return (int) birthMonth;
    }

    @Override
    public int getBirthDay() {
        return (int) birthDay;
    }

    @Override
    public int getDeathYear() {
        return (int) deathYear;
    }

    @Override
    public int getDeathMonth() {
        return (int) deathMonth;
    }

    @Override
    public int getDeathDay() {
        return (int) deathDay;
    }

    @Override
    public int getWeight() {
        return (int) weight;
    }

    @Override
    public int getHeight() {
        return (int) height;
    }


}
