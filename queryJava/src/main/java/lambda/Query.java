package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import java.sql.*;
import java.util.*;
import saaf.Inspector;
import saaf.Response;

public class Query implements RequestHandler<Request, HashMap<String, Object>> {

	private static final String DB_URL = "proxy-1701925589163-database-1.proxy-c3tca0a5cslt.us-east-2.rds.amazonaws.com";
	private static final String DB_USER = "admin";
	private static final String DB_PASSWORD = "testpassword";

	/**
	* Lambda Function Handler
	*
	* @param request Request POJO with defined variables from Request.java
	* @param context
	* @return HashMap that Lambda will automatically convert into JSON.
	*/
	public HashMap<String, Object> handleRequest(Request request, Context context) {
		// Collect initial data.
		Inspector inspector = new Inspector();
		//inspector.inspectAll();
        	LambdaLogger logger = context.getLogger();
		//****************START FUNCTION IMPLEMENTATION*************************
		try {
                    // Initialize the connection in the constructor
                    //Class.forName("com.mysql.jdbc.Driver");  
                    //this.connect = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    Connection connect = getRemoteConnection(logger);
                    logger.log("Checking for constructor\n");
             
                    Query service = new Query();
                    String[] aggregates = request.getAggregates().split(",");
                    String[] filters = request.getFilters().split(",");

                    List<String> columnsToAggregate = Arrays.asList(aggregates);
                    
                    int columns = columnsToAggregate.size();
                    
                    List<String> columnsToFilters = Arrays.asList(filters);
                    
                    logger.log("Creating ResultSet\n");
                    
                    try (Statement statement = connect.createStatement()) {
			// SELECT clause
			String SELECT = SELECTCLAUSE(columnsToAggregate);
			// WHERE clause
			String WHERE = WHERECLAUSE(columnsToFilters);

			String query = String.format("SELECT %s FROM %s %s", SELECT, "Records", WHERE);
                        logger.log("Query: " + query + "\n");
			// Return the result set
			ResultSet results = statement.executeQuery(query);
                        
                        //ResultSet results = service.gatheringData(connect, columnsToAggregate, columnsToFilters, logger);
                        logger.log("ResultSet created\n");

                        logger.log(results.toString()+"\n");

                        Response response = new Response();

                        logger.log("Filling response\n");
                        while (results.next()) {
                            String value = "";
                            for (int i = 1; i <= columns; i++) {
                                value += (results.getString(i) + ",");
                            }
                            response.setValue(value);
                        }
                        inspector.consumeResponse(response);
                        //CLOSE THE CONNECTION
                        closeConnect(connect, logger);
                        } catch (SQLException e) {
                            // Display exception message
                            logger.log(e.toString());
                        }
                    
                } catch (Exception e) {
                    // Display exception message and stack trace
                    logger.log(e.toString());
		}

		// Collect final information such as total runtime and CPU deltas.
		//inspector.inspectAllDeltas();
		return inspector.finish();
	}

        
        private static Connection getRemoteConnection(LambdaLogger logger) {
        
          try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String dbName = "ProjectDB";
            String userName = "admin";
            String password = "testpassword";
            String hostname = "database-tcss462.c3tca0a5cslt.us-east-2.rds.amazonaws.com";
            String port = "3306";
            String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
            logger.log("Getting remote connection with connection string from environment variables.\n");
            Connection con = DriverManager.getConnection(jdbcUrl);
            logger.log("Remote connection successful.\n");
            return con;
        }
        catch (ClassNotFoundException e) { logger.log(e.toString());}
        catch (SQLException e) { logger.log(e.toString());}
        
        return null;
      }
        
	public void closeConnect(Connection connect, LambdaLogger logger) {
		try {
			// Close the connection if it is not already closed
			if (connect != null && !connect.isClosed()) {
				logger.log("Checking for closed connection\n");
				connect.close();
				logger.log("Connection closed.\n");
			} else {
				logger.log("Already closed.\n");
			}
		} catch (SQLException e) {
			// Display exception message and stack trace
			logger.log("Uh oh. An error occurred when processing. Please try again.\n");
		}
	}

	private String SELECTCLAUSE(List<String> columnsToAggregate) {
		// Join the columns with commas for the SELECT clause
		return String.join(", ", columnsToAggregate);
	}

	private String WHERECLAUSE(List<String> filters) {
		// Check if there are filters and join them with " AND "
		return filters.isEmpty() ? "" : "WHERE " + String.join(" AND ", filters);
	}


}
