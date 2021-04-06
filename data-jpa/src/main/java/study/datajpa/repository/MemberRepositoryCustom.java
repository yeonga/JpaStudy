package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMemberCustom(); // 이 기능에 대한 구현은 MemberRepositoryImpl.java에서 구현함 - 실제 실행하면 MemberRepositoryImpl 에 있는 것이 실행됨
}
