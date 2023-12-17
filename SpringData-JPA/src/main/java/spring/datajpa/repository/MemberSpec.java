package spring.datajpa.repository;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import spring.datajpa.entity.Member;
import spring.datajpa.entity.Team;

public class MemberSpec {

    public static Specification<Member> teamName(final String teamName) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasLength(teamName)) {
                return null;
            }

            Join<Member, Team> t = root.join("team", JoinType.INNER);//회원과 조인
            return criteriaBuilder.equal(t.get("name"), teamName);
        };
    }

    public static Specification<Member> username(final String username) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("username"), username);
    }
}
