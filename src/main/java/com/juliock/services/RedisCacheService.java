package com.juliock.services;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisCacheService {

    // JedisPool é um pool (um "depósito", "reserva") de conexões para o servidor Redis.
    // Em vez de abrir e fechar uma conexão TCP para cada operação (o que é caro e lento), o pool mantém conexões abertas e as empresta para o código.
    private final JedisPool jedisPool;

    /* Oque o JedisPool.getResource() faz:

        - Ele te entrega uma conexão Jedis já aberta, vinda do pool.
        - Se há conexões ociosas no pool, ele pega uma pronta.
        - Se não há ociosas mas ainda cabe no limite do pool, ele abre uma nova.
        - Se o pool está cheio, ele espera ou lança exceção (dependendo das configurações).
    * */

    public RedisCacheService(String redisHost, int redisPort) {
        jedisPool = new JedisPool(redisHost, redisPort);
    }

    /*  SETEX() cria um registro (chave:valor) no redis, juntamente com o tempo de vida desse registro em segundos (ttl).
    * */
    public void set(String key, String value, int ttlSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, ttlSeconds, value);
        }
    }

    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public void close() {
        jedisPool.close();
    }
}
