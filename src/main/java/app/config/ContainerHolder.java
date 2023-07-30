package app.config;

public enum ContainerHolder {
    INSTANCE;

    private static final Container container = new Container();

    public Container getContainer(){
        return container;
    }
}
