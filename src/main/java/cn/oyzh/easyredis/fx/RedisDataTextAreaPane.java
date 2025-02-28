package cn.oyzh.easyredis.fx;

import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.store.RedisSettingStore;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextArea;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;

/**
 * redis数据文本域
 *
 * @author oyzh
 * @since 2023/07/28
 */
public class RedisDataTextAreaPane extends RichDataTextAreaPane {

    @Override
    protected void initTextArea() {
        RichDataTextArea textArea = super.getContent();
        // 200k
        textArea.setStyleBound(RichDataType.HEX, 200 * 1024);
        // 500k
        textArea.setStyleBound(RichDataType.JSON, 500 * 1024);
        // 100k
        textArea.setStyleBound(RichDataType.BINARY, 100 * 1024);
        super.initTextArea();
    }

    @Override
    protected void initFont() {
        // 禁用字体管理
        super.disableFont();
        // 初始化字体
        RedisSetting setting = RedisSettingStore.SETTING;
        this.setFontSize(setting.getEditorFontSize());
        this.setFontFamily(setting.getEditorFontFamily());
        this.setFontWeight2(setting.getEditorFontWeight());
    }
}
