package model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Courier {
    private String login;
    private String password;
    private String firstName;

    public Courier(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public String toString() {
        return "{login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '}';
    }
}
