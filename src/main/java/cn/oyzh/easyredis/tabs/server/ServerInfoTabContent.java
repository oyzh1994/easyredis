package cn.oyzh.easyredis.tabs.server;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.info.RedisInfoProp;
import cn.oyzh.easyredis.info.RedisInfoPropItem;
import cn.oyzh.fx.common.spring.ScopeType;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.fx.plus.util.TableViewUtil;
import com.alibaba.fastjson.JSONObject;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * redis服务信息tab内容组件
 *
 * @author oyzh
 * @since 2023/08/01
 */
@Slf4j
@Lazy
@Component
@Scope(ScopeType.PROTOTYPE)
public class ServerInfoTabContent {

    /**
     * tab面板
     */
    @FXML
    private FlexTabPane tabPane;

    /**
     * 执行初始化
     *
     * @param propProperty 属性对象
     */
    public void init(SimpleObjectProperty<RedisInfoProp> propProperty) {
        propProperty.addListener((observable, oldValue, newValue) -> this.initPropPane(newValue));
    }

    /**
     * 初始化属性面板
     */
    private void initPropPane(RedisInfoProp prop) {
        List<String> groups = prop.groups().stream().sorted().toList();
        for (String group : groups) {
            this.initPropTab(prop, group);
        }
    }

    /**
     * 初始化属性tab
     *
     * @param group 属性组
     */
    private void initPropTab(RedisInfoProp prop, String group) {
        JSONObject object = prop.getProps(group);
        if (object == null || object.isEmpty()) {
            return;
        }
        Optional<Tab> tabOptional = this.tabPane.getTabs().stream().filter(t -> StrUtil.equals(t.getId(), "prop-" + group)).findFirst();
        FlexTableView<RedisInfoPropItem> tableView;
        if (tabOptional.isEmpty()) {
            FXTab fxTab = new FXTab();
            fxTab.setText(group);
            fxTab.setId("prop-" + group);
            tableView = new FlexTableView<>();
            tableView.setFlexWidth("100%");
            tableView.setFlexHeight("100%");

            FlexTableColumn<RedisInfoPropItem, String> name = new FlexTableColumn<>();
            name.setText("属性名");
            name.setFlexWidth("50%");
            name.setCellValueFactory(new PropertyValueFactory<>("name"));

            FlexTableColumn<RedisInfoPropItem, String> value = new FlexTableColumn<>();
            value.setText("属性值");
            value.setFlexWidth("50% - 10");
            value.setCellValueFactory(new PropertyValueFactory<>("value"));

            tableView.getColumns().add(name);
            tableView.getColumns().add(value);

            // 双击时，复制列数据
            TableViewUtil.copyCellOnDoubleClicked(tableView);

            fxTab.setContent(tableView);
            this.tabPane.addTab(fxTab);
        } else {
            tableView = (FlexTableView<RedisInfoPropItem>) tabOptional.get().getContent();
        }
        for (String key : object.keySet()) {
            this.initPropItem(tableView, key, object.getString(key));
        }
    }

    /**
     * 初始化属性内容
     *
     * @param tableView 表单组件
     * @param name      名称
     * @param value     值
     */
    private void initPropItem(TableView<RedisInfoPropItem> tableView, String name, String value) {
        ObservableList<RedisInfoPropItem> items = tableView.getItems();
        Optional<RedisInfoPropItem> optional = items.parallelStream().filter(i -> StrUtil.equals(i.getName(), name)).findFirst();
        if (optional.isEmpty()) {
            tableView.getItems().add(new RedisInfoPropItem(name, value));
        } else if (!StrUtil.equals(optional.get().getValue(), value)) {
            optional.get().setValue(value);
        }
    }
}
