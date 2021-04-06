package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass   // jpa에서 진짜 상속 관계 말고 속성만 상속한 관계를 말함 (member의 속성 뿐 아니라 상속하고 있는 속성들도 다 가져옴)
@Getter
public class BaseEntity extends BaseTimeEntity{

    // 등록자, 수정자 적고 싶을 때 - 날짜는 자동으로 시간이라 LocalDate 이용하여 입력되지만 사람은 정보를 입력해야하기 때문에
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

}
