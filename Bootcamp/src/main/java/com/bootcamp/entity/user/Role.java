package com.bootcamp.entity.user;

import com.bootcamp.enums.Authority;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Role {

    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "role_id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority",unique = true, nullable = false)
    private Authority authority ;

    @OneToMany(mappedBy = "role")
    @JsonBackReference
    private Set<User> users = new HashSet<>();
    public Role(Authority authority) {
        this.authority = authority;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", authority=" + authority +
                ", users count=" + users.size() +
                '}';
    }

    public void addUser(User user){
        users.add(user);
    }
}
