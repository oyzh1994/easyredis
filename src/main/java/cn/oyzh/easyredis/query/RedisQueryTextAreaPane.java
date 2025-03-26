package cn.oyzh.easyredis.query;

import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.store.RedisSettingStore;
import cn.oyzh.fx.plus.font.FontManager;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import javafx.scene.text.Font;

import java.util.Set;

/**
 * @author oyzh
 * @since 2025/01/21
 */
public class RedisQueryTextAreaPane extends RichDataTextAreaPane {

    /**
     * db索引
     */
    private int dbIndex;

    /**
     * redis客户端
     */
    private RedisClient client;

    public RedisClient getClient() {
        return client;
    }

    public void setClient(RedisClient client) {
        this.client = client;
    }

    public int getDbIndex() {
        return dbIndex;
    }

    public void setDbIndex(int dbIndex) {
        this.dbIndex = dbIndex;
    }

    /**
     * 提示词组件
     */
    private final RedisQueryPromptPopup promptPopup = new RedisQueryPromptPopup();

    {
        this.setOnMouseReleased(e -> this.promptPopup.hide());
        this.promptPopup.setOnItemSelected(item -> this.promptPopup.autoComplete(this, item));
        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                this.promptPopup.hide();
            }
        });
        this.setOnKeyReleased(event -> this.promptPopup.prompt(this, event));
    }

    @Override
    public void initNode() {
        this.initFont();
        this.initContentPrompts();
    }

    @Override
    protected Font initFont() {
//        // 禁用字体管理
//        super.disableFont();
        // 初始化字体
        RedisSetting setting = RedisSettingStore.SETTING;
//        this.setFontSize(setting.getQueryFontSize());
//        this.setFontFamily(setting.getQueryFontFamily());
//        this.setFontWeight2(setting.getQueryFontWeight());
        return FontManager.toFont(setting.queryFontConfig());
    }

    @Override
    public void initContentPrompts() {
        // 设置内容提示符
        Set<String> set = RedisQueryUtil.getKeywords();
        set.addAll(RedisQueryUtil.getParams());
        this.setContentPrompts(set);
    }
}
