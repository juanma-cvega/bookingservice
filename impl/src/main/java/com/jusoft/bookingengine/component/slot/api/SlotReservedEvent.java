package com.jusoft.bookingengine.component.slot.api;

import com.jusoft.bookingengine.publisher.Event;
import lombok.Data;

@Data
public class SlotReservedEvent implements Event {

  private final long slotId;
  private final long userId;
}