package org.example.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class Person {
    /*
    * abstract class for
    * Student and Invigilator
    * id, name, surname
    * */
    protected String ID;
    protected String name;
    protected String surname;
}
