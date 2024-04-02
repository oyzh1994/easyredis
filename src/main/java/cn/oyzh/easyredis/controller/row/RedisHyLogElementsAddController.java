package cn.oyzh.easyredis.controller.row;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.string.RedisStringKeyTreeItem;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.util.List;
import java.util.stream.Collectors;


/**
 * redis添加set成员
 *
 * @author oyzh
 * @since 2023/06/27
 */
//@Slf4j
@StageAttribute(
        title = "添加hyperLogLog元素",
        iconUrls = RedisConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        // cssUrls = RedisStyle.COMMON,
        value = RedisConst.FXML_BASE_PATH + "row/redisHyperLogLogElementsAdd.fxml"
)
public class RedisHyLogElementsAddController extends Controller {

    /**
     * 行数据
     */
    @FXML
    private FlexTextArea rowValue;

    /**
     * redis键
     */
    private RedisStringKeyTreeItem treeItem;

    /**
     * 添加行
     */
    @FXML
    private void addRow() {
        try {
            // 行数据
            String rowValue = this.rowValue.getText();
            if (StrUtil.isEmpty(rowValue) || StrUtil.isBlank(rowValue)) {
                MessageBox.tipMsg("元素不能为空", this.rowValue);
                return;
            }
            List<String> elements = rowValue.lines().collect(Collectors.toList());
            elements = CollUtil.removeBlank(elements);
            if (elements.isEmpty()) {
                MessageBox.tipMsg("元素内容不能为空", this.rowValue);
                return;
            }
            // redis键
            String key = this.treeItem.key();
            // 获取键值
            int dbIndex = this.treeItem.dbIndex();
            // redis客户端
            RedisClient client = this.treeItem.client();
            String[] array = ArrayUtil.toArray(elements, String.class);
            if (client.pfadd(dbIndex, key, array) <= 0) {
                MessageBox.warn("新增元素失败或元素均已存在！");
                return;
            }
            // 发送事件
            RedisEventUtil.hyLogElementsAdded(this.treeItem, key, array);
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
