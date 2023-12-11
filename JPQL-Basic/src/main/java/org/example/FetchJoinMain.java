package org.example;

import org.example.jpql.Member;
import org.example.jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class FetchJoinMain {
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

            //String query = "select t from Team t";
            //String query = "select m from Member m";
            //String query = "select m from Member m join fetch m.team"; //한방 쿼리
            //String query = "select t from Team t join fetch t.members"; //컬렉션 패치조인 -> 중복 결과 발생
            String query = "select distinct t from Team t join fetch t.members"; //컬렉션 패치조인 -> distinct 자체에서 제거 실패해도 JPA가 같은 식별자 가진 중복 결과 제거
            /*List<Member> resultList = em.createQuery(query, Member.class)
                    .getResultList();*/
            List<Team> resultList = em.createQuery(query, Team.class)
                    .getResultList();
            System.out.println("resultList.size() = " + resultList.size());
            //for (Member member : resultList) {
            for (Team team : resultList) {
                /**
                 * fetch join 사용 전
                 * 회원1, 팀A(SQL)
                 * 회원2, 팀A(1차 캐시)
                 * 회원3, 팀B(SQL)
                 */
                //System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
                System.out.println("team = " + team.getName() + "|members = " + team.getMembers().size());
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
