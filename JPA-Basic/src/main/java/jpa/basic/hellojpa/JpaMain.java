package jpa.basic.hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello"); //애플리케이션 전체에서 하나만 생성해서 공유
        EntityManager em = emf.createEntityManager(); //스레드간에 공유 X

        //트랜잭션 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            //insert
//            Member member = new Member();
//            member.setId(1L);
//            member.setName("HelloA");
//            em.persist(member);

            //select
            Member foundMember = em.find(Member.class, 1L);
            List<Member> members = em.createQuery("select m from Member m", Member.class)
                    .setFirstResult(1) //1번부터
                    .setMaxResults(10) //10번까지만 조회 -> 페이징
                    .getResultList(); //JPQL

            //update
            //foundMember.setName("HelloJPA"); //이렇게만 해두면 업데이트가 된다 <- JPA 변경감지!

            //remove
            em.remove(foundMember);

            //트랜잭션 종료
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
