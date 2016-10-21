package payment;

import redis.clients.jedis.Jedis;

public class RedisTest {
	public static void main(String[] args) {
		Jedis jedis = new Jedis("localhost");
		System.out.println(jedis.ping());
		jedis.set("name", "牟奇");
		System.out.println("name :" + jedis.get("name"));
		jedis.close();
	}
}
