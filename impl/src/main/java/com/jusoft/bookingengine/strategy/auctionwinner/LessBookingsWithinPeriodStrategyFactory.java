package com.jusoft.bookingengine.strategy.auctionwinner;

import com.jusoft.bookingengine.component.booking.api.BookingComponent;
import com.jusoft.bookingengine.strategy.auctionwinner.api.AuctionWinnerStrategyFactory;
import com.jusoft.bookingengine.strategy.auctionwinner.api.LessBookingsWithinPeriodConfigInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.Clock;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class LessBookingsWithinPeriodStrategyFactory implements AuctionWinnerStrategyFactory<LessBookingsWithinPeriodConfigInfo> {

  private final BookingComponent bookingComponent;
  private final Clock clock;

  @Override
  public LessBookingsWithinPeriodStrategy createInstance(LessBookingsWithinPeriodConfigInfo config) {
    return new LessBookingsWithinPeriodStrategy(bookingComponent, clock, config);
  }
}