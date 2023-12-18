package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory jpaQueryFactory;

    @BeforeEach
    void before() {
        jpaQueryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    void startJPQL() {
        //member1 조회
        String qlString = "select m from Member m " +
                "where m.username = :username";

        Member foundMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(foundMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void startQuerydsl() {
        //QMember m = new QMember("m");
        //QMember m = QMember.member;

        Member foundMember = jpaQueryFactory
                .select(member) //static import -> 권장
                .from(member)
                .where(member.username.eq("member1")) //파라미터 바인딩 처리
                .fetchOne();

        assertThat(foundMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void search() {
        Member foundMember = jpaQueryFactory
                .selectFrom(member)
                .where(member.username.eq("member1").and(member.age.eq(10)))
                .fetchOne();

        assertThat(foundMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void searchAndParam() {
        Member foundMember = jpaQueryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10))
                .fetchOne();

        assertThat(foundMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void resultFetch() {
        //리스트 조회
        List<Member> fetch = jpaQueryFactory
                .selectFrom(member)
                .fetch();

        //단건 조회
        Member fetchOne = jpaQueryFactory
                .selectFrom(member)
                .fetchOne();

        //limit(1).fetchOne()
        Member fetchFirst = jpaQueryFactory
                .selectFrom(member)
                .fetchFirst();

        //페이징 정보 포함 -> total count 쿼리 추가 실행
        QueryResults<Member> fetchResults = jpaQueryFactory
                .selectFrom(member)
                .fetchResults();

        //카운트 조회
        long fetchCount = jpaQueryFactory
                .selectFrom(member)
                .fetchCount();
    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 3. 단, 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */
    @Test
    @Commit
    void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = jpaQueryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    @Test
    void paging() {
        List<Member> result = jpaQueryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(0)
                .limit(2)
                .fetch();

        assertThat(result.size()).isEqualTo(2);
    }
}
