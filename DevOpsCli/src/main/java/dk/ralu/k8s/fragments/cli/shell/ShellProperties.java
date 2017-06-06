package dk.ralu.k8s.fragments.cli.shell;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.shell.SimpleShellCommandLineOptions;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.shell")
public class ShellProperties {

    private int historySize = SimpleShellCommandLineOptions.DEFAULT_HISTORY_SIZE;

    private String commandFile;

    public int getHistorySize() {
        return historySize;
    }

    public void setHistorySize(int historySize) {
        this.historySize = historySize;
    }

    public String getCommandFile() {
        return commandFile;
    }

    public void setCommandFile(String commandFile) {
        this.commandFile = commandFile;
    }
}
