package com.kagg886.robot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

import org.meowy.cqp.jcq.entity.CoolQ;

public class ReplySender implements Runnable {

	private CoolQ CQ;
	private long fromGroup;
	public boolean isCancel = true;

	public ReplySender(CoolQ CQ, int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg,
			int font) {
		this.CQ = CQ;
		this.fromGroup = fromGroup;
		this.isCancel = true;
	}

	@Override
	public void run() {
		while (isCancel) {
			try {
				Thread.sleep(3000);
				File file = new File(CQ.getAppDirectory() + "\\reply\\");
				for (File groupFile : file.listFiles()) {
					for (File QQFile : groupFile.listFiles()) {
						for (File ReplyUnit : QQFile.listFiles()) {
							String a = null;
							StringBuffer msg = new StringBuffer();
							BufferedReader buf = Tools.getReader(ReplyUnit);
							while ((a = buf.readLine()) != null) {
								msg.append(a + "\n");
							}
							buf.close();
							if (msg.toString().indexOf("None") == -1)
							{
								
								CQ.sendGroupMsg(Long.parseLong(groupFile.getName()),Replycore.getNick(CQ, fromGroup, Long.parseLong(QQFile.getName())) + "的留言获得回调\n留言:" + msg.toString());
								ReplyUnit.delete();
							}
						}
						if (QQFile.list().length == 0) {
							QQFile.delete();
							continue;
						}
					}
					if (groupFile.list().length == 0) {
						groupFile.delete();
						continue;
					}
				}
			} catch (Exception e) {
				CQ.sendGroupMsg(fromGroup, e.toString());
			}
		}
	}
}
