package cn.oyzh.easyredis.query;

import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.store.RedisSettingStore;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * @author oyzh
 * @since 2025/01/21
 */
public class RedisQueryTextArea extends RichDataTextAreaPane {

    /**
     * zk客户端
     */
    @Getter
    @Setter
    private RedisClient client;

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

    protected void initFont() {
        // 禁用字体管理
        super.disableFont();
        // 初始化字体
        RedisSetting setting = RedisSettingStore.SETTING;
        this.setFontSize(setting.getQueryFontSize());
        this.setFontFamily(setting.getQueryFontFamily());
        this.setFontWeight2(setting.getQueryFontWeight());
    }

    @Override
    public void initContentPrompts() {
        // 设置内容提示符
        Set<String> set = RedisQueryUtil.getKeywords();
        set.addAll(RedisQueryUtil.getParams());
        this.setContentPrompts(set);
    }
}
