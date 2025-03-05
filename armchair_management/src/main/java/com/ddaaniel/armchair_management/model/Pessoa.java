package com.ddaaniel.armchair_management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Pessoa {

    public Pessoa() {} // Construtor vazio para JPA

    public Pessoa(String name, String cpf) {
        this.name = name;
        this.cpf = cpf;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pessoaID;

    @Column(length = 50)
    private String name;

    @Column(length = 11)
    private String cpf;

    @OneToOne
    @JoinColumn(name = "seat_ID")
    private Seat seat;
}
