package lambda;

/**
 *
 * @author Wes Lloyd
 */
public class Request {

    String name;
    private String filters;
    private String aggregates;

    public Request(){
        
    }
    public Request(String filters, String aggregates) {

        this.filters = filters;
        this.aggregates = aggregates;
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
     * @return the filters
     */
    public String getFilters() {
        return filters;
    }


    public void setFilters(String filters) {
        this.filters = filters;
    }

    /**
     * @return the aggregates
     */
    public String getAggregates() {
        return aggregates;
    }


    public void setAggregates(String aggregates) {
        this.aggregates = aggregates;
    }

}
