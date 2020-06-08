package pers.crobin.game.gui;

import pers.crobin.engine.event.ActionEvent;

/**
 * Created by Administrator
 *
 * @author Administrator
 * @Date 2020/5/3 18:45
 * @Description $
 **/
public class GuiController {

    public void buttonClicked(ActionEvent event) {
        if (event.isLeftClick()) {
            System.out.println(Thread.currentThread().getName());
        }
    }
}
