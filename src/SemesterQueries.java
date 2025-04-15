import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List; // Use List interface

/**
 * Contains static methods for querying the course scheduler database.
 * Handles interactions with Semester, Course, Class, Student, and Schedule tables.
 *
 * @author acv (Original)
 * @author Gemini (Refactored)
 */
public class SemesterQueries {

    public static void addSemester(String name) throws SQLException {
        String sql = "INSERT INTO app.semester (semester) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding semester: " + e.getMessage());
            throw e;
        }
    }

    public static ArrayList<String> getSemesterList() throws SQLException {
        ArrayList<String> semesters = new ArrayList<>();
        String sql = "SELECT semester FROM app.semester ORDER BY semester";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    semesters.add(rs.getString("semester"));
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error getting semester list: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.err.println("Error during rollback: " + rbEx.getMessage());
                }
            }
            throw e;
        }
        return semesters;
    }

    public static void addCourse(String courseCode, String description) throws SQLException {
        String sql = "INSERT INTO app.courses (coursecode, description) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseCode);
            pstmt.setString(2, description);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
            throw e;
        }
    }

    public static ArrayList<String> getAllCourseCodes() throws SQLException {
        ArrayList<String> courseList = new ArrayList<>();
        String sql = "SELECT coursecode FROM app.courses ORDER BY coursecode";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courseList.add(rs.getString("coursecode"));
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error getting all course codes: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.err.println("Error during rollback: " + rbEx.getMessage());
                }
            }
            throw e;
        }
        return courseList;
    }

    public static String getCourseDescription(String courseCode) throws SQLException {
        String desc = "";
        String sql = "SELECT description FROM app.courses WHERE coursecode = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, courseCode);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        desc = rs.getString("description");
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error getting course description: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.err.println("Error during rollback: " + rbEx.getMessage());
                }
            }
            throw e;
        }
        return desc;
    }

    public static void addClass(String semester, String courseCode, int seats) throws SQLException {
        String sql = "INSERT INTO app.classes (semester, coursecode, seats) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, semester);
            pstmt.setString(2, courseCode);
            pstmt.setInt(3, seats);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding class: " + e.getMessage());
            throw e;
        }
    }

    public static ArrayList<String> getAllCourseCodesBySemester(String semester) throws SQLException {
        ArrayList<String> classList = new ArrayList<>();
        String sql = "SELECT coursecode FROM app.classes WHERE semester = ? ORDER BY coursecode";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, semester);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        classList.add(rs.getString("coursecode"));
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error getting course codes by semester: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.err.println("Error during rollback: " + rbEx.getMessage());
                }
            }
            throw e;
        }
        return classList;
    }

    public static int getClassSeats(String semester, String courseCode) throws SQLException {
        int seats = 0;
        String sql = "SELECT seats FROM app.classes WHERE semester = ? AND coursecode = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, semester);
                pstmt.setString(2, courseCode);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        seats = rs.getInt("seats");
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error getting class seats: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.err.println("Error during rollback: " + rbEx.getMessage());
                }
            }
            throw e;
        }
        return seats;
    }

    public static int getScheduledStudentCount(String semester, String courseCode) throws SQLException {
        int count = 0;
        String sql = "SELECT COUNT(*) AS total FROM app.schedule WHERE semester = ? AND coursecode = ? AND status='scheduled'";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, semester);
                pstmt.setString(2, courseCode);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt("total");
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error getting scheduled student count: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.err.println("Error during rollback: " + rbEx.getMessage());
                }
            }
            throw e;
        }
        return count;
    }

    public static void addStudent(String studentID, String firstName, String lastName) throws SQLException {
        String sql = "INSERT INTO app.students (studentid, firstname, lastname) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentID);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
            throw e;
        }
    }

    public static ArrayList<String> getAllStudents() throws SQLException {
        ArrayList<String> students = new ArrayList<>();
        String sql = "SELECT studentid, firstname, lastname FROM app.students ORDER BY lastname, firstname";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String fullName = String.format("%s, %s (%s)",
                            rs.getString("lastname"),
                            rs.getString("firstname"),
                            rs.getString("studentid"));
                    students.add(fullName);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error getting all students: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.err.println("Error during rollback: " + rbEx.getMessage());
                }
            }
            throw e;
        }
        return students;
    }

    public static String getStudentIDFromComboBox(String comboBoxString) {
        if (comboBoxString == null || !comboBoxString.contains("(") || !comboBoxString.contains(")")) {
            return null;
        }
        try {
            int startIndex = comboBoxString.lastIndexOf('(');
            int endIndex = comboBoxString.lastIndexOf(')');
            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                return comboBoxString.substring(startIndex + 1, endIndex);
            }
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Error parsing student ID from string: " + comboBoxString);
        }
        return null;
    }

    public static String scheduleClass(String semester, String studentid, String courseCode) throws SQLException {
        String status;
        String sqlInsert = "INSERT INTO app.schedule (semester, studentid, coursecode, status, timestamp) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            int scheduledCount = getScheduledStudentCount(semester, courseCode);
            int seats = getClassSeats(semester, courseCode);

            if (scheduledCount < seats) {
                status = "scheduled";
            } else {
                status = "waitlisted";
            }

            try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                pstmtInsert.setString(1, semester);
                pstmtInsert.setString(2, studentid);
                pstmtInsert.setString(3, courseCode);
                pstmtInsert.setString(4, status);
                pstmtInsert.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                pstmtInsert.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error scheduling class: " + e.getMessage());
            throw e;
        }
        return status;
    }

    public static List<List<Object>> getClassesBySemester(String semester) throws SQLException {
        List<List<Object>> data = new ArrayList<>();
        String sql = "SELECT c.coursecode, cr.description, c.seats " +
                     "FROM app.classes c JOIN app.courses cr ON c.coursecode = cr.coursecode " +
                     "WHERE c.semester = ? ORDER BY c.coursecode";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, semester);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        List<Object> row = new ArrayList<>();
                        row.add(rs.getString("coursecode"));
                        row.add(rs.getString("description"));
                        row.add(rs.getInt("seats"));
                        data.add(row);
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error getting classes by semester: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.err.println("Error during rollback: " + rbEx.getMessage());
                }
            }
            throw e;
        }
        return data;
    }

    public static List<List<Object>> getScheduleByStudent(String semester, String studentid) throws SQLException {
        List<List<Object>> data = new ArrayList<>();
        String sql = "SELECT coursecode, status FROM app.schedule " +
                     "WHERE semester = ? AND studentid = ? ORDER BY timestamp";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, semester);
                pstmt.setString(2, studentid);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        List<Object> row = new ArrayList<>();
                        row.add(rs.getString("coursecode"));
                        row.add(rs.getString("status"));
                        data.add(row);
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error getting schedule by student: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.err.println("Error during rollback: " + rbEx.getMessage());
                }
            }
            throw e;
        }
        return data;
    }

    public static List<List<Object>> getStudentsInClass(String semester, String courseCode) throws SQLException {
        List<List<Object>> data = new ArrayList<>();
        String sql = "SELECT s.lastname, s.firstname, sc.status " +
                     "FROM app.schedule sc " +
                     "JOIN app.students s ON sc.studentid = s.studentid " +
                     "WHERE sc.semester = ? AND sc.coursecode = ? " +
                     "ORDER BY sc.status, sc.timestamp";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, semester);
                pstmt.setString(2, courseCode);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        List<Object> row = new ArrayList<>();
                        row.add(String.format("%s, %s", rs.getString("lastname"), rs.getString("firstname")));
                        row.add(rs.getString("status"));
                        data.add(row);
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error getting students in class: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.err.println("Error during rollback: " + rbEx.getMessage());
                }
            }
            throw e;
        }
        return data;
    }

    public static ArrayList<String> dropClass(String semester, String courseCode) throws SQLException {
        ArrayList<String> droppedStudents = new ArrayList<>();
        String sqlGetStudents = "SELECT s.lastname, s.firstname, sc.status " +
                                "FROM app.schedule sc JOIN app.students s ON sc.studentid = s.studentid " +
                                "WHERE sc.semester = ? AND sc.coursecode = ?";
        String sqlDeleteSchedule = "DELETE FROM app.schedule WHERE semester = ? AND coursecode = ?";
        String sqlDeleteClass = "DELETE FROM app.classes WHERE semester = ? AND coursecode = ?";

        Connection conn = null;
        boolean originalAutoCommit = true;

        try {
            conn = DBConnection.getConnection();
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtGet = conn.prepareStatement(sqlGetStudents)) {
                pstmtGet.setString(1, semester);
                pstmtGet.setString(2, courseCode);
                try (ResultSet rs = pstmtGet.executeQuery()) {
                    while (rs.next()) {
                        droppedStudents.add(String.format("%s, %s (%s)",
                                rs.getString("lastname"),
                                rs.getString("firstname"),
                                rs.getString("status")));
                    }
                }
            }

            try (PreparedStatement pstmtDelSched = conn.prepareStatement(sqlDeleteSchedule)) {
                pstmtDelSched.setString(1, semester);
                pstmtDelSched.setString(2, courseCode);
                pstmtDelSched.executeUpdate();
            }

            try (PreparedStatement pstmtDelClass = conn.prepareStatement(sqlDeleteClass)) {
                pstmtDelClass.setString(1, semester);
                pstmtDelClass.setString(2, courseCode);
                pstmtDelClass.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            System.err.println("Error dropping class: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(originalAutoCommit);
                }
            } catch (SQLException finalEx) {
                System.err.println("Error resetting auto-commit: " + finalEx.getMessage());
            }
        }
        return droppedStudents;
    }

    public static void dropStudent(String studentId) throws SQLException {
        String sqlGetScheduledClasses = "SELECT semester, coursecode FROM app.schedule WHERE studentid = ? AND status = 'scheduled'";
        String sqlDeleteSchedule = "DELETE FROM app.schedule WHERE studentid = ?";
        String sqlDeleteStudent = "DELETE FROM app.students WHERE studentid = ?";

        List<String[]> scheduledClasses = new ArrayList<>();
        Connection conn = null;
        boolean originalAutoCommit = true;

        try {
            conn = DBConnection.getConnection();
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtGetSched = conn.prepareStatement(sqlGetScheduledClasses)) {
                pstmtGetSched.setString(1, studentId);
                try (ResultSet rs = pstmtGetSched.executeQuery()) {
                    while (rs.next()) {
                        scheduledClasses.add(new String[]{rs.getString("semester"), rs.getString("coursecode")});
                    }
                }
            }

            try (PreparedStatement pstmtDelSched = conn.prepareStatement(sqlDeleteSchedule)) {
                pstmtDelSched.setString(1, studentId);
                pstmtDelSched.executeUpdate();
            }

            try (PreparedStatement pstmtDelStud = conn.prepareStatement(sqlDeleteStudent)) {
                pstmtDelStud.setString(1, studentId);
                pstmtDelStud.executeUpdate();
            }

            for (String[] classInfo : scheduledClasses) {
                promoteWaitlistedStudent(conn, classInfo[0], classInfo[1]);
            }

            conn.commit();

        } catch (SQLException e) {
            System.err.println("Error dropping student: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(originalAutoCommit);
                }
            } catch (SQLException finalEx) {
                System.err.println("Error resetting auto-commit: " + finalEx.getMessage());
            }
        }
    }

    public static void studentDropClass(String semester, String studentId, String courseCode) throws SQLException {
        String sqlCheckStatus = "SELECT status FROM app.schedule WHERE semester = ? AND studentid = ? AND coursecode = ?";
        String sqlDropClass = "DELETE FROM app.schedule WHERE semester = ? AND studentid = ? AND coursecode = ?";
        String currentStatus = null;
        Connection conn = null;
        boolean originalAutoCommit = true;

        try {
            conn = DBConnection.getConnection();
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheckStatus)) {
                pstmtCheck.setString(1, semester);
                pstmtCheck.setString(2, studentId);
                pstmtCheck.setString(3, courseCode);
                try (ResultSet rs = pstmtCheck.executeQuery()) {
                    if (rs.next()) {
                        currentStatus = rs.getString("status");
                    } else {
                        throw new SQLException("Student not enrolled in this class");
                    }
                }
            }

            try (PreparedStatement pstmtDrop = conn.prepareStatement(sqlDropClass)) {
                pstmtDrop.setString(1, semester);
                pstmtDrop.setString(2, studentId);
                pstmtDrop.setString(3, courseCode);
                int rowsAffected = pstmtDrop.executeUpdate();

                if ("scheduled".equals(currentStatus) && rowsAffected > 0) {
                    promoteWaitlistedStudent(conn, semester, courseCode);
                }
            }

            conn.commit();

        } catch (SQLException e) {
            System.err.println("Error for student dropping class: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(originalAutoCommit);
                }
            } catch (SQLException finalEx) {
                System.err.println("Error resetting auto-commit: " + finalEx.getMessage());
            }
        }
    }

    private static void promoteWaitlistedStudent(Connection conn, String semester, String courseCode) throws SQLException {
        String sqlFindWaitlisted = "SELECT studentid FROM app.schedule " +
                                   "WHERE semester = ? AND coursecode = ? AND status = 'waitlisted' " +
                                   "ORDER BY timestamp";
        String sqlUpdateStatus = "UPDATE app.schedule SET status = 'scheduled' WHERE semester = ? AND coursecode = ? AND studentid = ?";
        String studentToPromote = null;

        try (PreparedStatement pstmtFind = conn.prepareStatement(sqlFindWaitlisted)) {
            pstmtFind.setString(1, semester);
            pstmtFind.setString(2, courseCode);
            try (ResultSet rs = pstmtFind.executeQuery()) {
                if (rs.next()) {
                    studentToPromote = rs.getString("studentid");
                }
            }
        }

        if (studentToPromote != null) {
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateStatus)) {
                pstmtUpdate.setString(1, semester);
                pstmtUpdate.setString(2, courseCode);
                pstmtUpdate.setString(3, studentToPromote);
                pstmtUpdate.executeUpdate();
            }
        }
    }
}
