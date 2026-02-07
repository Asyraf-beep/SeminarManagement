public class Coordinator extends User {
    private String coordinatorId;

    public Coordinator(String username, String name, String email, String password, String coordinatorId) {
        super(username, name, email, password, "Coordinator");
        this.coordinatorId = coordinatorId;
    }

    public String getCoordinatorId() {
        return coordinatorId;
    }
}
