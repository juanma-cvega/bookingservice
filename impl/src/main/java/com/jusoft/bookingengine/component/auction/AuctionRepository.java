package com.jusoft.bookingengine.component.auction;

import com.jusoft.bookingengine.repository.Repository;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

interface AuctionRepository extends Repository {

  void save(Auction newAuction);

  Optional<Auction> find(long auctionId);

  void execute(long auctionId, UnaryOperator<Auction> function, Supplier<RuntimeException> notFoundException);

  Optional<Auction> findByReference(long referenceId);
}
