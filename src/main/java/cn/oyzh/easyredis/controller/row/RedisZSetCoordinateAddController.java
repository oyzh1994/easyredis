package cn.oyzh.easyredis.controller.row;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.digital.DecimalTextField;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;


/**
 * redis添加set成员
 *
 * @author oyzh
 * @since 2023/06/27
 */
@StageAttribute(
        title = "添加地理坐标",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        value = RedisConst.FXML_BASE_PATH + "row/redisZSetCoordinateAdd.fxml"
)
public class RedisZSetCoordinateAddController extends Controller {

    /**
     * 坐标名称
     */
    @FXML
    private FlexTextArea rowValue;

    /**
     * 经度
     */
    @FXML
    private DecimalTextField longitude;

    /**
     * 纬度
     */
    @FXML
    private DecimalTextField latitude;

    /**
     * redis键
     */
    private RedisZSetKeyTreeItem treeItem;

    /**
     * 添加行
     */
    @FXML
    private void addRow() {
        try {
            // 行数据
            String rowValue = this.rowValue.getText();
            if (StrUtil.isEmpty(rowValue)) {
                MessageBox.tipMsg("坐标不能为空", this.rowValue);
                return;
            }
            Number longitudeValue = this.longitude.getValue();
            if (longitudeValue == null) {
                MessageBox.tipMsg("经度不能为空", this.latitude);
                return;
            }
            Number latitudeValue = this.latitude.getValue();
            if (latitudeValue == null) {
                MessageBox.tipMsg("纬度不能为空", this.latitude);
                return;
            }
            // redis键
            String key = this.treeItem.key();
            // 获取键值
            int dbIndex = this.treeItem.dbIndex();
            // redis客户端
            RedisClient client = this.treeItem.client();
            if (client.zrank(dbIndex, key, rowValue) != null) {
                MessageBox.warn("此坐标已存在！");
                return;
            }
            double longitude = longitudeValue.doubleValue();
            double latitude = latitudeValue.doubleValue();
            // 添加元素
            client.geoadd(dbIndex, key, longitude, latitude, rowValue);
            // 发送事件
            // EventUtil.fire(RedisEventTypes.REDIS_GEO_COORDINATE_ADDED, this.treeItem);
            RedisEventUtil.zSetCoordinateAdded(this.treeItem, key, rowValue, longitude, latitude);
            // MessageBox.okToast("新增坐标成功！");
            this.closeStage();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 粘贴数据
     */
    @FXML
    private void pasteData() {
        this.rowValue.paste();
        this.rowValue.requestFocus();
    }

    /**
     * 清空数据
     */
    @FXML
    private void clearData() {
        this.rowValue.clear();
        this.rowValue.requestFocus();
    }

    @Override
    public void onStageShown(WindowEvent event) {
        this.treeItem = this.getStageProp("treeItem");
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
        super.onStageShown(event);
    }
}
