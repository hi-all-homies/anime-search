package app.injector;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class ViewInjector {

    private final Map<Class<?>, Callable<?>> injectMethods = new HashMap<>();

    public ViewInjector(){}

    public Parent load(final String location) {
        try {
            return this.createLoader(location).load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private FXMLLoader createLoader(String location){
        var loader = new FXMLLoader(ViewInjector.class.getResource(location));
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setControllerFactory(this::getController);
        return loader;
    }

    private Object getController(Class<?> clazz) {
        if(injectMethods.containsKey(clazz)) {
            return getControllerWithSavedMethod(clazz);
        } else {
            return getControllerWithDefaultConstructor(clazz);
        }
    }

    private Object getControllerWithSavedMethod(Class<?> clazz){
        try {
            return injectMethods.get(clazz).call();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Object getControllerWithDefaultConstructor(Class<?> clazz){
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    public void addMethod(Class<?> clazz, Callable<?> callback){
        this.injectMethods.put(clazz, callback);
    }
}