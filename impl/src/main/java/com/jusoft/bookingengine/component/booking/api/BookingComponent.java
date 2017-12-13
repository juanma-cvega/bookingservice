package com.jusoft.bookingengine.component.booking.api;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public interface BookingComponent {

  BookingView book(CreateBookingCommand createBookingCommand);

  void cancel(CancelBookingCommand cancelBookingCommand);

  BookingView find(long userId, long bookingId);

  List<BookingView> findAllBy(long userId);

  List<BookingView> getFor(long userId);

  List<BookingView> findUsersBookingsUntilFor(ZonedDateTime endTime, Set<Long> users);
}
