package asserts;

import org.assertj.core.api.SoftAssertions;

public class ProfileAssert {
    private final SoftAssertions softly;
    private final ProfileSnapshot snapshot;

    public ProfileAssert(ProfileSnapshot snapshot) {
        this.snapshot = snapshot;
        this.softly = new SoftAssertions();
    }

    public ProfileAssert isUnchanged() {
        SoftAssertions softly = new SoftAssertions();
        String before = snapshot.getBefore();
        String after = snapshot.getAfter();

        softly.assertThat(after)
                .as("Name is unchanged")
                .isEqualTo(before);

        softly.assertAll();
        return this;
    }

    public ProfileAssert isChanged(String name) {
        SoftAssertions softly = new SoftAssertions();
        String after = snapshot.getAfter();

        softly.assertThat(after)
                .as("Name is changed")
                .isEqualTo(name);

        softly.assertAll();
        return this;
    }
}


