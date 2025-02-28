package cn.oyzh.easyredis.tabs.query;

import cn.oyzh.easyredis.query.RedisQueryParam;
import cn.oyzh.easyredis.query.RedisQueryResult;
import cn.oyzh.fx.gui.tabs.RichTabController;
import cn.oyzh.fx.gui.text.area.ReadOnlyTextArea;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;

/**
 * @author oyzh
 * @since 2025/01/20
 */
public class RedisQueryMsgTabController extends RichTabController {

    @FXML
    private ReadOnlyTextArea msg;

    public void init(RedisQueryParam param, RedisQueryResult result) {
        this.msg.appendLine(param.getContent());
        this.msg.appendLine("> " + result.getMessage());
        this.msg.appendLine("> " + I18nHelper.cost() + ": " + result.costSeconds());
    }
}