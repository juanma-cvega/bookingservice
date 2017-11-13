package com.jusoft.bookingengine.component.room.api;

import com.jusoft.bookingengine.component.shared.Command;
import com.jusoft.bookingengine.component.timer.OpenTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CreateRoomCommand implements Command {
  private final int maxSlots;
  private final int slotDurationInMinutes;
  private final List<OpenTime> openTimePerDay;
  private final List<DayOfWeek> availableDays;
  private final boolean active;

  public List<OpenTime> getOpenTimePerDay() {
    return new ArrayList<>(openTimePerDay);
  }

  public List<DayOfWeek> getAvailableDays() {
    return new ArrayList<>(availableDays);
  }
}