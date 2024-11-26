import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Scanner;
import java.util.Vector;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import java.sql.JDBCType;


public class DbApplication {
	

	private Connection conn;
	

	public DbApplication() {
		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("Driver Found!");
		} catch (ClassNotFoundException e) {
			System.out.println("Driver not found!");
		}

	}
	
	public void dBappConnect(String ip, String dbappName, String username, String password) {
			try {
				conn = DriverManager.getConnection("jdbc:postgresql://" + ip + "/" + dbappName, username, password);
				System.out.println("The connection was established!");
				conn.setAutoCommit(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	public void dBappClose() {
		try {
			conn.close();
			System.out.println("Connection closed!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void commit() {
		try {
			conn.commit();
			System.out.println("Commit made!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void abort() {
		try {
			conn.rollback();
			System.out.println("Abort!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Vector<String> displayGradeOfStudentInClass(String am, String course_code) {
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT \"Student\".am, \"Register\".course_code, \"Register\".final_grade \r\n" +
					"FROM \"Student\" \r\n" +
					"INNER JOIN \"Register\" ON \"Register\".amka = \"Student\".amka \r\n" +
					"INNER JOIN \"Course\" ON \"Course\".course_code = \"Register\".course_code \r\n" +
					"WHERE am = '" + am + "' and \"Register\".course_code = '" + course_code + "' ;");

			        ResultSet result = stmt.executeQuery(); 
			        
			       
			        Vector<String> results =  new Vector<String>();
			        
			while (result.next()) {
				System.out.println("am= "+result.getString(1)+ " course_code= "+result.getString(2)+" final_grade= "+ result.getString(3));
				
			}
			
			result.close();
			return results;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public void update_grades_of_Student(double lab_grade, double exam_grade,String am, String course_code,int serial_number) {
		
		
		
		try {
			PreparedStatement stmt = conn.prepareStatement("update \"Register\"  \r\n" + 
					"set lab_grade = "+lab_grade+", exam_grade = "+exam_grade+", final_grade = 0.5*"+exam_grade+" + 0.5*"+lab_grade+"\r\n" + 
					"where amka in (\r\n"
					+ "SELECT \"Student\".amka\r\n"
					+ "FROM \"Register\"\r\n"
					+ "JOIN \"Student\" ON \"Register\".amka = \"Student\".amka\r\n"
					+ "WHERE \"Student\".am = '" + am +  "')\r\n"
					+ "  AND \"Register\".course_code ='" + course_code + "'\r\n"
					+ "  AND \"Register\".serial_number =" + serial_number + ";");
			
			stmt.executeUpdate(); 
			stmt.close();			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
	}


	
	public Vector<String> displayGradesOfStudent(int semesterrunsin,String am) {
		try {
			PreparedStatement stmt = conn.prepareStatement("select \"Student\".am,\"Register\".course_code,\"Course\".course_title,\"Register\".lab_grade,\"Register\".exam_grade,\"Register\".final_grade, \"CourseRun\".semesterrunsin \r\n" +
					"from \"Student\" \r\n" +
					"inner join \"Register\" \r\n" +
			  		"ON \"Register\".amka = \"Student\".amka \r\n" +
			  		"natural join \"Course\" \r\n" +
			  		"natural join \"CourseRun\" \r\n" +
			  		"inner join \"Semester\" \r\n" +
			  		"on \"Semester\".semester_id = \"CourseRun\".semesterrunsin \r\n" +
			  		"WHERE \"Student\".am = '" + am +"' and \"CourseRun\".semesterrunsin = " + semesterrunsin + "\r\n" +
					"order by semesterrunsin;");

			        ResultSet result = stmt.executeQuery(); 
			        
			       
			        Vector<String> results =  new Vector<String>();
			        
			while (result.next()) {
				System.out.println("am= "+result.getLong(1)+ " course code= "+result.getString(2)+" course title= "+ result.getString(3) + "\nlab grade= " + result.getDouble(4)+
				" exam grade= " + result.getDouble(5)+" final grade= " +result.getDouble(6) + " semester= " + result.getInt(7));
				results.add(String.valueOf(result.getLong(1)));
				results.add(result.getString(2));
				
			}
			
			result.close();
			return results;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	private void searchPersons(String initials, int resultsPerPage) throws SQLException {
	    String query = "SELECT * FROM \"Person\" WHERE surname LIKE ? ORDER BY surname";

	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, initials + "%");

	        ResultSet rs = stmt.executeQuery();

	        List<String> results = new ArrayList<>(); // Store the results in a data structure

	        while (rs.next()) {
	            // Assuming "surname" is a column in the "Person" table
	            String surname = rs.getString("surname");
	            results.add(surname);
	        }

	        int totalResults = results.size();
	        int totalPages = (int) Math.ceil((double) totalResults / resultsPerPage);

	        System.out.println("Total pages: " + totalPages);

	        int currentPage = 1;
	        while (true) {
	            System.out.println("Page " + currentPage);
	            System.out.println("Enter 'n' to go to the next page or enter the page number you want to view:");

	            Scanner scanner = new Scanner(System.in);
	            String input = scanner.nextLine();

	            if (input.equalsIgnoreCase("n")) {
	                if (currentPage < totalPages) {
	                    currentPage++;
	                } else {
	                    System.out.println("You have reached the last page.");
	                }
	            } else {
	                try {
	                    int pageNumber = Integer.parseInt(input);
	                    if (pageNumber > 0 && pageNumber <= totalPages) {
	                        currentPage = pageNumber;
	                    } else {
	                        System.out.println("Invalid page number. Please try again.");
	                    }
	                } catch (NumberFormatException e) {
	                    System.out.println("Invalid input. Please try again.");
	                }
	            }

	            int startIndex = (currentPage - 1) * resultsPerPage;
	            int endIndex = Math.min(startIndex + resultsPerPage, totalResults);

	            // Display the results for the current page
	            for (int i = startIndex; i < endIndex; i++) {
	                String surname = results.get(i);
	                System.out.println(surname);
	            }

	            // Break the loop if the user doesn't want to view more pages
	            if (currentPage >= totalPages) {
	                break;
	            }
	        }
	    }
	}
	
		
	public static void main(String[] args) throws SQLException {
		
		DbApplication dbapp = new DbApplication();
		Scanner reader = new Scanner(System.in);
		int action = 0;
		do {
		System.out.println("1. Display the grade of a student in a specific course");
		System.out.println("2. Update student grades");
		System.out.println("3. Search person base on given initials");
		System.out.println("4. Display students that are enrolled in a specific course");
		System.out.println("5. Display students grades for a given semester");
		System.out.println("6. Cancel connection");
		System.out.println("Enter your action: ");
		
		
		
        action = reader.nextInt();
        reader.nextLine();
	
        Vector<String> grades_info = new Vector<String>();
        int academic_year = 0;
        String academic_season = "";
        int amka = 0;
        String course_code = "";
        int id = -1;
		String update_AM = "";
		String initials = "";
		int resultsPerPage;
		
		String ip = "127.0.0.1:5432";
		String dbappName="test";
		String username="postgres";
		String password = "12345678";
		dbapp.dBappConnect(ip, dbappName, username, password);
        
		switch (action) {
		case 1:
			System.out.println("Enter AM:");
			String am = reader.nextLine();
			System.out.println("Enter course_code:");
			String search_course_code = reader.nextLine();
			dbapp.displayGradeOfStudentInClass(am, search_course_code);	
			break;
		case 2:
			System.out.println("Enter the lab grade:");
			double lab_grade = Double.parseDouble(reader.nextLine());
			System.out.println("Enter the exam grade:");
			double exam_grade = Double.parseDouble(reader.nextLine());
			System.out.println("Enter the AM:");
			update_AM = reader.nextLine();
			System.out.println("Enter the course code:");
			course_code=reader.nextLine();
			System.out.println("Enter the serial number:");
			int serial_number = Integer.parseInt(reader.nextLine());					
			dbapp.update_grades_of_Student(lab_grade,exam_grade,update_AM,course_code,serial_number);
			dbapp.commit();
			break;
		case 3:
			System.out.println("Enter the initials of the surname:");
			initials=reader.nextLine();
			System.out.println("Enter how many results you want per page:");
			resultsPerPage = Integer.parseInt(reader.nextLine());
			dbapp.searchPersons(initials,resultsPerPage);
			break;
		case 4:
			System.out.println("Enter the AM:");
			update_AM = reader.nextLine();
			System.out.println("Select the semester you want to display the grades of:");
			int semester = Integer.parseInt(reader.nextLine());
			dbapp.displayGradesOfStudent(semester,update_AM);
			break;	
		case 5:
			dbapp.dBappClose();
			break;				
		}

		} while (action != 6);
		reader.close();
	}


}
