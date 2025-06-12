package com.ddaaniel.armchair_management.service;

import com.ddaaniel.armchair_management.controller.service.implementation.ServicePersonImpl;
import com.ddaaniel.armchair_management.model.Person;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.repository.IPersonRepository;
import com.ddaaniel.armchair_management.model.repository.ISeatRepository;
import com.ddaaniel.armchair_management.utilsObjects.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class ServicePersonTest {


    @Mock
    IPersonRepository personRepository;

    @Mock
    ISeatRepository seatRepository;

    @Captor
    ArgumentCaptor<Seat> armChair;

    @Captor
    ArgumentCaptor<Person> person;

    @InjectMocks
    ServicePersonImpl servicePerson;


    @Nested
    class servicePersonMethods{

        @Test
        void mappingPersonCorrectly() {

            // ARRANGE
            var randomNumberPosition = Utils.randomIntegerWithRange15();
            var seatCreatedWithPerson = Utils.buildSeatEntityByPositionWithPerson(randomNumberPosition);
            var personEntityNoAttachedToTheSeatEntity = seatCreatedWithPerson.getPerson();
            var seatCreatedWithPersonRemoved = Utils.seatBuiltRemovingPerson(seatCreatedWithPerson);

            Mockito.doReturn(Optional.of(seatCreatedWithPerson))
                    .when(seatRepository).findByPosition(eq(randomNumberPosition));
            Mockito.doReturn(seatCreatedWithPersonRemoved)
                    .when(seatRepository).save(armChair.capture());
            Mockito.doNothing()
                    .when(personRepository).delete(person.capture());

            // ACT
            servicePerson.removePessoaFromSeat(randomNumberPosition);


            // ASSERT
            Assertions.assertEquals(person.getValue().getPersonID(), personEntityNoAttachedToTheSeatEntity.getPersonID());
            Assertions.assertEquals(person.getValue().getName(), personEntityNoAttachedToTheSeatEntity.getName());
            Assertions.assertEquals(person.getValue().getCpf(), personEntityNoAttachedToTheSeatEntity.getCpf());

            Assertions.assertEquals(armChair.getValue().getFree(), seatCreatedWithPersonRemoved.getFree());
            Assertions.assertNull(armChair.getValue().getPerson());
            Assertions.assertEquals(armChair.getValue().getPerson(), seatCreatedWithPersonRemoved.getPerson());

            Mockito.verify(seatRepository, Mockito.times(1))
                    .save(eq(seatCreatedWithPerson));

            Mockito.verify(personRepository, Mockito.times(1))
                    .delete(eq(personEntityNoAttachedToTheSeatEntity));

        }
    }

}
