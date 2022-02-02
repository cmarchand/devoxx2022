package com.oxiane.formation.devoxx22.refacto.model;

import javax.persistence.*;

@Entity
public class Client {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nom;
    private String prenom;
    @ManyToOne
    private Adresse adresse;

    public Client() {}
    public Client(Long id, String nom, String prenom, Adresse adresse) {
        this();
        this.id=id;
        this.nom=nom;
        this.prenom=prenom;
        this.adresse=adresse;
    }
    public Client(String nom, String prenom, Adresse adresse) {
        this(null, nom, prenom, adresse);
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;

        Client client = (Client) o;

        return id.equals(client.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                '}';
    }
}
