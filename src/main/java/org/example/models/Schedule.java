package org.example.models;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class Schedule {
    /*
    * this class is for empty schedule generation
    * startTime
    * endTime
    * availableDays
    * Timeslots can be equal like each of them = 1 hour
    * ,or they can vary like from 30 minutes to 3 hours
    * */
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private static final Logger logger = LogManager.getLogger(Schedule.class);

    public ArrayList<Timeslot> calculateTimeSlots() {
        ArrayList<Timeslot> availableTimeslots = new ArrayList<>();
        LocalDateTime currentDateTime = startDate;

        while (currentDateTime.isBefore(endDate)) {
            LocalDateTime timeSlotStart = LocalDateTime.of(currentDateTime.toLocalDate(), startTime.toLocalTime());
            LocalDateTime timeSlotEnd = LocalDateTime.of(currentDateTime.toLocalDate(), endTime.toLocalTime());

            while (timeSlotStart.isBefore(timeSlotEnd)) {
                if (!timeSlotStart.toLocalTime().isBefore(startTime.toLocalTime()) &&
                        !timeSlotStart.toLocalTime().isAfter(endTime.toLocalTime())) {
                    Timeslot timeslot = new Timeslot();
                    timeslot.setStart(timeSlotStart);
                    timeslot.setEnd(timeSlotStart.plusMinutes(60)); // Assuming each time slot is 30 minutes
                    availableTimeslots.add(timeslot);
                }
                timeSlotStart = timeSlotStart.plusMinutes(60);
            }
            currentDateTime = currentDateTime.plusDays(1);
        }
        return availableTimeslots;
    }


    public int calculateMaxTimeSlots() {
        long days = Duration.between(startDate.toLocalDate().atStartOfDay(), endDate.toLocalDate().atStartOfDay()).toDays();
        long timeSlotsPerDay = Duration.between(startTime.toLocalTime(), endTime.toLocalTime()).toHours();
        logger.info("# Available Days: " + days);
        logger.info("# Timeslots per day: " + timeSlotsPerDay);
        return (int) (days * timeSlotsPerDay);
    }


}
