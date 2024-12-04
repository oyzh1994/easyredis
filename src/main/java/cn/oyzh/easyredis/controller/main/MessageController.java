package cn.oyzh.easyredis.controller.main;

import cn.oyzh.easyredis.fx.RedisMsgTextArea;
import cn.oyzh.fx.plus.controller.SubStageController;
import javafx.fxml.FXML;


/**
 * redis消息业务
 *
 * @author oyzh
 * @since 2024/04/23
 */
public class MessageController extends SubStageController   {

    /**
     * 消息文本框
     */
    @FXML
    private RedisMsgTextArea msgArea;

    /**
     * 清空节点消息
     */
    @FXML
    private void clearMsg() {
        this.msgArea.clear();
    }

}
