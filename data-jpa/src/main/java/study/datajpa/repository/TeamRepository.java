package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Team;

// interface 는 @Repository 를 안적어줘도 됨
public interface TeamRepository extends JpaRepository<Team, Long> {
}
