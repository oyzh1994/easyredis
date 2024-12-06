package cn.oyzh.easyredis.trees.keys;

import cn.oyzh.common.util.ArrayUtil;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.redis.key.RedisZSetValue;
import cn.oyzh.easyredis.util.RedisVersionUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.NonNull;
import redis.clients.jedis.GeoCoordinate;

import java.util.List;
import java.util.Objects;

/**
 * @author oyzh
 * @since 2023/06/30
 */
public class RedisZSetKeyTreeItem extends RedisRowKeyTreeItem<RedisZSetValue.RedisZSetRow> {

    @Override
    public RedisZSetValue.RedisZSetRow currentRow() {
        return super.currentRow();
    }

    // /**
    //  * 分数属性
    //  */
    // private DigitalDecimalProperty scoreProperty;
    //
    // /**
    //  * 经度属性
    //  */
    // private DigitalDecimalProperty latitudeProperty;
    //
    // /**
    //  * 纬度属性
    //  */
    // private DigitalDecimalProperty longitudeProperty;

    // @Override
    // public void clearData() {
    //     this.score(null);
    //     this.latitude(null);
    //     this.longitude(null);
    //     super.clearData();
    // }

    @Override
    public RedisZSetValue.RedisZSetRow data() {
        return (RedisZSetValue.RedisZSetRow) super.data();
    }

    @Override
    public void data(Object data) {
        if (data instanceof RedisZSetValue.RedisZSetRow row) {
            super.data(row.clone());
        } else {
            super.clearData();
        }
    }

    // @Override
    // public boolean isDataUnsaved() {
    //     if (this.isGEOView()) {
    //         return this.longitude() != null || this.latitude() != null || super.isDataUnsaved();
    //     }
    //     return this.score() != null || super.isDataUnsaved();
    // }

    // /**
    //  * 获取分数属性
    //  *
    //  * @return 分数属性
    //  */
    // public DigitalDecimalProperty scoreProperty() {
    //     if (this.scoreProperty == null) {
    //         this.scoreProperty = new DigitalDecimalProperty(5);
    //     }
    //     return this.scoreProperty;
    // }
    //
    // /**
    //  * 获取纬度属性
    //  *
    //  * @return 纬度属性
    //  */
    // public DigitalDecimalProperty latitudeProperty() {
    //     if (this.latitudeProperty == null) {
    //         this.latitudeProperty = new DigitalDecimalProperty(5);
    //     }
    //     return this.latitudeProperty;
    // }
    //
    // /**
    //  * 获取经度属性
    //  *
    //  * @return 经度属性
    //  */
    // public DigitalDecimalProperty longitudeProperty() {
    //     if (this.longitudeProperty == null) {
    //         this.longitudeProperty = new DigitalDecimalProperty(5);
    //     }
    //     return this.longitudeProperty;
    // }

    /**
     * 获取分数
     *
     * @return 分数
     */
    public Double score() {
        // return this.scoreProperty == null ? null : this.scoreProperty().getDouble();
        if (this.data() == null) {
            return null;
        }
        return this.data().getScore();
    }

    /**
     * 设置分数
     *
     * @param score 分数
     */
    public void score(Double score) {
        // this.scoreProperty().setValue(score);
        if (this.data() != null) {
            this.data().setScore(score);
        }
    }

    /**
     * 获取纬度
     *
     * @return 纬度
     */
    public Double latitude() {
        // return this.latitudeProperty == null ? null : this.latitudeProperty().getDouble();
        if (this.data() == null) {
            return null;
        }
        return this.data().getLatitude();
    }

    /**
     * 设置纬度
     *
     * @param latitude 纬度
     */
    public void latitude(Double latitude) {
        // this.latitudeProperty().setValue(latitude);
        if (this.data() != null) {
            this.data().setLatitude(latitude);
        }
    }

    /**
     * 获取经度
     *
     * @return 经度
     */
    public Double longitude() {
        // return this.longitudeProperty == null ? null : this.longitudeProperty().getDouble();
        if (this.data() == null) {
            return null;
        }
        return this.data().getLongitude();
    }

    /**
     * 设置经度
     *
     * @param longitude 经度
     */
    public void longitude(Double longitude) {
        // this.longitudeProperty().setValue(longitude);
        if (this.data() != null) {
            this.data().setLongitude(longitude);
        }
    }

    /**
     * 显示类型 0: 有序集合 1: 地理坐标
     */
    private byte showType;

    @Override
    public RedisZSetKeyTreeItem currentRow(RedisZSetValue.RedisZSetRow currentRow) {
        this.currentRow = currentRow;
        this.clearData();
        return this;
    }

    public RedisZSetKeyTreeItem(@NonNull RedisKey value, @NonNull RedisKeysTreeView treeView) {
        super(value, treeView);
        // this.setValue(new RedisKeyTreeItemValue(this));
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
    public boolean saveKeyValue() {
        RedisZSetValue.RedisZSetRow value = this.data();
        // if (value == null) {
        //     value = this.currentRow.getValue();
        // }
        try {
            this.setKeyValue(value);
            this.currentRow.setValue(value.getValue());
            if (this.isGEOView()) {
                // if (this.latitude() != null) {
                this.currentRow.setLatitude(value.getLatitude());
                // }
                // if (this.longitude() != null) {
                this.currentRow.setLongitude(value.getLongitude());
                // }
            } else {
                // if (this.score() != null) {
                this.currentRow.setScore(value.getScore());
                // }
            }
            // 清除旧数据
            this.clearData();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return false;
    }

    @Override
    protected void setKeyValue(Object value) {
        if (value instanceof RedisZSetValue.RedisZSetRow row) {
            try {
                String rowValue = row.getValue();
                double rowScore = row.getScore();
                double rowLatitude = row.getLatitude();
                double rowLongitude = row.getLongitude();
                // 删除旧成员
                if (!Objects.equals(rowValue, this.currentRow.getValue())) {
                    this.client().zrem(this.dbIndex(), this.key(), this.currentRow.getValue());
                }
                // 更新坐标
                if (this.isGEOView()) {
                    // double latitude = this.latitude() != null ? this.latitude() : this.currentRow.getLatitude();
                    // double longitude = this.longitude() != null ? this.longitude() : this.currentRow.getLongitude();
                    this.client().geoadd(this.dbIndex(), this.key(), rowLongitude, rowLatitude, rowValue);
                } else {// 更新成员
                    // double score = this.score() != null ? this.score() : this.currentRow.getScore();
                    this.client().zadd(this.dbIndex(), this.key(), rowScore, rowValue);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            }
        }
    }

    @Override
    public boolean deleteRow() {
        try {
            long count = this.client().zrem(this.dbIndex(), this.key(), this.currentRow.getValue());
            if (count > 0) {
                this.rows().remove(this.currentRow);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return false;
    }

    @Override
    public void refreshKeyValue() {
        try {
            List<String> value = this.client().zrange(this.dbIndex(), this.key());
            if (this.isGEOView()) {
                List<GeoCoordinate> coordinates = this.client().geopos(this.dbIndex(), this.key(), ArrayUtil.toArray(value, String.class));
                this.value.valueOfCoordinates(value, coordinates);
            } else {
                List<Double> scores = this.client().zmscore_ext(this.dbIndex(), this.key(), ArrayUtil.toArray(value, String.class));
                this.value.valueOfZSet(value, scores);
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
    public boolean checkRowExists() {
        RedisZSetValue.RedisZSetRow row = this.data();
        String rowValue = row.getValue();
        if (this.isDataUnsaved() && !Objects.equals(this.currentRow.getValue(), rowValue)) {
            Long zrank = this.client().zrank(this.dbIndex(), this.key(), rowValue);
            return zrank != null;
        }
        return false;
    }

}
