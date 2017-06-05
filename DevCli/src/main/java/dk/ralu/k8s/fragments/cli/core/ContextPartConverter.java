package dk.ralu.k8s.fragments.cli.core;

import static dk.ralu.k8s.fragments.cli.core.Context.ContextPart.SYMBOL_FOR_ANY;

import dk.ralu.k8s.fragments.cli.core.Context.Application;
import dk.ralu.k8s.fragments.cli.core.Context.Cluster;
import dk.ralu.k8s.fragments.cli.core.Context.ContextPart;
import dk.ralu.k8s.fragments.cli.core.Context.Environment;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Service;

@Service
public class ContextPartConverter implements Converter<ContextPart> {

    @Autowired
    private Context context;

    @Override
    public boolean supports(Class<?> type, String optionContext) {
        return ContextPart.class.isAssignableFrom(type);
    }

    @Override
    public ContextPart convertFromText(String value, Class<?> targetType, String optionContext) {
        if (targetType == Cluster.class) {
            if (value.equals(SYMBOL_FOR_ANY)) {
                return Cluster.ANY;
            }
            return context.getAllClusters().stream()
                    .filter(cluster -> Objects.equals(cluster.getName(), value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Non-existing cluster: " + value));
        }
        if (targetType == Environment.class) {
            if (value.equals(SYMBOL_FOR_ANY)) {
                return Environment.ANY;
            }
            return context.getAllStandardEnvironments().stream()
                    .filter(environment -> Objects.equals(environment.getName(), value))
                    .findFirst()
                    .orElseGet(() -> new Environment(value));
        }
        if (targetType == Application.class) {
            if (value.equals(SYMBOL_FOR_ANY)) {
                return Application.ANY;
            }
            return context.getAllApplications().stream()
                    .filter(application -> Objects.equals(application.getName(), value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Non-existing application: " + value));
        }
        throw new IllegalArgumentException("Unknown target type: " + targetType);
    }

    @Override
    public boolean getAllPossibleValues(List<Completion> completions, Class<?> targetType, String existingData, String optionContext,
                                        MethodTarget target) {

        if (targetType == Cluster.class) {
            completions.addAll(possibleCompletions(context.getAllClusters(), existingData));

        } else if (targetType == Environment.class) {
            completions.addAll(possibleCompletions(context.getAllStandardEnvironments(), existingData));

        } else if (targetType == Application.class) {
            completions.addAll(possibleCompletions(context.getAllApplications(), existingData));
        }

        return true;
    }

    private Set<Completion> possibleCompletions(SortedSet<? extends ContextPart> contextParts, String existingData) {
        Set<Completion> possibleCompletions = contextParts.stream()
                .map(ContextPart::getName)
                .filter(name -> name.startsWith(existingData))
                .map(Completion::new)
                .collect(Collectors.toSet());
        if (existingData.equals("")) {
            possibleCompletions.add(new Completion(SYMBOL_FOR_ANY));
        }
        return possibleCompletions;
    }
}
