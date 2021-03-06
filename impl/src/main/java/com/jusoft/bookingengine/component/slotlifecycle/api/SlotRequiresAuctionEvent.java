package com.jusoft.bookingengine.component.slotlifecycle.api;

import com.jusoft.bookingengine.publisher.Event;
import com.jusoft.bookingengine.strategy.auctionwinner.api.AuctionConfigInfo;
import lombok.Data;

@Data(staticConstructor = "of")
public class SlotRequiresAuctionEvent implements Event {

  private final long slotId;
  private final AuctionConfigInfo auctionConfigInfo;
}
