package pers.crobin.engine.event;

import org.lwjgl.glfw.GLFW;
import pers.crobin.engine.ui.BaseGui;
import pers.crobin.engine.ui.control.Button;
import pers.crobin.engine.ui.control.Parent;
import pers.crobin.engine.ui.control.TextField;
import pers.crobin.engine.util.Utils;

import java.util.*;

/**
 * Created by Administrator
 *
 * @author Administrator
 * @date 2020/5/1 11:54
 **/
public class EventManager {
    private final List<IEvent>                                                      eventList;

    private final Map<Class<? extends IEvent>, Set<EventHandler<? extends IEvent>>> classSetMap;

    public EventManager() {
        classSetMap = new HashMap<>();
        eventList   = new LinkedList<>();
    }

    /**
     * 调用此方法前，请确保已经调用 #addEvent添加相应的事件类，否则此方法会直接忽略此事件处理器的注册。
     *
     * @param handler 事件处理器
     * @param clazz   注册的事件类
     * @param <T>     继承自{@link IEvent}的事件类
     */
    public <T extends IEvent> void register(EventHandler<? extends IEvent> handler, Class<T> clazz) {
        boolean flag = false;
        for (IEvent iEvent : eventList) {
            if (iEvent.getClass().equals(clazz)) {
                flag = true;
                break;
            }
        }

        if (flag) {
            Set<EventHandler<? extends IEvent>> set = classSetMap.computeIfAbsent(clazz, aEvent -> new HashSet<>());
            if (handler != null) {
                set.add(handler);
            }
        }
    }

    public <T extends IEvent> void unregister(EventHandler<? extends IEvent> handler, Class<T> clazz) {
        Set<EventHandler<? extends IEvent>> set = classSetMap.get(clazz);
        if (set != null) {
            set.remove(handler);
        }
    }

    public <T extends IEvent> void addEvent(T event) {
        boolean flag = true;
        for (IEvent iEvent : eventList) {
            if (iEvent.getClass().equals(event.getClass())) {
                flag = false;
                break;
            }
        }

        if (flag) {
            eventList.add(event);
        }
    }

    private void postActionEventToGui(ActionEvent event, Parent parent) {
        // 忽略隐藏、禁用的gui的事件处理
        parent.getChildren().forEach(control -> {
            if (control.isVisible()) {
                boolean hovered = Utils.isHover(control.getLayoutBounds(), event);

                EventHandler<ActionEvent> pressedEvent = control.getMousePressedEvent();
                EventHandler<ActionEvent> releasedEvent = control.getMouseReleasedEvent();
                EventHandler<ActionEvent> clickedEvent = control.getMouseClickedEvent();
                if (pressedEvent != null && event.isButtonPress(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                    pressedEvent.handle(event);
                }

                if (event.isButtonRelease(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                    if (hovered) {
                        if (releasedEvent != null) {
                            releasedEvent.handle(event);
                        }

                        if (clickedEvent != null) {
                            clickedEvent.handle(event);
                        }
                    }
                }

                if (control instanceof Button) {
                    Button button = (Button) control;
                    EventHandler<ActionEvent> handler = button.getOnAction();
                    if (handler != null) {
                        if (hovered) {
                            handler.handle(event);
                        }
                    }
                } else if (control instanceof TextField) {
                    TextField field = (TextField) control;
                    field.setFocus(hovered);
                }
            }
        });
    }

    private void postKeyEventToGui(KeyEvent event, Parent parent) {
        parent.getChildren().forEach(control -> {
            if (control.isVisible() && control instanceof TextField) {
                TextField field = (TextField) control;
                field.getCharUpdateHandler().handle(event);
                field.getCursorUpdateHandler().handle(event);
            }
        });
    }

    public <T extends IEvent> void postEventToActiveGui(T event, BaseGui gui) {
        Objects.requireNonNull(event);
        if (gui != null && !gui.isDisable() && gui.isVisible()) {
            Parent parent = gui.getParent();
            if (event instanceof ActionEvent) {
                postActionEventToGui((ActionEvent) event, parent);
            } else if (event instanceof KeyEvent) {
                postKeyEventToGui((KeyEvent) event, parent);
            }
        }
    }

    private <T extends IEvent> T getType(IEvent e) {
        return (T) e;
    }

    public void post() {
        eventList.forEach(event -> {
            if (event.isFired()) {
                // 有时可能会遇到调用了 addEvent方法，但并没有注册相应的任何处理程序。
                Set<EventHandler<? extends IEvent>> set = classSetMap.get(event.getClass());
                if (set != null) {
                    set.forEach(eventHandler -> eventHandler.handle(getType(event)));
                }
            }
        });
    }

    /**
     * 更新所有已经注册的事件，这会在每次游戏更新的最后进行调用
     */
    public void update() {
        eventList.forEach(IEvent::update);
    }
}
