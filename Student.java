public class Student extends User {
    private String studentId;
    private String researchTitle;
    private String supervisorName;
    private String presentationType;

    public Student(String username, String name, String email, String password, String studentId) {
        super(username, name, email, password, "Student");
        this.studentId = studentId;
        this.presentationType = "Poster";
    }

    // If you still want the simple constructor:
    public Student(String studentId) {
        super(null, null, null, null, "Student");
        this.studentId = studentId;
        this.presentationType = "Poster";
    }

    public String getId() { return studentId; }

    public String getResearchTitle() { return researchTitle; }
    public void setResearchTitle(String researchTitle) { this.researchTitle = researchTitle; }

    public String getSupervisorName() { return supervisorName; }
    public void setSupervisorName(String supervisorName) { this.supervisorName = supervisorName; }

    public String getPresentationType() { return presentationType; }
    public void setPresentationType(String presentationType) { this.presentationType = presentationType; }

    public String studentDetails() {
        return "Student ID: " + studentId
                + "\nResearch Title: " + researchTitle
                + "\nSupervisor Name: " + supervisorName
                + "\nPresentation Type: " + presentationType;
    }
}
