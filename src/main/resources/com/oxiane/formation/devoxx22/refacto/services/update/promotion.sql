# Creation de la table des promotions et des relations entre Facture et Promotion

DELIMITER ;

CREATE TABLE PROMOTION (
    ID BIGINT NOT NULL PRIMARY KEY,
    DATE_DEBUT TIMESTAMP NOT NULL,
    DATE_FIN TIMESTAMP NOT NULL,
    NOM VARCHAR(100) NOT NULL,
    MONTANT_REMISE NUMERIC(19,2),
    POURCENTAGE_REMISE NUMERIC(8,4),
    EXCLUSIVE BOOLEAN DEFAULT FALSE
);

CREATE TABLE FACTURE_PROMOTION (
    ID_FACTURE BIGINT NOT NULL,
    ID_PROMOTION BIGINT NOT NULL,
    CONSTRAINT UNICITY UNIQUE(ID_FACTURE, ID_PROMOTION),
    CONSTRAINT FK_FACTURE FOREIGN KEY (ID_FACTURE) REFERENCES FACTURE,
    CONSTRAINT FK_PROMOTION FOREIGN KEY (ID_PROMOTION) REFERENCES PROMOTION
);