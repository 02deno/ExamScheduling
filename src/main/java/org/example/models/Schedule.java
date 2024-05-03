package org.example.models;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
@ToString
public class Schedule {
    /*
    * this class is for empty schedule generation
    * startTime
    * endTime
    * availableDays
    * Timeslots can be equal like each of them = 1 hour
    * ,or they can vary like from 30 minutes to 3 hours
    * */
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private static final Logger logger = LogManager.getLogger(Schedule.class);

    public ArrayList<Timeslot> calculateTimeSlots() {
        ArrayList<Timeslot> availableTimeslots = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (currentDate.isBefore(endDate)) {
            LocalDateTime timeSlotStart = LocalDateTime.of(currentDate, startTime);
            LocalDateTime timeSlotEnd = LocalDateTime.of(currentDate, endTime);

            while (timeSlotStart.isBefore(timeSlotEnd)) {
                if (!timeSlotStart.toLocalTime().isBefore(startTime) &&
                        !timeSlotStart.toLocalTime().isAfter(endTime)) {
                    Timeslot timeslot = new Timeslot(timeSlotStart, timeSlotStart.plusMinutes(60));
                    availableTimeslots.add(timeslot);
                }
                timeSlotStart = timeSlotStart.plusMinutes(60);
            }
            currentDate = currentDate.plusDays(1);
        }
        return availableTimeslots;
    }


    public int calculateMaxTimeSlots() {
        long days = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays();
        long timeSlotsPerDay = Duration.between(startTime, endTime).toHours();
        //logger.info("# Available Days: " + days);
        //logger.info("# Timeslots per day: " + timeSlotsPerDay);
        return (int) (days * timeSlotsPerDay);
    }


}
