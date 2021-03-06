package com.oxiane.formation.devoxx22.refacto.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    private int qte;
    @Column(name = "REMISE")
    private BigDecimal remiseClient;
    @ManyToMany
    @JoinTable(
            name = "FACTURE_PROMOTION",
            joinColumns = @JoinColumn(name="ID_FACTURE", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "ID_PROMOTION", referencedColumnName = "ID")
    )
    private List<Promotion> promotions;

    public Facture() {
        promotions = new ArrayList<>();
    }
    public Facture(long id, Client client, Calendar date, BigDecimal totalHT, BigDecimal totalTVA, BigDecimal totalTTC) {
        this();
        this.id = id;
        this.client = client;
        this.date = date;
        this.totalHT = totalHT;
        this.totalTVA = totalTVA;
        this.totalTTC = totalTTC;
        this.remiseClient = BigDecimal.ZERO;
    }
    public Facture(Client client, Calendar date, int qte) {
        this();
        this.client = client;
        this.date = date;
        this.qte = qte;
        this.remiseClient = BigDecimal.ZERO;
    }
    public Facture(Client client, Calendar date) {
        this(client, date, 1);
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

    public int getQte() { return qte; }

    public List<Promotion> getPromotions() {
        return promotions;
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
        totalHT = vistamboire.getPrixUnitaireHT().multiply(BigDecimal.valueOf((long)qte));
        if(!BigDecimal.ZERO.equals(remiseClient)) {
            BigDecimal multiplicateurRemise = BigDecimal.ONE.min(remiseClient);
            totalHT = totalHT.multiply(multiplicateurRemise);
        }
        // application des promotions
        for(Promotion promotion: getPromotions()) {
            if(promotion.getMontantRemise()!=null) {
                totalHT = totalHT.subtract(promotion.getMontantRemise());
            } else {
                totalHT = totalHT.multiply(BigDecimal.ONE.subtract(promotion.getPourcentageRemise()));
            }
        }
        totalTVA = totalHT.multiply(vistamboire.getTauxTVA());
        totalTTC = totalHT.add(totalTVA);
    }

    public void setRemiseClient(BigDecimal remiseClient) {
        this.remiseClient = remiseClient;
    }

    public BigDecimal getRemiseClient() {
        return remiseClient;
    }
}
