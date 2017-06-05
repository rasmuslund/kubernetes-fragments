package dk.ralu.k8s.fragments.cli.tool;

import org.springframework.stereotype.Service;

@Service
public class KubeCtl extends Tool {

    public KubeCtl() {
        super("kubectl", "1.6");
    }

    @Override
    public String getPreciseVersionAsString() {
        return createExternalCommand()
                .command("kubectl version --client=true --short=true")
                .executeExpectingSingleStdOutLineMatchingRegEx("Client Version: v(?<version>.*)")
                .group("version");
    }
}
