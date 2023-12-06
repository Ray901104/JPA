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
            //member.setTeam(team);
            em.persist(member);

            /*
             * 양방향 연관 관계 에서는 양쪽 다 값을 넣어 주는 것이 맞다.
             * 그렇지 않을 경우 1차 캐시에서 조회해서 가져온 team을 사용할 경우(flush, clear X)
             * List<MemberV2>에는 아무런 값도 없는 상태가 된다.
             * 연관관계 편의 메소드를 작성하자!
             */
            //team.getMembers().add(member);
            member.changeTeam(team);

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
