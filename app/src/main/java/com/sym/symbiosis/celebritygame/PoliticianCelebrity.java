package com.sym.symbiosis.celebritygame;

/**
 * Created by symbiosis on 3/19/2015.
 */
public class PoliticianCelebrity extends  Celebrity {
    String name;
    String partyName;
    String servedAs;
    String bornYear;
    String politicianActive;
    String occupation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public void setServedAs(String servedAs) {
        this.servedAs = servedAs;
    }

    public void setBornYear(String bornYear) {
        this.bornYear = bornYear;
    }

    public void setPoliticianActive(String politicianActive) {
        this.politicianActive = politicianActive;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getPartyName() {
        return partyName;
    }

    public String getServedAs() {
        return servedAs;
    }

    public String getBornYear() {
        return bornYear;
    }

    public String getPoliticianActive() {
        return politicianActive;
    }

    public String getOccupation() {
        return occupation;
    }

    @Override
    public String toString() {
        return "PoliticianCelebrity{" +
                "name='" + name + '\'' +
                ", partyName='" + partyName + '\'' +
                ", servedAs='" + servedAs + '\'' +
                ", bornYear='" + bornYear + '\'' +
                ", politicianActive='" + politicianActive + '\'' +
                ", occupation='" + occupation + '\'' +
                '}';
    }
}
