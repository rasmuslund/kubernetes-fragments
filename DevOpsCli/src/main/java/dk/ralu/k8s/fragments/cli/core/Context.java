package dk.ralu.k8s.fragments.cli.core;

import com.google.common.collect.ComparisonChain;
import dk.ralu.k8s.fragments.cli.output.Out;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class Context {

    @NotNull
    private Cluster currentCluster = Cluster.ANY;

    @NotNull
    private Environment currentEnvironment = Environment.ANY;

    @NotNull
    private Application currentApplication = Application.ANY;

    public String getCurrentContextPath() {
        return asPathSegment("cluster", getCurrentCluster())
                + asPathSegment("environment", getCurrentEnvironment())
                + asPathSegment("application", getCurrentApplication());
    }

    private static String asPathSegment(@NotNull String type, @NotNull ContextPart value) {
        return new Out()
                .reset()
                .colorBlue()
                .styleBold()
                .append("/")
                .reset()
                .colorBlue()
                .append(type)
                .append(":")
                .styleBold()
                .append(value.getName())
                .toString()
                ;

        //."/" + type + "=" + (value == null ? "*" : value.getName());
    }

    public boolean noContextSelected() {
        return getCurrentCluster().isAny()
                && getCurrentEnvironment().isAny()
                && getCurrentApplication().isAny();
    }

    @NotNull
    public Cluster getCurrentCluster() {
        return currentCluster;
    }

    @NotNull
    public Environment getCurrentEnvironment() {
        return currentEnvironment;
    }

    @NotNull
    public Application getCurrentApplication() {
        return currentApplication;
    }

    @NotNull
    public SortedSet<Cluster> getAllClusters() {
        return Cluster.getAllClusters();
    }

    public SortedSet<Environment> getAllStandardEnvironments() {
        return Environment.getAllStandardEnvironments();
    }

    public SortedSet<Application> getAllApplications() {
        return Application.getAllApplications();
    }

    public void setContext(@Nullable Cluster newCluster, @Nullable Environment newEnvironment, @Nullable Application newApplication) {

        // Application is always settable - no matter the current and/or new value of cluster and environment
        if (newApplication != null) {
            if (!Application.getAllApplications().contains(newApplication)) {
                throw new IllegalArgumentException("Application " + newApplication.getName() + " does not exist");
            }
            this.currentApplication = newApplication;
        }
        try {
            if (newEnvironment != null) {
                currentEnvironment = newEnvironment;
                // If setting environment to a standard environment, then always make cluster the one that are known to host the environment
                if (newEnvironment.isStandardEnvironment()) {
                    currentCluster = newEnvironment.getHostingCluster();
                    return;
                }
            }
            if (newCluster != null) {
                currentCluster = newCluster;
                // If current environment is a standard environment, that is not hosted on what the cluster has just been set to, then
                // set unset the current environment
                if (currentEnvironment.isStandardEnvironment() && !currentCluster.equals(currentEnvironment.getHostingCluster())) {
                    currentEnvironment = Environment.ANY;
                }
            }
        } finally {
            // Used to validate invariant
            if (currentEnvironment.isStandardEnvironment() && !currentEnvironment.getHostingCluster().equals(currentCluster)) {
                String errorMessage =
                        "Context invariant has been violated! Current environment [" + currentEnvironment + "] is a standard environment "
                                + "known to be hosted at the cluster [" + currentEnvironment.getHostingCluster() + "], but current "
                                + "cluster is [" + currentCluster + "]. They have both been reset to avoid errors as a consequence.";
                currentCluster = Cluster.ANY;
                currentEnvironment = Environment.ANY;
                throw new IllegalStateException(errorMessage);
            }
        }
    }

    public static abstract class ContextPart implements Comparable<ContextPart> {

        public static final String SYMBOL_FOR_ANY = "*";

        private String name;

        ContextPart(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public abstract boolean isAny();

        public final boolean isNotAny() {
            return !isAny();
        }

        @Override
        public int compareTo(@NotNull ContextPart that) {
            return ComparisonChain.start()
                    .compare(this.getName(), that.getName())
                    .result();
        }
    }

    public static class Cluster extends ContextPart {

        private static final SortedSet<Cluster> allClusters = new TreeSet<>();

        public static final Cluster ANY = new Cluster(SYMBOL_FOR_ANY);

        public static final Cluster MINIKUBE = new Cluster("minikube");
        static final Cluster NON_PROD = new Cluster("non-prod");
        static final Cluster PROD = new Cluster("prod");

        private final SortedSet<Environment> hostedEnvironments = new TreeSet<>();

        Cluster(String name) {
            super(name);
            if (!name.equals("*")) {
                allClusters.add(this);
            }
        }

        public static SortedSet<Cluster> getAllClusters() {
            return allClusters;
        }

        public SortedSet<Environment> getHostedEnvironments() {
            return hostedEnvironments;
        }

        void addHostedEnvironment(Environment environment) {
            hostedEnvironments.add(environment);
        }

        public boolean isAny() {
            return equals(ANY);
        }
    }

    public static class Environment extends ContextPart {

        private static final SortedSet<Environment> allStandardEnvironments = new TreeSet<>();

        public static final Environment ANY = new Environment(SYMBOL_FOR_ANY, Cluster.ANY, false);

        static final Environment LOCAL = new Environment("local", Cluster.MINIKUBE, true);
        static final Environment DEV = new Environment("dev", Cluster.NON_PROD, true);
        static final Environment DEMO = new Environment("demo", Cluster.NON_PROD, true);
        static final Environment RMS = new Environment("rms", Cluster.NON_PROD, true);
        static final Environment PROD = new Environment("prod", Cluster.PROD, true);

        private Cluster hostingCluster;

        Environment(String name) {
            this(name, null, false);
        }

        Environment(String name, Cluster hostingCluster, boolean standardEnvironment) {
            super(name);
            this.hostingCluster = hostingCluster;
            if (hostingCluster != null) {
                hostingCluster.addHostedEnvironment(this);
            }
            if (standardEnvironment) {
                allStandardEnvironments.add(this);
            }
        }

        public static SortedSet<Environment> getAllStandardEnvironments() {
            return allStandardEnvironments;
        }

        public boolean isStandardEnvironment() {
            return allStandardEnvironments.contains(this);
        }

        public Cluster getHostingCluster() {
            return hostingCluster;
        }

        public boolean isAny() {
            return equals(ANY);
        }
    }

    public static class Application extends ContextPart {

        private static final SortedSet<Application> allApplications = new TreeSet<>();

        public static final Application ANY = new Application(SYMBOL_FOR_ANY);

        static final Application MANAGE_BACKEND = new Application("manage-backend");
        static final Application APP_BACKEND = new Application("app-backend");
        static final Application NOC_BACKEND = new Application("noc-backend");

        protected Application(String name) {
            super(name);
            allApplications.add(this);
        }

        public static SortedSet<Application> getAllApplications() {
            return allApplications;
        }

        public boolean isAny() {
            return equals(ANY);
        }
    }
}
