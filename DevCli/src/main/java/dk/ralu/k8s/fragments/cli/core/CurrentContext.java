package dk.ralu.k8s.fragments.cli.core;

import com.google.common.collect.ComparisonChain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class CurrentContext {

    private Context rootContext;

    private Context currentContext;

    public String getCurrentContextPath() {
        return currentContext.getPath().stream().map(Context::getName).collect(Collectors.joining("/", "/", ""));
    }

    public TreeSet<Context> getCurrentAvailableSubPaths() {
        return currentContext.getChildren();
    }

    @PostConstruct
    private void buildContextTree() {
        rootContext = new DirectoryContext(null, "");
        currentContext = rootContext;
        DirectoryContext environmentsContext = new DirectoryContext(rootContext, "Environments");
        for (String environmentName : Arrays.asList("Local", "Dev", "Demo", "RMS", "Prod")) {
            EnvironmentContext environmentContext = new EnvironmentContext(environmentsContext, environmentName);
            DirectoryContext applicationsContext = new DirectoryContext(environmentContext, "Applications");
            for (String applicationName : Arrays.asList("ManageBackend", "AppBackend")) {
                new ApplicationContext(applicationsContext, applicationName);
            }
        }
    }

    public void setCurrentContext(Context currentContext) {
        this.currentContext = currentContext;
    }

    public Context getParentOfCurrentContext() {
        return currentContext.getParent();
    }

    public Context getCurrentContext() {
        return currentContext;
    }

    public abstract class Context implements Comparable<Context> {

        private String name;
        private Context parent;
        private TreeSet<Context> children = new TreeSet<>();

        public Context(Context parent, String name) {
            this.name = name;
            this.parent = parent;
            if (parent != null) {
                getParent().getChildren().add(this);
            }
        }

        public ArrayList<Context> getPath() {
            ArrayList<Context> path = new ArrayList<>();
            Context current = this;
            while (current.getParent() != null) {
                path.add(0, current);
                current = current.getParent();
            }
            return path;
        }

        public String getName() {
            return name;
        }

        public Context getParent() {
            return parent;
        }

        public TreeSet<Context> getChildren() {
            return children;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int compareTo(Context that) {
            return ComparisonChain.start()
                    .compare(this.getName(), that.getName())
                    .result();
        }
    }

    public class DirectoryContext extends Context {

        public DirectoryContext(Context parent, String name) {
            super(parent, name);
        }
    }

    public class EnvironmentContext extends Context {

        public EnvironmentContext(Context parent, String name) {
            super(parent, name);
        }
    }

    public class ApplicationContext extends Context {

        public ApplicationContext(Context parent, String name) {
            super(parent, name);
        }
    }
}
