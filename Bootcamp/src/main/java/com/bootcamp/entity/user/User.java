package com.bootcamp.entity.user;

import com.bootcamp.auditing.Auditing;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "user_role")
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "user_id")
    private UUID id;

    @NotBlank(message = "email is required")
    @Email(message = "email invalid format")
    @Column(name = "email", unique = true,nullable = false)
    private String email;

    @NotBlank(message = "first name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    // middle name can be 0 zero length
    @Column(name = "middle_name")
    private String middleName;

    @NotBlank(message = "last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "password is required")
    @Column(name = "password", nullable = false)
    @Size(min = 8,message = "Password must be 8 Characters long")
    @JsonIgnore
    private String password;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted=false;

    @Column(name = "is_active", nullable = false)
    private boolean isActive=false;

    @Column(name = "is_expired", nullable = false)
    private boolean isExpired=false;

    @Column(name = "is_locked", nullable = false)
    private boolean isLocked=false;

    @Column(name = "invalid_attempt_count")
    private int invalidAttemptCount=0;

    @Column(name = "password_update_date", nullable = false)
    private LocalDate passwordUpdateDate;


    @Column(name = "activation_token_created_time",nullable = false)
    private Date activationTokenTime;

    @Column(name = "forgot_password_token_created_time",nullable = true)
    private Date forgotPasswordTokenTime;

    @ManyToOne
    @JoinColumn(name = "role")
    @JsonManagedReference
    private Role role;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "user",orphanRemoval = true)
    @JsonBackReference
    private Set<Address> addresses=new HashSet<>();

    @Embedded
    private Auditing auditing;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", isDeleted=" + isDeleted +
                ", isActive=" + isActive +
                ", isExpired=" + isExpired +
                ", isLocked=" + isLocked +
                ", invalidAttemptCount=" + invalidAttemptCount +
                ", passwordUpdateDate=" + passwordUpdateDate +
                ", activationTokenTime=" + activationTokenTime.toString() +
                ", addresses count=" + addresses.size() +
                ", role" + role.getAuthority() +
                '}';
    }
}
