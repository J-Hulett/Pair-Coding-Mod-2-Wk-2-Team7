package com.techelevator.dao;

import com.techelevator.model.Reservation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class JdbcReservationDaoTests extends BaseDaoTests {

    private ReservationDao dao;
    private static final Reservation RESERVATION_1 = new Reservation(44, 46, "Scott Family", LocalDate.parse("2022-05-20"), LocalDate.parse("2022-05-25"), LocalDate.parse("2022-04-15"));

    @Before
    public void setup() {
        dao = new JdbcReservationDao(dataSource);
    }

    @Test
    public void createReservation_Should_ReturnNewReservationId() {
        int reservationCreated = dao.createReservation(1,
                "TEST NAME",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3));

        assertEquals(5, reservationCreated);
    }
    @Test
    public void returns_upcoming_reservations_by_park_id(){
        int actual = dao.listUpcomingReservationsByPark(1).size();
        Assert.assertEquals(2, actual);
    }

}
