package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
// JPA 에서 진짜 상속 관계 말고 속성만 상속한 관계 (이걸 안적어주면 create table member 했을 때 member의 속성만 넣고 여기 있는 createDate, updateDate 속성은 안만들어줌)
public class JpaBaseEntity {

    @Column(updatable = false)  // createdDate는 update 되지 못하게 false로 해줌 - 실수로 값을 바꿔도 변경되지 않음 (DB의 값이 변경되지 않음)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {  // 저장하기 전 발생
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;  // this.createdDate = now; 에서 this는 중복되서 강조할 때 아니면 생략 가능 (id를 쓰기 때문)
        updatedDate = now;  // null;로 두지 않고 now;로 값을 넣어 둔 이유는/ 값을 넣어놔야 나중에 쿼리 날릴 때 편함 (최초 등록만 한 것임)
    }

    @PreUpdate
    public void preUpdate() {   // update 하기 전 호출
        updatedDate = LocalDateTime.now();
    }
}
