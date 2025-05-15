import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private String name;
    private String email;

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    // Setters with basic validation
    public void setName(String name) { 
        if (name != null) this.name = name.trim(); 
    }
    
    public void setEmail(String email) { 
        if (email != null) this.email = email.trim(); 
    }

    @Override
    public String toString() {
        return String.format("ID: %d, Name: %s, Email: %s", id, name, email);
    }
}