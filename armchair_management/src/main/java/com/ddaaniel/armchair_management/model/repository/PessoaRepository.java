package com.ddaaniel.armchair_management.model.repository;

import com.ddaaniel.armchair_management.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

}
