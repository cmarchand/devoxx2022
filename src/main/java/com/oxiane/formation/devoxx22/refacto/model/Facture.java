package com.oxiane.formation.devoxx22.refacto.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Calendar;

@Entity
public class Facture {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(optional = false)
    private Client client;
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Calendar date;
    private BigDecimal totalHT;
    private BigDecimal totalTVA;
    private BigDecimal totalTTC;
    @Transient
    private Vistamboire vistamboire;

    public Facture() {
        this.vistamboire = new Vistamboire();
    }
    public Facture(long id, Client client, Calendar date, BigDecimal totalHT, BigDecimal totalTVA, BigDecimal totalTTC) {
        this();
        this.id = id;
        this.client = client;
        this.date = date;
        this.totalHT = totalHT;
        this.totalTVA = totalTVA;
        this.totalTTC = totalTTC;
    }
    public Facture(Client client, Calendar date) {
        this();
        this.client = client;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public Calendar getDate() {
        return date;
    }

    public BigDecimal getTotalHT() {
        return totalHT;
    }

    public BigDecimal getTotalTVA() {
        return totalTVA;
    }

    public BigDecimal getTotalTTC() {
        return totalTTC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Facture)) return false;

        Facture facture = (Facture) o;

        return id.equals(facture.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Facture{" +
                "id=" + id +
                ", client=" + client +
                ", date=" + date +
                ", totalHT=" + totalHT +
                ", totalTVA=" + totalTVA +
                ", totalTTC=" + totalTTC +
                '}';
    }

    public void calculate(Vistamboire vistamboire) {
        totalHT = vistamboire.getPrixUnitaireHT();
        totalTVA = totalHT.multiply(vistamboire.getTauxTVA());
        totalTTC = totalHT.add(totalTVA);
    }
}
