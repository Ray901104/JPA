package org.example;

import org.example.jpql.Member;
import org.example.jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            /**
             * 조인
             */
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setAge(10);
            member1.setTeam(team);
            em.persist(member1);

            em.flush();
            em.clear();

            //내부조인
            String qlString = "select m from Member m inner join m.team t"; //inner 생략가능
            List<Member> resultInnerJoin = em.createQuery(qlString, Member.class)
                    .getResultList();

            //외부조인
            String qlString2 = "select m from Member m left outer join m.team t"; //outer 생략가능
            List<Member> resultOuterJoin = em.createQuery(qlString2, Member.class)
                    .getResultList();

            //세타조인
            String qlString3 = "select m from Member m, Team t where m.username = t.name";
            List<Member> resultThetaJoin = em.createQuery(qlString3, Member.class)
                    .getResultList();

            //조인 대상 필터링
            String qlString4 = "select m from Member m left join m.team t on t.name = 'teamA'";
            List<Member> resultFilterJoin = em.createQuery(qlString4, Member.class)
                    .getResultList();

            //연관관계 없는 엔티티 외부 조인
            String qlString5 = "select m from Member m left join Team t on m.username = t.name";
            List<Member> resultFilterOuterJoin = em.createQuery(qlString5, Member.class)
                    .getResultList();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
