package cn.oyzh.easyredis.controller.key;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.domain.RedisKeyFilterHistory;
import cn.oyzh.easyredis.fx.RedisKeyFilterHistoryPopup;
import cn.oyzh.easyredis.store.RedisKeyFilterHistoryJdbcStore;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.event.EventUtil;
import cn.oyzh.fx.gui.textfield.search.SearchTextField;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.window.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;


/**
 * redis键过滤业务
 *
 * @author oyzh
 * @since 2023/07/19
 */
@StageAttribute(
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        resizeable = false,
        value = RedisConst.FXML_BASE_PATH + "key/redisKeyFilter.fxml"
)
public class RedisKeyFilterController extends StageController {

    /**
     * 过滤模式
     */
    @FXML
    private SearchTextField keyFilter;

    /**
     * 树键
     */
    private RedisDBTreeItem treeItem;

    /**
     * 过滤历史储存
     */
    private final RedisKeyFilterHistoryJdbcStore historyStore = RedisKeyFilterHistoryJdbcStore.INSTANCE;

    @Override
    public void onStageShown(WindowEvent event) {
        EventUtil.register(this);
        this.stage.hideOnEscape();
        super.onStageShown(event);
        this.treeItem = this.getWindowProp("treeItem");
        String pattern = this.getWindowProp("pattern");
        if (!StrUtil.isBlank(pattern)) {
            this.keyFilter.setText(pattern);
        }
        this.keyFilter.requestFocus();
        this.keyFilter.setHistoryPopup(new RedisKeyFilterHistoryPopup());
    }

    /**
     * 键过滤
     */
    @FXML
    private void keyFilter() {
        String pattern = this.keyFilter.getText();
        if (StrUtil.isNotBlank(pattern) && !"*".equals(pattern)) {
            RedisKeyFilterHistory history = new RedisKeyFilterHistory();
            history.setPattern(pattern);
            this.historyStore.replace(history);
        }
        this.treeItem.doKeyFilter(pattern);
        this.closeWindow();
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("redis.title.key.filter");
    }
}
