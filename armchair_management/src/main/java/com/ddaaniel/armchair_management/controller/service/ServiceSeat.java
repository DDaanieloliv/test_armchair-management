package com.ddaaniel.armchair_management.controller.service;

import com.ddaaniel.armchair_management.controller.exception.AssentoInvalidoException;
import com.ddaaniel.armchair_management.controller.exception.BadRequestException;
import com.ddaaniel.armchair_management.controller.exception.NotFoundException;
import com.ddaaniel.armchair_management.controller.exception.ValidationException;
import com.ddaaniel.armchair_management.model.Pessoa;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.repository.PessoaRepository;
import com.ddaaniel.armchair_management.model.repository.SeatRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceSeat {


    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    // listando status de todas as poltronas
    public List<Seat> listStatusOfAllSeats(){
        List<Seat> list = seatRepository.findAll();
        return list;
    }

    // listando detalhes de uma poltrona específica
    public Seat detailsFromSpecificSeat (Integer position){
        if (position <= 0 || position > 15) {
            throw new AssentoInvalidoException("O assento informado é inválido.");
        }

        return seatRepository.findByPosition(position)
                .orElseThrow(() -> new NotFoundException("Poltrona não encontrada."));
    }


    // O método salva a entidade poltrona com o seu respectivo usuário
    public void allocateSeatToPessoa(Integer position, String name, String cpf) {
        // Validação do tamanho do nome
        if (name == null || name.length() > 50) {
            throw new ValidationException("O nome deve ter no máximo 50 caracteres.");
        }

        // Validação do tamanho do CPF
        if (cpf == null || cpf.length() != 11 || !cpf.matches("\\d{11}")) {
            throw new ValidationException("O CPF deve conter exatamente 11 dígitos numéricos.");
        }

        if (position <= 0 || position > 15 ) {
            throw new AssentoInvalidoException("O assento informado é inválido.");
        }

        Seat seat = seatRepository.findByPosition(position)
                .orElseThrow(() -> new NotFoundException("Poltrona não encontrada."));

        if (!seat.getFree()) {
            throw new BadRequestException("Poltrona já está ocupada.");
        }

        Pessoa pessoa = new Pessoa(name, cpf);
        pessoa.setSeat(seat);
        seat.setPessoa(pessoa);
        seat.setFree(false);

        pessoaRepository.save(pessoa);
        seatRepository.save(seat);
    }





}
