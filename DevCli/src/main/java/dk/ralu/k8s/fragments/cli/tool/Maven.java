
package dk.ralu.k8s.fragments.cli.tool;

import dk.ralu.k8s.fragments.cli.tool.ExternalCommand.OutputLine;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class Maven extends Tool {

    public Maven() {
        super("mvnw", "3.5");
    }

    @Override
    public String getPreciseVersionAsString() {
        List<OutputLine> outputLines = createExternalCommand()
                .clearEnvironmentVariables()
                .command("mvnw --version")
                .execute();
        OutputLine firstLine = outputLines.get(0);
        // |BOLD|Apache Maven 3.5.0 (ff8f5e7444045639af65f6095c62210b5713f426; 2017-04-03T21:39:06+02:00)
        Pattern pattern = Pattern.compile(".*Apache Maven (?<version>\\d+\\.\\d+\\.\\d+).*");
        Matcher matcher = pattern.matcher(firstLine.getContent());
        if (matcher.matches()) {
            return matcher.group("version");
        } else {
            throw new RuntimeException("Expected first line [" + firstLine + "] to match regex [" + pattern + "]");
        }
    }
}
