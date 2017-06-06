package dk.ralu.k8s.fragments.cli.tool;

import dk.ralu.k8s.fragments.cli.tool.ExternalCommand.OutputLine;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class Minikube extends Tool {

    public void ensureRunning() {
        if (getStatus() != Status.RUNNING) {
            writeLineToConsole(out -> out.styleBold().append("Minikube not running - starting..."));
            start();
        }
    }

    // TODO: Find right arguments (mem+disc should be configurable), and enable relevant plug-ins
    private void start() {
        createExternalCommand()
                .command("minikube start")
                .showOutput()
                .execute();
    }

    public enum Status {
        RUNNING,
        STOPPED,
        OTHER
    }

    public Minikube() {
        super("minikube", "0.19");
    }

    @Override
    public String getPreciseVersionAsString() {
        return createExternalCommand()
                .command("minikube version")
                .executeExpectingSingleStdOutLineMatchingRegEx("minikube version: v(?<version>.*)")
                .group("version");
    }

    public Status getStatus() {
        List<OutputLine> lines = createExternalCommand()
                .command("minikube status")
                .ignoreExitValue()
                .execute();
        if (lines.size() == 2) {
            String line1 = lines.get(0).getContent();
            String line2 = lines.get(1).getContent();

            if (line1.equals("minikubeVM: Running") && line2.equals("localkube: Running")) {
                return Status.RUNNING;
            }
            if ((line1.equals("minikubeVM: Does Not Exist") || line1.equals("minikubeVM: Stopped")) && line2.equals("localkube: N/A")) {
                return Status.STOPPED;
            }
        }
        return Status.OTHER;
    }

    public String getIp() {
        return createExternalCommand()
                .command("minikube ip")
                .executeExpectingSingleStdOutLine();
    }

    @Override
    public void writeDetailExtraLines() {
        writeDetailLine("status", () -> getStatus());
        writeDetailLine("ip", () -> getIp());
        writeDetailLine("dockerEnv", () -> getDockerEnvironmentVariables());
    }

    public Map<String, String> getDockerEnvironmentVariables() {
        /*
        export DOCKER_TLS_VERIFY="1"
        export DOCKER_HOST="tcp://192.168.99.100:2376"
        export DOCKER_CERT_PATH="/home/rasmus/.minikube/certs"
        export DOCKER_API_VERSION="1.23"
        # Run this command to configure your shell:
        # eval $(minikube docker-env)
         */
        Map<String, String> dockerEnvironmentVariables = new LinkedHashMap<>();
        List<OutputLine> outputLines = createExternalCommand()
                .command("minikube docker-env")
                .execute();
        for (int n = 0; n < 4; n++) {
            Pair<String, String> environmentVariable = extractEnvironmentVariable(outputLines.get(n).getContent());
            dockerEnvironmentVariables.put(environmentVariable.getKey(), environmentVariable.getValue());

        }
        return dockerEnvironmentVariables;
    }

    private static Pair<String, String> extractEnvironmentVariable(String line) {
        Pattern pattern = Pattern.compile("export (?<key>.+)=\"(?<value>.+)\"");
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            return new Pair<>(matcher.group("key"), matcher.group("value"));
        }
        throw new IllegalArgumentException("Expected [" + line + "] to match regex [" + pattern + "]");
    }
}
