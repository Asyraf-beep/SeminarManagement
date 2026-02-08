import java.io.*;
import java.util.*;

public class ReportManager {
    public static final String REPORTS_DIR = "reports";

    public static File ensureDir() {
        File dir = new File(REPORTS_DIR);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static List<String> listReportFiles() {
        File dir = ensureDir();
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
        List<String> names = new ArrayList<>();
        if (files == null) return names;

        Arrays.sort(files, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
        for (File f : files) names.add(f.getName());
        return names;
    }

    public static String readReport(String fileName) {
        if (fileName == null) return "";
        File f = new File(ensureDir(), fileName);
        if (!f.exists()) return "";

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
        } catch (IOException ignored) {}
        return sb.toString();
    }

    public static boolean writeReport(String fileName, String text) {
        if (fileName == null) return false;
        File f = new File(ensureDir(), fileName);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            bw.write(text == null ? "" : text);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean deleteReport(String fileName) {
        if (fileName == null) return false;
        File f = new File(ensureDir(), fileName);
        return f.exists() && f.delete();
    }

    public static boolean renameReport(String oldName, String newName) {
        if (oldName == null || newName == null) return false;
        File dir = ensureDir();
        File oldFile = new File(dir, oldName);
        File newFile = new File(dir, newName);
        if (!oldFile.exists()) return false;
        if (newFile.exists()) return false;
        return oldFile.renameTo(newFile);
    }

    public static String makeSafeFileName(String name) {
        if (name == null) return "report";
        String safe = name.replaceAll("[\\\\/:*?\"<>|]", "_");
        safe = safe.replaceAll("\\s+", "_").trim();
        if (safe.isEmpty()) safe = "report";
        return safe;
    }
}