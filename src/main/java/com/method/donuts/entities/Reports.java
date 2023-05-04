package com.method.donuts.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "reports")
public class Reports {

    @Id
    private String id;

    @Column
    private String fileName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    @CreationTimestamp
    private Date uploadedOn;

    public Reports(String id, String fileName) {
        this.id = id;
        this.fileName = fileName;
    }

    public Reports() {

    }

}
