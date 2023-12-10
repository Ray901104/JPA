package org.example;

import org.example.jpql.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class FunctionMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            Member member1 = new Member();
            member1.setUsername("abcdefg");
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("가나다라마바사");
            em.persist(member2);

            em.flush();
            em.clear();

            //JPQL 기본함수
            String qlString1 = "select concat('a', 'b') from Member m";
            String qlString2 = "select substring(m.username, 2, 3) from Member m";
            String qlString3 = "select locate('de', m.username) from Member m";
            String qlString4 = "select size(t.members) from Team t"; //Team에 있는 members 컬렉션의 사이즈
            Integer singleResult = em.createQuery(qlString3, Integer.class)
                    .getSingleResult();

            System.out.println("singleResult = " + singleResult);

            //사용자 정의 함수
            String qlString5 = "select group_concat(m.username) from Member m";
            List<String> resultList = em.createQuery(qlString5, String.class)
                    .getResultList();

            for (String s : resultList) {
                System.out.println("s = " + s);
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
