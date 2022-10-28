package com.redhat.cruffalo.quarkus.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import java.time.ZonedDateTime;

@Entity
public class Hit extends PanacheEntity {

    public String message;

    public ZonedDateTime timestamp;

}
