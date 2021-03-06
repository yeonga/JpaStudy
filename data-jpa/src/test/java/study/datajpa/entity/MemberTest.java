package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);    // 바로 db 에 insert 쿼리를 날리는 것이 아니라, 영속성 컨텍스트에 member 와 team 을 모아놓음
        em.persist(member2);    // 회원 엔티티 영속 상태
        em.persist(member3);
        em.persist(member4);

        // 엔티티 영속 상태 (영속성 컨텍스트)에서 분리, 준영속 상태 ( 영속성 컨텍스트에게 더는 해당 엔티티를 관리하지 말라는 것) - DB에도 저장 안됨
        // em.detach(member);
        // detach 후에는 transaction.commit(); 트랜잭션 커밋을 해줌 (commit - 저장되지 않은 모든 데이터를 데이터베이스에 저장하고 현재의 트랜잭션을 종료하라는 명령)

        // |====> 준영속 상태 엔티티를 다시 영속 상태로 변경할 때 - 병합 해주기 (merge)
        //

        // 초기화
        em.flush(); // flush 강제 호출 - flush 하면 강제로 db에 insert 쿼리를 날림
        em.clear(); // db 에 쿼리를 날리고 jpa의 영속성 컨텍스트에 있는 캐시를 날려 깨끗하게 해줌

        // 확인 - 깔끔하게 조회 가능

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();   // JPQL 짜는 것

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }

    @Test
    public void JpaEventBaseEntity() throws Exception{
        //given - 이런 데이터가 주어졌을 때
        Member member = new Member("member1");
        memberRepository.save(member);  // @PrePersist 발생

        Thread.sleep(100); //위에서 저장 후 잠시 쉰 다음 아래 코드 실행
        member.setUsername("member2");  // member1 로 저장 된 것을 member2로 변경

        em.flush(); // flush 할 때 이미 @PreUpdate 가 나감
        em.clear();

        //when - 이런 상황이 주어졌을 때
        Member findMember = memberRepository.findById(member.getId()).get();    // 데이터 조회

        //them - 이렇게 실행하면 이런 결과가 나온다
        System.out.println("findMember.CreatedDate= " + findMember.getCreatedDate());
        System.out.println("findMember.LastModifiedDate = " + findMember.getLastModifiedDate());
        System.out.println("findMember.CreatedBy = " + findMember.getCreatedBy());
        System.out.println("findMember.LastModifiedBy = " + findMember.getLastModifiedBy());
    }
}