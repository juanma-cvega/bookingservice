package com.jusoft.bookingengine.component.instructortimetables.api;

import com.jusoft.bookingengine.component.instructor.api.Timetable;
import lombok.Data;

import java.util.Map;

@Data(staticConstructor = "of")
public class RoomTimetableView {

  private final long roomId;
  private final Map<String, Timetable> classesTimetable;
}
