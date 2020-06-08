<<<<<<< HEAD
# Alone
一个基于LWJGL的简易Java游戏引擎
=======
# Alone：一个使用Java开发的简易游戏引擎

Alone是一个基于LWJGL的使用Java语言开发的简易游戏引擎。使用本引擎不会带给你任何酷炫的效果，但是能让你更加方便的开发一个游戏。本引擎目前包含了以下模块：

* 渲染模块
* 事件模块
* 场景模块
* UI模块
* 声音模块

是的，没有包含物理模块、网络模块（只是因为作者水平不够）



## 快速开始

```java
public class Alone extends BaseEngine {

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void errorCallback(Exception e) {

    }

    @Override
    public void start(String[] args, Linker linker, Window window) {
        window.setTitle("Scene Model Test");
        window.setSize(1000, 700);
        window.setCenter();

        window.show();
    }

    @Override
    public void run() {

    }

    @Override
    protected void cleanup() {

    }
}
```



## 自定义事件

```java
public static class CustomEvent implements IEvent {
    private int count = 0;

    @Override
    public boolean isFired() {
        return (count % 30) == 0;
    }

    @Override
    public void update() {
        count++;
        System.out.println("自定义事件更新。");
    }
}
```

只需要将其链接到事件管理器即可

```java
 EventManager manager = EngineThread.getEventManager();
 manager.addEvent(new CustomEvent());
 manager.register(event -> System.out.println("自定义事件处理程序触发"), CustomEvent.class);
```

## 渲染器

自定义渲染是为哪些高手所准备的，它们可能不满足于本引擎的基础着色、渲染，

```java
package pers.crobin.engine.renderer;

import pers.crobin.engine.IMemoryManager;

/**
 * 渲染基类，每个渲染器都需要指定一个渲染目标。
 * @author Administrator
 */
public abstract class BaseRenderer<T> implements IMemoryManager {
    protected T target;

    public BaseRenderer() {
    }

    public BaseRenderer(T target) {
        this.target = target;
    }

    public void setTarget(T t) {
        this.target = t;
    }

    public T getTarget() {
        return target;
    }

    /**
     * 对渲染目标进行渲染
     */
    public abstract void render();
}
```

渲染基类，继承此类之后将其链接到渲染管理器即可

## Gui

```java
package pers.crobin.engine.ui;

import org.lwjgl.nanovg.NVGColor;
import pers.crobin.engine.IMemoryManager;
import pers.crobin.engine.kernel.Window;
import pers.crobin.engine.ui.control.Parent;

import java.util.Objects;

/**
 * @author Administrator
 */
public abstract class BaseGui implements IMemoryManager {
    protected static final NVGColor RESULT_COLOR = NVGColor.create();
    protected              Parent   parent;
    protected              boolean  visible      = true;
    protected              boolean  disable      = false;

    protected BaseGui() {
        this.parent = new Parent().setSize(100, 100);
    }

    public boolean isVisible() {
        return visible;
    }

    public BaseGui setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public boolean isDisable() {
        return disable;
    }

    public BaseGui setDisable(boolean disable) {
        this.disable = disable;
        return this;
    }

    public Parent getParent() {
        return parent;
    }

    protected abstract void start();

    public abstract void draw(long context, Window window);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseGui baseGui = (BaseGui) o;
        return parent.equals(baseGui.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent);
    }
}
```

# Alone
>>>>>>> 630a64f... Add README
