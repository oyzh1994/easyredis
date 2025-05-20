package cn.oyzh.easyredis.filter;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.fx.plus.i18n.I18nSelectAdapter;
import cn.oyzh.fx.plus.node.NodeManager;
import cn.oyzh.i18n.I18nHelper;

import java.util.List;
import java.util.Locale;

/**
 * @author oyzh
 * @since 2024/4/19
 */
public class RedisKeySearchTypeComboBox extends FXComboBox<String> implements I18nSelectAdapter<String> {

    {
        NodeManager.init(this);
    }

    @Override
    public List<String> values(Locale locale) {
        this.clearItems();
        this.addItem(I18nHelper.allKeys());
        this.addItem(I18nHelper.collectKeys());
        this.addItem("STRING");
        this.addItem("LIST");
        this.addItem("SET");
        this.addItem("ZSET");
        this.addItem("HASH");
        this.addItem("STREAM");
        return this.getItems();
    }
}
