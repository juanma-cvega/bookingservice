package com.jusoft.bookingengine.controller.booking;

import com.jusoft.bookingengine.component.booking.api.BookingComponent;
import com.jusoft.bookingengine.component.booking.api.BookingNotFoundException;
import com.jusoft.bookingengine.component.booking.api.SlotAlreadyBookedException;
import com.jusoft.bookingengine.component.booking.api.SlotAlreadyStartedException;
import com.jusoft.bookingengine.component.booking.api.WrongBookingUserException;
import com.jusoft.bookingengine.controller.booking.api.CreateBookingRequest;
import com.jusoft.bookingengine.fixtures.BookingFixtures;
import com.jusoft.bookingengine.fixtures.SlotFixtures;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.StringJoiner;

import static com.jusoft.bookingengine.fixtures.BookingFixtures.BOOKINGS;
import static com.jusoft.bookingengine.fixtures.BookingFixtures.BOOKING_1;
import static com.jusoft.bookingengine.fixtures.BookingFixtures.BOOKING_ID_1;
import static com.jusoft.bookingengine.fixtures.BookingFixtures.BOOKING_ID_2;
import static com.jusoft.bookingengine.fixtures.BookingFixtures.BOOKING_RESOURCES;
import static com.jusoft.bookingengine.fixtures.BookingFixtures.BOOKING_RESOURCE_1;
import static com.jusoft.bookingengine.fixtures.BookingFixtures.CANCEL_BOOKING_COMMAND;
import static com.jusoft.bookingengine.fixtures.BookingFixtures.CREATE_BOOKING_COMMAND;
import static com.jusoft.bookingengine.fixtures.BookingFixtures.CREATE_BOOKING_REQUEST;
import static com.jusoft.bookingengine.fixtures.CommonFixtures.ROOM_ID;
import static com.jusoft.bookingengine.fixtures.CommonFixtures.USER_ID_1;
import static com.jusoft.bookingengine.fixtures.CommonFixtures.USER_ID_2;
import static com.jusoft.bookingengine.util.HelpUtils.OBJECT_MAPPER;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class BookingControllerRestTest {

  private static final CharSequence FORTHSLASH = "/";
  private static final String BOOKINGS_URL = "/bookings";
  private static final String CREATE_BOOKING_URL_TEMPLATE = "/room/%s/slot/%s/booking";
  private static final String BOOKING_URL_TEMPLATE = "/user/%s/booking/%s";
  private static final String GET_FOR_URL_TEMPLATE = "/user/%s";

  @Mock
  private BookingComponent mockBookingComponent;
  @Mock
  private BookingCommandFactory mockBookingCommandFactory;
  @Mock
  private BookingResourceFactory mockBookingResourceFactory;

  @InjectMocks
  private BookingControllerRest bookingControllerRest;

  private MockMvc mockMvc;

  @Before
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(bookingControllerRest).build();
  }

  @Test
  public void book() throws Exception {
    when(mockBookingCommandFactory.createFrom(ROOM_ID, SlotFixtures.SLOT_ID_1, USER_ID_1)).thenReturn(CREATE_BOOKING_COMMAND);
    when(mockBookingComponent.book(CREATE_BOOKING_COMMAND)).thenReturn(BOOKING_1);
    when(mockBookingResourceFactory.createFrom(BOOKING_1)).thenReturn(BOOKING_RESOURCE_1);

    String createUrl = String.format(CREATE_BOOKING_URL_TEMPLATE, ROOM_ID, SlotFixtures.SLOT_ID_1);
    String urlTemplate = new StringJoiner(FORTHSLASH).add(BOOKINGS_URL).add(createUrl).toString();
    mockMvc.perform(post(urlTemplate)
      .contentType(APPLICATION_JSON)
      .content(OBJECT_MAPPER.writeValueAsString(CREATE_BOOKING_REQUEST)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.bookingId", is(Long.valueOf(BOOKING_ID_1).intValue())))
      .andExpect(jsonPath("$.bookingTime", is(Long.valueOf(BookingFixtures.BOOKING_TIME).intValue())))
      .andExpect(jsonPath("$.slot.slotId", is(Long.valueOf(SlotFixtures.SLOT_ID_1).intValue())))
      .andExpect(jsonPath("$.slot.roomId", is(Long.valueOf(ROOM_ID).intValue())))
      .andExpect(jsonPath("$.slot.startDate", is(Long.valueOf(SlotFixtures.START_TIME).intValue())))
      .andExpect(jsonPath("$.slot.endDate", is(Long.valueOf(SlotFixtures.END_TIME).intValue())));
  }

  @Test
  public void cancel() throws Exception {
    when(mockBookingCommandFactory.createFrom(USER_ID_1, BOOKING_ID_1)).thenReturn(CANCEL_BOOKING_COMMAND);

    String cancelUrl = String.format(BOOKING_URL_TEMPLATE, USER_ID_1, BOOKING_ID_1);
    String urlTemplate = new StringJoiner(FORTHSLASH).add(BOOKINGS_URL).add(cancelUrl).toString();
    mockMvc.perform(delete(urlTemplate).contentType(APPLICATION_JSON))
      .andExpect(status().isNoContent());

    verify(mockBookingComponent).cancel(CANCEL_BOOKING_COMMAND);
  }

  @Test
  public void find() throws Exception {
    when(mockBookingComponent.find(USER_ID_1, BOOKING_ID_1)).thenReturn(BOOKING_1);
    when(mockBookingResourceFactory.createFrom(BOOKING_1)).thenReturn(BOOKING_RESOURCE_1);

    String findUrl = String.format(BOOKING_URL_TEMPLATE, USER_ID_1, BOOKING_ID_1);
    String urlTemplate = new StringJoiner(FORTHSLASH).add(BOOKINGS_URL).add(findUrl).toString();
    mockMvc.perform(get(urlTemplate).contentType(APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.bookingId", is(Long.valueOf(BOOKING_ID_1).intValue())))
      .andExpect(jsonPath("$.bookingTime", is(Long.valueOf(BookingFixtures.BOOKING_TIME).intValue())))
      .andExpect(jsonPath("$.slot.slotId", is(Long.valueOf(SlotFixtures.SLOT_ID_1).intValue())))
      .andExpect(jsonPath("$.slot.roomId", is(Long.valueOf(ROOM_ID).intValue())))
      .andExpect(jsonPath("$.slot.startDate", is(Long.valueOf(SlotFixtures.START_TIME).intValue())))
      .andExpect(jsonPath("$.slot.endDate", is(Long.valueOf(SlotFixtures.END_TIME).intValue())));
  }

  @Test
  public void getFor() throws Exception {
    when(mockBookingComponent.getFor(USER_ID_1)).thenReturn(BOOKINGS);
    when(mockBookingResourceFactory.createFrom(BOOKINGS)).thenReturn(BOOKING_RESOURCES);

    String cancelUrl = String.format(GET_FOR_URL_TEMPLATE, USER_ID_1);
    String urlTemplate = new StringJoiner(FORTHSLASH).add(BOOKINGS_URL).add(cancelUrl).toString();
    mockMvc.perform(get(urlTemplate).contentType(APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.bookings[0].bookingId", is(Long.valueOf(BOOKING_ID_1).intValue())))
      .andExpect(jsonPath("$.bookings[0].bookingTime", is(Long.valueOf(BookingFixtures.BOOKING_TIME).intValue())))
      .andExpect(jsonPath("$.bookings[0].slot.slotId", is(Long.valueOf(SlotFixtures.SLOT_ID_1).intValue())))
      .andExpect(jsonPath("$.bookings[0].slot.roomId", is(Long.valueOf(ROOM_ID).intValue())))
      .andExpect(jsonPath("$.bookings[0].slot.startDate", is(Long.valueOf(SlotFixtures.START_TIME).intValue())))
      .andExpect(jsonPath("$.bookings[0].slot.endDate", is(Long.valueOf(SlotFixtures.END_TIME).intValue())))
      .andExpect(jsonPath("$.bookings[1].bookingId", is(Long.valueOf(BOOKING_ID_2).intValue())))
      .andExpect(jsonPath("$.bookings[1].bookingTime", is(Long.valueOf(BookingFixtures.BOOKING_TIME).intValue())))
      .andExpect(jsonPath("$.bookings[1].slot.slotId", is(Long.valueOf(SlotFixtures.SLOT_ID_1).intValue())))
      .andExpect(jsonPath("$.bookings[1].slot.roomId", is(Long.valueOf(ROOM_ID).intValue())))
      .andExpect(jsonPath("$.bookings[1].slot.startDate", is(Long.valueOf(SlotFixtures.START_TIME).intValue())))
      .andExpect(jsonPath("$.bookings[1].slot.endDate", is(Long.valueOf(SlotFixtures.END_TIME).intValue())));
  }

  @Test
  public void bookWithNullUserIdFails() throws Exception {
    String createUrl = String.format(CREATE_BOOKING_URL_TEMPLATE, ROOM_ID, SlotFixtures.SLOT_ID_1);
    String urlTemplate = new StringJoiner(FORTHSLASH).add(BOOKINGS_URL).add(createUrl).toString();
    mockMvc.perform(post(urlTemplate)
      .contentType(APPLICATION_JSON)
      .content(OBJECT_MAPPER.writeValueAsString(new CreateBookingRequest(null))))
      .andExpect(status().isBadRequest());
  }

  @Test
  public void bookingNotFoundException() throws Exception {
    when(mockBookingComponent.find(USER_ID_1, BOOKING_ID_1)).thenThrow(new BookingNotFoundException(USER_ID_1, BOOKING_ID_1));

    String findUrl = String.format(BOOKING_URL_TEMPLATE, USER_ID_1, BOOKING_ID_1);
    String urlTemplate = new StringJoiner(FORTHSLASH).add(BOOKINGS_URL).add(findUrl).toString();
    mockMvc.perform(get(urlTemplate).contentType(APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andExpect(status().reason("Booking not found"));
  }

  @Test
  public void slotAlreadyBooked() throws Exception {
    String createUrl = String.format(CREATE_BOOKING_URL_TEMPLATE, ROOM_ID, SlotFixtures.SLOT_ID_1);
    String urlTemplate = new StringJoiner(FORTHSLASH).add(BOOKINGS_URL).add(createUrl).toString();
    when(mockBookingCommandFactory.createFrom(ROOM_ID, SlotFixtures.SLOT_ID_1, USER_ID_1)).thenReturn(CREATE_BOOKING_COMMAND);
    when(mockBookingComponent.book(CREATE_BOOKING_COMMAND)).thenThrow(new SlotAlreadyBookedException(ROOM_ID, SlotFixtures.SLOT_ID_1));

    mockMvc.perform(post(urlTemplate)
      .contentType(APPLICATION_JSON)
      .content(OBJECT_MAPPER.writeValueAsString(USER_ID_1)))
      .andExpect(status().isConflict())
      .andExpect(status().reason("Slot already booked"));
  }

  @Test
  public void slotAlreadyStarted() throws Exception {
    when(mockBookingCommandFactory.createFrom(ROOM_ID, SlotFixtures.SLOT_ID_1, USER_ID_1)).thenReturn(CREATE_BOOKING_COMMAND);
    when(mockBookingComponent.book(CREATE_BOOKING_COMMAND)).thenThrow(new SlotAlreadyStartedException(SlotFixtures.SLOT_ID_1, ROOM_ID));

    String createUrl = String.format(CREATE_BOOKING_URL_TEMPLATE, ROOM_ID, SlotFixtures.SLOT_ID_1);
    String urlTemplate = new StringJoiner(FORTHSLASH).add(BOOKINGS_URL).add(createUrl).toString();
    mockMvc.perform(post(urlTemplate).contentType(APPLICATION_JSON).content(OBJECT_MAPPER.writeValueAsString(USER_ID_1)))
      .andExpect(status().isPreconditionRequired())
      .andExpect(status().reason("Slot already started"));
  }

  @Test
  public void wrongBookingUser() throws Exception {
    when(mockBookingCommandFactory.createFrom(ROOM_ID, SlotFixtures.SLOT_ID_1, USER_ID_1)).thenReturn(CREATE_BOOKING_COMMAND);
    when(mockBookingComponent.book(CREATE_BOOKING_COMMAND)).thenThrow(new WrongBookingUserException(USER_ID_1, USER_ID_2, BOOKING_ID_1));

    String createUrl = String.format(CREATE_BOOKING_URL_TEMPLATE, ROOM_ID, SlotFixtures.SLOT_ID_1);
    String urlTemplate = new StringJoiner(FORTHSLASH).add(BOOKINGS_URL).add(createUrl).toString();
    mockMvc.perform(post(urlTemplate).contentType(APPLICATION_JSON).content(OBJECT_MAPPER.writeValueAsString(USER_ID_1)))
      .andExpect(status().isUnauthorized())
      .andExpect(status().reason("Booking does not belong to user"));
  }
}
