package dk.ralu.k8s.fragments.cli.core;

import dk.ralu.k8s.fragments.cli.core.CurrentContext.Context;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Service;

@Service
public class ContextConverter implements Converter<Context> {

    @Autowired
    private CurrentContext currentContext;

    @Override
    public boolean supports(Class<?> type, String optionContext) {
        return Context.class == type;
    }

    @Override
    public Context convertFromText(String value, Class<?> targetType, String optionContext) {
        if (value.equals("..")) {
            return currentContext.getParentOfCurrentContext() == null ? currentContext.getCurrentContext() : currentContext.getParentOfCurrentContext();
        }
        return currentContext.getCurrentAvailableSubPaths().stream().filter(context -> context.getName().equals(value)).findFirst().get();
    }

    @Override
    public boolean getAllPossibleValues(List<Completion> completions, Class<?> targetType, String existingData, String optionContext,
                                        MethodTarget target) {

        if (Arrays.asList("", ".").contains(existingData)) {
            completions.add(new Completion(".."));
        }
        currentContext.getCurrentAvailableSubPaths().stream().map(Context::getName).filter(name -> name.startsWith(existingData))
                .forEach(name -> completions.add(new Completion(name)));
        return true;
    }
}
