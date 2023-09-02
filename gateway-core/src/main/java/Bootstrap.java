


import com.sun.xml.internal.ws.api.server.ServiceDefinition;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.ServiceLoader;


/**
 * API网关启动类
 */

@Slf4j
public class Bootstrap {

    public static void main(String[] args) {
        //加载网关核心静态配置


        //插件初始化
        //配置中心管理器初始化，连接配置中心，监听配置的新增、修改、删除


        //启动容器

        //连接注册中心，将注册中心的实例加载到本地


        //服务优雅关机
        //收到kill信号时调用

    }


}
