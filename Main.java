import java.util.*;
import java.io.*;
import java.time.*;
import java.time.format.*;

public class Main {
    private static final ArrayList<User> users = new ArrayList<>();
    private static int nextId = 1;
    private static final String DATA_FILE = "users.dat";
    private static final DateTimeFormatter DT_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        loadUsers();
        runApplication();
    }

    private static void runApplication() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                displayMenu();
                int choice = readChoice(scanner);
                processChoice(choice, scanner);
            }
        }
    }

    private static int readChoice(Scanner scanner) {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine(); // Clear invalid input
            return -1; // Will trigger default case in switch
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== User Management ===");
        System.out.println("1. Add User");
        System.out.println("2. List Users");
        System.out.println("3. Update User");
        System.out.println("4. Delete User");
        System.out.println("5. Generate Report");
        System.out.println("6. Exit");
        System.out.print("Select: ");
    }

    private static void processChoice(int choice, Scanner scanner) {
        switch (choice) {
            case 1: addUser(scanner); break;
            case 2: listUsers(); break;
            case 3: updateUser(scanner); break;
            case 4: deleteUser(scanner); break;
            case 5: generateReport(); break;
            case 6: shutdown(); break;
            default: System.out.println("Invalid selection. Please enter 1-6.");
        }
    }

    private static void addUser(Scanner scanner) {
        try {
            System.out.print("Enter name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();
            
            if (name.isEmpty()) {
                throw new ValidationException("Name cannot be empty");
            }
            if (!name.matches("^[a-zA-Z ]+$")) {
                throw new ValidationException("Name can only contain letters and spaces");
            }
            if (!email.matches("^[\\w.+\\-]+@[a-zA-Z\\d\\-]+(\\.[a-zA-Z\\d\\-]+)+$")) {
                throw new ValidationException("Invalid email format (example: user@example.com)");
            }
            
            User user = new User(nextId++, name, email);
            users.add(user);
            saveUsers();
            System.out.println("User added successfully! ID: " + user.getId());
        } catch (ValidationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listUsers() {
        if (users.isEmpty()) {
            System.out.println("No users found!");
            return;
        }
        System.out.println("\n=== User List (" + users.size() + ") ===");
        System.out.println("ID  Name                Email");
        System.out.println("-------------------------------");
        users.forEach(user -> System.out.printf("%-3d %-20s %s%n", 
            user.getId(), user.getName(), user.getEmail()));
    }

    private static void updateUser(Scanner scanner) {
        listUsers();
        if (users.isEmpty()) return;

        try {
            System.out.print("Enter user ID to update: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            for (User user : users) {
                if (user.getId() == id) {
                    System.out.print("New name (current: " + user.getName() + "): ");
                    String name = scanner.nextLine().trim();
                    if (!name.isEmpty()) {
                        if (!name.matches("^[a-zA-Z ]+$")) {
                            throw new ValidationException("Name can only contain letters and spaces");
                        }
                        user.setName(name);
                    }

                    System.out.print("New email (current: " + user.getEmail() + "): ");
                    String email = scanner.nextLine().trim();
                    if (!email.isEmpty()) {
                        if (!email.matches("^[\\w.+\\-]+@[a-zA-Z\\d\\-]+(\\.[a-zA-Z\\d\\-]+)+$")) {
                            throw new ValidationException("Invalid email format");
                        }
                        user.setEmail(email);
                    }

                    saveUsers();
                    System.out.println("User updated successfully!");
                    return;
                }
            }
            System.out.println("User not found!");
        } catch (InputMismatchException e) {
            System.out.println("Error: Please enter a valid numeric ID");
            scanner.nextLine();
        } catch (ValidationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteUser(Scanner scanner) {
        listUsers();
        if (users.isEmpty()) return;

        try {
            System.out.print("Enter user ID to delete: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            if (users.removeIf(user -> user.getId() == id)) {
                saveUsers();
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("User not found!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: Please enter a valid numeric ID");
            scanner.nextLine();
        }
    }

    private static void generateReport() {
        System.out.println("\n=== System Report ===");
        System.out.println("Total Users: " + users.size());
        System.out.println("Last Updated: " + LocalDateTime.now().format(DT_FORMAT));
        if (!users.isEmpty()) {
            System.out.println("Last User Added: " + users.get(users.size()-1).getName());
        }
    }

    private static void shutdown() {
        saveUsers();
        System.out.println("Exiting system...");
        System.exit(0);
    }

    @SuppressWarnings("unchecked")
    private static void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            ArrayList<User> loadedUsers = (ArrayList<User>) ois.readObject();
            users.clear();
            users.addAll(loadedUsers);
            nextId = users.stream().mapToInt(User::getId).max().orElse(0) + 1;
            System.out.println("Loaded " + users.size() + " users from database");
        } catch (FileNotFoundException e) {
            System.out.println("Starting with new user database");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Warning: Could not load user data. Starting fresh. " + e.getMessage());
        }
    }

    private static void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }

    private static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}