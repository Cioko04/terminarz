package com.example.terminarzg;


public class UserProfile {
    private String imie;
    private String nazwisko;
    private String email;
    private Boolean czyFryzjer;

    public UserProfile(){}

    public UserProfile(String userImie, String userNazwisko, String userEmail, Boolean czyFryzjer) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.email = email;
        this.czyFryzjer = czyFryzjer;
    }
    public String getImie(){
        return imie;
    }
    public String getNazwisko(){
        return nazwisko;
    }
    public String getEmail(){
        return email;
    }
    public Boolean getCzyFryzjer(){
        return czyFryzjer;
    }
}

