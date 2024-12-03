package cn.oyzh.easyredis.trees.zset;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.key.RedisZSetKey;
import cn.oyzh.easyredis.redis.row.RedisZSetRow;
import cn.oyzh.easyredis.trees.RedisRowKeyTreeItem;
import cn.oyzh.easyredis.trees.connect.RedisDBTreeItem;
import cn.oyzh.easyredis.util.RedisVersionUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.property.DigitalDecimalProperty;
import lombok.NonNull;
import redis.clients.jedis.GeoCoordinate;

import java.util.List;
import java.util.Objects;

/**
 * @author oyzh
 * @since 2023/06/30
 */
public class RedisZSetKeyTreeItem extends RedisRowKeyTreeItem<RedisZSetKey, RedisZSetKeyTreeItemValue, RedisZSetRow> {

    /**
     * 分数属性
     */
    private DigitalDecimalProperty scoreProperty;

    /**
     * 经度属性
     */
    private DigitalDecimalProperty latitudeProperty;

    /**
     * 纬度属性
     */
    private DigitalDecimalProperty longitudeProperty;

    @Override
    public void clearData() {
        this.score(null);
        this.latitude(null);
        this.longitude(null);
        super.clearData();
    }

    @Override
    public boolean dataUnsaved() {
        if (this.isGEOView()) {
            return this.longitude() != null || this.latitude() != null || super.dataUnsaved();
        }
        return this.score() != null || super.dataUnsaved();
    }

    /**
     * 获取分数属性
     *
     * @return 分数属性
     */
    public DigitalDecimalProperty scoreProperty() {
        if (this.scoreProperty == null) {
            this.scoreProperty = new DigitalDecimalProperty(5);
        }
        return this.scoreProperty;
    }

    /**
     * 获取纬度属性
     *
     * @return 纬度属性
     */
    public DigitalDecimalProperty latitudeProperty() {
        if (this.latitudeProperty == null) {
            this.latitudeProperty = new DigitalDecimalProperty(5);
        }
        return this.latitudeProperty;
    }

    /**
     * 获取经度属性
     *
     * @return 经度属性
     */
    public DigitalDecimalProperty longitudeProperty() {
        if (this.longitudeProperty == null) {
            this.longitudeProperty = new DigitalDecimalProperty(5);
        }
        return this.longitudeProperty;
    }

    /**
     * 获取分数
     *
     * @return 分数
     */
    public Double score() {
        return this.scoreProperty == null ? null : this.scoreProperty().getDouble();
    }

    /**
     * 设置分数
     *
     * @param score 分数
     */
    public void score(Double score) {
        this.scoreProperty().setValue(score);
    }

    /**
     * 获取纬度
     *
     * @return 纬度
     */
    public Double latitude() {
        return this.latitudeProperty == null ? null : this.latitudeProperty().getDouble();
    }

    /**
     * 设置纬度
     *
     * @param latitude 纬度
     */
    public void latitude(Double latitude) {
        this.latitudeProperty().setValue(latitude);
    }

    /**
     * 获取经度
     *
     * @return 经度
     */
    public Double longitude() {
        return this.longitudeProperty == null ? null : this.longitudeProperty().getDouble();
    }

    /**
     * 设置经度
     *
     * @param longitude 经度
     */
    public void longitude(Double longitude) {
        this.longitudeProperty().setValue(longitude);
    }

    /**
     * 显示类型 0: 有序集合 1: 地理坐标
     */
    private byte showType;

    @Override
    public RedisZSetKeyTreeItem currentRow(RedisZSetRow currentRow) {
        this.currentRow = currentRow;
        this.clearData();
        return this;
    }

    public RedisZSetKeyTreeItem(@NonNull RedisZSetKey value, @NonNull RedisDBTreeItem parent) {
        super(value, parent);
        this.setValue(new RedisZSetKeyTreeItemValue(this));
    }

    /**
     * 反转显示类型
     */
    public void reverseView() {
        this.showType = (byte) (this.isGEOView() ? 0 : 1);
        RedisEventUtil.zSetReverseView(this);
    }

    /**
     * 是否地理坐标视图
     *
     * @return 结果
     */
    public boolean isGEOView() {
        return this.showType == 1;
    }

    /**
     * 是否支持地理坐标
     *
     * @return 结果
     */
    public boolean isSupportGEO() {
        return RedisVersionUtil.isCommandSupported(this.getServerVersion(), "geopos");
    }

    /**
     * 获取服务端版本号
     *
     * @return 服务端版本号
     */
    public String getServerVersion() {
        return this.client().getServerVersion();
    }

    @Override
    public boolean saveNodeValue() {
        String value = (String) this.data();
        if (value == null) {
            value = this.currentRow.getValue();
        }
        try {
            this.setNodeValue(value);
            this.currentRow.setValue(value);
            if (this.isGEOView()) {
                if (this.latitude() != null) {
                    this.currentRow.setLatitude(this.latitude());
                }
                if (this.longitude() != null) {
                    this.currentRow.setLongitude(this.longitude());
                }
            } else {
                if (this.score() != null) {
                    this.currentRow.setScore(this.score());
                }
            }
            this.clearData();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return false;
    }

    @Override
    protected void setNodeValue(Object value) {
        try {
            if (!Objects.equals(value, this.currentRow.getValue())) {
                this.client().zrem(this.dbIndex(), this.key(), this.currentRow.getValue());
            }
            if (this.isGEOView()) {
                double latitude = this.latitude() != null ? this.latitude() : this.currentRow.getLatitude();
                double longitude = this.longitude() != null ? this.longitude() : this.currentRow.getLongitude();
                this.client().geoadd(this.dbIndex(), this.key(), longitude, latitude, (String) value);
            } else {
                double score = this.score() != null ? this.score() : this.currentRow.getScore();
                this.client().zadd(this.dbIndex(), this.key(), score, (String) value);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    public boolean deleteRow() {
        try {
            long count = this.client().zrem(this.dbIndex(), this.key(), this.currentRow.getValue());
            if (count > 0) {
                this.nodeValue().remove(this.currentRow);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return false;
    }

    @Override
    public void refreshNodeValue() {
        try {
            List<String> value = this.client().zrange(this.dbIndex(), this.key());
            if (this.isGEOView()) {
                List<GeoCoordinate> coordinates = this.client().geopos(this.dbIndex(), this.key(), ArrayUtil.toArray(value, String.class));
                this.value.valueOfCoordinate(value, coordinates);
            } else {
                List<Double> scores = this.client().zmscore_ext(this.dbIndex(), this.key(), ArrayUtil.toArray(value, String.class));
                this.value.valueOfScore(value, scores);
            }
            this.clearData();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    public String rawValue() {
        if (this.currentRow != null) {
            return this.currentRow.getValue();
        }
        return null;
    }

    @Override
    public boolean checkExists() {
        String data = this.data() == null ? this.currentRow.getValue() : (String) this.data();
        if (this.dataUnsaved() && !Objects.equals(this.currentRow.getValue(), data)) {
            Long zrank = this.client().zrank(this.dbIndex(), this.key(), data);
            return zrank != null;
        }
        return false;
    }
}
