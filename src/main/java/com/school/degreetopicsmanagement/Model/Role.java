package com.school.degreetopicsmanagement.Model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "authorities")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "authority", nullable = false)
    private String authority;

    @ManyToOne // Many authorities belong to one user
    @JoinColumn(name = "user_id") // Foreign key reference to user's id
    private User user;


    public Role(String authority, User user) {
        this.authority = authority;
        this.user = user;
    }
}
