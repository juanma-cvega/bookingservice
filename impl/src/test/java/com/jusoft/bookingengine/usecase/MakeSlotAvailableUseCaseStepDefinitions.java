package com.jusoft.bookingengine.usecase;

import com.jusoft.bookingengine.component.slot.api.MakeSlotAvailableCommand;
import com.jusoft.bookingengine.component.slot.api.SlotComponent;
import com.jusoft.bookingengine.component.slot.api.SlotMadeAvailableEvent;
import com.jusoft.bookingengine.component.slot.api.SlotState;
import com.jusoft.bookingengine.component.slot.api.SlotView;
import com.jusoft.bookingengine.config.AbstractUseCaseStepDefinitions;
import org.springframework.beans.factory.annotation.Autowired;

import static com.jusoft.bookingengine.holder.DataHolder.slotCreated;
import static org.assertj.core.api.Assertions.assertThat;

public class MakeSlotAvailableUseCaseStepDefinitions extends AbstractUseCaseStepDefinitions {

  @Autowired
  private SlotComponent slotComponent;

  @Autowired
  private MakeSlotAvailableUseCase makeSlotAvailableUseCase;

  public MakeSlotAvailableUseCaseStepDefinitions() {
    When("^the slot is made available$", () ->
      makeSlotAvailableUseCase.makeSlotAvailable(new MakeSlotAvailableCommand(slotCreated.getId())));
    Then("^the slot should be available$", () -> {
      SlotView slot = slotComponent.find(slotCreated.getId());
      assertThat(slot.getState()).isEqualTo(SlotState.AVAILABLE);
    });
    Then("^a notification of a slot made available should be published$", () -> {
      SlotMadeAvailableEvent event = verifyAndGetMessageOfType(SlotMadeAvailableEvent.class);
      assertThat(event.getSlotId()).isEqualTo(slotCreated.getId());
    });
  }
}
