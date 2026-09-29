package murach.business;
import javax.persistence.Entity;
import javax.persistence.Id;

import java.io.Serializable;
@Entity
public class User implements Serializable {
    @Id
    private String email;
    private String firstName;
    private String lastName;

    public User() {
    }

    public User(String firstName, String lastName, String email) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
