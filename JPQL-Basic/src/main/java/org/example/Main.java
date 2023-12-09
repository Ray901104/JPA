package org.example;

import org.example.jpql.Address;
import org.example.jpql.Member;
import org.example.jpql.MemberDTO;
import org.example.jpql.Team;

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

            em.flush();
            em.clear();

            List<Member> result = em.createQuery("select m from Member m", Member.class)
                    .getResultList(); //엔티티 조회

            List<Team> resultTeam = em.createQuery("select t from Member m join m.team t", Team.class)
                    .getResultList(); //join을 명시해주자(명시안해도 join이 나감)

            List<Address> resultAddress = em.createQuery("select o.address from Order o", Address.class)
                    .getResultList(); //embedded

            /**
             * 반환타입이 명시적이지 않을 때 사용
             */
            //첫번째 방법
            List resultScala = em.createQuery("select distinct m.username, m.age from Member m")
                    .getResultList(); //distinct + scala

            Object o = resultScala.get(0);
            Object[] objects = (Object[]) o;
            System.out.println("objects[0] = " + objects[0]); //username
            System.out.println("objects[1] = " + objects[1]); //age

            //두번째 방법
            List<Object[]> resultScala2 = em.createQuery("select distinct m.username, m.age from Member m")
                    .getResultList();//distinct + scala

            Object[] objects1 = resultScala2.get(0);
            System.out.println("objects1[0] = " + objects1[0]);
            System.out.println("objects1[1] = " + objects1[1]);

            //세번째 방법 -> 가장 권장하는 방법!
            List<MemberDTO> memberDTOS = em.createQuery("select new org.example.jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();

            MemberDTO memberDTO = memberDTOS.get(0);
            System.out.println("memberDTO.username = " + memberDTO.getUsername());
            System.out.println("memberDTO.age = " + memberDTO.getAge());

            /**
             * 페이징 API
             */
            for (int i = 0; i < 100; i++) {
                Member member1 = new Member();
                member1.setUsername("member" + i);
                member1.setAge(i);
                em.persist(member1);
            }

            em.flush();
            em.clear();

            List<Member> resultPaging = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(0)
                    .setMaxResults(10)
                    .getResultList();

            for (Member member1 : resultPaging) {
                System.out.println("member1 = " + member1);
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
