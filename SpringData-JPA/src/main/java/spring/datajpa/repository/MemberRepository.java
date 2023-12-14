package spring.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query(name = "Member.findByUsername") //생략해도 동작(엔티티안에 메소드명의 namedQuery가 있는지 먼저 체크)
    List<Member> findByUsername(@Param("username") String username);
}
