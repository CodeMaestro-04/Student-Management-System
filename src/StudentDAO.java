import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class StudentDAO {

    private Connection connection;

    public StudentDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (student_number, first_name, last_name, email, " +
                "phone_number, address, date_of_birth, gender, course, year_level, " +
                "photo, photo_name, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentNumber());
            pstmt.setString(2, student.getFirstName());
            pstmt.setString(3, student.getLastName());
            pstmt.setString(4, student.getEmail());
            pstmt.setString(5, student.getPhoneNumber());
            pstmt.setString(6, student.getAddress());
            pstmt.setDate(7, student.getDateOfBirth() != null ?
                    new Date(student.getDateOfBirth().getTime()) : null);
            pstmt.setString(8, student.getGender());
            pstmt.setString(9, student.getCourse());
            pstmt.setInt(10, student.getYearLevel());

            if (student.getPhoto() != null) {
                pstmt.setBytes(11, student.getPhoto());
                pstmt.setString(12, student.getPhotoName());
            } else {
                pstmt.setNull(11, Types.LONGVARBINARY);
                pstmt.setNull(12, Types.VARCHAR);
            }

            pstmt.setString(13, student.getStatus());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error adding student: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET student_number=?, first_name=?, last_name=?, email=?, " +
                "phone_number=?, address=?, date_of_birth=?, gender=?, course=?, year_level=?, " +
                "photo=?, photo_name=?, status=? WHERE student_id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentNumber());
            pstmt.setString(2, student.getFirstName());
            pstmt.setString(3, student.getLastName());
            pstmt.setString(4, student.getEmail());
            pstmt.setString(5, student.getPhoneNumber());
            pstmt.setString(6, student.getAddress());
            pstmt.setDate(7, student.getDateOfBirth() != null ?
                    new Date(student.getDateOfBirth().getTime()) : null);
            pstmt.setString(8, student.getGender());
            pstmt.setString(9, student.getCourse());
            pstmt.setInt(10, student.getYearLevel());

            if (student.getPhoto() != null) {
                pstmt.setBytes(11, student.getPhoto());
                pstmt.setString(12, student.getPhotoName());
            } else {
                pstmt.setNull(11, Types.LONGVARBINARY);
                pstmt.setNull(12, Types.VARCHAR);
            }

            pstmt.setString(13, student.getStatus());
            pstmt.setInt(14, student.getStudentId());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error updating student: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean deleteStudent(int studentId) {
        String sql = "DELETE FROM students WHERE student_id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error deleting student: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY last_name, first_name";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Student student = mapResultSetToStudent(rs);
                students.add(student);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving students: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error retrieving students: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return students;
    }

    public Student getStudentById(int studentId) {
        String sql = "SELECT * FROM students WHERE student_id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding student: " + e.getMessage());
        }

        return null;
    }

    public List<Student> searchStudents(String searchTerm) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE first_name LIKE ? OR last_name LIKE ? " +
                "OR student_number LIKE ? OR email LIKE ? ORDER BY last_name, first_name";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String pattern = "%" + searchTerm + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            pstmt.setString(4, pattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Student student = mapResultSetToStudent(rs);
                students.add(student);
            }

        } catch (SQLException e) {
            System.err.println("Error searching students: " + e.getMessage());
        }

        return students;
    }

    public boolean studentNumberExists(String studentNumber, int excludeId) {
        String sql = "SELECT COUNT(*) FROM students WHERE student_number=? AND student_id!=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentNumber);
            pstmt.setInt(2, excludeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking student number: " + e.getMessage());
        }

        return false;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setStudentNumber(rs.getString("student_number"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setEmail(rs.getString("email"));
        student.setPhoneNumber(rs.getString("phone_number"));
        student.setAddress(rs.getString("address"));
        student.setDateOfBirth(rs.getDate("date_of_birth"));
        student.setGender(rs.getString("gender"));
        student.setCourse(rs.getString("course"));
        student.setYearLevel(rs.getInt("year_level"));
        student.setEnrollmentDate(rs.getDate("enrollment_date"));
        student.setPhoto(rs.getBytes("photo"));
        student.setPhotoName(rs.getString("photo_name"));
        student.setStatus(rs.getString("status"));
        student.setCreatedAt(rs.getTimestamp("created_at"));
        student.setUpdatedAt(rs.getTimestamp("updated_at"));
        return student;
    }
}
