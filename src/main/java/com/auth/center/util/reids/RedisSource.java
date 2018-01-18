package com.auth.center.util.reids;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis原信息配置
 *
 * @author zhnaghongjie
 */
public class RedisSource {

    private static JedisPool jedisPool;

    //初始化redis连接池
    static {
        JedisPoolConfig config = new JedisPoolConfig();
        //配置最大jedis实例数
        config.setMaxTotal(1000);
        //配置资源池最大闲置数
        config.setMaxIdle(5);
        //等待可用连接的最大时间
        config.setMaxWaitMillis(1000);
        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的
        config.setTestOnBorrow(true);
        jedisPool = new JedisPool("10.133.17.79", 6379);

    }

    /**
     * 获取连接
     *
     * @return
     */
    public static Jedis getConnection() {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jedis;
    }

    /**
     * 关闭连接
     *
     * @param jedis
     */

    public static void closeConnection(Jedis jedis) {

        if (null != jedis) {
            try {
                jedisPool.returnResource(jedis);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void destoryConnection(Jedis jedis) {

        if (null != jedis) {
            try {
                jedisPool.returnBrokenResource(jedis);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置连接池
     *
     * @param JedisPool 数据源
     */
    public static void setJedisPool(JedisPool JedisPool) {

        jedisPool = JedisPool;
    }

    /**
     * 获取连接池
     *
     * @return 数据源
     */
    public static JedisPool getJedisPool() {

        return jedisPool;
    }

}
