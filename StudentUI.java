import javax.swing.*;
import java.awt.*;
import java.io.*;

public class StudentUI {

    private Student student;
    private Presentation presentation;

    static final String PRESENTATION_FILE = "presentations.txt";

    public StudentUI(Student student, Presentation presentation) {
        this.student = student;
        this.presentation = presentation;
        StudentPage(this.student, this.presentation);
    }

    public static void StudentPage(Student student, Presentation presentation) {

        loadPresentationFromFile(student, presentation);

        MyFrame frame = new MyFrame(750, 600);
        frame.setTitle("Student Dashboard");
        frame.add(new HeaderPanel(), BorderLayout.NORTH);

        // ---------- TEXT AREAS ----------
        JTextArea studentInfoArea = new JTextArea(student.studentDetails());
        studentInfoArea.setEditable(false);
        studentInfoArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        studentInfoArea.setLineWrap(true);
        studentInfoArea.setWrapStyleWord(true);
        studentInfoArea.setOpaque(false);

        JTextArea presInfoArea = new JTextArea(presentation.presentationDetails());
        presInfoArea.setEditable(false);
        presInfoArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        presInfoArea.setLineWrap(true);
        presInfoArea.setWrapStyleWord(true);
        presInfoArea.setOpaque(false);

        JPanel studentBox = new JPanel(new BorderLayout());
        studentBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        studentBox.add(studentInfoArea, BorderLayout.CENTER);

        JPanel presBox = new JPanel(new BorderLayout());
        presBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        presBox.add(presInfoArea, BorderLayout.CENTER);

        JPanel detailsRow = new JPanel(new GridLayout(1, 2, 15, 0));
        detailsRow.add(studentBox);
        detailsRow.add(presBox);
        detailsRow.setPreferredSize(new Dimension(0, 160));

        // ---------- BUTTONS ----------
        JButton logoutBtn = new JButton("Logout");
        JButton detailBtn = new JButton("View Presentation Details");
        JButton setResearchTitleBtn = new JButton("Set Research Title");
        JButton setSupervisorBtn = new JButton("Set Supervisor Name");
        JButton viewSubmittedBtn = new JButton("View Submitted Presentation");
        JButton setTypeBtn = new JButton("Set Presentation Type");
        JButton setTitleBtn = new JButton("Set Presentation Title");
        JButton setDescBtn = new JButton("Set Presentation Description");
        JButton uploadBtn = new JButton("Upload Slides");
        JButton deleteSlidesBtn = new JButton("Delete Slides");
        JButton uploadPosterBtn = new JButton("Upload Poster");
        JButton deletePosterBtn = new JButton("Delete Poster");

        JPanel actionPanel = new JPanel(new GridLayout(0, 1, 0, 15));

        Runnable rebuildActionPanel = () -> {
            actionPanel.removeAll();
            actionPanel.add(detailBtn);
            actionPanel.add(setResearchTitleBtn);
            actionPanel.add(setSupervisorBtn);
            actionPanel.add(viewSubmittedBtn);
            actionPanel.add(setTypeBtn);
            actionPanel.add(setTitleBtn);
            actionPanel.add(setDescBtn);

            if (student.getPresentationType().equals("Oral")) {
                actionPanel.add(uploadBtn);
                actionPanel.add(deleteSlidesBtn);
            } else {
                actionPanel.add(uploadPosterBtn);
                actionPanel.add(deletePosterBtn);
            }

            actionPanel.revalidate();
            actionPanel.repaint();
        };

        rebuildActionPanel.run();

        // ---------- MAIN LAYOUT ----------
        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.WHITE, 5),
                        BorderFactory.createEmptyBorder(20, 30, 20, 30)
                )
        );

        // LOGOUT AT VERY TOP (ABOVE INFO BOXES)
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerWrapper.add(logoutBtn);
        centerWrapper.add(Box.createVerticalStrut(20));

        centerWrapper.add(detailsRow);
        centerWrapper.add(Box.createVerticalStrut(20));
        centerWrapper.add(actionPanel);
        centerWrapper.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(centerWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        frame.add(scrollPane, BorderLayout.CENTER);

        // ---------- ACTIONS ----------
        logoutBtn.addActionListener(e -> {
            frame.dispose();
            LoginSignupUI.showLogin();
        });

        uploadBtn.addActionListener(e -> uploadFile(frame, presentation));
        uploadPosterBtn.addActionListener(e -> uploadFile(frame, presentation));

        detailBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(frame, presentation.presentationDetails())
        );

        deletePosterBtn.addActionListener(e -> {
            if (presentation.deletePresentation())
                JOptionPane.showMessageDialog(frame, "Presentation file deleted!");
        });

        deleteSlidesBtn.addActionListener(e -> {
            if (presentation.deletePresentation())
                JOptionPane.showMessageDialog(frame, "Presentation file deleted!");
        });

        setTypeBtn.addActionListener(e -> {
            String[] options = {"Oral", "Poster"};
            String choice = (String) JOptionPane.showInputDialog(
                    frame, "Select Presentation Type:",
                    "Presentation Type",
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, student.getPresentationType()
            );
            if (choice == null) return;

            student.setPresentationType(choice);
            presentation.setPresentationType(choice);
            savePresentation(student, presentation);

            studentInfoArea.setText(student.studentDetails());
            presInfoArea.setText(presentation.presentationDetails());
            rebuildActionPanel.run();
        });

        setResearchTitleBtn.addActionListener(e -> {
            String t = JOptionPane.showInputDialog(frame, "Enter Research Title:", student.getResearchTitle());
            if (t == null || t.trim().isEmpty()) return;
            student.setResearchTitle(t.trim());
            savePresentation(student, presentation);
            studentInfoArea.setText(student.studentDetails());
        });

        setSupervisorBtn.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(frame, "Enter Supervisor Name:", student.getSupervisorName());
            if (s == null || s.trim().isEmpty()) return;
            student.setSupervisorName(s.trim());
            savePresentation(student, presentation);
            studentInfoArea.setText(student.studentDetails());
        });

        viewSubmittedBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(frame, presentation.presentationDetails())
        );

        frame.setVisible(true);
    }

    // ---------- FILE HELPERS ----------
    private static void loadPresentationFromFile(Student student, Presentation presentation) {
        File f = new File(PRESENTATION_FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 4) continue;
                if (!p[0].trim().equals(student.getId().trim())) continue;

                student.setPresentationType(p[1].trim());
                presentation.setPresentationType(p[1].trim());
                presentation.setTitle(p[2].trim());
                presentation.setDescription(p[3].trim());

                if (p.length >= 6) {
                    student.setResearchTitle(p[4].trim());
                    student.setSupervisorName(p[5].trim());
                }
                return;
            }
        } catch (IOException ignored) {}
    }

    private static void uploadFile(JFrame frame, Presentation presentation) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return;
        presentation.uploadPresentation(chooser.getSelectedFile());
    }

    private static void savePresentation(Student student, Presentation presentation) {
        File input = new File(PRESENTATION_FILE);
        File temp = new File("temp_presentations.txt");

        try (
                BufferedReader br = input.exists() ? new BufferedReader(new FileReader(input)) : null;
                BufferedWriter bw = new BufferedWriter(new FileWriter(temp))
        ) {
            boolean found = false;

            if (br != null) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(student.getId() + ",")) {
                        bw.write(buildLine(student, presentation));
                        found = true;
                    } else bw.write(line);
                    bw.newLine();
                }
            }

            if (!found) {
                bw.write(buildLine(student, presentation));
                bw.newLine();
            }

        } catch (IOException ignored) {}

        if (input.exists()) input.delete();
        temp.renameTo(input);
    }

    private static String buildLine(Student student, Presentation presentation) {
        return student.getId() + "," +
                presentation.getPresentationType() + "," +
                presentation.getTitle() + "," +
                presentation.getDescription() + "," +
                student.getResearchTitle() + "," +
                student.getSupervisorName();
    }
}
