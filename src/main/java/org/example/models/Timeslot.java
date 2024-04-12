package org.example.models;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class Timeslot {
    /*
    * start : Daytime object
    * end : Daytime object
    * */
    private LocalDateTime start;
    private LocalDateTime end;
}
