package sg.edu.nus.iss.pafrevision.controller;

import java.util.List;

import javax.print.attribute.standard.Media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.pafrevision.models.AggregateRSVP;
import sg.edu.nus.iss.pafrevision.models.RSVP;
import sg.edu.nus.iss.pafrevision.services.RSVPService;

@RestController
@RequestMapping(path="/api/rsvp", produces= MediaType.APPLICATION_JSON_VALUE)
public class RSVPController {
    
    @Autowired
    private RSVPService rsvpService;

    @GetMapping
    public ResponseEntity<String> getAllRSVP(@RequestParam(value = "q", 
                    required = false) String q){
        List<RSVP> rsvps = rsvpService.getAllRSVP(q);
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        if(rsvps.isEmpty()){
            return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{'error_message': 'No RSVP found'}");
        }
        for(RSVP rsvp: rsvps){
            arrBuilder.add(rsvp.toJSON());
        }

        JsonArray result = arrBuilder.build();
        return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(result.toString());
    }

    @GetMapping("/total")
    public ResponseEntity<String> getTotalRSVP(){
        int total = rsvpService.getTotalRSVP();
        JsonObject resp = Json.createObjectBuilder()
            .add("total", total)
            .build();
        return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(resp.toString());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveRSVP(@RequestBody String json){
    
        RSVP rsvp = RSVP.create(json);
        if(rsvp == null){
            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{'error_message': 'Invalid JSON'}");
        }

        RSVP saved = rsvpService.insertRSVP(rsvp);
        JsonObject resp = Json.createObjectBuilder()
            .add("id", saved.getId())
            .build();
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(resp.toString());
    }

    @GetMapping("/aggregate")
    public ResponseEntity<String> getAggregateRSVP(){
        List<AggregateRSVP> aggr = rsvpService.aggregateRSVPByFoodType();
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        if(aggr.isEmpty()){
            return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{'error_message': 'No RSVP aggregation found'}");
        }
        for(AggregateRSVP a: aggr){
            arrBuilder.add(a.toJSON());
        }

        JsonArray result = arrBuilder.build();
        return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(result.toString());
    }
}
