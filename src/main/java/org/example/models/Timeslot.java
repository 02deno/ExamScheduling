package org.example.models;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
@ToString
public class Timeslot {
    /*
    * start : Daytime object
    * end : Daytime object
    * */
    private LocalDateTime start;
    private LocalDateTime end;

    public static boolean checkSameDay(Timeslot timeslot1, Timeslot timeslot2) {
        LocalDate date1 = timeslot1.getStart().toLocalDate();
        LocalDate date2 = timeslot2.getStart().toLocalDate();
        return date1.equals(date2);
    }
}
