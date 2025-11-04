package iteration1;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import utils.AccountBalanceUtils;
import utils.UserUtils;
import utils.factory.TestDataFactory;


public class BaseTest {

    protected SoftAssertions softly;
    protected AccountBalanceUtils accountBalanceUtils;
    protected UserUtils userUtils;
    protected TestDataFactory factory;


    @BeforeEach
    public void setupTest(){
        this.softly = new SoftAssertions();
        this.userUtils = new UserUtils();
        this.accountBalanceUtils = new AccountBalanceUtils();
        this.factory = new TestDataFactory();
    }

    @AfterEach
    public void afterTest(){
        softly.assertAll();
    }
}