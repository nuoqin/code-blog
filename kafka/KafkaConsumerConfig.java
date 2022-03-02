package com.caas.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Author nuoqin
 * @DATA 2022/3/2 10:25
 */
@Slf4j
@Configuration
public class KafkaConsumerConfig {


    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String,String>> kafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, String>
                factory = new ConcurrentKafkaListenerContainerFactory<>();
        //设置消费工厂
        factory.setConsumerFactory(consumerFactory());

        //消费线程数量
        factory.setConcurrency(5);

        //拉取超时时间
        factory.getContainerProperties()
                .setPollTimeout(3000);

        // 当使用批量监听器时需要设置为true
        factory.setBatchListener(false);
        //设置ACK模式(手动提交模式，这里有七种)
        /**
         * @TODO org.springframework.kafka.listener.ContainerProperties.AckMode
         * RECORD： 每处理完一条记录后提交。
         * BATCH(默认)： 每次poll一批数据后提交一次，频率取决于每次poll的调用频率。
         * TIME： 每次间隔ackTime的时间提交。
         * COUNT： 处理完poll的一批数据后并且距离上次提交处理的记录数超过了设置的ackCount就提交。
         * COUNT_TIME： TIME和COUNT中任意一条满足即提交。
         * MANUAL： 手动调用Acknowledgment.acknowledge()后，并且处理完poll的这批数据后提交。
         * MANUAL_IMMEDIATE： 手动调用Acknowledgment.acknowledge()后立即提交。
         *
         */
        factory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }

    @Bean
    public ConsumerFactory<String,String> consumerFactory() {

        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {

        HashMap<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.1.180:9092");

        props.put(ConsumerConfig.GROUP_ID_CONFIG,"message-group");

        // Session超时设置
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        // 键的反序列化方式
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // 值的反序列化方式
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);



        return props;
    }


    /**
     * 异常处理器
    @Bean
    public ConsumerAwareListenerErrorHandler listenErrorHandler() {
        return (message, e, consumer) -> {
            MessageHeaders headers = message.getHeaders();
            String topics = headers.get(KafkaHeaders.RECEIVED_TOPIC, String.class);
            Integer partitions = headers.get(KafkaHeaders.RECEIVED_PARTITION_ID, Integer.class);
            Long offsets = headers.get(KafkaHeaders.OFFSET, Long.class);
            log.info("consumerAwareErrorHandler receive : "+message.getPayload().toString());
            log.info("consumerAwareErrorHandler receive : topics {},partitions {}, offsets {}",topics,partitions,offsets);
            return null;
        };
    }*/
}
