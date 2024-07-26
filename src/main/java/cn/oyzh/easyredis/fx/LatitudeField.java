package cn.oyzh.easyredis.fx;


import cn.oyzh.fx.plus.controls.textfield.DecimalTextField;
import cn.oyzh.fx.plus.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2023/7/5
 */
public class LatitudeField extends DecimalTextField {

    {
        this.setRequire(true);
        this.setMax(85.05112878);
        this.setMin(-85.05112878);
        this.setTipText(I18nHelper.latitude());
    }
}
