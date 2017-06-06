package dk.ralu.k8s.fragments.cli.tool;

import dk.ralu.k8s.fragments.cli.core.Context.Cluster;
import dk.ralu.k8s.fragments.cli.core.Context.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KubeCtl extends Tool {

    @Autowired
    private Minikube minikube;

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

    public void createNewEnvironment(Cluster cluster, Environment environment, String creator) {
        requireClusterIsMinikube(cluster);

        minikube.ensureRunning();

        /**
         * TODO: Use yaml file instead - see: https://kubernetes.io/docs/tasks/administer-cluster/namespaces/#creating-a-new-namespace
         */

        createExternalCommand()
                .command("kubectl create namespace " + environment.getName() + " --save-config=true --context=minikube")
                .showOutput()
                .execute();

        createExternalCommand()
                .command("kubectl label namespace " + environment.getName() + " creator=" + creator + " --context=minikube")
                .showOutput()
                .execute();

        writeLineToConsole(out -> out.styleBold().append("New environment created:"));

        createExternalCommand()
                .command("kubectl describe namespace " + environment.getName() + " --context=minikube")
                .showOutput()
                .execute();
    }

    // TODO: Support other environments as well - also modify the use of --context=minikube above
    private void requireClusterIsMinikube(Cluster cluster) {
        if (!cluster.equals(Cluster.MINIKUBE)) {
            throw new RuntimeException("Currently only supports using minikube");
        }
    }
}

