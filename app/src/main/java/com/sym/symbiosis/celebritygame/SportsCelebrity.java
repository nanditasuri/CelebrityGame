package com.sym.symbiosis.celebritygame;

/**
 * Created by symbiosis on 3/17/2015.
 */
public class SportsCelebrity extends Celebrity {
    String name;
    String sportName;
    String sportType;
    String playerActive;
    String bornYear;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public String getPlayerActive() {
        return playerActive;
    }

    public void setPlayerActive(String playerActive) {
        this.playerActive = playerActive;
    }

    public String getBornYear() {
        return bornYear;
    }

    public void setBornYear(String bornYear) {
        this.bornYear = bornYear;
    }
}
