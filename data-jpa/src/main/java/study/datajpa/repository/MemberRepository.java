package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.NamedEntityGraph;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

// interface 는 @Repository 를 안적어줘도 됨
// MemberRepository 와 JpaRepository는 둘 다 interface 이므로 extends 상속을 적어줌
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();
    // By 뒤에 아무 condition 을 작성하지 않으면 전체 조회 & Top 3 와 같이 조건을 작성해줄 수 있음

    //@Query(name = "Member.findByUsername") - @Query 이 부분은 생략을 해도 되고 안해도 잘 동작함
    List<Member> findByUsername(@Param("username") String username);

    // jpql을 다른 데가 아니라 인터페이스 메소드에 바로 적을 수 있음
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();
    // username이 String 타입이기 때문에 List<String>을 해줌

    // DTO 를 가져올 때는 new operation을 꼭 쿼리문에 써줘야함 - Dto 로 반환
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // 컬렉션 파라미터 바인딩
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);
    // List<String> names 에서 List 의 상위 레벨인 Collection을 사용하면 다른 애들도 받을 수 있기 때문에 Collection을 사용해도 됨

    List<Member> findListByUsername(String username);             // 컬렉션
    Member findMemberByUsername(String username);                 // 단건
    Optional<Member> findOptionalByUsername(String username);     // 단건 Optional\

    // join을 많이 할 경우(쿼리가 복잡해질 경우 - 성능테스트에서 느릴 경우)는 countQuery를 별도로 작성해주기!!!
    @Query(value = "select m from Member m left join m.team t",     // value = "" - 컨텐츠를 가져오는 쿼리
            countQuery = "select count(m) from Member m")
        // countQuery = "" - 별로도 분리해서 작성하는 이유? 쿼리가 복잡해지면 countQuery도 복잡해지고 그걸 다 가져오려해서 복잡해지기 때문에 join이 없어서 데이터가 많아도 simple하게 가져올 수 있음
        // countQuery 는 전체를 다 가져오기 때문에 left join 이 되어있어 join을 해줄 필요가 없음
    Page<Member> findByAge(int age, Pageable pageable); // 반환 타입을 Page로 받음

    @Modifying(clearAutomatically = true)  // 이걸 적어줘야 jpa executeUpdate를 실행시킴. 이게 없으면 getResultList 나 getSingleResult 와 같은 것들을 호출함
        // @Modifying(clearAutomatically = true) 이걸 해주면 이 쿼리가 나가고 난 뒤에 clear 과정을 자동으로 해줌.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    // 방법 1 - fetchJoin
    @Query("select m from Member m left join fetch m.team") // fetch join 하면 member 와 연관된 team 쿼리를 한 번에 다 끌고 옴
    List<Member> findMemberFetchJoin();

    // 방법 2 - 위와 같이 JPQL 없이도 객체 그래프를 한 번에 엮어서 성능 최적화를 해서 가져옴 (위와 값 같이 나옴)
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // *간단한 거 할 때 @EntityGraph를 사용함 / 복잡한 거 할 때는 JPQL 작성해줌*
    // 방법 3 - JPQL도 짜고 fetchJoin도 할 때 (JPQL 안에 EntityGraph를 넣는 것) (위와 값 같이 나옴)
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 방법 4 - (위와 값 같이 나옴)
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

/*  방법 5 (Member 엔티티에 @NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team")) 추가해줌 / (위와 값 같이 나옴)
    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);
*/

    // JPA Hint - 성능 최적화를 위해 사용 (사용 잘 안함) / 진짜 중요하고 traffic이 많은 것에 사용하지, 하나 하나 다 사용하는건 아님.
    // 읽을 때부터 readOnly, true로 되어있으면 변경감지 체크를 아예 안하고, 최적화시켜 snapshot을 안만듬 - 변경이 안된다고 가정하고 다 무시함(내부적으로 읽기로만 쓴다고 생각함)
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}

/* 여기서 구현체가 없는데 MemberRepositoryTest 에서
@Autowired
MemberRepository memberRepository
여기서 injection 해서 들어온 것의 정체가 뭐냐?
Spring이 이 인터페이스를 보고 SpringDataJpa가 구현 클래스를 (Proxy) 만들어서 MemberRepositoryTest 에 값을 꽂아줌
=> 개발자가 인터페이스만 해두면 구현체는 SpringDataJpa 가 알아서 injection 해서 값을 넣어줌
* */