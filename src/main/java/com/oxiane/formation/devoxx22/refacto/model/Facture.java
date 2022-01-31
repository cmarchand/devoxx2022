package com.oxiane.formation.devoxx22.refacto.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Calendar;

@Entity
public class Facture {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Client client;
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Calendar date;
    private BigDecimal totalHT;
    private BigDecimal totalTVA;
    private BigDecimal totalTTC;

    public Facture() {}
    public Facture(long id, Client client, Calendar date, BigDecimal totalHT, BigDecimal totalTVA, BigDecimal totalTTC) {
        this();
        this.id = id;
        this.client = client;
        this.date = date;
        this.totalHT = totalHT;
        this.totalTVA = totalTVA;
        this.totalTTC = totalTTC;
    }
    public Facture(Client client) {
        this();
        this.client = client;
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
}
