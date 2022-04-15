package com.techelevator.dao;

import com.techelevator.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcReservationDao implements ReservationDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcReservationDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int createReservation(int siteId, String name, LocalDate fromDate, LocalDate toDate) {
        String sql = "INSERT INTO reservation (site_id, name, from_date, to_date) " +
                "VALUES (?, ?, ?, ?) RETURNING reservation_id;";
        int newId = jdbcTemplate.queryForObject(sql, int.class, siteId, name, fromDate, toDate);


        return newId;
    }
    @Override
    public List<Reservation> listUpcomingReservationsByPark(int parkId){
        List<Reservation> upcomingReservations = new ArrayList<>();
        String sql = "SELECT reservation.reservation_id, reservation.site_id, reservation.name, reservation.from_date, " +
                "reservation.to_date, reservation.create_date " +
                "FROM park " +
        "JOIN campground on park.park_id = campground.park_id " +
        "JOIN site on campground.campground_id = site.campground_id " +
        "JOIN reservation on site.site_id = reservation.site_id " +
                "WHERE park.park_id = ? AND reservation.from_date >= ? AND reservation.to_date <= ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId, LocalDate.now(), LocalDate.now().plusDays(30));
        while(results.next()){
            Reservation reservation = mapRowToReservation(results);
            upcomingReservations.add(reservation);
        }
        return upcomingReservations;
    }
    @Override
    public Reservation getReservation(int reservationId){
        Reservation reservation = null;

        String sql = "SELECT * " + "FROM reservation " + "WHERE reservation_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, reservationId);
        if (results.next()){
            reservation = mapRowToReservation(results);
        }
        return reservation;
    }

    private Reservation mapRowToReservation(SqlRowSet results) {
        Reservation r = new Reservation();
        r.setReservationId(results.getInt("reservation_id"));
        r.setSiteId(results.getInt("site_id"));
        r.setName(results.getString("name"));
        r.setFromDate(results.getDate("from_date").toLocalDate());
        r.setToDate(results.getDate("to_date").toLocalDate());
        r.setCreateDate(results.getDate("create_date").toLocalDate());
        return r;
    }


}
