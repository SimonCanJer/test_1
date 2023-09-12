package com.intuit.test.model.dao.api.entities;
import java.util.Date;

/**
 * interface representing a player getters.
 * It  is a unified  data representation interface, which must be implemented for a concrete
 * POJO and NON POJO anemic objects
 */

public interface IPlayer {



    public String getPlayerID();

    public int getBirthYear();

    public int getBirthMonth();

    public int getBirthDay();

    public String getBirthCountry();

    public String getBirthState();

    public String getBirthCity() ;

    public int getDeathYear() ;

    public int getDeathMonth() ;
    public int getDeathDay();

    public String getDeathCountry();
    public String getDeathState() ;

    public String getDeathCity();
    public String getNameFirst();

    public String getNameLast() ;

    public String getNameGiven() ;

    public int getWeight();

    public int getHeight() ;

    public String getBats() ;

    public String getTthrows() ;

    public Date getDebut() ;

    public Date getFinalGame();

    public String getRetroID() ;

    public String getBbrefID() ;
}
