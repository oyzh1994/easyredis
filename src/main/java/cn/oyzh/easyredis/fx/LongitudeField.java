package cn.oyzh.easyredis.fx;


import cn.oyzh.fx.plus.controls.textfield.DecimalTextField;
import cn.oyzh.fx.plus.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2023/7/5
 */
public class LongitudeField extends DecimalTextField {

    {
        this.setMax(180D);
        this.setMin(-180D);
        this.setRequire(true);
        this.setTipText(I18nHelper.longitude());
    }
}
