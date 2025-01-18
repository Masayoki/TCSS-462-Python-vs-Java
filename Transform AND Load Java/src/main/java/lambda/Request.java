package lambda;

/**
 *
 * @author Wes Lloyd
 */
public class Request {

    String name;
    private String bucketname;
    private String filename;
    private int row;
    private int col;
    public Request(){
        
    }
    public Request(int row, int col, String bucketName, String fileName) {
        this.row = row;
        this.col = col;
        this.bucketname = bucketName;
        this.filename = fileName;
    }
    
    public String getName() {
        return name;
    }
    
    public String getNameALLCAPS() {
        return name.toUpperCase();
    }

    public void setName(String name) {
        this.name = name;
    }

    public Request(String name) {
        this.name = name;
    }

    /**
     * @return the bucketName
     */
    public String getBucketname() {
        return bucketname;
    }

    /**
     * @param bucketName the bucketName to set
     */
    public void setBucketname(String bucketName) {
        this.bucketname = bucketName;
    }

    /**
     * @return the fileName
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFilename(String fileName) {
        this.filename = fileName;
    }

    /**
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * @param row the row to set
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * @return the col
     */
    public int getCol() {
        return col;
    }

    /**
     * @param col the col to set
     */
    public void setCol(int col) {
        this.col = col;
    }
}
