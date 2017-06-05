package dk.ralu.k8s.fragments.cli.commmand;

import dk.ralu.k8s.fragments.cli.core.DebugFlags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class DebugCommands extends BaseCommand {

    private static final String DEBUG_COMMAND = "debug specific";
    private static final String DEBUG_ALL_COMMAND = "debug all";
    private static final String DEBUG_NONE_COMMAND = "debug none";

    @Autowired
    private DebugFlags shellDebugFlags;

    @CliAvailabilityIndicator({DEBUG_COMMAND, DEBUG_ALL_COMMAND, DEBUG_NONE_COMMAND})
    public boolean isCommandAvailable() {
        return true;
    }

    @CliCommand(value = DEBUG_COMMAND, help = "Enables or disables specific debug output")
    public String debug(
            @CliOption(
                    key = {"forceShowCommands"},
                    help = "Show command used when external tool is invoked"
            ) Boolean forceShowCommands,
            @CliOption(
                    key = {"forceShowOutput"},
                    help = "Show output from command when external tool is invoked"
            ) Boolean forceShowOutput,
            @CliOption(
                    key = {"forceShowExitCode;"},
                    help = "Show exit code when external tool is used"
            ) Boolean forceShowExitCode
    ) {
        if (forceShowCommands != null) {
            shellDebugFlags.setForceShowCommands(forceShowCommands);
        }
        if (forceShowOutput != null) {
            shellDebugFlags.setForceShowOutput(forceShowOutput);
        }
        if (forceShowExitCode != null) {
            shellDebugFlags.setForceShowExitCode(forceShowExitCode);
        }
        return shellDebugFlags.toString();
    }

    @CliCommand(value = DEBUG_ALL_COMMAND, help = "Enables all debug output")
    public String debugAll() {
        shellDebugFlags.enableAll();
        return shellDebugFlags.toString();
    }

    @CliCommand(value = DEBUG_NONE_COMMAND, help = "Disables all debug output")
    public String debugNone() {
        shellDebugFlags.disableAll();
        return shellDebugFlags.toString();
    }
}
