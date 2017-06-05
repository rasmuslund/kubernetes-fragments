package dk.ralu.k8s.fragments.cli.commmand;

import dk.ralu.k8s.fragments.cli.core.CurrentContext;
import dk.ralu.k8s.fragments.cli.core.CurrentContext.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class ContextCommands extends BaseCommand {

    private static final String LIST_COMMAND = "ls";
    private static final String MOVE_COMMAND = "cd";

    @Autowired
    private CurrentContext currentContext;

    @CliAvailabilityIndicator({LIST_COMMAND, MOVE_COMMAND})
    public boolean isCommandAvailable() {
        return true;
    }

    @CliCommand(value = LIST_COMMAND, help = "Lists sub contexts of the current context")
    public void list() {
        currentContext.getCurrentAvailableSubPaths().forEach(
                subContext -> writeLineToConsole(out -> out.append(subContext.getName()))
        );
    }

    @CliCommand(value = MOVE_COMMAND, help = "Changes context")
    public void move(
            @CliOption(key = "to", mandatory = true, help = "Move into the given child context") Context context) {

        if (context != null) {
            currentContext.setCurrentContext(context);
        }

    }
}
