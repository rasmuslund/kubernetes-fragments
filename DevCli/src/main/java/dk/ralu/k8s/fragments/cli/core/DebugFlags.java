package dk.ralu.k8s.fragments.cli.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "devcli.debug")
public class DebugFlags {

    private boolean forceShowCommands;
    private boolean forceShowOutput;
    private boolean forceShowExitCode;

    public boolean isForceShowCommands() {
        return forceShowCommands;
    }

    public void setForceShowCommands(boolean forceShowCommands) {
        this.forceShowCommands = forceShowCommands;
    }

    public boolean isForceShowOutput() {
        return forceShowOutput;
    }

    public void setForceShowOutput(boolean forceShowOutput) {
        this.forceShowOutput = forceShowOutput;
    }

    public boolean isForceShowExitCode() {
        return forceShowExitCode;
    }

    public void setForceShowExitCode(boolean forceShowExitCode) {
        this.forceShowExitCode = forceShowExitCode;
    }

    @Override
    public String toString() {
        return "DebugFlags{" +
                "forceShowCommands=" + forceShowCommands +
                ", forceShowOutput=" + forceShowOutput +
                ", forceShowExitCode=" + forceShowExitCode +
                '}';
    }

    public void enableAll() {
        setForceShowCommands(true);
        setForceShowExitCode(true);
        setForceShowOutput(true);
    }

    public void disableAll() {
        setForceShowCommands(false);
        setForceShowExitCode(false);
        setForceShowOutput(false);
    }
}
