package api.generators;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomData {

    //generators/ – генераторы случайных данных для тестов (RandomData).

    private RandomData(){}

    public static String getUsername(){
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String getPassword(){
        return RandomStringUtils.randomAlphabetic(3).toUpperCase() +
                RandomStringUtils.randomAlphabetic(2).toLowerCase() +
                RandomStringUtils.randomNumeric(3).toLowerCase() +
                "!;.";
    }

    public static String getName(){
        return RandomStringUtils.randomAlphabetic(4).toLowerCase() + " " + RandomStringUtils.randomAlphabetic(4).toLowerCase();
    }

    public static double getAmount(double min, double max) {
        double value = min + (max - min) * Math.random();
        return Math.round(value * 100.0) / 100.0; // округляем до 2 знаков
    }
}
