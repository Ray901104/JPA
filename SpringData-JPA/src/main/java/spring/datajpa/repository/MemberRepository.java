package spring.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import spring.datajpa.dto.MemberDto;
import spring.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query(name = "Member.findByUsername") //생략해도 동작(엔티티안에 메소드명의 namedQuery가 있는지 먼저 체크)
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age") //실무에서 주로 사용
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new spring.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String username); //컬렉션

    Member findMemberByUsername(String username); //단건

    Optional<Member> findOptionalMemberByUsername(String username); //단건 Optional

    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findSliceByAge(int age, Pageable pageable);

    List<Member> findListByAge(int age, Pageable pageable);

    //카운트 쿼리 분리(복잡한 sql에서 사용, 데이터는 left join, 카운트는 left join 안해도 됨)
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findWithCountByAge(int age, Pageable pageable);

    //@Modifying: JPA의 executeUpdate 실행
    @Modifying(clearAutomatically = true) //이 옵션을 주면 영속성컨텍스트를 수동으로 초기화(em.flush())해주지 않아도 된다.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    //@EntityGraph("Member.all") //@NamedEntityGraph
    List<Member> findEntityGraphByUsername(@Param("username") String username); //메소드명 쿼리방식에도 적용 가능

    //변경감지를 위한 스냅샷을 찍지 않는다!(JPA 표준이 아닌 하이버네이트의 기능) -> 단순히 조회만으로 사용하는 경우
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    //DB Lock(select for update): 실무에서 잘 사용하지는 않는다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
