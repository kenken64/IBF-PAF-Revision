package sg.edu.nus.iss.pafrevision.models;

import java.util.List;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class AggregateRSVP {
    private String id;
    private List<String> foodTypes;
    private Integer count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getFoodTypes() {
        return foodTypes;
    }

    public void setFoodTypes(List<String> foodTypes) {
        this.foodTypes = foodTypes;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public static AggregateRSVP create(Document doc){
        AggregateRSVP g = new AggregateRSVP();
        g.setId(doc.getString("_id"));
        g.setCount(doc.getInteger("count"));
        g.setFoodTypes(doc.getList("foodType", String.class));
        return g;
    }

    public JsonObject toJSON(){
        return Json.createObjectBuilder()
            .add("id", id)
            .add("count", count)
            .add("foodTypes", foodTypes.toString())
            .build();
    }

}
