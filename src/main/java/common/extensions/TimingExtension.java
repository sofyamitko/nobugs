package common.extensions;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback{
    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {

    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {

    }
}
