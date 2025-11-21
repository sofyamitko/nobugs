package api.asserts;

import api.models.customer.GetUserProfileResponseModel;
import api.requests.steps.UserSteps;

public class ProfileSnapshot {
    private final String username;
    private final String password;
    private final String name;

    private ProfileSnapshot(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public static ProfileSnapshot of(String username, String password) {
        GetUserProfileResponseModel model = UserSteps.getProfile(username, password);
        return new ProfileSnapshot(username, password, model.getName());
    }

    public String getBefore() {
        return this.name;
    }

    public String getAfter() {
        GetUserProfileResponseModel model = UserSteps.getProfile(username, password);
        return model.getName();
    }

    public ProfileAssert assertThat() {
        return new ProfileAssert(this);
    }
}
