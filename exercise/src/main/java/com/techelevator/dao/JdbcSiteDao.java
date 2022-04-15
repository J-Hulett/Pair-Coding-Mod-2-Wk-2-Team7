package com.techelevator.dao;

import com.techelevator.model.Campground;
import com.techelevator.model.Reservation;
import com.techelevator.model.Site;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcSiteDao implements SiteDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcSiteDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Site> getSitesThatAllowRVs(int parkId) {
        List<Site> siteList = new ArrayList<>();
        String sql = "SELECT * " + "FROM site " + "JOIN campground ON campground.campground_id = site.campground_id "
        + "WHERE max_rv_length != 0 AND park_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId );
        while (results.next()){
            Site site = mapRowToSite(results);
            siteList.add(site);
        }

        return siteList;
    }
    public List<Site> getSitesWithoutReservation(int parkId) {
        List<Site> siteWithoutReservation = new ArrayList<>();
        String sql = "SELECT site.site_id, site.campground_id, site.site_number, site.max_occupancy, site.accessible, site.max_rv_length, site.utilities " +
                "FROM park " +
                "JOIN campground on park.park_id = campground.park_id " +
                "JOIN site on campground.campground_id = site.campground_id " +
                "JOIN reservation on site.site_id = reservation.site_id " +
                "WHERE park.park_id = ? AND CURRENT_DATE BETWEEN CURRENT_DATE AND reservation.from_date -2 OR park.park_id = ? AND CURRENT_DATE BETWEEN reservation.to_date AND CURRENT_DATE";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId, parkId);
        while (results.next()){
            Site site = mapRowToSite(results);
            siteWithoutReservation.add(site);
        }
        return siteWithoutReservation;
    }

    private Site mapRowToSite(SqlRowSet results) {
        Site site = new Site();
        site.setSiteId(results.getInt("site_id"));
        site.setCampgroundId(results.getInt("campground_id"));
        site.setSiteNumber(results.getInt("site_number"));
        site.setMaxOccupancy(results.getInt("max_occupancy"));
        site.setAccessible(results.getBoolean("accessible"));
        site.setMaxRvLength(results.getInt("max_rv_length"));
        site.setUtilities(results.getBoolean("utilities"));
        return site;
    }
}
