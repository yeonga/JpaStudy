package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

//------------------------------- Member 와 Team 의 기본적인 CRUD 를 JPA를 통해서 만듬(Member와 Team의 타입만 다르지 비슷함) ------------------------------------

        // CRUD  중 Update 는 작성 안한 이유 ? JPA 는 기본적으로 entity를 변경을 할 때 "변경 감지"라는 기능으로 데이터를 바꿈 (update라는 메소드가 필요 없음)
        // Entity Manager를 통해서 값을 조회해 온 다음 entity를 직접 수정하고 transactional commit 하면 자동으로 변경된 것을 알아 DB에 update 쿼리를 날림

@Repository // Spring이 Component scan을 해야하기 때문에 사용
public class TeamJpaRepository {

    @PersistenceContext // JPA의 Entity Manager를 injection해주는 역할
    private EntityManager em;

    public Team save(Team team) {
        em.persist(team);
        return team;    //결과 반환
    }

    public void delete(Team team) {
        em.remove(team);
    }

    public List<Team> findAll() {
        return em.createQuery("select t from Team t", Team.class)
                .getResultList();
    }

    public Optional<Team> findById(Long id) {
        Team team = em.find(Team.class, id);
        return Optional.ofNullable(team);
    }

    public long count() {
        return em.createQuery("select count(t) from Team t", Long.class)
                .getSingleResult();
    }
}
