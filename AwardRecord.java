import java.io.*;
import java.util.*;

public class AwardRecord {
    public static final String FILE = "awards.txt";

    private String award;
    private String sessionId;
    private String studentId;
    private String username;
    private int mark;

    public AwardRecord(String award, String sessionId, String studentId, String username, int mark) {
        this.award = safe(award);
        this.sessionId = safe(sessionId);
        this.studentId = safe(studentId);
        this.username = safe(username);
        this.mark = mark;
    }

    public String getAward() { return award; }
    public String getSessionId() { return sessionId; }
    public String getStudentId() { return studentId; }
    public String getUsername() { return username; }
    public int getMark() { return mark; }

    public String toLine() {
        return award + "," + sessionId + "," + studentId + "," + username + "," + mark;
    }

    public static AwardRecord fromLine(String line) {
        if (line == null) return null;
        String[] p = line.split(",", -1);
        if (p.length < 5) return null;

        int mark = parseInt(p[4], 0);
        return new AwardRecord(p[0], p[1], p[2], p[3], mark);
    }

    public static List<AwardRecord> loadAll() {
        List<AwardRecord> list = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                AwardRecord a = fromLine(line);
                if (a != null) list.add(a);
            }
        } catch (IOException ignored) {}
        return list;
    }

    public static boolean saveAll(List<AwardRecord> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE))) {
            for (AwardRecord a : list) {
                bw.write(a.toLine());
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static int parseInt(String s, int fallback) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return fallback; }
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace(",", " ").trim();
    }
    public String toDisplay() {
        String name = (username == null || username.trim().isEmpty()) ? "unknown" : username;
        return "Winner: " + name + " (" + studentId + ")\n"
                + "Session: " + sessionId + "\n"
                + "Total Mark: " + mark;
    }

    public static AwardRecord pickBestForType(String wantedType) {
        AwardRecord best = null;

        // Build sessionId -> type map from sessions.txt
        Map<String, String> typeMap = new HashMap<>();
        for (SessionRecord s : SessionRecord.loadAll()) {
            typeMap.put(s.getSessionId(), s.getType());
        }

        // Scan evaluations.txt, pick highest total for matching session type
        for (EvaluationRecord e : EvaluationRecord.loadAll()) {
            String type = typeMap.get(e.getSessionId());
            if (type == null) continue;
            if (!type.equalsIgnoreCase(wantedType)) continue;

            int total = e.getTotal();
            if (best == null || total > best.getMark()) {
                best = new AwardRecord(
                        "Best " + wantedType,
                        e.getSessionId(),
                        e.getStudentId(),
                        "",       // username can be filled by dashboard if you want
                        total
                );
            }
        }

        return best;
    }

    public static List<String> loadEvaluatedStudentIds() {
        Set<String> set = new LinkedHashSet<>();
        for (EvaluationRecord e : EvaluationRecord.loadAll()) {
            String sid = e.getStudentId();
            if (sid != null && !sid.trim().isEmpty()) set.add(sid.trim());
        }
        return new ArrayList<>(set);
    }

    public static boolean saveWinners(String fileName, AwardRecord... awards) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (AwardRecord a : awards) {
                if (a == null) continue;
                bw.write(a.toLine());
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
