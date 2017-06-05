package dk.ralu.k8s.fragments.cli.commmand;

import static dk.ralu.k8s.fragments.cli.core.Context.ContextPart.SYMBOL_FOR_ANY;

import dk.ralu.k8s.fragments.cli.core.Context;
import dk.ralu.k8s.fragments.cli.core.Context.Application;
import dk.ralu.k8s.fragments.cli.core.Context.Cluster;
import dk.ralu.k8s.fragments.cli.core.Context.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class ContextCommands extends BaseCommand {

    private static final String CONTEXT_SET_COMMAND = "context set";
    private static final String CONTEXT_CLEAR_COMMAND = "context clear";

    @Autowired
    private Context context;

    @CliAvailabilityIndicator({CONTEXT_SET_COMMAND})
    public boolean isCommandAvailable() {
        return true;
    }

    @CliAvailabilityIndicator({CONTEXT_CLEAR_COMMAND})
    public boolean isContextClearCommandAvailable() {
        return !context.noContextSelected();
    }

    @CliCommand(value = CONTEXT_CLEAR_COMMAND, help = "Reset context to nothing")
    public void contextClear() {
        context.setContext(Cluster.ANY,Environment.ANY, Application.ANY);
    }

    @CliCommand(value = CONTEXT_SET_COMMAND, help = "Change context")
    public void contextChange(

            @CliOption(
                    key = "cluster",
                    help = "Set cluster as part of current context (use nothing or * to clear)",
                    specifiedDefaultValue = SYMBOL_FOR_ANY
            ) Cluster cluster,

            @CliOption(
                    key = "environment",
                    help = "Set environment as part of current context (use nothing or * to clear)",
                    specifiedDefaultValue = SYMBOL_FOR_ANY
            ) Environment environment,

            @CliOption(
                    key = "application",
                    help = "Set application as part of current context (use nothing or * to clear)",
                    specifiedDefaultValue = SYMBOL_FOR_ANY
            ) Application application
    ) {

        context.setContext(cluster, environment, application);
    }
}
