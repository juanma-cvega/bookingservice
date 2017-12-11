package com.jusoft.bookingengine.component.booking;

import com.jusoft.bookingengine.component.booking.api.SlotAlreadyBookedException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

import static com.jusoft.bookingengine.util.LockingTemplate.withLock;
import static java.util.stream.Collectors.toList;

class BookingRepositoryInMemory implements BookingRepository {

  private final Map<Long, Booking> store;
  private final Lock lock;

  BookingRepositoryInMemory(Map<Long, Booking> store) {
    this.store = store;
    this.lock = new ReentrantLock();
  }

  /**
   * Naive implementation of the database. Locks the whole operation to ensure the constraint that there cannot be
   * two entries with the same slotId
   *
   * @param newBooking booking to save
   * @throws SlotAlreadyBookedException in case the slot is already booked
   */
  @Override
  public void save(Booking newBooking) throws SlotAlreadyBookedException {
    withLock(lock, () -> {
      store.values().stream().filter(booking -> Long.compare(booking.getSlotId(), newBooking.getSlotId()) == 0).findFirst()
        .ifPresent(booking -> {
          throw new SlotAlreadyBookedException(newBooking.getRoomId(), newBooking.getId());
        });
      store.put(newBooking.getId(), newBooking);
    });
  }

  @Override
  public boolean delete(long bookingId) {
    return store.remove(bookingId) != null;
  }

  @Override
  public Optional<Booking> find(long bookingId) {
    return Optional.ofNullable(store.get(bookingId));
  }

  @Override
  public List<Booking> getByUser(long userId) {
    return store.values().stream().filter(booking -> Long.compare(userId, booking.getUserId()) == 0).collect(toList());
  }

  @Override
  public List<Booking> findBookingsUntilFor(ZonedDateTime endTime, Set<Long> users) {
    return store.values().stream()
      .filter(byBookingBelongsToUserFrom(users))
      .filter(byBookingCreatedBeforeOrAt(endTime))
      .collect(toList());
  }

  private Predicate<Booking> byBookingBelongsToUserFrom(Set<Long> users) {
    return booking -> users.contains(booking.getUserId());
  }

  private Predicate<Booking> byBookingCreatedBeforeOrAt(ZonedDateTime endTime) {
    return booking -> booking.getBookingTime().isBefore(endTime) || booking.getBookingTime().isEqual(endTime);
  }
}
