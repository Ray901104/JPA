package spring.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import spring.datajpa.dto.MemberDto;
import spring.datajpa.entity.Member;
import spring.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    void testMember() {
        System.out.println("memberRepository.getClass() = " + memberRepository.getClass()); //프록시 객체
        //given
        Member member = new Member("memberA");

        //when
        Member savedMember = memberRepository.save(member);

        //then
        Member foundMember = memberRepository.findById(savedMember.getId()).orElse(new Member("memberB"));
        assertThat(foundMember.getId()).isEqualTo(member.getId());
        assertThat(foundMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(foundMember).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member foundMember1 = memberRepository.findById(member1.getId()).orElse(new Member("member1"));
        Member foundMember2 = memberRepository.findById(member2.getId()).orElse(new Member("member2"));
        assertThat(foundMember1).isEqualTo(member1);
        assertThat(foundMember2).isEqualTo(member2);

        //update 메소드가 필요없는 이유 -> 변경감지
        foundMember1.setUsername("member!!!!!!");

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterThanTest() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void namedQueryTest() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> membersByUsername = memberRepository.findByUsername("AAA");
        Member foundMember = membersByUsername.get(0);
        assertThat(foundMember).isEqualTo(member1);
    }

    @Test
    void annotationQueryTest() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> membersByUsername = memberRepository.findUser("AAA", 10);
        assertThat(membersByUsername.get(0)).isEqualTo(member1);
    }

    @Test
    void findUsernameListTest() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }

        assertThat(usernameList.size()).isEqualTo(2);
    }

    @Test
    void findMemberDtoTest() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10);
        member1.setTeam(team);
        memberRepository.save(member1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    void findByNamesTest() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member m : members) {
            System.out.println("m = " + m);
        }

        assertThat(members.size()).isEqualTo(2);
    }

    @Test
    void returnTypeTest() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> aaa = memberRepository.findListByUsername("AAA");
        Member aaa1 = memberRepository.findMemberByUsername("AAA");
        Optional<Member> aaa2 = memberRepository.findOptionalMemberByUsername("AAA");
    }

    @Test
    void pagingTest() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        //페이지가 0부터 시작인 것에 주의
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //entity -> dto translate -> api return 가능
        page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);
        List<Member> list = memberRepository.findListByAge(age, pageRequest);

        //then
        List<Member> contents = page.getContent();
        long totalCount = page.getTotalElements();

        for (Member content : contents) {
            System.out.println("content = " + content);
        }

        System.out.println("totalCount = " + totalCount);

        //page
        assertThat(contents.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

        //slice
        assertThat(slice.getNumber()).isEqualTo(0);
        assertThat(slice.isFirst()).isTrue();
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    void bulkAgePlusTest() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20); //영속성 컨텍스트에서 벌크연산을 인식하지 못하고 DB에 반영된다.

        //벌크연산 이후에는 가급적 영속성 컨텍스트를 초기화 하는 것이 좋다.
//        entityManager.flush();
//        entityManager.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5.getAge() = " + member5.getAge()); //40 -> DB와 데이터 정합성 깨진다.

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    void findMemberLazy() {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll(); // N + 1 문제
        //List<Member> memberFetchJoin = memberRepository.findMemberFetchJoin(); //fetch join

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

        /*for (Member member : memberFetchJoin) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }*/
    }

    @Test
    void queryHint() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush();
        em.clear();

        //when
        //Member foundMember = memberRepository.findById(member1.getId()).orElse(new Member("member1", 10));
        //foundMember.setUsername("member2"); //변경감지

        Member foundMember = memberRepository.findReadOnlyByUsername("member1");
        foundMember.setUsername("member2"); //변경감지 X <- 스냅샷이 없기 때문에

        em.flush();
    }

    @Test
    void lockTest() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush();
        em.clear();

        //when
        List<Member> member = memberRepository.findLockByUsername("member1");
    }
}
