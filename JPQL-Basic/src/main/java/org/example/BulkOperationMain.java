package org.example;

import org.example.jpql.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class BulkOperationMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            Member member1 = new Member();
            member1.setUsername("회원1");
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            em.persist(member3);

            Member member4 = new Member();
            member4.setUsername("회원4");
            em.persist(member4);

            em.flush();
            em.clear();

            /**
             * 벌크 연산 주의점: 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리 실행
             * 해결책
             *  1. 벌크 연산 먼저 실행
             *  2. 벌크 연산 수행(flush 자동 호출) 후 영속성 컨텍스트 초기화
             */
            int resultCount = em.createQuery("update Member m set m.age = 20")
                    .executeUpdate();

            System.out.println("resultCount = " + resultCount);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
