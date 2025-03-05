package com.ddaaniel.armchair_management.controller.service;

import com.ddaaniel.armchair_management.controller.exception.AssentoInvalidoException;
import com.ddaaniel.armchair_management.controller.exception.BadRequestException;
import com.ddaaniel.armchair_management.controller.exception.NotFoundException;
import com.ddaaniel.armchair_management.model.Pessoa;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.repository.PessoaRepository;
import com.ddaaniel.armchair_management.model.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ServicePessoa {

    // Dependências
    @Autowired
    private PessoaRepository pessoaRepository;
    @Autowired
    private SeatRepository seatRepository;


    public void removePessoaFromSeat(Integer position) {
        if (position <= 0 || position > 15) {   // Verifica se é um parâmetro válido
            throw new AssentoInvalidoException("O assento informado é inválido.");
        }

        // Buscando seat de acordo com position
        Seat armchair = seatRepository.findByPosition(position)
                .orElseThrow(()-> new NotFoundException("Poltrona não encontrada."));

        // Garantindo que a poltrona não esteja vazia
        if (armchair.getPessoa() == null) {
            throw new BadRequestException("A Poltrona já está desocupada.");
        }

        Pessoa pessoa = armchair.getPessoa();

        armchair.setPessoa(null);
        armchair.setFree(true);
        seatRepository.save(armchair);

        pessoaRepository.delete(pessoa);
    }

}
