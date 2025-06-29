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

    /* antes de seguir com a chamada à API, temos que verificar se 2 limites não foram atingidos:
          - Limite de 10 requests/hora por IP de usuário;
          - Limite de 1000 chamadas globais por dia;
       O registro desses limites ficarão gravados no servidor Redis e serão atualizados com auxílio da função RedisCacheService.incrementCounter() */
    public Long incrementCounter(String key, int expireSeconds) {
        try(Jedis jedis = jedisPool.getResource()) {
            Long count = jedis.incr(key);  // incrementa 1 no valor associado à chave fornecida como parametro. Se essa chave nao existir: cria a chave com o valor 0 e incrementa 1.
            if(count == 1) {    // Quer dizer: se a chave nao exisia e foi criada na linha acima. Isso indica que é o primeiro request à API VCrossing em determinado período...
                jedis.expire(key, expireSeconds); // a partir de agora as request estão sendo contadas (10 requests por hora para cada IP / 1000 requests por dia global)
            }
            return count;
        }
    }

    public void close() {
        jedisPool.close();
    }
}
