package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository     // springboot 가 component 대상이 되서 스캔을 할 수 있음. spring bean으로 등록할 수 있음
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member); // member를 저장하는 저장소 역할  - jpa가 insert 쿼리를 알아서 db에 날려줌
        return member;
    }

    public void delete(Member member) {
        em.remove(member);  // remove를 적어주면 실제 DB에서 데이터가 나오면서 삭제가 됨
    }

    public List<Member> findAll() {     // 전체 조회
        // JPQL 사용해야함 &  여기 () 안의 Member는 Member 엔티티를 말함 - 테이블을 대상으로 하는 것이 아니라 객체를 대상으로 함
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();

        /* List<Member> result = em.createQuery("select m from Member m", Member.class)
                .getResultList();*/
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)  // count 가 숫자 Long 타입으로 나옴
                .getSingleResult(); // 결과를 하나만 반환함
    }

    public Member find(Long id) {   // 단 건 조회
        return em.find(Member.class, id);   // find를 하면 jpa가 알아서 Member.class 엔티티에 맞는 select 쿼리를 날려서 값을 가져오게됨
    }

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }
    // 쿼리문 안에서 :username 과 같이 :의 의미 = "파라미터로 넘어온 username을 의미함"

    public List<Member> findByUsername(String username) {
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", "username")
                .getResultList();
    }

    // 페이징 처리하기 Apr-02
    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                .setParameter("age", age)       // 파라미터 넣어줌
                .setFirstResult(offset)     // 어디서부터 가져올지를 파라미터로 넘김
                .setMaxResults(limit)       // 어디까지 가져올지를 파라미터로 넘김
                .getResultList();
    }

    // 페이징 처리를 하면 totalCount로 똑같은 쿼리 하나 더 해줘야함
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    // 벌크성 수정 쿼리 - 한 번에 쿼리를 날려 커밋하는 것
    public int bulkAgePlus(int age) {
        return em.createQuery(
                "update Member m set m.age = m.age + 1" +
                        "where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
    }
}
