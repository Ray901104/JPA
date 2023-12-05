import jpa.basic.hellojpa.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class PersistenceContextTest {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        //트랜잭션 시작
        tx.begin();

        try {
            //비영속 상태
            Member member = new Member();
            member.setId(10L);
            member.setName("ChoA");

            //영속성 컨텍스트에 저장 -> 영속 상태
            System.out.println("===BEFORE em.persis(member)===");
            em.persist(member);
            System.out.println("===AFTER em.persis(member)===");

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
