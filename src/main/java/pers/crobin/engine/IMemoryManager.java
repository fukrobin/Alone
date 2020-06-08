package pers.crobin.engine;

/**
 * 内存管理接口，任何需要使用到底层代码（C/C++库）的类，都必须实现此接口
 * @author Administrator
 */
public interface IMemoryManager {


    /**
     * 虽然Java拥有垃圾回收机制，但是LWJGL提供的C/C++库所使用的内存Java无法管理，
     * 除非使用try-with-resource块分配能自动弹出的堆栈，并在其上分配内存，否则所有其余
     * 情况都必须手动释放内存
     */
    void cleanup();
}
