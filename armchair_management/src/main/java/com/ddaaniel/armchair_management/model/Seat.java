package com.ddaaniel.armchair_management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tb_seats")
public class Seat {

    public Seat(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long SeatID;

    private Integer position;

    private Boolean free = true;

    @OneToOne(mappedBy = "seat", cascade = CascadeType.ALL)
    @JsonIgnore  // Ignora esse campo na serialização JSON para evitar loop
    private Pessoa pessoa;


}
