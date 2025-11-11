package generators;

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
}
