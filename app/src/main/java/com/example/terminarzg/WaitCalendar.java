package com.example.terminarzg;

public class WaitCalendar {
    private String wCzas;
    private String wFryzjer;
    private String userUID;
    private String wImie;
    private String wNazwisko;
    private String wDataStr;
    private Boolean czyOk;
    private String fEmail;


    public WaitCalendar(){}

    public WaitCalendar(String wCzas,
                        String wFryzjer,
                        int wData, String userUID,
                        String wImie, String wNazwisko,
                        String wDataStr, Boolean czyOk, String fEmail) {
        this.wCzas = wCzas;
        this.wFryzjer = wFryzjer;
        this.userUID = userUID;
        this.wImie = wImie;
        this.wNazwisko = wNazwisko;
        this.wDataStr = wDataStr;
        this.czyOk = czyOk;
        this.fEmail = fEmail;
    }
    public String getwCzas(){
        return wCzas;
    }
    public String getwFryzjer(){
        return wFryzjer;
    }
    public String getUserUID(){ return userUID;}
    public String getwImie(){return wImie;}
    public String getwNazwisko(){return wNazwisko;}
    public String getwDataStr(){return wDataStr;}
    public Boolean getczyOk(){return czyOk;}
    public String getfEmail(){return fEmail;}
}
