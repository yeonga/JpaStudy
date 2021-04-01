package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

import java.util.List;

// interface 는 @Repository 를 안적어줘도 됨
// MemberRepository 와 JpaRepository는 둘 다 interface 이므로 extends 상속을 적어줌
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();
    // By 뒤에 아무 condition을 작성하지 않으면 전체 조회 & Top 3 와 같이 조건을 작성해줄 수 있음
}

/* 여기서 구현체가 없는데 MemberRepositoryTest 에서
@Autowired
MemberRepository memberRepository
여기서 injection 해서 들어온 것의 정체가 뭐냐?
Spring이 이 인터페이스를 보고 SpringDataJpa가 구현 클래스를 (Proxy) 만들어서 MemberRepositoryTest 에 값을 꽂아줌
=> 개발자가 인터페이스만 해두면 구현체는 SpringDataJpa 가 알아서 injection 해서 값을 넣어줌
* */