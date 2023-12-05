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
            //테스트
            Member member = new Member();
            member.setUsername("A");
            em.persist(member);

            //select 쿼리가 한 번만 실행된다.
            Member member1 = em.find(Member.class, 10L); //DB에서 최초 조회 -> 영속성 컨텍스트 1차 캐시에 저장
            Member member2 = em.find(Member.class, 10L); //두 번째 부터는 1차 캐시에서 조회

            System.out.println("member1 == member2 = " + (member1 == member2)); //true <- 동일성 보장

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
