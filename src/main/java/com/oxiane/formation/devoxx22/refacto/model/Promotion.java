package com.oxiane.formation.devoxx22.refacto.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Calendar;

@Entity
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Calendar dateDebut;
    @Temporal(TemporalType.DATE)
    private Calendar dateFin;
    private String nom;
    private BigDecimal montantRemise;
    private BigDecimal pourcentageRemise;
    private boolean exclusive;

    public Promotion() {

    }
    public Promotion(Long id, Calendar dateDebut, Calendar dateFin, String nom, BigDecimal montantRemise, BigDecimal pourcentageRemise, boolean exclusive) throws RuntimeException {
        this();
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.nom = nom;
        this.montantRemise = montantRemise;
        this.pourcentageRemise = pourcentageRemise;
        this.exclusive = exclusive;
        validate();
    }
    public Promotion(Calendar dateDebut, Calendar dateFin, String nom, BigDecimal montantRemise, BigDecimal pourcentageRemise, boolean exclusive) throws RuntimeException {
        this(null, dateDebut, dateFin, nom, montantRemise, pourcentageRemise, exclusive);
    }

    /**
     * Si la promotion n'est pas valide, jette une RuntimeException
     * @throws RuntimeException
     */
    private void validate() throws RuntimeException {
        if(
                montantRemise!=null && !montantRemise.equals(BigDecimal.ZERO) &&
                pourcentageRemise!=null && !pourcentageRemise.equals(BigDecimal.ZERO)
        ) {
            throw new RuntimeException("Une promotion ne peut pas être en montant et en pourcentage en même temps");
        }
    }

    public Long getId() {
        return id;
    }

    public Calendar getDateDebut() {
        return dateDebut;
    }

    public Calendar getDateFin() {
        return dateFin;
    }

    public String getNom() {
        return nom;
    }

    public BigDecimal getMontantRemise() {
        return montantRemise;
    }

    public BigDecimal getPourcentageRemise() {
        return pourcentageRemise;
    }

    public boolean isExclusive() {
        return exclusive;
    }
}
