package com.project.ins.calimnumbergenerator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClaimNumberGenerator {

    @Id
    private String claim;

    @Column(nullable = false)
    private Long value;

}
