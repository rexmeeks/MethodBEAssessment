package com.method.donuts.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "entities")
public class EntityMapping {

    @Id
    @Column
    private String dunkinId;

    @Column
    private String entityId;

    private Boolean individual;

    public EntityMapping(String dunkinId, String entityId, Boolean individual) {
        this.dunkinId = dunkinId;
        this.entityId = entityId;
        this.individual = individual;
    }

    public EntityMapping() {}
}
