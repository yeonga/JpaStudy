package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})       // 객체를 바로 찍을 때 출력되는 것 {team}은 team 의 연관관계까지 출력되기에 무한루프로 돌아 안써주는게 좋음
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username= :username"
)   // name="엔티티명.메소드명"
public class Member {

    // @Setter 은 가급적이면 실무에서 사용하지 않음(꼭 변경해야 할 때만 사용)

    @Id
    @GeneratedValue     // @GeneratedValue - PK값을 JPA가 알아서 순차적인 값을 generated해줘서 넣어줌
    @Column(name = "member_id") // (테이블 명 _ id) 객체는 member라는 타입도 있어서 id로 되는데, 테이블은 테이블명_id를 해줘야함
    // @Column(name = "NAME", nullable = false, length = 숫자) => nullable = false이면 자동 생성되는 DDL에 not null 제약 조건을 추가할 수 있음.
    // length 속성 값을 사용하면 자동 생성되는 DDL에 문자의 크기를 지정할 수 있다.

    private Long id;
    private String username;
    private int age;

    // Member 와 Team 의 관계 - JPA에서 ManyToOne을 사용할 때 FetchType은 기본적으로 EAGER로 되어있는데, LAZY (지연)로 세팅해줘야함
    // 지연로딩 LAZY LOADING : 실제 객체 대신 프록시 객체를 로딩해두고 해당 객체를 실제 사용할 때 영속성 컨텍스트를 통해 데이터를 불러오는 방법
    // Ex) Member를 조회할 때는 Member만 가져오고, Team이 필요할 때는 그 때 쿼리를 만들어서 가져옴
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")       // foreign key 명이 조인
    private Team team;

 /*   protected Member() {    // entity는 기본적으로 default 생성자가 파라미터 없이 하나 있어야함 & access level이 private 되면 안되고 protected까지 열어둬야함
      } - 이걸 적는 대신 위에 @NoArgsConstructor를 적어줌
  */

    public Member(String username) {    // JPA는 기본적으로 parameter 없는 기본 생성자가 필요함 (private 가 아니라 protected까지만 허용이 되는데 위에서 @NoArgsConstructor를 해줬으므로 public으로 작성

        this.username = username;   // this.username에서 username은 위의 필드 username을 말하고 / = username은 변수로 가져온 username을 말한다.
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {  // MemberTest 에서 Member member1 = new Member("member1", 10, teamA); 의 생성자
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
    /* @Setter 써야하는 경우 변경할 때가 포함되는데 그 때는 이런식으로 작성하기
    public void changeUsername(String username) {
        this.username = username;
    }*/
}
