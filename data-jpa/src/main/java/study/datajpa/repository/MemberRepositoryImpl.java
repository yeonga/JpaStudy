package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor    // 아래 EntityManager 생성자를 생략할 수 있음
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    //데이터베이스에 직접 접근해서 네이티브 쿼리를 쓰고싶은데, JPA 기능이 아니라 JDBC 템플릿을 쓰고 싶을 때 여기서 데이터베이스 커넥션 얻어서 쓰면 됨
    //이 기능을 순수한 jpa로 직접 쓰고 싶을 때
    private final EntityManager em;

    /* RequiredArgsConstructor 어노테이션을 해줌으로서 생성자 생략 가능
    public MemberRepositoryImpl(EntityManager em) {
        this.em = em;
    }*/

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m") //순수한 jpa 씀
                .getResultList();
    }
}
