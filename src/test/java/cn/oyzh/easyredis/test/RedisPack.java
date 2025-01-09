package cn.oyzh.easyredis.test;

import cn.oyzh.common.util.OSUtil;
import cn.oyzh.fx.pkg.Packer;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2023/3/8
 */
public class RedisPack {

    private String getProjectPath() {
        String projectPath = getClass().getResource("").getPath();
        if (OSUtil.isWindows()) {
            projectPath = projectPath.substring(1, projectPath.indexOf("/target/"));
        } else {
            projectPath = projectPath.substring(0, projectPath.indexOf("/target/"));
        }
        return projectPath;
    }

    private String getPackagePath() {
        return this.getProjectPath() + "/package/";
    }

    @Test
    public void easyredis_win_amd64_pack() throws Exception {
        String packagePath = this.getPackagePath();
        String win_pack_config = packagePath + "win_amd64_pack_config.json";

        Packer packer = new Packer();
        packer.registerProjectHandler();
        packer.registerJdepsHandler();
        packer.pack(win_pack_config);
    }

    @Test
    public void easyredis_linux_amd64_pack() throws Exception {
        String packagePath = this.getPackagePath();
        String linux_pack_config = packagePath + "linux_amd64_pack_config.json";

        Packer packer = new Packer();
        packer.registerProjectHandler();
        packer.pack(linux_pack_config);
    }

    @Test
    public void easyredis_linux_arm64_pack() throws Exception {
        String packagePath = this.getPackagePath();
        String linux_pack_config = packagePath + "linux_arm64_pack_config.json";

        Packer packer = new Packer();
        packer.registerProjectHandler();
        packer.pack(linux_pack_config);
    }

    @Test
    public void easyredis_macos_amd64_pack() throws Exception {
        String packagePath = this.getPackagePath();
        String macos_pack_config = packagePath + "macos_amd64_pack_config.json";

        Packer packer = new Packer();
        packer.registerProjectHandler();
        packer.pack(macos_pack_config);
    }

    @Test
    public void easyredis_macos_arm64_pack() throws Exception {
        String packagePath = this.getPackagePath();
        String win_pack_config = packagePath + "macos_arm64_pack_config.json";
        String getProjectPath = this.getProjectPath();
        Map<String, Object> properties = new HashMap<>();
        properties.put("projectPath", getProjectPath);

        Packer packer = new Packer();
        packer.registerProjectHandler();
        packer.registerJdepsHandler();
        packer.pack(win_pack_config, properties);
    }

    @Test
    public void easyredis_all_pack() throws Exception {
        String packagePath = this.getPackagePath();
        String win_amd64_pack_config = packagePath + "win_amd64_pack_config.json";
        String linux_amd64_pack_config = packagePath + "linux_amd64_pack_config.json";
        String linux_arm64_pack_config = packagePath + "linux_arm64_pack_config.json";
        String macos_amd64_pack_config = packagePath + "macos_amd64_pack_config.json";

        String baseDir = "D:\\Workspaces\\OYZH\\fx-base\\";
        String projectDir = "D:\\Workspaces\\OYZH\\easyredis\\";

        Packer packer = new Packer();
        packer.registerProjectHandler();
        packer.registerMvnHandler(projectDir, List.of(baseDir));
        packer.pack(win_amd64_pack_config);
        packer.pack(linux_amd64_pack_config);
        packer.pack(linux_arm64_pack_config);
        packer.pack(macos_amd64_pack_config);
    }

}
