package cn.com.flaginfo.rocketmq.factory.producer;

import cn.com.flaginfo.rocketmq.domain.SendResultDO;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author: Meng.Liu
 * @date: 2018/11/22 下午12:02
 */
@Slf4j
public class OnsMqProducer extends RocketMqTemplate {

    private Producer producer;

    public OnsMqProducer(Producer producer) {
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
        message.setKey(keys);
        message.setTag(tags);
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

        if (!StringUtils.isEmpty(sendResult.getMessageId())) {
            response.setSuccess(true);
            response.setMessageId(sendResult.getMessageId());
        }
        if( log.isDebugEnabled() ){
            log.debug("ons mq send result : {}", sendResult);
        }
        log.info("ons send success {}, takes:{}ms", message.getTopic(), System.currentTimeMillis() - start);
        return response;
    }


}
