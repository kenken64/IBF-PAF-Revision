package sg.edu.nus.iss.pafrevision.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.pafrevision.models.AggregateRSVP;
import sg.edu.nus.iss.pafrevision.models.RSVP;
import sg.edu.nus.iss.pafrevision.repositories.RSVPRepository;

@Service
public class RSVPService {
    @Autowired
    private RSVPRepository rsvpRepository;

    public List<RSVP> getAllRSVP(String s){
        return rsvpRepository.getAllRSVP(s);
    }

    public RSVP searchRSVPbyEmail (String email){
        return rsvpRepository.searchRSVPbyEmail(email);
    }

    public RSVP insertRSVP(RSVP rsvp){
        return rsvpRepository.insertRSVP(rsvp);
    }

    public boolean updateRSVP(RSVP rsvp){
        return rsvpRepository.updateRSVP(rsvp);
    }

    public int getTotalRSVP(){
        return rsvpRepository.getTotalRSVP();
    }

    public List<AggregateRSVP> aggregateRSVPByFoodType(){
        return rsvpRepository.aggregateRSVPByFoodType();
    }
}
