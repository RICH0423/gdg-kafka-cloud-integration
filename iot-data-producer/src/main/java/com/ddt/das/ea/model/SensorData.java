package com.ddt.das.ea.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Random;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorData {

    private long id;
    private String temperature;

    public static SensorData create() {
        Random r = new Random();
        String temperature = String.format("%03dF",
                r.longs(14, 120).findFirst().getAsLong());

        return new SensorData(r.longs(1, 10).findFirst().getAsLong(),
                temperature);
    }
}
