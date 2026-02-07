import java.io.*;
import java.util.*;

public class PresentationRecord {
    public static final String FILE = "presentations.txt";

    // studentId,type,title,desc,researchTitle,supervisor
    private String studentId;
    private String type;
    private String title;
    private String description;
    private String researchTitle;
    private String supervisorName;

    public PresentationRecord(String studentId, String type, String title, String description,
                              String researchTitle, String supervisorName) {
        this.studentId = safe(studentId);
        this.type = safe(type);
        this.title = safe(title);
        this.description = safe(description);
        this.researchTitle = safe(researchTitle);
        this.supervisorName = safe(supervisorName);
    }

    public String getStudentId() { return studentId; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getResearchTitle() { return researchTitle; }
    public String getSupervisorName() { return supervisorName; }

    public void setType(String type) { this.type = safe(type); }
    public void setTitle(String title) { this.title = safe(title); }
    public void setDescription(String description) { this.description = safe(description); }
    public void setResearchTitle(String researchTitle) { this.researchTitle = safe(researchTitle); }
    public void setSupervisorName(String supervisorName) { this.supervisorName = safe(supervisorName); }

    public String toLine() {
        return studentId + "," + type + "," + title + "," + description + "," + researchTitle + "," + supervisorName;
    }

    public static PresentationRecord fromLine(String line) {
        if (line == null) return null;
        String[] p = line.split(",", -1);
        if (p.length < 6) return null;
        return new PresentationRecord(p[0], p[1], p[2], p[3], p[4], p[5]);
    }

    public static PresentationRecord loadByStudentId(String studentId) {
        File f = new File(FILE);
        if (!f.exists()) return null;

        String target = studentId == null ? "" : studentId.trim();
        if (target.isEmpty()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                PresentationRecord r = fromLine(line);
                if (r != null && r.getStudentId().equals(target)) return r;
            }
        } catch (IOException ignored) {}
        return null;
    }

    // insert if missing, replace if exists (your StudentUI needs this behavior)
    public static boolean upsert(PresentationRecord record) {
        if (record == null) return false;

        File input = new File(FILE);
        File temp = new File("temp_presentations.txt");
        boolean found = false;

        try (
                BufferedReader br = input.exists() ? new BufferedReader(new FileReader(input)) : null;
                BufferedWriter bw = new BufferedWriter(new FileWriter(temp))
        ) {
            if (br != null) {
                String line;
                while ((line = br.readLine()) != null) {
                    PresentationRecord existing = fromLine(line);
                    if (existing != null && existing.getStudentId().equals(record.getStudentId())) {
                        bw.write(record.toLine());
                        bw.newLine();
                        found = true;
                    } else {
                        bw.write(line);
                        bw.newLine();
                    }
                }
            }

            if (!found) {
                bw.write(record.toLine());
                bw.newLine();
            }

        } catch (IOException e) {
            return false;
        }

        if (input.exists()) input.delete();
        return temp.renameTo(input);
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace(",", " ").trim();
    }
}