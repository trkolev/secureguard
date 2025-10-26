package com.project.ins.policyCounter.model;

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
public class PolicyNumberCounter {

    @Id
    private String policy;

    @Column(nullable = false)
    private Long value;

}
