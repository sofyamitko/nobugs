package utils;

import models.accounts.AccountRequestModel;
import models.accounts.AccountResponseModel;
import models.accounts.AccountTransactionModel;
import requests.CreateAccountRequester;
import requests.DepositAccountRequester;
import requests.GetAccountTransactionsRequester;
import requests.GetAccountsUserRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.Arrays;


public class AccountBalanceUtils {

    //создание аккаунта c возвращением его id
    public int createAccount(String username, String password) {

        AccountResponseModel accountResponseModel = new CreateAccountRequester(
                RequestSpecs.authAsUserSpec(username, password),
                ResponseSpecs.entityWasCreatedSpec())
                .post(null)
                .extract()
                .as(AccountResponseModel.class);

        return accountResponseModel.getId();
    }

    //получение баланса аккаунта
    public double getBalanceOfAccount(String username, String password, int id) {
        AccountResponseModel[] accounts = new GetAccountsUserRequester(
                RequestSpecs.authAsUserSpec(username, password),
                ResponseSpecs.requestReturnsOkSpec())
                .get()
                .extract().as(AccountResponseModel[].class);

        return Arrays.stream(accounts)
                .filter(account -> account.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Аккаунт с id " + id + " не найден"))
                .getBalance();
    }

    // безопасное пополнение депозитом аккаунта
    public void depositAccount(String username, String password, int id, double amount) {

        AccountResponseModel[] accounts = new GetAccountsUserRequester(
                RequestSpecs.authAsUserSpec(username, password),
                ResponseSpecs.requestReturnsOkSpec())
                .get()
                .extract().as(AccountResponseModel[].class);

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

            AccountRequestModel accountRequest = AccountRequestModel.builder()
                    .id(id)
                    .balance(chunk)
                    .build();

            new DepositAccountRequester(
                    RequestSpecs.authAsUserSpec(username, password),
                    ResponseSpecs.requestReturnsOkSpec())
                    .post(accountRequest);

            remaining -= chunk;
        }
    }

    //возврат всех транзакций пользователя по аккаунту
    public AccountTransactionModel[] getAccountTransactions(String username, String password, int id){

        AccountTransactionModel[] accountTransactions = new GetAccountTransactionsRequester(
                RequestSpecs.authAsUserSpec(username, password),
                ResponseSpecs.requestReturnsOkSpec())
                .get(id)
                .extract()
                .as(AccountTransactionModel[].class);

        return accountTransactions;
    }

    //возврат транзакций по id конкретного аккаунта
    public AccountTransactionModel getTransactionsOfIdAccount(String username, String password, int id, double amount, int relatedAccountId){
        AccountTransactionModel[] accountTransactions = getAccountTransactions(username, password, id);
        return Arrays.stream(accountTransactions)
                .filter(account -> account.getAmount() == amount && account.getRelatedAccountId() == relatedAccountId)
                .findFirst()
                .get();
    }
}
