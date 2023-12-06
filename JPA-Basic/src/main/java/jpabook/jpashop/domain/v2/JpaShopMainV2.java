package jpabook.jpashop.domain.v2;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaShopMainV2 {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            MemberV2 member = new MemberV2();
            member.setName("member1");
            //member.setTeamId(team.getId());
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            //Long teamId = foundMember.getTeamId();
            //Team foundTeam = em.find(Team.class, teamId);
            MemberV2 foundMember = em.find(MemberV2.class, member.getId());
//            Team foundTeam = foundMember.getTeam();

            //양방향 연관관계
            List<MemberV2> members = foundMember.getTeam().getMembers();
            for (MemberV2 memberV2 : members) {
                System.out.println("memberV2 = " + memberV2.getName());
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
