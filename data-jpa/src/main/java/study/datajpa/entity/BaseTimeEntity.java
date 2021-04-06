package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass   // jpa에서 진짜 상속 관계 말고 속성만 상속한 관계를 말함 (member의 속성 뿐 아니라 상속하고 있는 속성들도 다 가져옴)
@Getter
public class BaseTimeEntity {
    @CreatedDate
    @Column(updatable = false)  // 값을 실수로 바꿔도 변경되지 않도록 하는 기능
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
