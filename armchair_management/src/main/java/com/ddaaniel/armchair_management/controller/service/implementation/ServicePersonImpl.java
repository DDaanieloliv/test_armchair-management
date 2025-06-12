package com.ddaaniel.armchair_management.controller.service.implementation;

import com.ddaaniel.armchair_management.controller.exception.AssentoInvalidoException;
import com.ddaaniel.armchair_management.controller.exception.BadRequestException;
import com.ddaaniel.armchair_management.controller.exception.NotFoundException;
import com.ddaaniel.armchair_management.controller.service.IPersonService;
import com.ddaaniel.armchair_management.model.Person;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.repository.IPersonRepository;
import com.ddaaniel.armchair_management.model.repository.ISeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ServicePersonImpl implements IPersonService {

    // Dependências
    private final IPersonRepository iPersonRepository;
    private final ISeatRepository iSeatRepository;

    @Autowired
    public ServicePersonImpl(IPersonRepository iPersonRepository, ISeatRepository iSeatRepository) {
        this.iPersonRepository = iPersonRepository;
        this.iSeatRepository = iSeatRepository;
    }


    @Override
    @Transactional
    public void removePessoaFromSeat(Integer position) {
        positionIsValid(position);
        Seat armchair = findSeat(position);

        if (selectedSeatIsFree(armchair)) {
            throw new BadRequestException("A Poltrona já está desocupada.");
        }

        Person pessoa = armchair.getPerson();
        removeOccupantFromSeat(armchair);
        iPersonRepository.delete(pessoa);
    }





    private void positionIsValid(Integer position) {
        if (position <= 0 || position > 15) {   // Verifica se é um parâmetro válido
            throw new AssentoInvalidoException("O assento informado é inválido.");
        }
    }

    private Seat findSeat(Integer position) {
        return iSeatRepository.findByPosition(position)
                .orElseThrow(()-> new NotFoundException("Poltrona não encontrada."));
    }

    private boolean selectedSeatIsFree(Seat armchair){
        return armchair.getFree();
    }

    private void removeOccupantFromSeat(Seat armchair) {
        armchair.setPerson(null);
        armchair.setFree(true);
        iSeatRepository.save(armchair);
    }

}
