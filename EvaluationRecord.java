import java.io.*;
import java.util.*;

public class EvaluationRecord {
    public static final String FILE = "evaluations.txt";

    private String sessionId;
    private String studentId;
    private String evaluatorName; // you currently store username here
    private int total;
    private int rq, res, meth, pres, orig;
    private String comments;

    public EvaluationRecord(String sessionId, String studentId, String evaluatorName,
                            int total, int rq, int res, int meth, int pres, int orig, String comments) {
        this.sessionId = safe(sessionId);
        this.studentId = safe(studentId);
        this.evaluatorName = safe(evaluatorName);
        this.total = total;
        this.rq = rq; this.res = res; this.meth = meth; this.pres = pres; this.orig = orig;
        this.comments = safe(comments);
    }

    public String getSessionId() { return sessionId; }
    public String getStudentId() { return studentId; }
    public String getEvaluatorName() { return evaluatorName; }
    public int getTotal() { return total; }
    public String getComments() { return comments; }

    public String toLine() {
        return sessionId + "," + studentId + "," + evaluatorName + "," + total + ","
                + rq + "," + res + "," + meth + "," + pres + "," + orig + "," + comments;
    }

    public static EvaluationRecord fromLine(String line) {
        if (line == null) return null;
        String[] p = line.split(",", -1);
        if (p.length < 10) return null;

        int total = parseInt(p[3], -1);
        int rq = parseInt(p[4], -1);
        int res = parseInt(p[5], -1);
        int meth = parseInt(p[6], -1);
        int pres = parseInt(p[7], -1);
        int orig = parseInt(p[8], -1);

        if (total < 0) return null;

        return new EvaluationRecord(p[0], p[1], p[2], total, rq, res, meth, pres, orig, p[9]);
    }

    public static List<EvaluationRecord> loadAll() {
        List<EvaluationRecord> list = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                EvaluationRecord r = fromLine(line);
                if (r != null) list.add(r);
            }
        } catch (IOException ignored) {}
        return list;
    }

    public static List<EvaluationRecord> loadByEvaluatorName(String evaluatorName) {
        List<EvaluationRecord> out = new ArrayList<>();
        for (EvaluationRecord r : loadAll()) {
            if (r.getEvaluatorName().equals(evaluatorName)) out.add(r);
        }
        return out;
    }

    public static boolean append(EvaluationRecord record) {
        if (record == null) return false;
        try (FileWriter fw = new FileWriter(FILE, true)) {
            fw.write(record.toLine() + "\n");
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
}