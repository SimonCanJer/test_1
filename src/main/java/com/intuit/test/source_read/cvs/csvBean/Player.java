package com.intuit.test.source_read.cvs.csvBean;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.intuit.test.model.dao.api.IDao;
import com.intuit.test.model.dao.api.entities.IPlayer;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

/**
 * This class is a data transport objec  when reading from CSV.
 * Implements the {@link IPlayer} interface for compatability with the common compatibility with the  {@link IDao} signatures
 */
@Getter
@Setter
public class Player implements IPlayer {

   @CsvBindByName(column="playerID")
    private String playerID;

    @CsvBindByName(column="birthYear")
    private int birthYear;

    @CsvBindByName(column="birthMonth")
    private int birthMonth;

 @CsvBindByName(column="birthDay")
    private int birthDay;

 @CsvBindByName(column="birthCountry")
    private String birthCountry;

 @CsvBindByName(column="birthState")
    private String birthState;

 @CsvBindByName(column="birthCity")
    private String birthCity;

 @CsvBindByName(column="deathYear")
    private int deathYear;

 @CsvBindByName(column="deathMonth")
    private int deathMonth;

 @CsvBindByName(column="deathDay")
    private int deathDay;

 @CsvBindByName(column="deathCountry")
    private String deathCountry;

 @CsvBindByName(column="deathState")
    private String deathState;

 @CsvBindByName(column="deathCity")
    private String  deathCity;

    @CsvBindByName(column="nameFirst")
    private String nameFirst;

 @CsvBindByName(column="nameLast")
    private String nameLast;

 @CsvBindByName(column="nameGiven")
    private String nameGiven;

   @CsvBindByName(column="weight")
    private int weight;

    @CsvBindByName(column="height")
    private int height;

 @CsvBindByName(column="bats")
    private String bats;

    @JsonProperty("throws")
    @CsvBindByName(column="throws")
    private String tthrows;

 @CsvBindByName(column="debut")
    private Date debut;

 @CsvBindByName(column="finalGame")
    private Date finalGame;

 @CsvBindByName(column="retroID")
    private String retroID;

 @CsvBindByName(column=" bbrefID")
    private String bbrefID;


}
