package dk.ralu.k8s.fragments.cli.commmand;

public final class OptionContexts {

    private OptionContexts() {}

    public static final String CREATE_CONTEXT = "CREATE";
    public static final String DELETE_CONTEXT = "DELETE";

    public static boolean isCreateContext(String optionContext) {
        return optionContext.contains(CREATE_CONTEXT);
    }

    public static boolean isDeleteContext(String optionContext) {
        return optionContext.contains(DELETE_CONTEXT);
    }

    public static boolean isCreateOrDeleteContext(String optionContext) {
        return isCreateContext(optionContext) || isDeleteContext(optionContext);
    }
}
