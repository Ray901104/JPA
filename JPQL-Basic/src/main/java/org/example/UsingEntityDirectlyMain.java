package org.example;

import org.example.jpql.Member;
import org.example.jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class UsingEntityDirectlyMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            Team teamA = new Team();
            teamA.setName("TeamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("TeamB");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);

            Member member4 = new Member();
            member4.setUsername("회원4");
            em.persist(member4);

            em.flush();
            em.clear();

            //String query = "select m from Member m where m = :member"; //위 아래 두 쿼리 모두 동일
            //String query = "select m from Member m where m.id = :memberId";
            String query = "select m from Member m where m.team = :team"; //외래키 엔티티 직접 사용 (TEAM_ID)
            Member foundMember = em.createQuery(query, Member.class)
                    //.setParameter("member", member1)
                    //.setParameter("memberId", member1.getId())
                    .setParameter("team", teamB)
                    .getSingleResult();

            System.out.println("foundMember = " + foundMember);

            //named query: 애플리케이션 로딩 시점에 검증 및 초기화 <- 굉장한 이점!
            List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", "회원1")
                    .getResultList();

            for (Member member : resultList) {
                System.out.println("member = " + member);
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
