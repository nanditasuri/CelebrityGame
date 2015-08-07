package com.sym.symbiosis.celebritygame;

/**
 * Created by symbiosis on 3/2/2015.
 */
public class Celebrity {

    String celebName;
    String celebGender;
    int celebAlive;
    String celebContinent;
    String celebCountry;
    String celebProfession;
    String celebPhoto;
    String celebLink;

    public Celebrity() {

    }

    public String getCelebLink() {
        return celebLink;
    }

    public void setCelebLink(String celebLink) {
        this.celebLink = celebLink;
    }

    public String getCelebPhoto() {
        return celebPhoto;
    }

    public void setCelebPhoto(String celebPhoto) {
        this.celebPhoto = celebPhoto;
    }

    public String getCelebName() {
        return celebName;
    }

    public void setCelebName(String celebName) {
        this.celebName = celebName;
    }

    public String getCelebGender() {
        return celebGender;
    }

    public void setCelebGender(String celebGender) {
        this.celebGender = celebGender;
    }

    public int getCelebAlive() {
        return celebAlive;
    }

    public void setCelebAlive(int celebAlive) {
        this.celebAlive = celebAlive;
    }

    public String getCelebContinent() {
        return celebContinent;
    }

    public void setCelebContinent(String celebContinent) {
        this.celebContinent = celebContinent;
    }

    public String getCelebCountry() {
        return celebCountry;
    }

    public void setCelebCountry(String celebCountry) {
        this.celebCountry = celebCountry;
    }

    public String getCelebProfession() {
        return celebProfession;
    }

    public void setCelebProfession(String celebProfession) {
        this.celebProfession = celebProfession;
    }

    @Override
    public String toString() {
        return "Celebrity{" +
                "celebName='" + celebName + '\'' +
                ", celebGender='" + celebGender + '\'' +
                ", celebAlive=" + celebAlive +
                ", celebContinent='" + celebContinent + '\'' +
                ", celebCountry='" + celebCountry + '\'' +
                ", celebProfession='" + celebProfession + '\'' +
                ", celebPhoto='" + celebPhoto + '\'' +
                ", celebLink ='" + celebLink + '\'' +
                '}';
    }
}
