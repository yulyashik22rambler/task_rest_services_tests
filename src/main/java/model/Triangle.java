package model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@ToString
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Triangle {
    private String id;
    private String firstSide;
    private String secondSide;
    private String thirdSide;
}
