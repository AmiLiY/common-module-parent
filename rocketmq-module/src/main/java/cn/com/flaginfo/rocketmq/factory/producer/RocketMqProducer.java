package cn.com.flaginfo.rocketmq.factory.producer;

import cn.com.flaginfo.rocketmq.domain.SendResultDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

/**
 * @author: Meng.Liu
 * @date: 2018/11/22 下午12:01
 */
@Slf4j
public class RocketMqProducer extends RocketMqTemplate {

    private DefaultMQProducer producer;

    public RocketMqProducer(DefaultMQProducer producer) {
        this.producer = producer;
    }

    @Override
    public SendResultDO sendMessage(String topicName, String body) {
        return sendMessage(topicName, "", null, body);
    }

    @Override
    public SendResultDO sendMessage(final String topicName, final String tags, final String keys, String body) {
        if (body == null) {
            body = "";
        }

        Message message = new Message();
        message.setTopic(topicName);
        message.setBody(body.getBytes(StandardCharsets.UTF_8));
        message.setKeys(keys);
        message.setTags(tags);

        return sendMessage(message);
    }

    private SendResultDO sendMessage(Message message) {
        SendResultDO response = new SendResultDO();

        long start = System.currentTimeMillis();
        SendResult sendResult;
        try {
            sendResult = producer.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
            response.setSuccess(true);
            response.setMessageId(sendResult.getMsgId());
        }
        if( log.isDebugEnabled() ){
            log.debug("rocket mq send result:{}", sendResult);
        }
        log.info("mq send success {}, takes:{}ms", message.getTopic(), System.currentTimeMillis() - start);
        return response;
    }

}
