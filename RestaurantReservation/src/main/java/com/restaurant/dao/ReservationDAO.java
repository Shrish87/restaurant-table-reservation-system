package com.restaurant.dao;

import com.restaurant.model.Customer;
import com.restaurant.model.Reservation;
import com.restaurant.model.RestaurantTable;
import com.restaurant.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import jakarta.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.List;

public class ReservationDAO {

    // Save or Update Customer
    public Customer saveCustomer(Customer customer) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Check if customer exists by email
            Query<Customer> query = session.createQuery(
                    "FROM Customer WHERE email = :email", Customer.class);
            query.setParameter("email", customer.getEmail());
            Customer existing = query.uniqueResult();

            if (existing != null) {
                // Update existing customer
                existing.setName(customer.getName());
                existing.setPhone(customer.getPhone());
                session.update(existing);
                transaction.commit();
                return existing;
            } else {
                // Save new customer
                session.save(customer);
                transaction.commit();
                return customer;
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw new RuntimeException("Error saving customer: " + e.getMessage());
        }
    }

    // Get all available tables
    public List<RestaurantTable> getAvailableTables() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<RestaurantTable> query = session.createQuery(
                    "FROM RestaurantTable WHERE isAvailable = 'Y' ORDER BY tableNumber",
                    RestaurantTable.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching tables: " + e.getMessage());
        }
    }

    // Check if table is available for specific date and time slot
    public boolean isTableAvailable(Long tableId, LocalDate date, String timeSlot) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(r) FROM Reservation r WHERE r.table.tableId = :tableId " +
                            "AND r.reservationDate = :date AND r.timeSlot = :timeSlot " +
                            "AND r.status = 'CONFIRMED'", Long.class);
            query.setParameter("tableId", tableId);
            query.setParameter("date", date);
            query.setParameter("timeSlot", timeSlot);

            Long count = query.uniqueResult();
            return count == 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error checking availability: " + e.getMessage());
        }
    }

    // Create reservation with double-booking prevention
    public Reservation createReservation(Reservation reservation) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Double-check availability within transaction
            if (!isTableAvailable(reservation.getTable().getTableId(),
                    reservation.getReservationDate(),
                    reservation.getTimeSlot())) {
                throw new RuntimeException("Table is not available for the selected time slot");
            }

            // Merge customer and table to attach them to current session
            Customer managedCustomer = (Customer) session.merge(reservation.getCustomer());
            RestaurantTable managedTable = (RestaurantTable) session.merge(reservation.getTable());

            reservation.setCustomer(managedCustomer);
            reservation.setTable(managedTable);

            session.save(reservation);
            transaction.commit();
            return reservation;

        } catch (PersistenceException e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Double booking prevented! This table is already reserved for the selected time slot.");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw new RuntimeException("Error creating reservation: " + e.getMessage());
        }
    }

    // Get all reservations
    public List<Reservation> getAllReservations() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Reservation> query = session.createQuery(
                    "FROM Reservation ORDER BY reservationDate DESC, timeSlot",
                    Reservation.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching reservations: " + e.getMessage());
        }
    }

    // Get reservations by date
    public List<Reservation> getReservationsByDate(LocalDate date) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Reservation> query = session.createQuery(
                    "FROM Reservation WHERE reservationDate = :date AND status = 'CONFIRMED' " +
                            "ORDER BY timeSlot", Reservation.class);
            query.setParameter("date", date);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching reservations: " + e.getMessage());
        }
    }

    // Cancel reservation
    public void cancelReservation(Long reservationId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Reservation reservation = session.get(Reservation.class, reservationId);
            if (reservation != null) {
                reservation.setStatus("CANCELLED");
                session.update(reservation);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw new RuntimeException("Error cancelling reservation: " + e.getMessage());
        }
    }

    // Get table by ID
    public RestaurantTable getTableById(Long tableId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(RestaurantTable.class, tableId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching table: " + e.getMessage());
        }
    }
}
