package com.terran4j.demo.dsql;

import javax.persistence.*;

@Entity(name = "demo_address")
@Table(indexes = {
		@Index(name = "idx_gps", columnList = "lon,lat"),
        @Index(name = "idx_name", columnList = "name")
})
public class Address {

    public Address() {
    }

    public Address(String name, Double lon, Double lat) {
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