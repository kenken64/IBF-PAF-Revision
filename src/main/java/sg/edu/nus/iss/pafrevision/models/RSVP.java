package sg.edu.nus.iss.pafrevision.models;

import java.io.StringReader;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class RSVP {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private DateTime confirmationDate;
    private String comments;
    private String foodType;
    private Integer totalCnt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public DateTime getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(DateTime confirmationDate) {
        this.confirmationDate = confirmationDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public Integer getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(Integer totalCnt) {
        this.totalCnt = totalCnt;
    }

    public static RSVP create(SqlRowSet rs){
        RSVP rsvp = new RSVP();
        rsvp.setId(rs.getInt("id"));
        rsvp.setName(rs.getString("full_name"));
        rsvp.setEmail(rs.getString("email"));
        rsvp.setPhone(rs.getString("phone"));

        // rs return date -> retrieve as string 
        //-> parse to DateTime -> set to your pojo/object
        rsvp.setConfirmationDate(new DateTime(
            DateTimeFormat.forPattern("dd/MM/yyyy")
            .parseDateTime(rs.getString("confirmation_date"))));
        rsvp.setComments(rs.getString("comment"));
        rsvp.setFoodType(rs.getString("food_type"));
        return rsvp;
    }

    public static RSVP create(JsonObject readObject){
        RSVP rsvp= new RSVP();
        rsvp.setName(readObject.getString("name"));
        rsvp.setEmail(readObject.getString("email"));
        rsvp.setPhone(readObject.getString("phone"));

        // json return date -> retrieve as string 
        //-> parse using Instant -> set to your pojo/object
        rsvp.setConfirmationDate(new DateTime(
            Instant.parse(readObject.getString("confirmation_date"))));
        rsvp.setComments(readObject.getString("comment"));
        rsvp.setFoodType(readObject.getString("food_type"));
        return rsvp;
    }


    public JsonObject toJSON(){
        return Json.createObjectBuilder()
            .add("id", id)
            .add("name", name)
            .add("email", email)
            .add("phone", phone)
            .add("confirmation_date", 
                confirmationDate != null ? confirmationDate.toString() : "")
            .add("comment", comments)
            .add("food_type", foodType)
            .build();
    }

    public static RSVP create(String jsonStr){
        JsonReader jsonReader = Json.createReader(new StringReader(jsonStr));
        return create(jsonReader.readObject());
    }

}
