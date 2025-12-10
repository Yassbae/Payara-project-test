package ch.unil.doplab.bankease.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public class User implements Serializable {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "username", length = 100, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @Column(name = "phone_number", length = 50, nullable = false)
    private String phoneNumber;

    protected User() {
        // requis par JPA
    }

    public User(String username, String password, String firstName, String lastName,
                String email, String phoneNumber) {
        this.id = UUID.randomUUID().toString();
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
        this.firstName = Objects.requireNonNull(firstName);
        this.lastName = Objects.requireNonNull(lastName);
        this.email = Objects.requireNonNull(email);
        this.phoneNumber = Objects.requireNonNull(phoneNumber);
    }

    public String getId() {
        return id;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = Objects.requireNonNull(username); }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = Objects.requireNonNull(password); }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = Objects.requireNonNull(firstName); }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = Objects.requireNonNull(lastName); }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = Objects.requireNonNull(email); }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = Objects.requireNonNull(phoneNumber); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}