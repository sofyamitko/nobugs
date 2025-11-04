package utils;

import models.admin.CreateUserRequestModel;
import models.customer.GetUserProfileResponseModel;
import requests.AdminCreateUserRequester;
import requests.GetAndUpdateCustomerProfileRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class UserUtils {

     private AccountBalanceUtils accountBalanceUtils;

     public UserUtils(){
         this.accountBalanceUtils = new AccountBalanceUtils();
     }

    //создание юзера
    public void createUser(CreateUserRequestModel model){
        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreatedSpec())
                .post(model);
    }

   public int prepareUserWithAccount(CreateUserRequestModel model){
       //создание пользователя
       createUser(model);
       //создание аккаунта c извлечением id
       int id = accountBalanceUtils.createAccount(model.getUsername(), model.getPassword());
       return id;
   }

   public GetUserProfileResponseModel getUserProfile(String username, String password){
      return new GetAndUpdateCustomerProfileRequester(
               RequestSpecs.authAsUserSpec(username, password),
               ResponseSpecs.requestReturnsOkSpec())
               .get()
               .extract()
               .as(GetUserProfileResponseModel.class);
   }
}
