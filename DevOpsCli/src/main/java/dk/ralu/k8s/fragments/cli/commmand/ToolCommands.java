package dk.ralu.k8s.fragments.cli.commmand;

import dk.ralu.k8s.fragments.cli.tool.Tool;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;

@Component
public class ToolCommands extends BaseCommand {

    private static final String VERIFY_COMMAND = "tools verify";
    private static final String INFO_COMMAND = "tools info";

    @Autowired
    private Collection<Tool> tools;

    @CliAvailabilityIndicator({VERIFY_COMMAND})
    public boolean isCommandAvailable() {
        return true;
    }

    @CliCommand(value = VERIFY_COMMAND, help = "Verifies that expected tools seems to work and have at least the required version")
    public void verifyInfo() {
        tools.forEach(Tool::writeVerifyInfo);
    }

    @CliCommand(value = INFO_COMMAND, help = "Prints detailed info about every external tool used")
    public void debugInfo() {
        tools.forEach(Tool::writeDetails);
    }
}
