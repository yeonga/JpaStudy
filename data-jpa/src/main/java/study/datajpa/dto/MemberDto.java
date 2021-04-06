package study.datajpa.dto;

import lombok.Data;
import study.datajpa.entity.Member;

@Data   // 단순 DTO 이기 때문에 Getter / Setter 가 다 들어가 있는 @Data 어노테이션을 붙여줌 - 엔티티에는 왠만하면 적지 말기

public class MemberDto {    // Dto는 어차피 Entity 라는 application 내에서 공통으로 보는 것이기 때문에 dto는 엔티티를 받아도 됌 ( 그러나!! 엔티티는 dto를 절대 받아선 안됌)

    private Long id;    // member 의 id
    private String username;    // member 의 username
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
    }
}
