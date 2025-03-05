package com.ddaaniel.armchair_management.controller;

import com.ddaaniel.armchair_management.controller.service.ServicePessoa;
import com.ddaaniel.armchair_management.controller.service.ServiceSeat;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.record.RecordDTO;
import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/seats")
public class SeatController {

    @Autowired
    private ServiceSeat serviceSeat;

    @Autowired
    private ServicePessoa servicePessoa;

    // buscando os status de cada poltrona pela sua posição
    @GetMapping
    public ResponseEntity<List<Seat>> getAllStatusPoltronas(){
        var response = serviceSeat.listStatusOfAllSeats();
        return ResponseEntity.ok().body(response);
    }

    // Buscando detalhes de uma poltrona específica pelo seu número de posição
    @GetMapping("/{position}")
    public ResponseEntity<Seat> getBySeat(@PathVariable Integer position){
        var response = serviceSeat.detailsFromSpecificSeat(position);
        return ResponseEntity.ok().body(response);
    }

    // Alocando uma poltrona para pessoa
    @PutMapping("/allocate")
    public ResponseEntity<?> addPessoaToSeat (@RequestBody RecordDTO dto)  {
        serviceSeat.allocateSeatToPessoa(dto.position(), dto.name(), dto.cpf());
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "Poltrona alocada com sucesso.");
        return ResponseEntity.ok(response);
    }

    // Removendo a relação de pessoa e poltrona e também removendo o registro da respectiva
    // pessoa para não ficar acumulando no BD
    @PutMapping("/remove/{position}")
    public ResponseEntity<?> removePessoaFromSeat(@PathVariable Integer position) {

        servicePessoa.removePessoaFromSeat(position);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "Pessoa removida da Poltrona.");
        return ResponseEntity.ok(response);
    }
}
