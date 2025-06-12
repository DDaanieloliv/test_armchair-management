package com.ddaaniel.armchair_management.service;

import com.ddaaniel.armchair_management.controller.service.implementation.ServiceSeatImpl;
import com.ddaaniel.armchair_management.controller.service.mapper.SeatMapper;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.repository.ISeatRepository;
import com.ddaaniel.armchair_management.utilsObjects.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ServiceSeatTest {


    @Mock
    ISeatRepository seatRepository;

    @Mock
    SeatMapper seatMapper;

    @InjectMocks
    ServiceSeatImpl serviceSeat;

    @Captor
    ArgumentCaptor<List<Seat>> seatsListCaptor;

    @Captor
    ArgumentCaptor<Seat> seatCaptor;




    @Nested
    class listStatusOfAllSeats {

        @Test
        void shouldMappingSeatCorrectly() {

            // ARRANGE
            var createdSeatListEntities = Utils.seatListGenerate();
            var seatListConvertedToDTO = Utils.convertSeatListToDTO(createdSeatListEntities);

            Mockito.doReturn(createdSeatListEntities).when(seatRepository).findAll();
            Mockito.doReturn(seatListConvertedToDTO).when(seatMapper)
                    .toDTOList(seatsListCaptor.capture());

            // ACT
            serviceSeat.listStatusOfAllSeats();

            // ASSERT
            Mockito.verify(seatRepository, Mockito.times(1)).findAll();
            Mockito.verify(seatMapper, Mockito.times(1))
                    .toDTOList(Mockito.eq(createdSeatListEntities));

            Assertions.assertEquals(seatsListCaptor.getValue().get(0).getFree(),
                    seatListConvertedToDTO.get(0).free());
            Assertions.assertEquals(seatsListCaptor.getValue().get(0).getPosition(),
                    seatListConvertedToDTO.get(0).position());
        }
    }

    @Nested
    class detailsFromSpecificSeat {

        @Test
        void shouldCallRightDependencies() {

            // ARRANGE
            var randomPosition = Utils.randomIntegerWithRange15();
            var seatEntityBuilt = Utils.randomlyCreateSeatEntity(randomPosition);
            var convertedToDto = Utils.moveToDTO(seatEntityBuilt);

            Mockito.doReturn(Optional.of(seatEntityBuilt))
                    .when(seatRepository).findByPosition(Mockito.eq(randomPosition));
            Mockito.doReturn(convertedToDto).when(seatMapper).toDTO(Mockito.eq(seatEntityBuilt));

            // ACT
            serviceSeat.detailsFromSpecificSeat(randomPosition);

            // ASSERT
            Mockito.verify(seatRepository, Mockito.times(1))
                    .findByPosition(Mockito.eq(randomPosition));
            Mockito.verify(seatMapper, Mockito.times(1))
                    .toDTO(Mockito.eq(seatEntityBuilt));

        }
    }


    @Nested
    class allocateSeatToPessoa {


        @Test
        void someTestName() {

            // ARRANGE
            var randomPosition = Utils.randomIntegerWithRange15();
            var randomName = Utils.randomNameString();
            var randomCpf = Utils.randomCpf();

            var seatCreated = Utils.createSeatWithoutPerson(randomPosition);
            var expectedSeatEntitySaved =
                    Utils.createSeatWithParameterWithPersonID(seatCreated.getSeatID(), randomPosition, randomName, randomCpf);
            var expectedSeatEntityUnSaved =
                    Utils.createSeatWithParameterWithoutPersonID(seatCreated.getSeatID(), randomPosition, randomName, randomCpf);

            Mockito.doReturn(Optional.of(seatCreated))
                    .when(seatRepository).findByPosition(randomPosition);

            Mockito.doReturn(expectedSeatEntitySaved)
                    .when(seatRepository).save(seatCaptor.capture());

            // ACT
            serviceSeat.allocateSeatToPessoa(randomPosition, randomName, randomCpf);

            // ASSERT
            Mockito.verify(seatRepository, Mockito.times(1))
                    .findByPosition(Mockito.eq(randomPosition));
            Mockito.verify(seatRepository, Mockito.times(1))
                    .save(Mockito.eq(expectedSeatEntityUnSaved));

            Assertions.assertEquals(seatCaptor.getValue().getPosition(), seatCreated.getPosition());
            Assertions.assertEquals(seatCaptor.getValue().getFree(), seatCreated.getFree());
            Assertions.assertEquals(seatCaptor.getValue().getPerson(), seatCreated.getPerson());
            Assertions.assertEquals(seatCaptor.getValue().getPerson().getName(), seatCreated.getPerson().getName());
            Assertions.assertEquals(seatCaptor.getValue().getPerson().getCpf(), seatCreated.getPerson().getCpf());

        }
    }
}



















