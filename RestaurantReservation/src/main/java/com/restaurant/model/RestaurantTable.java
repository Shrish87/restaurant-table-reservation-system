package com.restaurant.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "RESTAURANT_TABLE")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "table_seq")
    @SequenceGenerator(name = "table_seq", sequenceName = "table_seq", allocationSize = 1)
    @Column(name = "table_id")
    private Long tableId;

    @Column(name = "table_number", nullable = false, unique = true)
    private Integer tableNumber;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "location", length = 50)
    private String location;

    @Column(name = "is_available", length = 1)
    private String isAvailable = "Y";

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Reservation> reservations = new HashSet<>();

    public RestaurantTable() {
    }

    public RestaurantTable(Integer tableNumber, Integer capacity, String location) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.location = location;
        this.isAvailable = "Y";
    }

    // Getters and Setters
    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

    public boolean isAvailable() {
        return "Y".equals(isAvailable);
    }

    @Override
    public String toString() {
        return "Table " + tableNumber + " (Capacity: " + capacity + ", Location: " + location + ")";
    }
}