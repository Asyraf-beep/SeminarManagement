import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;

public class CoordDashboard {
    private JFrame frame;
    private JPanel cards;
    private String username;
    private String coordID;

    // For create events
    private DefaultTableModel assignTableModel;
    // For reports
    private DefaultListModel<String> reportListModel;
    private JTable assignTable;
    private JList<String> reportList;
    private JTextArea reportTextArea;

    // For creating sessions
    static final String SESSION_FILE = "sessions.txt";
    private String currentReportName = null;
    private boolean reportIncomplete = false; //check if the report is drafted or not

    public CoordDashboard(String username, String coordID) {
        this.username = username;
        this.coordID = coordID;
        frame = new MyFrame(750, 600);
        frame.setTitle("Coordinator Dashboard");
        frame.setLayout(new BorderLayout());

        // HEADER --------------------------------------------------------------------------------
        frame.add(new HeaderPanel(), BorderLayout.NORTH);

        // ----- SIDEBAR -----
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(5, 1, 0, 10));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        sidebar.setBackground(new Color(230, 230, 230));

        JButton homeBtn = new JButton("Main");
        JButton sessionBtn = new JButton("Manage Sessions");
        JButton assignBtn = new JButton("Assign Roles");
        JButton reportBtn = new JButton("Reports");
        JButton awardBtn = new JButton("Awards");

        sidebar.add(homeBtn);
        sidebar.add(sessionBtn);
        sidebar.add(assignBtn);
        sidebar.add(reportBtn);
        sidebar.add(awardBtn);

        frame.add(sidebar, BorderLayout.WEST);

        // ----- MAIN CONTENT (CARDS) -----
        cards = new JPanel(new CardLayout());

        cards.add(createMainPanel(), "main");
        cards.add(createSessionPanel(), "sessions");
        cards.add(createAssignPanel(), "assign");
        cards.add(createReportPanel(), "reports");
        cards.add(createAwardPanel(), "awards");

        frame.add(cards, BorderLayout.CENTER);

        // ----- BUTTON ACTIONS -----
        homeBtn.addActionListener(e -> showCard("main"));
        sessionBtn.addActionListener(e -> showCard("sessions"));
        assignBtn.addActionListener(e -> showCard("assign"));
        reportBtn.addActionListener(e -> showCard("reports"));
        awardBtn.addActionListener(e -> showCard("awards"));

        frame.setVisible(true);
    }

    private void showCard(String name) {
        CardLayout cl = (CardLayout) cards.getLayout();
        cl.show(cards, name);

        if (name.equals("assign")) {
            loadAssignments(assignTableModel); // reload every time
        }
    }

    // ---------------- PANELS ----------------
// ---------------- PANELS ----------------
// ---------------- PANELS ----------------
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Main Menu"));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("WELCOME " + username + "! YOUR ROLE IS COORDINATOR");
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoutBtn.addActionListener(e -> {
            frame.dispose();               // same behavior as Evaluator
            LoginSignupUI.showLogin();     // return to login screen
        });

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(welcomeLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(logoutBtn);
        centerPanel.add(Box.createVerticalGlue());

        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }



    // ------------------------CREATE SESSION--------------------------------------
    private JPanel createSessionPanel() { 
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Create Seminar Session"));

        // ---------- FORM ----------
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Host
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Session Host:"), gbc);

        gbc.gridx = 1;
        JTextField hostField = new JTextField(username);
        hostField.setEditable(false);
        hostField.setBackground(Color.LIGHT_GRAY);
        form.add(hostField, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Date (DD-MM-YYYY):"), gbc);

        gbc.gridx = 1;
        JTextField dateField = new JTextField(10);
        form.add(dateField, gbc);

        // Time
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Time Slot:"), gbc);

        gbc.gridx = 1;
        JTextField startField = new JTextField(5);
        JTextField endField = new JTextField(5);
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.add(startField);
        timePanel.add(new JLabel("to"));
        timePanel.add(endField);
        form.add(timePanel, gbc);

        // Location
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Location:"), gbc);

        gbc.gridx = 1;
        JTextField locationField = new JTextField(15);
        form.add(locationField, gbc);

        // Type
        gbc.gridx = 0; gbc.gridy = 4;
        form.add(new JLabel("Presentation Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> typeBox = new JComboBox<>(
                new String[]{"Oral", "Poster"}
        );
        form.add(typeBox, gbc);

        // Button
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton createBtn = new JButton("Create Session");
        form.add(createBtn, gbc);
        panel.add(form, BorderLayout.NORTH);
        

        // ---------- SESSION TABLE ----------
        String[] columnNames = {
            "Session ID",
            "Coordinator Name",
            "Date",
            "Time",
            "Location",
            "Presentation Type"
        };

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // table is read-only
            }
        };

        JTable sessionTable = new JTable(tableModel);
        sessionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sessionTable.setRowHeight(22);

        // Load sessions into table
        loadSessionsIntoTable(tableModel);

        // Title
        JPanel tablePanel = new JPanel(new BorderLayout());
        JLabel tableTitle = new JLabel("Existing Sessions");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 13));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(sessionTable), BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);

        // ---------- DELETE ----------
        JButton deleteBtn = new JButton("Delete Selected Session");
        panel.add(deleteBtn, BorderLayout.SOUTH);

        // ---------- ACTIONS ----------
        createBtn.addActionListener(e -> {
            String start = startField.getText();
            String end = endField.getText();
            String location = locationField.getText();
            String type = (String) typeBox.getSelectedItem();
            String date = dateField.getText();
            if (start.isEmpty() || end.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields");
                return;
            }

            saveSession(username, date, start, end, location, type);
            loadSessionsIntoTable(tableModel); // refresh table

            dateField.setText("");
            startField.setText("");
            endField.setText("");
            locationField.setText("");
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = sessionTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Select a session to delete");
                return;
            }

            deleteSession(selectedRow);
            // refresh sessions
            loadSessionsIntoTable(tableModel);
        });

    return panel;
    }

    // session create a new unique ID
    static String generateSessionID() {
        return SessionRecord.generateNextId();
    }

    // Read from the sessions.txt file and put into table
    // public static void loadSessionsIntoTable(DefaultTableModel model) {
    //     model.setRowCount(0);
    //     try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE))) {
    //         String line;
    //         while ((line = br.readLine()) != null) {
    //             String[] data = line.split(",");
    //             if (data.length < 7) continue;

    //             model.addRow(new Object[]{
    //                     data[0], // Session ID
    //                     data[1], // Host Name
    //                     data[2], // Date
    //                     data[3] + " - " + data[4], // Time
    //                     data[5], // Location
    //                     data[6] // Type
    //             });
    //         }
    //     } catch (IOException ignored) {}
    // }
    public static void loadSessionsIntoTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (SessionRecord s : SessionRecord.loadAll()) {
            model.addRow(new Object[]{
                    s.getSessionId(),
                    s.getHostName(),
                    s.getDate(),
                    s.getStart() + " - " + s.getEnd(),
                    s.getLocation(),
                    s.getType()
            });
        }
    }

    // Write into session.txt
    // public static void saveSession(String hostName, String date, String start, String end, String location, String type) {
    //     String sessionID = generateSessionID(); // auto-generate ID
    //     try (FileWriter fw = new FileWriter(SESSION_FILE, true)) {
    //         fw.write(sessionID + "," + hostName + "," + date + "," + start + "," + end + "," +
    //                 location + "," + type + "\n");
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }
    public static void saveSession(String hostName, String date, String start, String end, String location, String type) {
        String sessionID = SessionRecord.generateNextId();
        SessionRecord s = new SessionRecord(sessionID, hostName, date, start, end, location, type);
        if (!SessionRecord.append(s)) {
            // keep your current behavior
            System.out.println("Failed to save session.");
        }
    }

    // delete session from file
    // static void deleteSession(int indexToDelete) {
    //     try {
    //         File inputFile = new File(SESSION_FILE);
    //         File tempFile = new File("temp_sessions.txt");

    //         BufferedReader reader = new BufferedReader(new FileReader(inputFile));
    //         BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

    //         String line;
    //         int index = 0;

    //         while ((line = reader.readLine()) != null) {
    //             if (index != indexToDelete) {
    //                 writer.write(line);
    //                 writer.newLine();
    //             }
    //             index++;
    //         }
    //         reader.close();
    //         writer.close();

    //         inputFile.delete();
    //         tempFile.renameTo(inputFile);

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // } 
    static void deleteSession(int indexToDelete) {
        SessionRecord.deleteByIndex(indexToDelete);
    }
    // ------------------------------------------------- CREATE SESSION END -----------------------------------------------

    // --------------------------------------ASSIGN EVALUATORS AND STUDENTS------------------------------------------
    private JPanel createAssignPanel() { 
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Assign Evaluators & Student Presenter"));

        String[] columnNames = {"Session ID", "Date", "Evaluators", "Student Presenter"};

        assignTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        assignTable = new JTable(assignTableModel);
        assignTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        assignTable.setRowHeight(25);

        panel.add(new JScrollPane(assignTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addEvaluatorBtn = new JButton("Add Evaluator(s)");
        JButton setPresenterBtn = new JButton("Set Student Presenter");
        

        buttonPanel.add(addEvaluatorBtn);
        buttonPanel.add(setPresenterBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // ---------- ACTIONS ----------

        addEvaluatorBtn.addActionListener(e -> {
            int selectedRow = assignTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Select a session first!");
                return;
            }

            String[] evaluatorOptions = loadUserDisplayByRole("Evaluator"); // "asyraf (EV113)"
            JList<String> list = new JList<>(evaluatorOptions);
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            int result = JOptionPane.showConfirmDialog(
                    frame,
                    new JScrollPane(list),
                    "Select Evaluators",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {
                java.util.List<String> ids = new java.util.ArrayList<>();
                for (String s : list.getSelectedValuesList()) {
                    ids.add(extractIdFromDisplay(s));
                }

                // use ';' to avoid CSV conflict
                String evalIds = String.join(";", ids);
                assignTableModel.setValueAt(evalIds, selectedRow, 2);

                saveAssignments(assignTable); // autosave
            }
        });

        setPresenterBtn.addActionListener(e -> {
            int selectedRow = assignTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Select a session first!");
                return;
            }

            String[] studentOptions = loadUserDisplayByRole("Student"); // "kim (STU009)"
            String selected = (String) JOptionPane.showInputDialog(
                    frame,
                    "Select Student Presenter:",
                    "Student Presenter",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    studentOptions,
                    null
            );

            if (selected != null) {
                String studentId = extractIdFromDisplay(selected);
                assignTableModel.setValueAt(studentId, selectedRow, 3);

                saveAssignments(assignTable); // autosave
            }
        });

        return panel;
    }

    // private void saveAssignments(JTable table) {
    //     try (FileWriter fw = new FileWriter("assignments.txt")) {
    //         DefaultTableModel model = (DefaultTableModel) table.getModel();

    //         for (int i = 0; i < model.getRowCount(); i++) {
    //             String sessionID = (String) model.getValueAt(i, 0);

    //             String evaluators = (String) model.getValueAt(i, 2);
    //             if (evaluators == null) evaluators = "";

    //             String presenter = (String) model.getValueAt(i, 3);
    //             if (presenter == null) presenter = "";

    //             fw.write(sessionID + "," + evaluators + "," + presenter + "\n");
    //         }
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }
    private void saveAssignments(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        java.util.List<AssignmentRecord> list = new java.util.ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            String sessionID = (String) model.getValueAt(i, 0);

            String evaluatorIds = (String) model.getValueAt(i, 2);
            if (evaluatorIds == null) evaluatorIds = "";

            // normalize: allow user to type commas, but store as ';'
            evaluatorIds = evaluatorIds.replace(",", ";").replace(" ", "").trim();

            String presenterId = (String) model.getValueAt(i, 3);
            if (presenterId == null) presenterId = "";

            list.add(new AssignmentRecord(sessionID, evaluatorIds, presenterId));
        }

        AssignmentRecord.saveAll(list);
    }

    private static String[] loadUserDisplayByRole(String role) {
        java.util.List<String> list = new java.util.ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(LoginSignupUI.FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length < 4) continue;

                String username = data[0].trim();
                String r = data[2].trim();
                String id = data[3].trim();

                if (r.equalsIgnoreCase(role) && !id.isEmpty()) {
                    list.add(username + " (" + id + ")");
                }
            }
        } catch (IOException ignored) {}

        return list.toArray(new String[0]);
    }

    private static String extractIdFromDisplay(String s) {
        int l = s.lastIndexOf('(');
        int r = s.lastIndexOf(')');
        if (l == -1 || r == -1 || r <= l) return s.trim();
        return s.substring(l + 1, r).trim();
    }

    // private void loadAssignments(DefaultTableModel model) {
    //     model.setRowCount(0); // clear table
    //     // Load session info from sessions.txt
    //     try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE))) {
    //         String line;
    //         while ((line = br.readLine()) != null) {
    //             String[] data = line.split(",");
    //             if (data.length < 7) continue;
    //             String sessionID = data[0];
    //             String date = data[2];

    //             // Load existing assignments if available
    //             String evaluators = "";
    //             String presenter = "";
    //             File assignFile = new File("assignments.txt");
    //             if (assignFile.exists()) {
    //                 try (BufferedReader abr = new BufferedReader(new FileReader(assignFile))) {
    //                     String aLine;
    //                     while ((aLine = abr.readLine()) != null) {
    //                         String[] aData = aLine.split(",", -1); // sessionID,evaluators,presenter
    //                         if (aData.length >= 3 && aData[0].equals(sessionID)) {
    //                             evaluators = aData[1];
    //                             presenter = aData[2];
    //                         }
    //                     }
    //                 }
    //             }
    //             model.addRow(new Object[]{sessionID, date, evaluators, presenter});
    //         }
    //     } catch (IOException ignored) {}
    // }
    private void loadAssignments(DefaultTableModel model) {
        model.setRowCount(0);

        java.util.List<SessionRecord> sessions = SessionRecord.loadAll();
        for (SessionRecord s : sessions) {
            AssignmentRecord a = AssignmentRecord.findBySessionId(s.getSessionId());

            String evals = (a == null) ? "" : a.getEvaluatorIds();
            String presenter = (a == null) ? "" : a.getPresenterId();

            model.addRow(new Object[]{
                    s.getSessionId(),
                    s.getDate(),
                    evals,
                    presenter
            });
        }
    }
// ---------------------------------------------------------END ASSIGN PANEL --------------------------------------------


    // --------------------------------------- CREATE REPORT PANEL -----------------------------------------------------
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Schedules & Reports"));

        ReportManager.ensureDir();

        // Left: report list
        reportListModel = new DefaultListModel<>();
        reportList = new JList<>(reportListModel);
        reportList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScroll = new JScrollPane(reportList);
        listScroll.setPreferredSize(new Dimension(220, 0));

        // Right: editor
        reportTextArea = new JTextArea();
        reportTextArea.setLineWrap(true);
        reportTextArea.setWrapStyleWord(true);
        JScrollPane editorScroll = new JScrollPane(reportTextArea);

        // Top buttons
        JPanel topBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JButton newBtn = new JButton("New Report");
        JButton saveBtn = new JButton("Save");
        JButton deleteBtn = new JButton("Delete");
        JButton renameBtn = new JButton("Rename");
        topBtns.add(newBtn);
        topBtns.add(saveBtn);
        topBtns.add(deleteBtn);
        topBtns.add(renameBtn);

        panel.add(topBtns, BorderLayout.NORTH);
        panel.add(listScroll, BorderLayout.WEST);
        panel.add(editorScroll, BorderLayout.CENTER);

        refreshReportList();

        // Mark incomplete when user edits
        reportTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                reportIncomplete = true;
            }
        });

        // Open selected report
        reportList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            String selected = reportList.getSelectedValue();
            if (selected == null) return;

            if (reportIncomplete && currentReportName != null) {
                int choice = JOptionPane.showConfirmDialog(
                        frame,
                        "You have unsaved changes. Discard them and open another report?",
                        "Unsaved Changes",
                        JOptionPane.YES_NO_OPTION
                );
                if (choice != JOptionPane.YES_OPTION) {
                    reportList.setSelectedValue(currentReportName, true);
                    return;
                }
            }

            currentReportName = selected;
            reportTextArea.setText(ReportManager.readReport(selected));
            reportIncomplete = false;
        });

        // Create new report
        newBtn.addActionListener(e -> {
            if (reportIncomplete && currentReportName != null) {
                int choice = JOptionPane.showConfirmDialog(
                        frame,
                        "You have unsaved changes. Continue and lose them?",
                        "Unsaved Changes",
                        JOptionPane.YES_NO_OPTION
                );
                if (choice != JOptionPane.YES_OPTION) return;
            }

            String name = JOptionPane.showInputDialog(frame, "Enter report name:");
            if (name == null) return;
            name = name.trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Report name cannot be empty.");
                return;
            }

            String fileName = ReportManager.makeSafeFileName(name) + ".txt";

            // avoid duplicates
            java.util.List<String> existing = ReportManager.listReportFiles();
            if (existing.contains(fileName)) {
                JOptionPane.showMessageDialog(frame, "A report with that name already exists.");
                return;
            }

            ReportManager.writeReport(fileName, "");
            refreshReportList();
            reportList.setSelectedValue(fileName, true);
            reportTextArea.requestFocus();
            reportIncomplete = false;
        });

        // Save report
        saveBtn.addActionListener(e -> {
            if (currentReportName == null) {
                JOptionPane.showMessageDialog(frame, "Select a report first (or create a new one).");
                return;
            }
            boolean ok = ReportManager.writeReport(currentReportName, reportTextArea.getText());
            if (!ok) {
                JOptionPane.showMessageDialog(frame, "Failed to save report.");
                return;
            }
            reportIncomplete = false;
            JOptionPane.showMessageDialog(frame, "Report saved.");
        });

        // Delete report
        deleteBtn.addActionListener(e -> {
            if (currentReportName == null) {
                JOptionPane.showMessageDialog(frame, "Select a report to delete.");
                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                    frame,
                    "Delete this report permanently?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );
            if (choice != JOptionPane.YES_OPTION) return;

            boolean ok = ReportManager.deleteReport(currentReportName);
            if (!ok) {
                JOptionPane.showMessageDialog(frame, "Failed to delete report.");
                return;
            }

            currentReportName = null;
            reportTextArea.setText("");
            reportIncomplete = false;
            refreshReportList();
        });

        // Rename report
        renameBtn.addActionListener(e -> {
            if (currentReportName == null) {
                JOptionPane.showMessageDialog(frame, "Select a report to rename.");
                return;
            }

            String newName = JOptionPane.showInputDialog(frame, "Enter new report name:");
            if (newName == null) return;
            newName = newName.trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Name cannot be empty.");
                return;
            }

            String newFileName = ReportManager.makeSafeFileName(newName) + ".txt";

            java.util.List<String> existing = ReportManager.listReportFiles();
            if (existing.contains(newFileName)) {
                JOptionPane.showMessageDialog(frame, "A report with that name already exists.");
                return;
            }

            boolean ok = ReportManager.renameReport(currentReportName, newFileName);
            if (!ok) {
                JOptionPane.showMessageDialog(frame, "Failed to rename report.");
                return;
            }

            currentReportName = newFileName;
            refreshReportList();
            reportList.setSelectedValue(newFileName, true);
        });

        return panel;
    }

    private void refreshReportList() {
        reportListModel.clear();
        for (String name : ReportManager.listReportFiles()) {
            reportListModel.addElement(name);
        }
    }
    // ---------------------------------- END REPORT CLASS ----------------------------------------------------------------------

    // ---------------------------------AWARD PANEL -----------------------------------------------------------------------------
    private JPanel createAwardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Award Delegation"));

        JTextArea output = new JTextArea(14, 45);
        output.setEditable(false);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadBtn = new JButton("Load Evaluations & Pick Winners");
        JButton saveBtn = new JButton("Save Winners");
        top.add(loadBtn);
        top.add(saveBtn);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JLabel("People's Choice (manual):"));
        JComboBox<String> choiceBox = new JComboBox<>();
        JButton setChoiceBtn = new JButton("Set People's Choice");
        bottom.add(choiceBox);
        bottom.add(setChoiceBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(output), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        final AwardRecord[] bestOral = new AwardRecord[1];
        final AwardRecord[] bestPoster = new AwardRecord[1];
        final AwardRecord[] peopleChoice = new AwardRecord[1];

        loadBtn.addActionListener(e -> {
            bestOral[0] = null;
            bestPoster[0] = null;
            peopleChoice[0] = null;

            java.util.Map<String, String> idToUser = loadIdToUsernameMap(LoginSignupUI.FILE_NAME);

            // fill dropdown from evaluations
            choiceBox.removeAllItems();
            for (String sid : AwardRecord.loadEvaluatedStudentIds()) {
                String uname = idToUser.getOrDefault(sid, "unknown");
                choiceBox.addItem(uname + " (" + sid + ")");
            }

            bestOral[0] = AwardRecord.pickBestForType("Oral");
            bestPoster[0] = AwardRecord.pickBestForType("Poster");

            // show
            output.setText("");
            output.append("Best Oral Presentation:\n" + (bestOral[0] == null ? "None\n" : bestOral[0].toDisplay()) + "\n\n");
            output.append("Best Poster:\n" + (bestPoster[0] == null ? "None\n" : bestPoster[0].toDisplay()) + "\n\n");
            output.append("People's Choice:\nNot set (manual)\n");
        });

        setChoiceBtn.addActionListener(e -> {
            int idx = choiceBox.getSelectedIndex();
            if (idx == -1) {
                JOptionPane.showMessageDialog(frame, "Select a student first.");
                return;
            }

            // Extract ID from "username (STUxxx)"
            String item = (String) choiceBox.getSelectedItem();
            String sid = extractIdFromCombo(item);

            java.util.Map<String, String> idToUser = loadIdToUsernameMap(LoginSignupUI.FILE_NAME);
            String uname = idToUser.getOrDefault(sid, "unknown");

            AwardRecord pc = new AwardRecord("People's Choice Award", "-", sid, uname, 0);
            peopleChoice[0] = pc;
            output.append("\nPeople's Choice set to:\n" + pc.toDisplay() + "\n");
        });

        saveBtn.addActionListener(e -> {
            if (bestOral[0] == null && bestPoster[0] == null) {
                JOptionPane.showMessageDialog(frame, "Load evaluations first.");
                return;
            }
            if (peopleChoice[0] == null) {
                JOptionPane.showMessageDialog(frame, "Set People's Choice manually first.");
                return;
            }

            boolean ok = AwardRecord.saveWinners("awards.txt", bestOral[0], bestPoster[0], peopleChoice[0]);
            JOptionPane.showMessageDialog(frame, ok ? "Saved to awards.txt" : "Failed to save.");
        });

        return panel;
    }

    private String extractIdFromCombo(String item) {
        // expects "username (STUXXX)"
        int l = item.lastIndexOf('(');
        int r = item.lastIndexOf(')');
        if (l == -1 || r == -1 || r <= l) return item;
        return item.substring(l + 1, r).trim();
    }

    private java.util.Map<String, String> loadIdToUsernameMap(String usersFileName) {
        java.util.Map<String, String> map = new java.util.HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(usersFileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length < 4) continue;

                String username = data[0].trim();
                String id = data[3].trim();

                if (!id.isEmpty()) map.put(id, username);
            }
        } catch (IOException ignored) {}

        return map;
    }
}
