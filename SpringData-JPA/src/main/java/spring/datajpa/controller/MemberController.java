package spring.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import spring.datajpa.dto.MemberDto;
import spring.datajpa.entity.Member;
import spring.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).orElse(new Member("member1"));
        return member.getUsername();
    }

    //단순 조회용으로 사용 -> 트랜잭션이 없는 범위에서 엔티티를 조회한 것이기 때문에
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> getMemberList(@PageableDefault(size = 5, sort = "username", direction = Sort.Direction.DESC) Pageable pageable) { //개별 설정
        Page<Member> page = memberRepository.findAll(pageable);
        Page<MemberDto> mapped = page.map(member -> new MemberDto(member)); //DTO로 변환해서 반환하자!
        return mapped;
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
