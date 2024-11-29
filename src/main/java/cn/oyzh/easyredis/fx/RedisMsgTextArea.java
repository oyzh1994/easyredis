package cn.oyzh.easyredis.fx;

import cn.oyzh.common.Const;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.event.EventListener;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.plus.controls.textarea.MsgTextArea;

/**
 * @author oyzh
 * @since 2024/3/29
 */
public class RedisMsgTextArea extends MsgTextArea implements EventListener {

    @EventSubscribe
    private void onEventMsg(EventFormatter formatter) {
        String formatMsg = formatter.eventFormat();
        if (formatMsg != null) {
            this.appendLine(String.format("%s %s", Const.DATE_TIME_FORMAT.format(System.currentTimeMillis()), formatMsg));
        }
    }
}
