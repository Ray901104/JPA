package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() { //entity 자체를 api 스펙에 매핑 -> 문제! -> 별도의 DTO 필요
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public ResultMember membersV2() {
        List<Member> members = memberService.findMembers();
        List<MemberDto> result = members.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new ResultMember<>(result, result.size());
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { //entity 자체를 api 스펙에 매핑 -> 문제! -> 별도의 DTO 필요
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) { //별도의 DTO -> 권장
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member foundMember = memberService.findOne(id);
        return new UpdateMemberResponse(foundMember.getId(), foundMember.getName());
    }

    /**
     * 단순 예제이므로 static inner class 생성 -> 실무에서는 별도 DTO!
     */
    @Data
    @AllArgsConstructor
    static class ResultMember<T> {
        private T data;
        private int count;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }
}
