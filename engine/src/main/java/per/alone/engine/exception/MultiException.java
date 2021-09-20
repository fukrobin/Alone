package per.alone.engine.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * 组合多个异常
 *
 * @author fkrobin
 * @date 2021/9/17 0:33
 */
public class MultiException extends RuntimeException {

    private static final long serialVersionUID = 286399101572057537L;

    private final List<Exception> exceptionList;

    public MultiException() {
        exceptionList = new ArrayList<>(4);
    }

    public void addException(Exception exception) {
        exceptionList.add(exception);
    }

    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder().append("MultiException: [");
        for (Exception exception : exceptionList) {
            builder.append(exception.getMessage()).append("];");
        }
        return builder.append("]").toString();
    }
}
