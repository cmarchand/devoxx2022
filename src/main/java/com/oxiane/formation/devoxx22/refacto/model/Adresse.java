package com.oxiane.formation.devoxx22.refacto.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Adresse {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String adresse1;
    private String adresse2;
    private String adresse3;
    private String codepostal;
    private String ville;
    private String pays;

    public Adresse() {
        super();
    }

    public Adresse(
            Long id,
            String adresse1,
            String adresse2,
            String adresse3,
            String codePostal,
            String ville,
            String pays) {
        this.id = id;
        this.adresse1 = adresse1;
        this.adresse2 = adresse2;
        this.adresse3 = adresse3;
        this.codepostal = codePostal;
        this.ville = ville;
        this.pays = pays;
    }

    public Adresse(
            String adresse1,
            String adresse2,
            String adresse3,
            String codePostal,
            String ville,
            String pays) {
        this(null, adresse1, adresse2, adresse3, codePostal, ville, pays);
    }

    public Long getId() {
        return id;
    }

    public String getAdresse1() {
        return adresse1;
    }

    public String getAdresse2() {
        return adresse2;
    }

    public String getAdresse3() {
        return adresse3;
    }

    public String getCodepostal() {
        return codepostal;
    }

    public String getVille() {
        return ville;
    }

    public String getPays() {
        return pays;
    }

    public void setAdresse1(String adresse1) {
        this.adresse1 = adresse1;
    }

    public void setAdresse2(String adresse2) {
        this.adresse2 = adresse2;
    }

    public void setAdresse3(String adresse3) {
        this.adresse3 = adresse3;
    }

    public void setCodepostal(String codepostal) {
        this.codepostal = codepostal;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Adresse)) return false;

        Adresse adresse = (Adresse) o;

        return id.equals(adresse.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Adresse{" +
                "id=" + id +
                ", adresse2='" + adresse2 + '\'' +
                ", codepostal='" + codepostal + '\'' +
                ", ville='" + ville + '\'' +
                '}';
    }
}