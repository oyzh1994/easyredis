package cn.oyzh.easyredis.search;

import cn.oyzh.fx.plus.controls.textfield.LimitTextField;
import cn.oyzh.fx.plus.event.AnonymousEvent;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.EventHandler;
import javafx.scene.control.Skin;
import lombok.Getter;
import lombok.Setter;

/**
 * 搜索文本域
 *
 * @author oyzh
 * @since 2023/10/24
 */
public class RedisKeySearchTextField extends LimitTextField {

    {
        this.setPromptText(I18nHelper.contains());
    }

    @Setter
    @Getter
    private EventHandler<AnonymousEvent<Object>> onSearch;

    /**
     * 当前皮肤
     *
     * @return 皮肤
     */
    public RedisKeySearchTextFieldSkin skin() {
        return (RedisKeySearchTextFieldSkin) this.getSkin();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new RedisKeySearchTextFieldSkin(this) {
            @Override
            public void onSearch(String text) {
                super.onSearch(text);
                if (onSearch != null) {
                    onSearch.handle(AnonymousEvent.of(text));
                }
            }
        };
    }

    public int getSelectedIndex() {
        return this.skin().getSelectedIndex();
    }
}
