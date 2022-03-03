package com.oxiane.formation.devoxx22.refacto.services.jpa.spi;

import com.oxiane.formation.devoxx22.refacto.model.Promotion;
import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;
import com.oxiane.formation.devoxx22.refacto.services.jpa.PromotionRepositoryCustom;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Component
public class PromotionRepositoryImpl implements PromotionRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Promotion> findPromotionsValidAtDate(Calendar date) {
        Query query = em.createQuery(
                "select p from Promotion p where p.dateDebut <= :date and :date <= p.dateFin",
                Promotion.class);
        query.setParameter("date", date, TemporalType.DATE);
        List<Promotion> liste = query.getResultList();
        if(liste==null || liste.isEmpty()) return Collections.emptyList();
        return liste;
    }
}
