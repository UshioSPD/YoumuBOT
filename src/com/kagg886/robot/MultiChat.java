package com.kagg886.robot;

import org.meowy.cqp.jcq.entity.CoolQ;
import org.meowy.cqp.jcq.entity.Group;

public class MultiChat implements Runnable {
	private CoolQ CQ;
	private long fromGroup;
	private String msg;
	private long fromQQ;

	public MultiChat(CoolQ CQ, int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg,
			int font) {
		this.CQ = CQ;
		this.fromGroup = fromGroup;
		this.msg = msg;
		this.fromQQ = fromQQ;
	}

	public static String changeGTB(String value, char secret) {
		byte[] bt = value.getBytes();
		for (int i = 0; i < bt.length; i++) {
			bt[i] = (byte) (bt[i] ^ (int) secret);
		}
		return new String(bt, 0, bt.length);
	}

	@Override
	public void run() {
		if (msg.matches(".multichat help")) {
			CQ.sendGroupMsg(fromGroup,
					"MultiChat使用详解:\n本工具旨在利用一个群达到与其他群聊天的效果，达到真正的跨群聊天\n命令列表如下\n.multichat list——显示可用的群列表\n.multichat send <gid> <字符串>——响为gid的群发送一条消息");
		}

		if (msg.matches(".multichat list")) {
			StringBuffer buf = new StringBuffer();
			buf.append("可用的gid如下:前面是群名，后面是gid\n");
			for (Group g : CQ.getGroupList()) {
				buf.append("\"" + g.getName() + "\"——" + changeGTB(String.valueOf(g.getId()), (char) 5) + "\n");

			}
			CQ.sendGroupMsg(fromGroup, buf.toString());
		}

		if (msg.matches(".multichat send .* .*")) {
			try {
				boolean issend = false;
				if (fromGroup == Long.parseLong(changeGTB(msg.split(" ")[2], (char) 5))) {
					CQ.sendGroupMsg(fromGroup, "发信失败\n原因:不能向自己群发信");
					return;
				}

				long gid = Long.parseLong(changeGTB(msg.split(" ")[2], (char) 5));
				for (Group g : CQ.getGroupList()) {
					if (g.getId() == gid) {
						issend = true;
					}

				}
				if (issend) {
					int a = CQ.sendGroupMsg(gid,
							"[MultiChat]:接收到来自gid为" + changeGTB(String.valueOf(fromGroup), (char) 5) + "的消息\n消息发送者:"
									+ Replycore.getNick(CQ, fromGroup, fromQQ) + "(" + fromQQ + ")\n——以下为本次发信内容——\n"
									+ msg.replaceAll(".multichat send .* ", ""));
					if (a > 0) {
						CQ.sendGroupMsg(fromGroup, "发信成功\n消息id:" + a);
					} else {
						if (a == -34) {
							CQ.sendGroupMsg(fromGroup, "发信失败\n原因:BOT在该群已被禁用");
						} else {
							CQ.sendGroupMsg(fromGroup, "发信失败\n错误码:" + a);
						}

					}
				} else {
					CQ.sendGroupMsg(fromGroup, "发信失败\n原因:目标群不存在");
				}

			} catch (Exception e) {
				CQ.sendGroupMsg(fromGroup, e.toString());
			}

		}

	}
}
