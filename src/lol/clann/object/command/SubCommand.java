package lol.clann.object.command;

import java.lang.reflect.Method;

public class SubCommand {

    public final SubCommandAnnotation annotation;
    public final Method method;

    public SubCommand(SubCommandAnnotation annotation, Method method) {
        this.annotation = annotation;
        this.method = method;
    }
}
