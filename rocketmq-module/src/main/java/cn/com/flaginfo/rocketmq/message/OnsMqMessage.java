package cn.com.flaginfo.rocketmq.message;

import cn.com.flaginfo.module.common.utils.StringPool;
import cn.com.flaginfo.module.common.utils.StringUtils;
import com.aliyun.openservices.ons.api.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;


/**
 * @author: Meng.Liu
 * @date: 2018/11/22 上午11:40
 */
@Getter
@Setter
@ToString
@Slf4j
public class OnsMqMessage implements MqMessage {

	private Message mqMessageExt;

	public OnsMqMessage(Message messageExt) {
		this.mqMessageExt = messageExt;
		this.message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
		this.msgId = messageExt.getMsgID();
		this.topic = messageExt.getTopic();
		this.tag = StringUtils.isBlank(messageExt.getTag()) ? StringPool.ASTERISK : messageExt.getTag();
		this.keys = messageExt.getKey();
		this.bornTimestamp = messageExt.getBornTimestamp();
		this.reconsumeTimes = messageExt.getReconsumeTimes();
		this.retryTimes = messageExt.getReconsumeTimes();
	}

	/**
	 * 消息的keys
	 */
	private String keys;

	/**
	 * 消息的topic
	 */
	private String topic;

	/**
	 * 消息的tags
	 */
	private String tag;

	/**
	 * 消息ID
	 */
	private String msgId;

	/**
	 * 消息内容
	 */
	private String message;

	/**
	 * 消息产生时间
	 */
	private Long bornTimestamp;

	/**
	 * 消息消费次数
	 */
	private Integer reconsumeTimes;

	/**
	 * retry次数
	 */
	private int retryTimes;
}
