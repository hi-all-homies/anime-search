package app.service.injector;

import javafx.scene.Parent;
import java.util.function.Supplier;

public interface ViewInjector {
    Parent load(final String location);

    void addMethod(Class<?> clazz, Supplier<?> callback);
}