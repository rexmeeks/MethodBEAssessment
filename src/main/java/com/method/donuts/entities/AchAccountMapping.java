package com.method.donuts.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "achAccounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"dunkinId", "routing", "bankNumber"})
})
public class AchAccountMapping {

    @Column
    private String dunkinId;

    @Id
    @Column
    private String accountId;

    @Column
    private String routing;

    @Column
    private String bankNumber;

    public AchAccountMapping(String dunkinId, String accountId, String routing, String bankNumber) {
        this.dunkinId = dunkinId;
        this.accountId = accountId;
        this.routing = routing;
        this.bankNumber = bankNumber;
    }

    public AchAccountMapping() {

    }
}
