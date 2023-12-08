# Maven打包
mvn -X clean install package -DskipTests

###### Maven打包注意
检查cmd里面java -version的版本号和项目版本号是否一致，否则可能出现无效的目标版本号21之类的问题

# 程序打包
注意:  
jre目录是自行裁剪jdk的，把需要的模块裁剪即可，java.scripting,java.desktop,java.naming,java.security.jgss,java.management等模块是桌面程序必须的  
runtime目录是自行建立的，把需要的资源和类库放到此目录即可

###### 定制JRE(windows)
jlink --verbose --compress=2 --no-header-files --no-man-pages --strip-debug --strip-java-debug-attributes --vm=server --add-modules java.xml,java.logging,java.base,java.sql,jdk.unsupported,java.scripting,java.desktop,java.naming,java.security.jgss,java.management --output D:\Package\EasyRedis\jre

###### 定制JRE(macos)
jlink --verbose --compress=2 --no-header-files --no-man-pages --strip-debug --strip-java-debug-attributes --vm=server --add-modules java.xml,java.logging,java.base,java.sql,jdk.unsupported,java.scripting,java.desktop,java.naming,java.security.jgss,java.management --output /Users/oyzh/Desktop/Package/EasyRedis/jre

###### 定制JRE(linux)
jlink --verbose --compress=2 --no-header-files --no-man-pages --strip-debug --strip-java-debug-attributes --vm=server --add-modules java.xml,java.logging,java.base,java.sql,jdk.unsupported,java.scripting,java.desktop,java.naming,java.security.jgss,java.management --output /home/oyzh/Desktop/Package/EasyRedis/jre

###### docker启动redis(单个)
docker run -itd -p 6379:6379 redis

###### docker启动redis(集群)
docker-compose -f .\redis-cluster-compose.yml up -d

###### mac无法启动解决方案1
sudo chmod +x EasyRedis.app

###### mac无法启动解决方案2
chmod -R 755 /路径/EasyRedis.app(可拖入命令行窗口)

###### jvm参数
-Dprism.verbose=true -Xmx512m -Xms128m -XX:NewRatio=2 -XX:MinHeapFreeRatio=8 -XX:MaxHeapFreeRatio=20
