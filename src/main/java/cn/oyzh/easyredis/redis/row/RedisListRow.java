package cn.oyzh.easyredis.redis.row;

import cn.oyzh.easyredis.redis.RedisRow;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;

/**
 * @author oyzh
 * @since 2023/6/16
 */
public class RedisListRow extends RedisRow {

    /**
     * 行号
     */
    @Getter
    private int lineIndex;

    /**
     * 值
     */
    private SimpleStringProperty valueProperty;

    public RedisListRow(int lineIndex, String value) {
        this.lineIndex = lineIndex;
        this.setValue(value);
    }

    public SimpleStringProperty valueProperty() {
        if (this.valueProperty == null) {
            this.valueProperty = new SimpleStringProperty();
        }
        return valueProperty;
    }

    public void setValue(String value) {
        this.valueProperty().setValue(value);
    }

    public String getValue() {
        return this.valueProperty == null ? null : this.valueProperty.get();
    }
}
