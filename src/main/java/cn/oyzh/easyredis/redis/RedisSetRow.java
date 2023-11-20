package cn.oyzh.easyredis.redis;

import javafx.beans.property.SimpleStringProperty;

/**
 * redis set行
 *
 * @author oyzh
 * @since 2023/6/16
 */
public class RedisSetRow extends RedisRow {

    /**
     * 值
     */
    private SimpleStringProperty valueProperty;

    public RedisSetRow(String value) {
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
