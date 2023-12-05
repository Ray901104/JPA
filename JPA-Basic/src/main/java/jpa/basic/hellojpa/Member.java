package jpa.basic.hellojpa;

import javax.persistence.*;
import java.util.Date;

//@Entity
/*@SequenceGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ", //매핑할 데이터베이스 시퀀스 이름
        initialValue = 1, allocationSize = 1) //테이블안에서 시퀀스 매핑*/
/*@TableGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "MEMBER_SEQ", allocationSize = 1)*/
public class Member {

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO) //(디폴트)DB 방언에 맞춰서 자동으로 생성된다.
    //@GeneratedValue(strategy = GenerationType.IDENTITY) //기본키 생성을 DB에 위임 ex) MYSQL
    /*@GeneratedValue(strategy =
            GenerationType.SEQUENCE,
            generator = "MEMBER_SEQ_GENERATOR")*/ //Oracle 등에서 사용하는 시퀀스
    /*@GeneratedValue(
            strategy = GenerationType.TABLE,
            generator = "MEMBER_SEQ_GENERATOR")*/ //키 생성 전용 테이블을 하나 만들어서 DB 시퀀스 흉내내는 전략
    private Long id;

    @Column(name = "name")
    private String username;

    private Integer age;

    //enum 타입 매핑, ORDINAL(enum의 순서를 저장)은 사용하지 말자.
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP) //날짜 타입 매핑
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob //varchar를 넘어서는 긴 문자열을 넣고 싶을 때 사용
    private String description;

    @Transient //DB와 매핑하지 않는다.
    private int temp;

    //JPA 스펙상 기본 생성자(public, protected)가 있어야 한다.
    public Member() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
