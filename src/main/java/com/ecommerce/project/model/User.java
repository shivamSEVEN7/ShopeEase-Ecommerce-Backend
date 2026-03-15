package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users",
uniqueConstraints =
        @UniqueConstraint(columnNames = {"username", "email"})
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;
    
    @Size(max=30)
    private String name;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @NotBlank
    @Size(max = 20)
    @Column(name = "username")
    private String username;
    @NotBlank
    @Size(max = 50)
    @Email
    @Column(name = "email")
    private String email;

    private String mobileNumber;
    @NotBlank
    @Column(name = "password")
    private String password;
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus = AccountStatus.UNVERIFIED;
    public User(String name, Gender gender, String username, String email, String mobileNumber, String password) {
        this.gender=gender;
        this.name = name;
        this.username = username;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.password = password;
    }
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Getter
    @Setter
    private Set<Role> roles = new HashSet<>();
    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE,CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Address> addresses;
    @OneToOne(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Cart cart;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

}
