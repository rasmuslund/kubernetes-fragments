package dk.ralu.k8s.fragments.cli.tool;

import dk.ralu.k8s.fragments.cli.tool.ExternalCommand.OutputLine;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class Java extends Tool {

    public Java() {
        super("java", "1.8");
    }

    @Override
    public String getPreciseVersionAsString() {
        List<OutputLine> outputLines = createExternalCommand()
                .clearEnvironmentVariables()
                .command("java -version")
                .execute();
        if (outputLines.size() != 3) {
            throw new RuntimeException("Expected 3 output lines, but got " + outputLines.size());
        }
        OutputLine firstLine = outputLines.get(0);
        // java version "1.8.0_131"
        Pattern pattern = Pattern.compile("java version \"(?<version>.*)\"");
        Matcher matcher = pattern.matcher(firstLine.getContent());
        if (matcher.matches()) {

            return matcher.group("version");
        } else {
            throw new RuntimeException("Expected first line [" + firstLine + "] to match regex [" + pattern + "]");
        }
    }
}
