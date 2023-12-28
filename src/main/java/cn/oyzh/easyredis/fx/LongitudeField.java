package cn.oyzh.easyredis.fx;


import cn.oyzh.fx.plus.controls.digital.DecimalTextField;

/**
 * @author oyzh
 * @since 2023/7/5
 */
public class LongitudeField extends DecimalTextField {

    {
        this.setMax(180D);
        this.setMin(-180D);
        this.setRequire(true);
    }
}
