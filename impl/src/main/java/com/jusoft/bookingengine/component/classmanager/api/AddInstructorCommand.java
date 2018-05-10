package com.jusoft.bookingengine.component.classmanager.api;

import com.jusoft.bookingengine.publisher.Command;
import lombok.Data;

@Data(staticConstructor = "of")
public class AddInstructorCommand implements Command {

  private final long classId;
  private final long instructorId;
}
