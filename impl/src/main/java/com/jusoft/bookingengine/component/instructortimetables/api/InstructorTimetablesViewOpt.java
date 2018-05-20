package com.jusoft.bookingengine.component.instructortimetables.api;

import lombok.Data;

import java.util.List;

@Data(staticConstructor = "of")
public class InstructorTimetablesViewOpt {

  private final long id;
  private final List<TimetableEntry> timetableEntries;
}
