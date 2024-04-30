package sg.edu.nus.iss.pafrevision.repositories;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.pafrevision.models.AggregateRSVP;
import sg.edu.nus.iss.pafrevision.models.RSVP;
import static sg.edu.nus.iss.pafrevision.repositories.RSVPQueries.*;

@Repository
public class RSVPRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Retrieve all RSVPs from the dabatase 
     * All record and filter by name
     * @param s
     * @return
     */
    public List<RSVP> getAllRSVP(String s){
        final List<RSVP> rsvps= new LinkedList<>();
        SqlRowSet rs  = null;
        if(s == null || s.isEmpty()){
            rs = jdbcTemplate.queryForRowSet(SQL_SELECT_ALL_RSVP);
        }else{
            rs = jdbcTemplate.queryForRowSet(SQL_SELECT_RSVP_BY_NAME, s);
        }

        while(rs.next()){
            rsvps.add(RSVP.create(rs));
        }
        return rsvps;
    }

    public RSVP searchRSVPbyEmail (String email){
        SqlRowSet rs = jdbcTemplate
            .queryForRowSet(SQL_SEARCH_RSVP_BY_EMAIL, email);
        if(rs.next()){
            return RSVP.create(rs);
        }
        return null;
    }

    public RSVP insertRSVP(RSVP rsvp){
        KeyHolder keyholder = new GeneratedKeyHolder();
        try{
            jdbcTemplate.update(conn-> {
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT_RSVP, 
                                Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, rsvp.getName());
                ps.setString(2, rsvp.getEmail());
                ps.setString(3, rsvp.getPhone());
                ps.setTimestamp(4, 
                    new Timestamp(rsvp.getConfirmationDate()
                    .toDateTime().getMillis()));
                ps.setString(5, rsvp.getComments());
                ps.setString(6, rsvp.getFoodType());
                return ps;
            }, keyholder);
            BigInteger primaryKeyVal = (BigInteger)keyholder.getKey();
            rsvp.setId(primaryKeyVal.intValue());
            if(rsvp.getId() > 0){
                rsvp.setConfirmationDate(null);
                mongoTemplate.save(rsvp, "rsvp");
            }
        }catch(DataIntegrityViolationException e){
            RSVP existingRSVP = searchRSVPbyEmail(rsvp.getEmail());
            existingRSVP.setName(rsvp.getName());
            existingRSVP.setName(rsvp.getName());
            existingRSVP.setComments(rsvp.getComments());
            existingRSVP.setPhone(rsvp.getPhone());
            existingRSVP.setConfirmationDate(rsvp.getConfirmationDate());
            if(updateRSVP(existingRSVP)){
                rsvp.setId(existingRSVP.getId());
            }
        }
        
        return rsvp;
    }

    public boolean updateRSVP(RSVP rsvp){
        int rows = jdbcTemplate.update(SQL_UPDATE_RSVP, 
            rsvp.getName(), rsvp.getEmail(), rsvp.getPhone(), 
            new Timestamp(rsvp.getConfirmationDate().toDateTime().getMillis()), 
            rsvp.getComments(), rsvp.getFoodType(), rsvp.getEmail());
        if(rows > 0){
            rsvp.setConfirmationDate(null);
            mongoTemplate.save(rsvp, "rsvp");
            return true;
        }
        return false;
    }

    public int getTotalRSVP(){
        SqlRowSet rs = jdbcTemplate.queryForRowSet(RSVPQueries.SQL_TOTAL_CNT_RSVP);
        if(rs.next()){
            return rs.getInt("total");
        }
        return 0;
    }

    public List<AggregateRSVP> aggregateRSVPByFoodType(){
        List<AggregateRSVP> aggregateRSVPs = new LinkedList<>();
        
        GroupOperation grpByFoodTypeOpr = Aggregation
                    .group("foodType")
                    .push("foodType").as("foodType")
                    .count().as("count");
            
        SortOperation sortByCount = Aggregation.sort(
                    Sort.by(Direction.DESC, "count")); 
                    
        Aggregation pipeline = Aggregation.newAggregation(grpByFoodTypeOpr, sortByCount);
        AggregationResults results = mongoTemplate.aggregate(pipeline, "rsvp", Document.class);
        
        Iterator<Document> cursor = results.iterator();
        while(cursor.hasNext()){
            Document doc = cursor.next();
            aggregateRSVPs.add(AggregateRSVP.create(doc));
        }
        return aggregateRSVPs;
    }
}
