package com.terran4j.demo.dsql;

import com.terran4j.commons.util.Strings;

public class AddressDistance {

    private Address address;

    private Long distance;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public String toString() {
        return Strings.toString(this);
    }

}
