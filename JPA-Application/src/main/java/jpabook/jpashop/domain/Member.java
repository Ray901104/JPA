package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty //문제!
    private String name;

    @Embedded
    private Address address;

    //@JsonIgnore //문제!
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
