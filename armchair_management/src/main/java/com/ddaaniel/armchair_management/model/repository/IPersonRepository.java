package com.ddaaniel.armchair_management.model.repository;

import com.ddaaniel.armchair_management.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IPersonRepository extends JpaRepository<Person, UUID> {

}
