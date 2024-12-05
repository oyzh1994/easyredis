package cn.oyzh.easyredis.fx;


import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.fx.plus.i18n.I18nSelectAdapter;
import cn.oyzh.fx.plus.node.NodeManager;
import cn.oyzh.i18n.I18nHelper;

import java.util.List;
import java.util.Locale;

/**
 * redis格式下拉框
 *
 * @author oyzh
 * @since 2023/8/14
 */
public class RedisFormatComboBox extends FlexComboBox<String> implements I18nSelectAdapter<String> {

    {
        NodeManager.init(this);
        this.setTipText(I18nHelper.format());
    }

    /**
     * 获取格式
     *
     * @return 格式
     */
    public String getFormat() {
        int val = this.getSelectedIndex();
        return switch (val) {
            case 1 -> "JSON";
            case 2 -> "BINARY";
            case 3 -> "HEX";
            case 4 -> "STRING";
            default -> "RAW";
        };
    }

    /**
     * 是否raw格式
     *
     * @return 结果
     */
    public boolean isRawFormat() {
        return "RAW".equals(this.getFormat());
    }

    /**
     * 是否json格式
     *
     * @return 结果
     */
    public boolean isJsonFormat() {
        return "JSON".equals(this.getFormat());
    }

    /**
     * 是否二进制格式
     *
     * @return 结果
     */
    public boolean isBinaryFormat() {
        return "BINARY".equals(this.getFormat());
    }

    /**
     * 是否十六进制格式
     *
     * @return 结果
     */
    public boolean isHexFormat() {
        return "HEX".equals(this.getFormat());
    }

    /**
     * 是否字符串格式
     *
     * @return 结果
     */
    public boolean isStringFormat() {
        return "STRING".equals(this.getFormat());
    }

    /**
     * 选择字符串格式
     */
    public void selectString() {
        this.select(4);
    }

    /**
     * 选择二进制格式
     */
    public void selectBinary() {
        this.select(2);
    }

    /**
     * 选择十六进制格式
     */
    public void selectHex() {
        this.select(3);
    }

    /**
     * 选择json格式
     */
    public void selectJson() {
        this.select(1);
    }

    @Override
    public List<String> values(Locale locale) {
        this.clearItems();
        this.addItem(I18nHelper.raw());
        this.addItem(I18nHelper.json());
        this.addItem(I18nHelper.binary());
        this.addItem(I18nHelper.hex());
        this.addItem(I18nHelper.string());
        return this.getItems();
    }
}
