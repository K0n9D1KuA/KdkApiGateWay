package com.qyh.gateway.core.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Properties;


/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 这个类的作用是创建和管理一个连接到Redis数据库的连接池，并提供方法来获取和关闭Jedis实例。
 * - 该类定义了一个名为`JedisPool`的Java类。
 * - 静态字段`pool`是一个`redis.clients.jedis.JedisPool`类型的对象，表示一个全局的Redis连接池。
 * - 一个静态字段`dbNum`表示选择的数据库编号，默认为0。
 * - 在静态代码块中，通过加载`gateway.properties`文件并解析其中的配置信息，创建了一个`JedisPoolConfig`对象`conf`，用于配置连接池的参数。
 * - 从配置文件中读取Redis服务器的主机名、端口号、超时时间、最大空闲连接数、最大总连接数、Redis密码、最大等待时间、最小空闲连接数以及选择的数据库编号等配置信息。
 * - 使用配置对象`conf`、主机名、端口号、超时时间、Redis密码等参数创建一个`redis.clients.jedis.JedisPool`实例，并将其赋值给`pool`字段。
 * - `getJedis()`方法用于从连接池中获取一个Jedis实例，并选择指定的数据库（`jedis.select(dbNum)`）。
 * - `close()`方法用于关闭Jedis实例，并将其返回到连接池中。
 * <p>
 * 这个类的作用是通过读取配置文件初始化一个全局的Redis连接池，并提供了方法来获取和关闭Jedis实例，以便于对指定数据库的Redis进行操作。
 * @email 3161788646@qq.com
 * @date 2023/9/4 21:35
 */

public class JedisPool {

    private static final redis.clients.jedis.JedisPool pool;

    //选择哪个数据库
    private static int dbNum = 0;

    static { //静态代码块 保证只初始化一次
        Properties prop = new Properties();
        //加载文件获取数据 文件带后缀
        try {
            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("gateway.properties"));
            JedisPoolConfig conf = new JedisPoolConfig();

            String host = prop.getProperty("redis.host").toString();
            Integer port = Integer.parseInt(prop.getProperty("redis.port"));
            int timeout = Integer.parseInt(prop.getProperty("redis.timeout"));
            int maxTotal = Integer.parseInt(prop.getProperty("redis.maxTotal"));
            int maxIdle = Integer.parseInt(prop.getProperty("redis.maxIdle"));
            String password = prop.get("redis.password").toString();
            int maxWaitMIlls = Integer.parseInt(prop.getProperty("redis.maxWaitMIlls"));
            int minIdle = Integer.parseInt(prop.getProperty("redis.minIdle"));


            //选择哪个数据库
            dbNum = Integer.parseInt(prop.getProperty("redis.dbNum"));
            //设置连接池的最大空闲连接数为
            conf.setMaxIdle(maxIdle);
            //设置连接池的最大总连接数为
            conf.setMaxTotal(maxTotal);
            //设置连接池的最小空闲连接数为
            conf.setMinIdle(minIdle);
            //设置连接池在创建连接时是否进行测试（setTestOnCreate(true)）。
            conf.setTestOnCreate(true);
            //设置连接池在获取连接时是否进行测试（setTestOnBorrow(true)）。
            conf.setTestOnBorrow(true);
            //设置连接池在归还连接时是否进行测试（setTestOnReturn(true)）。
            conf.setTestOnReturn(true);
            //设置获取连接的最大等待时间为10000毫秒
            conf.setMaxWaitMillis(maxWaitMIlls);

            pool = new redis.clients.jedis.JedisPool(conf, host, port, timeout, password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Jedis getJedis() {
        Jedis jedis = pool.getResource();
        jedis.select(dbNum);
        return jedis;
    }

    public static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
