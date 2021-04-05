package study.datajpa.repository;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@Rollback(false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;    // 같은 Transaction이면 같은 Entity Manager 가 불러와져서 동작함


    @Test
    @Order(1)
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        /* 있을 수도 있고 없을 수도 있기 때문에 Optional 로 받아줌
        Optional<Member> byId = memberRepository.findById(savedMember.getId());
        Member member1 = byId.get();
        아래와 같은 것
        */

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);   // JPA 엔티티 동일성 보장
    }

    @Test
    @Order(2)
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    @Order(3)
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @Order(4)
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    @Order(5)
    public void testNamedQuery() {

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    @Order(6)
    public void testQuery() {

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    @Order(7)
    public void findUsernameList() {

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    @Order(8)
    public void findMemberDto() {

        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        memberRepository.save(m1);
        m1.setTeam(team);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    // 컬렉션 파라미터 바인딩 테스트 (실무에서 많이 사용)
    @Test
    @Order(9)
    public void findByNames() {

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));    //Arrays.asList - 배열을 List로
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    @Order(10)
    public void returnType() {

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        Optional<Member> findMember = memberRepository.findOptionalByUsername("AAA");
        System.out.println("findMember = " + findMember);
        // Optional<Member> 에서 나는 Member라는 하나의 타입을 지정해서 한 건 조회를 했는데, AAA가 두 개일 경우 Exception이 터짐

        /* 매우 중요!! => 컬렉션 조회할 때 이상한 값을 파라미터에 넣어주면 데이터가 없을 수 있는데 그 때 null이 아니라 빈 컬렉션을 제공해줌
        =>  List 는 그냥 받으면 됨! null이 아니기 때문에 if(result != null) 이런거 써주면 안됌!
        List<Member> result = memberRepository.findListByUsername("asfddhfh");
        System.out.println("result = " + result.size());*/

        /* 단건인 경우 => 컬렉션 조회할 때 이상한 값을 파라미터에 넣어주면 데이터가 없을 수 있는데 그 때 null 값을 받음
        Member findMember1 = memberRepository.findMemberByUsername("assdhfhfjgj");
        System.out.println("findMember = " + findMember);
         */

        /* Optional로 받을 경우 => 없을 수도 있다고 가정하기 때문에 null일 경우 Optional.empty로 값이 나옴
        Optional<Member> findMember = memberRepository.findOptionalByUsername("assggd");
        System.out.println("findMember = " + findMember);

        따라서, 데이터가 있을 수도 있고 없을 수도 있기 때문에 Optional을 사용
       */
    }

    @Test
    @Order(11)
    public void paging() {
        //given
        memberRepository.save(new Member(("member1"), 10));  // memberJpaRepository에 가짜객체를 넣어줌 new Member를 해줌으로써.
        memberRepository.save(new Member(("member2"), 10));
        memberRepository.save(new Member(("member3"), 10));
        memberRepository.save(new Member(("member4"), 10));
        memberRepository.save(new Member(("member5"), 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        // page index는 1이 아니라 0부터 시작하고 / size 는 한 페이지에 몇 개를 가져올지 / sort는 안할꺼면 안적어도 됨 (위 : username을 DESC로 할꺼다)
        // sort 가 너무 복잡할 경우 여기서 작성해도 안먹을 때가 있기 때문에 MemberRepository에서 @Query문 작성에서 직접 적어줌


        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        // long totalCount = memberRepository.totalCount(age); 이걸 적어주지 않아도 됨
        // 반환 타입을 Page라고 받으면 page는 totalCount를 필요로 하는 걸 알고 알아서 totalCountQuery 까지 같이 날림
        // ***API의 경우 바로 Controller에 반환하면 안됌/ entity는 반드시 외부에 노출시키면 안되고 내 Application에 숨겨야함. DTO로 변환해서 넘겨야함!!!!!***
        // => 왜? entity 가 바뀌면 API 스펙이 변해버림

        // |=>페이지를 유지하면서 DTO로 변환하는 방법
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));
        // page가 member를 감싸고 있는데, map은 내부의 것을 바꿔서 다른 결과를 냄 (위 : 내부의 entity에서 memberDto로 변환됨 -> api로 반환해도 됌)

        //Slice<Member> page = memberRepository.findByAge(age, pageRequest); - Slice 타입 (특정 개수만 보여주고 "더 보기" 등의 버튼을 누를 시 특정 개수만큼 다음 페이지 더 보여주는 것
        //Slice : 요청을 할 때 예를 들어, 한 페이지에 3개를 요청하면 3개가 아니라 +1을 해서 4개를 가져옴 (totalCount 를 안씀)

        //List<Member> page = memberRepository.findByAge(age, pageRequest); - 페이징 쿼리는 0페이지부터 3개만 가져오고 싶을 때 & 몇 개도 있고 없고 등에서 사용

        //then
        List<Member> content = page.getContent(); // getContent - 페이지 안에서 0번째 페이지에서 3개를 꺼내오고 싶을 때 사용
        long totalElements = page.getTotalElements();   // totalElements라고 하면 totalCount를 말함  // Slice 사용 시엔 totalCount 를 안쓰기 때문에 필요 없음

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);     // Slice 사용 시엔 totalCount 를 안쓰기 때문에 필요 없음
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);        // Slice 사용 시엔 totalCount 를 안쓰기 때문에 필요 없음
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

/*        for(Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElement = " + totalElements);
    }*/

    @Test
    @Order(12)
    public void bulkUpdate() {
        //given
        memberRepository.save(new Member(("member1"), 10));
        memberRepository.save(new Member(("member2"), 19));
        memberRepository.save(new Member(("member3"), 20));
        memberRepository.save(new Member(("member4"), 21));
        memberRepository.save(new Member(("member5"), 27));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);  // 20살이거나 20살 이상인 사람들이 해당 -> +1 해줌

        /*em.flush(); - 혹시 남아있는 변경사항을 DB에 저장
        em.clear(); - 이게 핵심!!!!! <영속성 컨텍스트 안에 있는 데이터 비워주기> - 그래야 깔끔한 상태에서 DB를 다시 가져올 수 있음 -> 벌크업데이트 (+1) 연산 된 것으로 가져옴
        */

        // springDataJpa em.clear()를 안적어줘도 지원을 해주기 때문에 MemberRepository 의 @Modifying(clearAutomatically = true)과 같이 작성해주면 됨


        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    @Order(13)
    public void findMemberLazy() {
        //given
        //member1 -> teamA (member1 은 teamA를 참조함)
        //member2 -> teamB (member2 은 teamB를 참조함)

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();
        // 이렇게 하면 완전히 영속성 컨텍스트에 있는 캐시 정보들을 완전히 다 insert를 해서 반영하고 clear를 해서 깔끔하게 날려버림

        //when - "N(결과 값) + 1 문제"라고 부름 ex) 결과 값이 2개 나오면 N 이 2이므로 N(2) + 1  이라고 함
        //select Member 1 만 해서 가져옴 (Team 말고) - 쿼리를 1 번 날렸는데 결과가 2개 나옴
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");  // findAll - 순수하게 member 객체만 가져오는 것

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            // fetch가 LAZY로 되있기에, 이 때까지는 member의 객체들만 가져오고, member의 team은 null이 아닌 proxy (가짜)객체로 값을 가져옴 - Team$HibernateProxy$rHx1mYPD 이런 식으로 가져옴
            System.out.println("member.team = " + member.getTeam().getName());
            // member.getTeam().getName() 매소드를 호출 했을 때 - (team 엔티티 클래스에서 실제 가지고 있는 값 호출할 때)그제서야 실제 DataBase에 team에 대해 쿼리를 날려서 데이터를 가져옴
        }
    }

    @Test
    @Order(14)
    public void queryHint() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    @Order(15)
    public void lock() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        List<Member> findMember = memberRepository.findLockByUsername("member1");
    }
}