
import javax.swing.*;

public class StudentManagementSystem {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system Look and Feel: " + e.getMessage());
        }

        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(null, 
                "Could not connect to database.\n" +
                "Please ensure MySQL is running and the database is properly configured.\n" +
                "Check DatabaseConnection.java for connection settings.",
                "Database Connection Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error starting application: " + e.getMessage(),
                    "Application Error", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
