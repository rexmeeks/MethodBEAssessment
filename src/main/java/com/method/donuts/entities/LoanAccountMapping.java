package com.method.donuts.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "loanAccounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"dunkinId", "loanNumber", "plaidId"})
})
public class LoanAccountMapping {

    @Column
    private String dunkinId;

    @Id
    @Column
    private String accountId;

    @Column
    private String loanNumber;

    @Column
    private String plaidId;

    public LoanAccountMapping(String dunkinId, String accountId, String loanNumber, String plaidId) {
        this.dunkinId = dunkinId;
        this.accountId = accountId;
        this.loanNumber = loanNumber;
        this.plaidId = plaidId;
    }

    public LoanAccountMapping() {

    }
}
