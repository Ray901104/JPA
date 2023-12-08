package org.example;

import org.example.jpql.Member;

import javax.persistence.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("memberA");
            member.setAge(10);
            em.persist(member);

            /*
             * JPQL start
             */
            TypedQuery<Member> typedQuery = em.createQuery("select m from Member m", Member.class); //타입을 특정할 수 있을 때
            Query query = em.createQuery("select m.username, m.age from Member m"); //타입을 특정할 수 없을 때

            //get result
            List<Member> typedQueryResultList = typedQuery.getResultList();
            Member typedQuerySingleResult = typedQuery.getSingleResult(); //데이터가 하나만 나올 것이 확실한 경우, 없거나 둘 이상이면 예외 발생
            List queryResultList = query.getResultList();

            //parameter binding
            TypedQuery<Member> typedQuery2 = em.createQuery("select m from Member m where m.username = :username", Member.class);
            typedQuery2.setParameter("username", "memberA");
            Member singleResult = typedQuery2.getSingleResult();
            System.out.println("singleResult = " + singleResult.getClass());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
