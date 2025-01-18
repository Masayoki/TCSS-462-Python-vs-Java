package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import saaf.Inspector;
import saaf.Response;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * uwt.lambda_test::handleRequest
 *
 * @author Wes Lloyd
 * @author Robert Cordingly
 */
public class ModifyCSV implements RequestHandler<Request, HashMap<String, Object>> {

    /**
     * Lambda Function Handler
     * 
     * @param request Request POJO with defined variables from Request.java
     * @param context 
     * @return HashMap that Lambda will automatically convert into JSON.
     */
    public HashMap<String, Object> handleRequest(Request request, Context context) {
        
        //Collect inital data.
        Inspector inspector = new Inspector();
        //inspector.inspectAll();
        
        //****************START FUNCTION IMPLEMENTATION*************************
        String bucketname = request.getBucketname();
        String filename = request.getFilename();

        // Create new file on S3
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
        //get object file using source bucket and srcKey name
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketname, filename));
        
        //get content of the file
        InputStream objectData = s3Object.getObjectContent();
        //scanning data line by line
        LambdaLogger logger = context.getLogger();
        logger.log("CSV recieved");
        StringWriter csvWrite = new StringWriter();
        try {
            Scanner scanner = new Scanner(objectData);

            while (scanner.hasNextLine()) {
                String text = scanner.nextLine();
                String[] textArr = text.split(",");
                int i = 0;
                for (String str : textArr) {

                    switch(str) {
                        case("C"): csvWrite.write("Critical"); break;
                        case("L"): csvWrite.write("Low"); break;
                        case("M"): csvWrite.write("Medium"); break;
                        case("H"): csvWrite.write("High"); break;
                        default: csvWrite.write(str);                    
                    }
                    if (i < 13) {
                        csvWrite.write(",");
                    } else {
                        csvWrite.write("\n");
                        i = 0;
                    }
                    i++;
                }
            }
            scanner.close();
            csvWrite.close();
        } catch(Exception e) {
            logger.log("Error: " + e);
        }


        byte[] bytes = csvWrite.toString().getBytes(StandardCharsets.UTF_8);
        InputStream is = new ByteArrayInputStream(bytes);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(bytes.length);
        meta.setContentType("text/plain");
        // Create new file on S3
        s3Client.putObject(bucketname, "mod " +filename, is, meta);
        
        
        //Create and populate a separate response object for function output. (OPTIONAL)
        Response response = new Response();
        response.setValue("Bucket: " + bucketname + " filename:" + filename + "processed.");
        
        inspector.consumeResponse(response);
        
        //****************END FUNCTION IMPLEMENTATION***************************
        
        //Collect final information such as total runtime and cpu deltas.
        //inspector.inspectAllDeltas();
        return inspector.finish();
    }
}
