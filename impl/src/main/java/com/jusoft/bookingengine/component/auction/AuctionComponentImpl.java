package com.jusoft.bookingengine.component.auction;

import com.jusoft.bookingengine.component.auction.api.AuctionComponent;
import com.jusoft.bookingengine.component.auction.api.AuctionNotFoundException;
import com.jusoft.bookingengine.component.auction.api.AuctionWinnerFoundEvent;
import com.jusoft.bookingengine.component.auction.api.CreateAuctionCommand;
import com.jusoft.bookingengine.component.auction.api.FinishAuctionCommand;
import com.jusoft.bookingengine.component.auction.api.SlotNotInAuctionException;
import com.jusoft.bookingengine.component.auction.api.strategy.AuctionConfigInfo;
import com.jusoft.bookingengine.component.shared.MessagePublisher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
class AuctionComponentImpl implements AuctionComponent {

  private final AuctionRepository auctionRepository;
  private final AuctionStrategyRegistrar auctionStrategyRegistrar;
  private final AuctionFactory auctionFactory;
  private final MessagePublisher messagePublisher;

  @Override
  public Auction startAuction(CreateAuctionCommand createAuctionCommand) {
    Auction newAuction = auctionFactory.createFrom(createAuctionCommand);
    auctionRepository.save(newAuction);
    return newAuction;
  }

  @Override
  public void addBuyerTo(long slotId, long userId) {
    Auction auction = findBySlot(slotId).orElseThrow(() -> new SlotNotInAuctionException(slotId));
    auction.addBuyers(userId);
  }

  private Optional<Auction> findBySlot(long slotId) {
    return auctionRepository.findOneBySlot(slotId);
  }

  @Override
  public void finishAuction(FinishAuctionCommand command) {
    Auction auction = auctionRepository.find(command.getAuctionId()).orElseThrow(() -> new AuctionNotFoundException(command.getAuctionId()));
    AuctionConfigInfo auctionConfigInfo = command.getAuctionConfigInfo();
    AuctionWinnerStrategy strategy = auctionStrategyRegistrar.createStrategyWith(auctionConfigInfo);
    auction.findAuctionWinner(strategy).ifPresent(auctionWinner ->
      messagePublisher.publish(new AuctionWinnerFoundEvent(auction.getId(), auctionWinner, auction.getSlotId(), auction.getRoomId())));
  }

  @Override
  public boolean isAuctionOpenForSlot(long slotId) {
    return findBySlot(slotId).map(Auction::isOpen).orElse(false);
  }
}
