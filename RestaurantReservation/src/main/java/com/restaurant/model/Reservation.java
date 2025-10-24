package com.restaurant.model;


import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "RESERVATION",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"table_id", "reservation_date", "time_slot", "status"}
        ))
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_seq")
    @SequenceGenerator(name = "reservation_seq", sequenceName = "reservation_seq", allocationSize = 1)
    @Column(name = "reservation_id")
    private Long reservationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "table_id", nullable = false)
    private RestaurantTable table;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "time_slot", nullable = false, length = 20)
    private String timeSlot;

    @Column(name = "number_of_guests", nullable = false)
    private Integer numberOfGuests;

    @Column(name = "status", length = 20)
    private String status = "CONFIRMED";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Reservation() {
        this.createdAt = LocalDateTime.now();
        this.status = "CONFIRMED";
    }

    public Reservation(Customer customer, RestaurantTable table, LocalDate reservationDate,
                       String timeSlot, Integer numberOfGuests) {
        this.customer = customer;
        this.table = table;
        this.reservationDate = reservationDate;
        this.timeSlot = timeSlot;
        this.numberOfGuests = numberOfGuests;
        this.status = "CONFIRMED";
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public RestaurantTable getTable() {
        return table;
    }

    public void setTable(RestaurantTable table) {
        this.table = table;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", customer=" + customer.getName() +
                ", table=" + table.getTableNumber() +
                ", date=" + reservationDate +
                ", timeSlot='" + timeSlot + '\'' +
                ", guests=" + numberOfGuests +
                ", status='" + status + '\'' +
                '}';
    }
}
