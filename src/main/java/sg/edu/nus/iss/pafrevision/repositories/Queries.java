package sg.edu.nus.iss.pafrevision.repositories;

public interface Queries {
    public static final String SQL_SELECT_ALL_RSVP = """
        SELECT id, full_name, email, phone, 
        DATE_FORMAT(confirmation_date, \"%d/%m/%Y\") as confirmation_date,
         comment, food_type FROM rsvp      
    """;

    public static final String SQL_SELECT_RSVP_BY_NAME ="""
        SELECT id, full_name, email, phone,
        DATE_FORMAT(confirmation_date, \"%d/%m/%Y\") as confirmation_date,
        comment, food_type FROM rsvp WHERE full_name = ?
    """;

    public static final String SQL_INSERT_RSVP = """
        INSERT INTO rsvp (full_name, email, phone, confirmation_date, comment, food_type) 
        VALUES (?, ?, ?, ?, ?, ?) 
    """;
}
