package jpabook.jpashop.domain.v2;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@Entity
public class Team {

    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    private List<MemberV2> members = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MemberV2> getMembers() {
        return members;
    }

    public void setMembers(List<MemberV2> members) {
        this.members = members;
    }
}
