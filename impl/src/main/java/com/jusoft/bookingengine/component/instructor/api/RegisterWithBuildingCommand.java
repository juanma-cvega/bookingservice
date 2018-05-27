package com.jusoft.bookingengine.component.instructor.api;

import com.jusoft.bookingengine.publisher.Command;
import lombok.Data;

@Data(staticConstructor = "of")
public class RegisterWithBuildingCommand implements Command {

  private final long clubId;
  private final long buildingId;
  private final long instructorId;
}