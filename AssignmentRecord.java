import java.io.*;
import java.util.*;

public class AssignmentRecord {
    public static final String FILE = "assignments.txt";

    private String sessionId;
    private String evaluatorIds; // joined with ';' (e.g. EV113;EV123)
    private String presenterId;  // student ID (e.g. STU008)

    public AssignmentRecord(String sessionId, String evaluatorIds, String presenterId) {
        this.sessionId = safe(sessionId);
        this.evaluatorIds = safeKeepDelimiters(evaluatorIds);
        this.presenterId = safe(presenterId);
    }

    public String getSessionId() { return sessionId; }
    public String getEvaluatorIds() { return evaluatorIds; }
    public String getPresenterId() { return presenterId; }

    public String toLine() {
        return sessionId + "," + evaluatorIds + "," + presenterId;
    }

    public static AssignmentRecord fromLine(String line) {
        if (line == null) return null;
        String[] p = line.split(",", -1);
        if (p.length < 3) return null;
        return new AssignmentRecord(p[0], p[1], p[2]);
    }

    public static List<AssignmentRecord> loadAll() {
        List<AssignmentRecord> list = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                AssignmentRecord a = fromLine(line);
                if (a != null) list.add(a);
            }
        } catch (IOException ignored) {}
        return list;
    }

    public static AssignmentRecord findBySessionId(String sessionId) {
        for (AssignmentRecord a : loadAll()) {
            if (a.getSessionId().equals(sessionId)) return a;
        }
        return null;
    }

    public static List<AssignmentRecord> findByEvaluatorId(String evaluatorId) {
        List<AssignmentRecord> out = new ArrayList<>();
        for (AssignmentRecord a : loadAll()) {
            if (containsEvaluator(a.getEvaluatorIds(), evaluatorId)) out.add(a);
        }
        return out;
    }

    public static boolean saveAll(List<AssignmentRecord> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE))) {
            for (AssignmentRecord a : list) {
                bw.write(a.toLine());
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean containsEvaluator(String evaluatorIdsJoinedBySemicolon, String evaluatorId) {
        if (evaluatorId == null) return false;
        String target = evaluatorId.trim();
        if (target.isEmpty()) return false;

        String raw = evaluatorIdsJoinedBySemicolon == null ? "" : evaluatorIdsJoinedBySemicolon.trim();
        if (raw.isEmpty()) return false;

        String[] ids = raw.split(";");
        for (String id : ids) {
            if (id.trim().equals(target)) return true;
        }
        return false;
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace(",", " ").trim();
    }

    // keep ';' as list delimiter but still prevent commas breaking CSV
    private static String safeKeepDelimiters(String s) {
        return s == null ? "" : s.replace(",", " ").trim();
    }
}
