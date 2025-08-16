import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainFrame extends JFrame implements ActionListener {

    private JTable studentTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;

    private JTextField studentIdField;
    private JTextField studentNumberField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JTextField dobField;
    private JComboBox<String> genderCombo;
    private JTextField courseField;
    private JSpinner yearSpinner;
    private JComboBox<String> statusCombo;
    private JLabel photoLabel;
    private JButton photoButton;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton searchButton;
    private JButton printButton;
    private JTextField searchField;

    private StudentDAO studentDAO;
    private byte[] currentPhotoData;
    private String currentPhotoName;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public MainFrame() {
        studentDAO = new StudentDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupFrame();
        loadStudentData();
    }

    private void initializeComponents() {
        String[] columnNames = {"ID", "Student #", "First Name", "Last Name", "Email", "Phone", "Course", "Year", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        tableSorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(tableSorter);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        studentIdField = new JTextField();
        studentIdField.setEditable(false);
        studentNumberField = new JTextField();
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        addressArea = new JTextArea(3, 20);
        dobField = new JTextField();
        dobField.setToolTipText("Format: YYYY-MM-DD");

        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        courseField = new JTextField();
        yearSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 6, 1));
        statusCombo = new JComboBox<>(new String[]{"Active", "Inactive", "Graduated"});

        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(150, 150));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        photoLabel.setHorizontalAlignment(JLabel.CENTER);
        photoLabel.setText("No Photo");
        photoButton = new JButton("Upload Photo");

        addButton = new JButton("Add Student");
        updateButton = new JButton("Update Student");
        deleteButton = new JButton("Delete Student");
        clearButton = new JButton("Clear Form");
        searchButton = new JButton("Search");
        printButton = new JButton("Print Student Details");
        searchField = new JTextField(20);
        searchField.setToolTipText("Search by name, student number, or email");

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Student Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel contentPanel = new JPanel(new BorderLayout());
        JPanel formPanel = createFormPanel();
        JPanel tablePanel = createTablePanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, tablePanel);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);

        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(splitPane, BorderLayout.CENTER);

        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        fieldsPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(studentIdField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(new JLabel("Student Number:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(studentNumberField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(new JLabel("First Name:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(firstNameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(new JLabel("Last Name:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(lastNameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(new JLabel("Email:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(emailField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(phoneField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        fieldsPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setPreferredSize(new Dimension(200, 60));
        fieldsPanel.add(addressScroll, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(dobField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(genderCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(new JLabel("Course:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(courseField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(new JLabel("Year Level:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(yearSpinner, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(statusCombo, gbc);
        row++;

        JPanel photoPanel = new JPanel(new BorderLayout());
        photoPanel.setBorder(BorderFactory.createTitledBorder("Student Photo"));
        photoPanel.add(photoLabel, BorderLayout.CENTER);
        photoPanel.add(photoButton, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(printButton);

        panel.add(fieldsPanel, BorderLayout.NORTH);
        panel.add(photoPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void setupEventListeners() {
        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        clearButton.addActionListener(this);
        searchButton.addActionListener(this);
        photoButton.addActionListener(this);
        printButton.addActionListener(this);

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = studentTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadStudentToForm(selectedRow);
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                } else {
                    updateButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }
            }
        });

        studentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = studentTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        loadStudentToForm(selectedRow);
                    }
                }
            }
        });

        searchField.addActionListener(this);
    }

    private void setupFrame() {
        setTitle("Student Management System - Main Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        } catch (Exception e) {}
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) addStudent();
        else if (e.getSource() == updateButton) updateStudent();
        else if (e.getSource() == deleteButton) deleteStudent();
        else if (e.getSource() == clearButton) clearForm();
        else if (e.getSource() == searchButton || e.getSource() == searchField) searchStudents();
        else if (e.getSource() == photoButton) uploadPhoto();
        else if (e.getSource() == printButton) printStudentDetails();
    }

    private void addStudent() {
        if (!validateForm()) return;
        Student student = createStudentFromForm();
        if (studentDAO.studentNumberExists(student.getStudentNumber(), 0)) {
            JOptionPane.showMessageDialog(this, "Student number already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (studentDAO.addStudent(student)) {
            JOptionPane.showMessageDialog(this, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadStudentData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add student!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        if (!validateForm()) return;
        Student student = createStudentFromForm();
        int studentId = Integer.parseInt(studentIdField.getText());
        student.setStudentId(studentId);
        if (studentDAO.studentNumberExists(student.getStudentNumber(), studentId)) {
            JOptionPane.showMessageDialog(this, "Student number already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (studentDAO.updateStudent(student)) {
            JOptionPane.showMessageDialog(this, "Student updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadStudentData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update student!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int studentId = (Integer)tableModel.getValueAt(studentTable.convertRowIndexToModel(selectedRow), 0);
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            if (studentDAO.deleteStudent(studentId)) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadStudentData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete student!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchStudents() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadStudentData();
            return;
        }
        List<Student> students = studentDAO.searchStudents(searchTerm);
        populateTable(students);
    }

    private void uploadPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Student Photo");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif", "bmp"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                FileInputStream fis = new FileInputStream(selectedFile);
                currentPhotoData = new byte[(int)selectedFile.length()];
                fis.read(currentPhotoData);
                fis.close();
                currentPhotoName = selectedFile.getName();
                ImageIcon imageIcon = new ImageIcon(currentPhotoData);
                Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(image));
                photoLabel.setText("");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading image file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        if (studentNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student number is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            studentNumberField.requestFocus();
            return false;
        }
        if (firstNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            firstNameField.requestFocus();
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Last name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            lastNameField.requestFocus();
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return false;
        }
        if (courseField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            courseField.requestFocus();
            return false;
        }
        String email = emailField.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return false;
        }
        if (!dobField.getText().trim().isEmpty()) {
            try {
                dateFormat.parse(dobField.getText().trim());
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Please enter date in YYYY-MM-DD format!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                dobField.requestFocus();
                return false;
            }
        }
        return true;
    }

    private Student createStudentFromForm() {
        Student student = new Student();
        student.setStudentNumber(studentNumberField.getText().trim());
        student.setFirstName(firstNameField.getText().trim());
        student.setLastName(lastNameField.getText().trim());
        student.setEmail(emailField.getText().trim());
        student.setPhoneNumber(phoneField.getText().trim());
        student.setAddress(addressArea.getText().trim());
        if (!dobField.getText().trim().isEmpty()) {
            try {
                Date dob = dateFormat.parse(dobField.getText().trim());
                student.setDateOfBirth(dob);
            } catch (ParseException e) {}
        }
        student.setGender((String)genderCombo.getSelectedItem());
        student.setCourse(courseField.getText().trim());
        student.setYearLevel((Integer)yearSpinner.getValue());
        student.setStatus((String)statusCombo.getSelectedItem());
        student.setPhoto(currentPhotoData);
        student.setPhotoName(currentPhotoName);
        return student;
    }

    private void clearForm() {
        studentIdField.setText("");
        studentNumberField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressArea.setText("");
        dobField.setText("");
        genderCombo.setSelectedIndex(0);
        courseField.setText("");
        yearSpinner.setValue(1);
        statusCombo.setSelectedIndex(0);
        photoLabel.setIcon(null);
        photoLabel.setText("No Photo");
        currentPhotoData = null;
        currentPhotoName = null;
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        studentTable.clearSelection();
    }

    private void loadStudentData() {
        List<Student> students = studentDAO.getAllStudents();
        populateTable(students);
    }

    private void populateTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student student : students) {
            Object[] row = {
                    student.getStudentId(),
                    student.getStudentNumber(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getEmail(),
                    student.getPhoneNumber(),
                    student.getCourse(),
                    student.getYearLevel(),
                    student.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void loadStudentToForm(int selectedRow) {
        int modelRow = studentTable.convertRowIndexToModel(selectedRow);
        int studentId = (Integer)tableModel.getValueAt(modelRow, 0);
        Student student = studentDAO.getStudentById(studentId);
        if (student != null) {
            studentIdField.setText(String.valueOf(student.getStudentId()));
            studentNumberField.setText(student.getStudentNumber());
            firstNameField.setText(student.getFirstName());
            lastNameField.setText(student.getLastName());
            emailField.setText(student.getEmail());
            phoneField.setText(student.getPhoneNumber() != null ? student.getPhoneNumber() : "");
            addressArea.setText(student.getAddress() != null ? student.getAddress() : "");
            if (student.getDateOfBirth() != null) {
                dobField.setText(dateFormat.format(student.getDateOfBirth()));
            } else {
                dobField.setText("");
            }
            if (student.getGender() != null) {
                genderCombo.setSelectedItem(student.getGender());
            }
            courseField.setText(student.getCourse());
            yearSpinner.setValue(student.getYearLevel());
            statusCombo.setSelectedItem(student.getStatus());
            if (student.getPhoto() != null) {
                currentPhotoData = student.getPhoto();
                currentPhotoName = student.getPhotoName();
                ImageIcon imageIcon = new ImageIcon(currentPhotoData);
                Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(image));
                photoLabel.setText("");
            } else {
                photoLabel.setIcon(null);
                photoLabel.setText("No Photo");
                currentPhotoData = null;
                currentPhotoName = null;
            }
        }
    }

    private void printStudentDetails() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to print details.",
                    "No Selection", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int modelRow = studentTable.convertRowIndexToModel(selectedRow);
        int studentId = (Integer)tableModel.getValueAt(modelRow, 0);
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Student details not found.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        StringBuilder printContent = new StringBuilder();
        printContent.append("Student Details\n");
        printContent.append("---------------------\n");
        printContent.append("Student Number: ").append(student.getStudentNumber()).append("\n");
        printContent.append("Name: ").append(student.getFullName()).append("\n");
        printContent.append("Email: ").append(student.getEmail()).append("\n");
        printContent.append("Phone: ").append(student.getPhoneNumber()).append("\n");
        printContent.append("Course: ").append(student.getCourse()).append("\n");
        printContent.append("Year Level: ").append(student.getYearLevel()).append("\n");
        printContent.append("Status: ").append(student.getStatus()).append("\n");
        JTextArea textArea = new JTextArea(printContent.toString());
        try {
            boolean done = textArea.print();
            if (done) {
                JOptionPane.showMessageDialog(this, "Printing completed", "Print Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Printing canceled", "Print Result", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Printing failed: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
