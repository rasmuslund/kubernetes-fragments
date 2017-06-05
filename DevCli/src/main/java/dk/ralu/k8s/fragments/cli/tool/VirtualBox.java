package dk.ralu.k8s.fragments.cli.tool;

import dk.ralu.k8s.fragments.cli.tool.ExternalCommand.OutputLine;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VirtualBox extends Tool {

    public VirtualBox() {
        super("VBoxHeadless", "5");
    }

    @Override
    public String getPreciseVersionAsString() {
        List<OutputLine> outputLines = createExternalCommand()
                .clearEnvironmentVariables()
                .command("VBoxHeadless --version")
                .execute();
        OutputLine lastLine = outputLines.get(outputLines.size() - 1);
        return lastLine.getContent();
    }
}
