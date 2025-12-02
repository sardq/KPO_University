package demo.Core.Models;

import demo.Core.Configuration.Constants;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;

@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = Constants.SEQUENCE_NAME)
    @SequenceGenerator(name = Constants.SEQUENCE_NAME, sequenceName = Constants.SEQUENCE_NAME, allocationSize = 1)
    protected Long id;

    protected BaseEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
