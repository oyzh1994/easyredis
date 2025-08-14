package cn.oyzh.easyredis.fx;

import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.store.RedisSettingStore;
import cn.oyzh.fx.editor.rsyntaxtextarea.EditorPane;
import cn.oyzh.fx.plus.font.FontManager;
import javafx.scene.text.Font;

/**
 * redis数据文本域
 *
 * @author oyzh
 * @since 2023/07/28
 */
public class RedisDataEditorPane extends EditorPane {
    //
    // @Override
    // public void initNode() {
    //     RichDataTextArea textArea = super.getContent();
    //     // 200k
    //     textArea.setStyleBound(RichDataType.HEX, 200 * 1024);
    //     // 500k
    //     textArea.setStyleBound(RichDataType.JSON, 500 * 1024);
    //     // 100k
    //     textArea.setStyleBound(RichDataType.BINARY, 100 * 1024);
    //     super.initNode();
    // }

//     @Override
//     protected Font initFont() {
// //        // 禁用字体管理
// //        super.disableFont();
//         // 初始化字体
//         RedisSetting setting = RedisSettingStore.SETTING;
//         return FontManager.toFont(setting.editorFontConfig());
//     }

    @Override
    public void changeFont(Font font) {
        // 初始化字体
        RedisSetting setting = RedisSettingStore.SETTING;
        Font font1 = FontManager.toFont(setting.editorFontConfig());
        super.changeFont(font1);
    }
}
