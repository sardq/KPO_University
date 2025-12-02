package demo.Models;

import java.util.Objects;

import demo.Core.Models.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {
    @Column(nullable = false)
    @Size(min = 4, max = 50)
    private String login;
    @Column(nullable = false)
    @Size(min = 5, max = 30)
    @Email
    private String email;
    @Column(nullable = false)
    @Size(min = 5)
    private String password;
    @Column(nullable = false)
    @Size(min = 1, max = 20)
    private String firstName;
    @Size(min = 1, max = 20)
    private String lastName;
    @Column(nullable = false)
    private UserRole role;

    public UserEntity() {
    }

    public UserEntity(String login, String email, String password, String firstName, String lastName, UserRole role) {

        this.login = login;
        this.email = email;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        final UserEntity other = (UserEntity) obj;
        return Objects.equals(other.getId(), id)
                && Objects.equals(other.getLogin(), login)
                && Objects.equals(other.getEmail(), email);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}