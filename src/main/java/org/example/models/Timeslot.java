package org.example.models;

import lombok.*;

import java.time.Duration;
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

    public boolean overlaps(Timeslot other) {
        return !this.end.isBefore(other.start) && !this.start.isAfter(other.end);
    }

    public long getOverlapMinutes(Timeslot other) {
        if (!this.overlaps(other)) {
            return 0;
        }

        LocalDateTime overlapStart = this.start.isAfter(other.start) ? this.start : other.start;
        LocalDateTime overlapEnd = this.end.isBefore(other.end) ? this.end : other.end;

        return Duration.between(overlapStart, overlapEnd).toMinutes();
    }
}
