package cn.oyzh.easyredis.fx;

import cn.hutool.core.util.HexUtil;
import cn.hutool.json.JSONUtil;
import cn.oyzh.easyredis.exception.DataTooBigException;
import cn.oyzh.fx.common.util.StringUtil;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import lombok.Getter;
import lombok.Setter;

/**
 * redis数据文本域
 *
 * @author oyzh
 * @since 2023/07/28
 */
public class RedisDataTextArea extends FlexTextArea {

    /**
     * 原始数据
     */
    @Getter
    private Object rawData;

    /**
     * 显示类型
     * 0 raw
     * 1 json
     * 2 binary
     * 3 hex
     * 4 string
     */
    @Setter
    @Getter
    private byte showType = 0;

    /**
     * 设置原始数据
     *
     * @param rawData 原始数据
     */
    public void setRawData(Object rawData) {
        this.rawData = rawData;
        this.showData();
    }

    /**
     * 获取json数据
     *
     * @return json数据
     */
    public String getJsonData() {
        if (this.rawData == null || !(this.rawData instanceof String rawValue)) {
            return null;
        }
        if (!rawValue.contains("{") && !rawValue.contains("[")) {
            return rawValue;
        }
        try {
            return JSONUtil.toJsonPrettyStr(rawValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rawValue;
    }

    /**
     * 获取二进制数据
     *
     * @return 二进制数据
     */
    public String getBinaryData() {
        if (this.rawData instanceof byte[] bytes) {
            return StringUtil.toBinary(bytes);
        }
        if (this.rawData instanceof String string) {
            return StringUtil.toBinary(string);
        }
        return null;
    }

    /**
     * 获取十六进制数据
     *
     * @return 十六进制数据
     */
    public String getHexData() {
        if (this.rawData == null) {
            return "";
        }
        if (this.rawData instanceof String str) {
            return HexUtil.encodeHexStr(str.getBytes(), false);
        }
        if (this.rawData instanceof byte[] bytes) {
            return HexUtil.encodeHexStr(bytes, false);
        }
        return null;
    }

    /**
     * 获取字符串数据
     *
     * @return 十字符串数据
     */
    public String getStringData() {
        if (this.rawData == null) {
            return "";
        }
        if (this.rawData instanceof String str) {
            return str;
        }
        if (this.rawData instanceof byte[] bytes) {
            return new String(bytes);
        }
        return null;
    }

    /**
     * 显示数据
     */
    public void showData() {
        switch (this.showType) {
            case 0 -> this.showRawData();
            case 1 -> this.showJsonData();
            case 2 -> this.showBinaryData();
            case 3 -> this.showHexData();
            case 4 -> this.showStringData();
        }
    }

    /**
     * 显示原始数据
     */
    public void showRawData() {
        this.checkDataTooBig(this.rawData);
        if (this.rawData instanceof String s) {
            this.setText(s);
        } else if (this.rawData instanceof byte[] bytes) {
            this.setText(StringUtil.toBinary(bytes));
        }
        this.showType = 0;
    }

    /**
     * 显示json数据
     */
    public void showJsonData() {
        String jsonData = this.getJsonData();
        this.checkDataTooBig(jsonData);
        this.setText(jsonData);
        this.showType = 1;
    }

    /**
     * 显示二进制数据
     */
    public void showBinaryData() {
        String binaryData = this.getBinaryData();
        this.checkDataTooBig(binaryData);
        this.setText(binaryData);
        this.showType = 2;
    }

    /**
     * 显示十六进制数据
     */
    public void showHexData() {
        String hexData = this.getHexData();
        this.checkDataTooBig(hexData);
        this.setText(hexData);
        this.showType = 3;
    }

    /**
     * 显示字符串数据
     */
    public void showStringData() {
        String stringData = this.getStringData();
        this.checkDataTooBig(stringData);
        this.setText(stringData);
        this.showType = 4;
    }

    /**
     * 检查数据是否太大
     *
     * @param data 数据
     */
    protected void checkDataTooBig(Object data) {
        if (data != null) {
            if (data instanceof String string && string.length() > 1024 * 1024) {
                throw new DataTooBigException();
            }
            if (data instanceof byte[] bytes && bytes.length > 1024 * 1024) {
                throw new DataTooBigException();
            }
        }
    }
}
