package org.xbee.project.model;

import javax.persistence.*;

@MappedSuperclass
public abstract class AbstractEntity {

    public static final int START_SEQ = 1000;

    @Id
    @SequenceGenerator(name = "global_seq", sequenceName = "global_seq", allocationSize = 1, initialValue = START_SEQ)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq")
    protected Integer id;

    protected AbstractEntity() {
    }

    protected AbstractEntity(Integer id) {
        this.id = id;
    }

    public boolean isNew(){
        return getId() == null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
