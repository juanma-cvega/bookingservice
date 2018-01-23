package com.jusoft.bookingengine.component.member;

import com.jusoft.bookingengine.repository.Repository;

interface MemberRepository extends Repository<Member> {

  void save(Member member);

  boolean isMemberOf(long clubId, long userId);
}
