package api.requests.steps;

import api.models.accounts.AccountRequestModel;
import api.models.accounts.AccountResponseModel;
import api.models.accounts.TransactionResponseModel;
import api.models.admin.CreateUserRequestModel;
import api.models.customer.GetUserProfileResponseModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.Arrays;
import java.util.List;

public class UserSteps {

    private String username;
    private String password;

    public UserSteps(String username,String password){
        this.username = username;
        this.password = password;
    }

    public static AccountResponseModel createAccount(CreateUserRequestModel model) {
        return new ValidatedCrudRequester<AccountResponseModel>(
                RequestSpecs.authAsUserSpec(model.getUsername(), model.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreatedSpec())
                .post(null);
    }

    public static AccountResponseModel[] getAllAccounts(String username, String password) {
        return new CrudRequester(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOkSpec())
                .get()
                .extract().as(AccountResponseModel[].class);
    }

    public static Double checkAccountBalance(String username, String password, int id) {

        AccountResponseModel[] accounts = getAllAccounts(username, password);

        return Arrays.stream(accounts)
                .filter(account -> account.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Аккаунт с id " + id + " не найден"))
                .getBalance();
    }

    // безопасное пополнение депозитом аккаунта
    public static void depositAccount(String username, String password, int id, double amount) {

        AccountResponseModel[] accounts = getAllAccounts(username, password);

        //проверка есть ли аккаунт с id у юзера
        boolean containsIdOrNot = Arrays.stream(accounts)
                .anyMatch(account -> account.getId() == id);
        if (!containsIdOrNot) {
            throw new IllegalArgumentException("Аккаунт с id " + id + " не найден");
        }

        //безопасное пополнение баланса
        if (amount < 0.0) {
            throw new IllegalArgumentException("Минимальная сумма пополнения баланса 0.01");
        }

        double remaining = amount;
        while (remaining > 0) {
            double chunk = Math.min(remaining, 5000.00);

            AccountRequestModel accountRequestModel = AccountRequestModel.builder()
                    .id(id)
                    .balance(chunk)
                    .build();

            new CrudRequester(
                    RequestSpecs.authAsUserSpec(username, password),
                    Endpoint.DEPOSIT,
                    ResponseSpecs.requestReturnsOkSpec())
                    .post(accountRequestModel);

            remaining -= chunk;
        }
    }

    //возврат конкретной транзакции по id аккаунта
    public static TransactionResponseModel getTransaction(String username, String password, int id, double amount, int relatedAccountId) {

        //десериализация вручную, тк требуется преобразование в [] массив, класс Endpoint это не поддерживает
        return Arrays.stream(
                        new CrudRequester(
                                RequestSpecs.authAsUserSpec(username, password),
                                Endpoint.TRANSACTIONS,
                                ResponseSpecs.requestReturnsOkSpec()
                        )
                                .get(id)
                                .extract()
                                .as(TransactionResponseModel[].class)
                )
                .filter(tx -> tx.getAmount() == amount && tx.getRelatedAccountId() == relatedAccountId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Не найдена транзакция: accountId=%d, amount=%.2f, relatedAccountId=%d",
                                id, amount, relatedAccountId)
                ));
    }

    public static GetUserProfileResponseModel getProfile(String username, String password) {
        return new ValidatedCrudRequester<GetUserProfileResponseModel>(
                RequestSpecs.authAsUserSpec(username, password),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOkSpec())
                .get();
    }

    public List<AccountResponseModel> getAllAccounts(){
        return new ValidatedCrudRequester<AccountResponseModel>(
                RequestSpecs.authAsUserSpec(this.username, this.password),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOkSpec()
        )
                .getAll(AccountResponseModel[].class);
    }
}