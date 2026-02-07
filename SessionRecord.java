import java.io.*;
import java.util.*;

public class SessionRecord {
    public static final String FILE = "sessions.txt";

    private String sessionId;
    private String hostName;
    private String date;
    private String start;
    private String end;
    private String location;
    private String type; // Oral / Poster

    public SessionRecord(String sessionId, String hostName, String date, String start, String end, String location, String type) {
        this.sessionId = safe(sessionId);
        this.hostName = safe(hostName);
        this.date = safe(date);
        this.start = safe(start);
        this.end = safe(end);
        this.location = safe(location);
        this.type = safe(type);
    }

    public String getSessionId() { return sessionId; }
    public String getHostName() { return hostName; }
    public String getDate() { return date; }
    public String getStart() { return start; }
    public String getEnd() { return end; }
    public String getLocation() { return location; }
    public String getType() { return type; }

    public String toLine() {
        return sessionId + "," + hostName + "," + date + "," + start + "," + end + "," + location + "," + type;
    }

    public static SessionRecord fromLine(String line) {
        if (line == null) return null;
        String[] d = line.split(",", -1);
        if (d.length < 7) return null;
        return new SessionRecord(d[0], d[1], d[2], d[3], d[4], d[5], d[6]);
    }

    public static List<SessionRecord> loadAll() {
        List<SessionRecord> list = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                SessionRecord s = fromLine(line);
                if (s != null) list.add(s);
            }
        } catch (IOException ignored) {}
        return list;
    }

    public static SessionRecord findById(String sessionId) {
        for (SessionRecord s : loadAll()) {
            if (s.getSessionId().equals(sessionId)) return s;
        }
        return null;
    }

    public static String generateNextId() {
        int max = 0;
        for (SessionRecord s : loadAll()) {
            String id = s.getSessionId();
            if (id != null && id.startsWith("SES")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > max) max = num;
                } catch (Exception ignored) {}
            }
        }
        return "SES" + String.format("%03d", max + 1);
    }

    public static boolean append(SessionRecord session) {
        if (session == null) return false;
        try (FileWriter fw = new FileWriter(FILE, true)) {
            fw.write(session.toLine() + "\n");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean deleteByIndex(int indexToDelete) {
        List<String> lines = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
        } catch (IOException e) {
            return false;
        }

        if (indexToDelete < 0 || indexToDelete >= lines.size()) return false;
        lines.remove(indexToDelete);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace(",", " ").trim();
    }
}
