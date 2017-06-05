package dk.ralu.k8s.fragments.cli.shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.CommandLine;
import org.springframework.shell.SimpleShellCommandLineOptions;

class ShellCommandLineParser {

    private static final Logger logger = LoggerFactory.getLogger(ShellCommandLineParser.class);

    CommandLine parse(ShellProperties shellProperties, String[] applicationArguments) {
        List<String> commands = new ArrayList<>();
        if (shellProperties.getCommandFile() != null) {
            File f = new File(shellProperties.getCommandFile());
            try {
                commands.addAll(FileUtils.readLines(f));
            } catch (IOException e) {
                logger.error("Unable to read from " + f.toString(), e);
            }
        }
        String[] commandsToExecute = (commands.size() > 0) ? commands.toArray(new String[commands.size()]) : null;

        int historySize = shellProperties.getHistorySize();
        if (historySize < 0) {
            logger.warn("historySize option must be > 0, using default value of "
                                + SimpleShellCommandLineOptions.DEFAULT_HISTORY_SIZE);
            historySize = SimpleShellCommandLineOptions.DEFAULT_HISTORY_SIZE;
        }

        return new CommandLine(applicationArguments, historySize, commandsToExecute);
    }
}
