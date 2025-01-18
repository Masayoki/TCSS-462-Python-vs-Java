package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import saaf.Inspector;
import saaf.Response;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import java.sql.ResultSet;
import java.sql.Statement;


public class LoadDataService implements RequestHandler<Request, HashMap<String, Object>> {

    /**
     * Lambda Function Handler
     *
     * @param request Hashmap containing request JSON attributes.
     * @param context
     * @return HashMap that Lambda will automatically convert into JSON.
     */
    
    public HashMap<String, Object> handleRequest(Request request, Context context) {
        Inspector inspector = new Inspector();
        LambdaLogger logger = context.getLogger();
        try {
            // Set specific values for bucketname and filename
            //request.setBucketname("project.bucket.462562f23.md");
            //request.setFilename("mod_5000_Sales_Records.csv");
            
            String bucketname = request.getBucketname();
            String filename = request.getFilename();
            
            // Create new file on S3
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
            //get object file using source bucket and srcKey name
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketname, filename));
            
            //get content of the file
            InputStream objectData = s3Object.getObjectContent();
            //scanning data line by line

            logger.log("CSV recieved\n");
            Connection connect = getRemoteConnection(logger);
            
            Statement statement = connect.createStatement();

            
            //scanning data line by line


            logger.log("Checks for scanner closing\n");

            // Create the table if it doesn't exist
            logger.log("Create table\n");
            statement.executeUpdate("DROP TABLE IF EXISTS Records;");
            statement.executeUpdate("create table Records ( Region varchar(255), Country varchar(255), ItemType varchar(255), SalesChannel varchar(255), " 
                    + "OrderPriority varchar(255), OrderDate varchar(255), OrderID BIGINT(20) NOT NULL, " 
                    + "ShipDate varchar(255), UnitsSold BIGINT(20), UnitPrice double(10,2), UnitCost double(10,2), "
                    + "TotalRevenue double(10,2), TotalCost double(10,2), TotalProfit double(10,2), PRIMARY KEY (OrderID));");
            // Read and insert data from the CSV file
            logger.log("table created\n");
            logger.log("Inserting data\n");
            Scanner scanner = new Scanner(objectData);
            scanner.nextLine();
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] rowSplit = line.split(",");
                if (rowSplit.length != 14) {
                    continue;
                }
                //insertData(connection, headers, data);
                String region = rowSplit[0];
                String country = rowSplit[1];
                if (country.contains("\'")) {
                    country = country.replace('\'', ' ');
                }
                String itemType = rowSplit[2];
                String salesChannel = rowSplit[3];
                String orderPriority = rowSplit[4];
                String orderDate = rowSplit[5];
                int orderID = Integer.parseInt(rowSplit[6]);
                String shipDate = rowSplit[7];
                int unitsSold = Integer.parseInt(rowSplit[8]);
                double unitPrice = Double.parseDouble(rowSplit[9]);
                double unitCost = Double.parseDouble(rowSplit[10]);
                double totalRevenue = Double.parseDouble(rowSplit[11]);
                double totalCost = Double.parseDouble(rowSplit[12]);
                double totalProfit = Double.parseDouble(rowSplit[13]);
                String query = String.format("insert into Records values('%s','%s','%s',"
                + " '%s', '%s', '%s', %d, '%s', %d, "
                + "%.2f, %.2f, %.2f, %.2f, %.2f);", region, country, itemType, salesChannel, orderPriority, orderDate,
                orderID, shipDate, unitsSold, unitPrice, unitCost, totalRevenue, totalCost, totalProfit);
                statement.executeUpdate(query);
            }
            scanner.close();
            logger.log("All items added\n");
            ResultSet results = statement.executeQuery("Select count(OrderID) from Records");
            Response response = new Response();
            while (results.next()) {
                response.setValue("Rows made: " + results.getString(1));
            }
            inspector.consumeResponse(response);
            closeConnect(connect, logger);
            
        } catch (SQLException ex) {
            logger.log(ex.toString());
        }
            
            // ****************END FUNCTION IMPLEMENTATION***************************
            
        // Collect final information such as total runtime and CPU deltas.
        inspector.inspectAllDeltas();
        return inspector.finish();

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

    private static void createTable(Connection connection, String[] headers, LambdaLogger logger) throws SQLException {
        
        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE IF NOT EXISTS Records");
        for (String header : headers) {
            createTableQuery.append(header).append(" VARCHAR(255),");
        }
        createTableQuery.setLength(createTableQuery.length() - 1); // Remove the trailing comma
        createTableQuery.append(")");
        logger.log("Checks createTable");
        try (PreparedStatement statement = connection.prepareStatement(createTableQuery.toString())) {
            statement.executeUpdate();
        }
    }

 
}
