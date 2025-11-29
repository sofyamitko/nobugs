package api.asserts;

import api.models.accounts.TransactionResponseModel;
import org.assertj.core.api.SoftAssertions;

public class TransactionAssert {
    private final TransactionResponseModel transaction;
    private final SoftAssertions softly;

    private TransactionAssert(TransactionResponseModel transaction) {
        this.transaction = transaction;
        this.softly = new SoftAssertions();
    }

    public static TransactionAssert assertThat(TransactionResponseModel transaction) {
        return new TransactionAssert(transaction);
    }

    public TransactionAssert isTransferOut(double expectedAmount, int expectedReceiverAccountId) {
        softly.assertThat(transaction).as("Transaction should not be null").isNotNull();
        softly.assertThat(transaction.getType()).as("Transaction type").isEqualTo("TRANSFER_OUT");
        softly.assertThat(transaction.getAmount()).as("Transaction amount").isEqualTo(expectedAmount);
        softly.assertThat(transaction.getRelatedAccountId()).as("Related account ID").isEqualTo(expectedReceiverAccountId);
        softly.assertThat(transaction.getTimestamp()).as("Transaction timestamp").isNotNull();

        softly.assertAll();
        return this;
    }


    public TransactionAssert isTransferIn(double expectedAmount, int expectedSenderAccountId) {
        softly.assertThat(transaction).as("Transaction should not be null").isNotNull();
        softly.assertThat(transaction.getType()).as("Transaction type").isEqualTo("TRANSFER_IN");
        softly.assertThat(transaction.getAmount()).as("Transaction amount").isEqualTo(expectedAmount);
        softly.assertThat(transaction.getRelatedAccountId()).as("Related account ID").isEqualTo(expectedSenderAccountId);
        softly.assertThat(transaction.getTimestamp()).as("Transaction timestamp").isNotNull();

        softly.assertAll();
        return this;
    }
}
