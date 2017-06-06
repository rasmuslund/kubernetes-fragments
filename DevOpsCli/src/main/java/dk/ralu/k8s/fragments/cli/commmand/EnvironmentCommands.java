package dk.ralu.k8s.fragments.cli.commmand;

import com.google.common.base.Strings;
import dk.ralu.k8s.fragments.cli.core.Context;
import dk.ralu.k8s.fragments.cli.core.Context.Cluster;
import dk.ralu.k8s.fragments.cli.core.Context.Environment;
import dk.ralu.k8s.fragments.cli.tool.KubeCtl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentCommands extends BaseCommand {

    private static final String ENVIRONMENT_CREATE = "environment create";
    private static final String ENVIRONMENT_LIST = "environment list";
    private static final String ENVIRONMENT_VIEW = "environment view";
    private static final String ENVIRONMENT_DELETE = "environment delete";

    @Autowired
    private KubeCtl kubeCtl;

    @Autowired
    private Context context;

    @CliAvailabilityIndicator({ENVIRONMENT_CREATE, ENVIRONMENT_DELETE, ENVIRONMENT_VIEW, ENVIRONMENT_LIST})
    public boolean isCommandAvailable() {
        return true;
    }

    @CliCommand(value = ENVIRONMENT_CREATE, help = "Creates a new environment (namespace) in the given cluster")
    public void create(

            @CliOption(
                    key = "creator",
                    help = "The name of the person creating the environment (you!)",
                    mandatory = true
            ) String creator,

            @CliOption(
                    key = "cluster",
                    help = "The cluster in which to create the new environment (defaults to current cluster)",
                    optionContext = OptionContexts.CREATE_CONTEXT
            ) Cluster cluster,

            @CliOption(
                    key = "environment",
                    help = "The environment to create (defaults to current environment)",
                    optionContext = OptionContexts.CREATE_CONTEXT
            ) Environment environment

    ) {

        if (cluster == null) {
            if (context.getCurrentCluster().isAny()) {
                throw new IllegalArgumentException("When no current cluster in context, cluster must be specified as an argument");
            }
            cluster = context.getCurrentCluster();
        }
        if (environment == null) {
            if (context.getCurrentEnvironment().isAny()) {
                throw new IllegalArgumentException("When no current environment in context, environment must be specified as an argument");
            }
            environment = context.getCurrentEnvironment();
        }
        if (Strings.isNullOrEmpty(creator)) {
            throw new IllegalArgumentException("You must provide your name as creator of the new environment");
        }

        kubeCtl.createNewEnvironment(cluster, environment, creator);
    }

    @CliCommand(value = ENVIRONMENT_VIEW, help = "Describes an environment (namespace)")
    public void view(

            @CliOption(
                    key = "cluster",
                    help = "The cluster in which the environment to describe exists (defaults to current cluster)"
            ) Cluster cluster,

            @CliOption(
                    key = "environment",
                    help = "The environment to describe (defaults to current environment)",
                    optionContext = OptionContexts.CREATE_CONTEXT
            ) Environment environment

    ) {

    }

    @CliCommand(value = ENVIRONMENT_DELETE, help = "Deletes an environment (namespace) from the given cluster)")
    public void delete(

            @CliOption(
                    key = "cluster",
                    help = "The cluster from which to delete the environment (defaults to current cluster)"
            ) Cluster cluster,

            @CliOption(
                    key = "environment",
                    help = "The environment to delete (defaults to current environment)",
                    optionContext = OptionContexts.DELETE_CONTEXT
            ) Environment environment

    ) {

    }
}
