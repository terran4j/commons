package com.terran4j.test.dsql.dao;

import javax.persistence.*;

@Entity(name = "test_location")
@Table(indexes = { //
		@Index(name = "ux_location", columnList = "lon,lat", unique = true)
})
public class Location {

    public Location() {
    }

    public Location(String name, Double lon, Double lat) {
        this.name = name;
        this.lon = lon;
        this.lat = lat;
    }

    /**
	 * id, 自增主键
	 */
	@Id
	@GeneratedValue
	@Column(length = 20)
	private Long id;

    @Column(length = 100)
    private String name;

	@Column(length = 20, precision = 8)
	private Double lon;

	@Column(length = 20, precision = 8)
	private Double lat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }
}