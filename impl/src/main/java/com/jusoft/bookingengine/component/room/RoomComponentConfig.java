package com.jusoft.bookingengine.component.room;

import com.jusoft.bookingengine.component.room.api.RoomComponent;
import com.jusoft.bookingengine.component.scheduler.SchedulerComponent;
import com.jusoft.bookingengine.component.shared.MessagePublisher;
import com.jusoft.bookingengine.component.slot.api.SlotComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Configuration
public class RoomComponentConfig {

  @Autowired
  private MessagePublisher messagePublisher;
  @Autowired
  private SlotComponent slotComponent;
  @Autowired
  private SchedulerComponent schedulerComponent;
  @Autowired
  private Clock clock;

  @Bean
  public RoomComponent roomComponent() {
    return new RoomComponentImpl(roomRepository(), roomFactory(), roomEventFactory(), messagePublisher, slotComponent, schedulerComponent, clock);
  }

  private RoomFactory roomFactory() {
    return new RoomFactory(idGenerator());
  }

  private Supplier<Long> idGenerator() {
    AtomicLong idGenerator = new AtomicLong(1);
    return idGenerator::getAndIncrement;
  }

  private RoomEventFactory roomEventFactory() {
    return new RoomEventFactory();
  }

  private RoomRepository roomRepository() {
    return new RoomRepositoryInMemory(new ConcurrentHashMap<>());
  }
}