package utils;

import models.accounts.AccountResponseModel;
import models.accounts.AccountTransactionModel;
import models.accounts.TransferMoneyResponseModel;
import org.assertj.core.api.SoftAssertions;


public class AssertionsUtils {

    public static void assertSuccessfulTransfer(SoftAssertions softly, TransferMoneyResponseModel response, Double amount, Integer senderId, Integer receiverId) {
        softly.assertThat(response.getMessage()).isEqualTo("Transfer successful");
        softly.assertThat(response.getAmount()).isEqualTo(amount);
        softly.assertThat(response.getSenderAccountId()).isEqualTo(senderId);
        softly.assertThat(response.getReceiverAccountId()).isEqualTo(receiverId);
    }

    public static void assertBalancesUpdatedAfterTransfer(SoftAssertions softly, Double senderBefore, Double receiverBefore, Double senderAfter, Double receiverAfter, Double amount) {
        softly.assertThat(senderBefore - amount)
                .isEqualTo(senderAfter);
        softly.assertThat(receiverBefore + amount)
                .isEqualTo(receiverAfter);
    }

    public static void assertSuccessfulDeposit(SoftAssertions softly,AccountResponseModel accountResponseModel, Double amount){
        softly.assertThat(accountResponseModel.getBalance()).isEqualTo(amount);
    }

    public static void assertBalancesUpdatedAfterDeposit(SoftAssertions softly, Double balanceBeforeDeposit, Double balanceAfterDeposit, Double amount){
        softly.assertThat(balanceAfterDeposit).isEqualTo(balanceBeforeDeposit + amount);
    }

    public static void assertBalancesUpdatedAfterDepositSeveralTimes(SoftAssertions softly, Double balanceBeforeDeposit, Double balanceAfterDeposit, Double amount, Integer times){
        softly.assertThat(balanceAfterDeposit).isEqualTo(balanceBeforeDeposit + (amount * times));
    }

    public static void assertBalancesUnchangedAfterDeposit(SoftAssertions softly, Double balanceBeforeDeposit, Double balanceAfterDeposit) {
        softly.assertThat(balanceAfterDeposit).isEqualTo(balanceBeforeDeposit);
    }

    public static void assertBalancesUnchangedAfterTransfer(SoftAssertions softly, Double senderBefore, Double receiverBefore, Double senderAfter, Double receiverAfter) {
        softly.assertThat(senderBefore).isEqualTo(senderAfter);
        softly.assertThat(receiverBefore).isEqualTo(receiverAfter);
    }

    public static void assertTransaction(SoftAssertions softly, AccountTransactionModel transaction, String expectedType) {
        softly.assertThat(transaction).isNotNull();
        softly.assertThat(transaction.getType()).isEqualTo(expectedType);
    }
}