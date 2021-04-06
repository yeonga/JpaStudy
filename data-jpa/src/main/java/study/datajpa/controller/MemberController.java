package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    // 도메인 클래스 컨버터 - 권장하는 기능은 아님 (pk를 외부에서 공개하는 것은 좋지 않기 때문 & 간단할 때만 사용 가능하기 때문)
   @GetMapping("/members2/{id}")   // Spring 이 중간에서 converting 하는 과정을 끝내고 member를 바로 parameter 로 injection 해줌
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size=5, sort = "username") Pageable pageable) {   // parameter로 Pageable (interface)로 주고, Page<Member>의 Page는 결과 정보의 interface를 말함
        // parameter 가 controller 에서 binding 될 때 pageable 이 있으면 pageable request 라는 객체를 생성해서 그걸 가지고 injection을 시켜줌
        return memberRepository.findAll(pageable)
                .map(MemberDto::new);   // method reference 이용해서 코드 줄임
                // .map(member -> MemberDto(member.getId(), member.getUsername(), null)); 에서 변수들을 MemberDto에서 Entity를 가져왔기 때문에 MemberDto에서 선언한 member를 가져오면 됌
        // = return memberRepository.findAll(pageable); // 반환 타입이 Page인 것을 알기 때문에 굳이 변수로 Page<Member> page를 할 필요가 없음.
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
