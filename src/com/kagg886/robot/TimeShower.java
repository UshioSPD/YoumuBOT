package com.kagg886.robot;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.meowy.cqp.jcq.entity.CoolQ;
import org.meowy.cqp.jcq.entity.Group;

public class TimeShower implements Runnable {

	private CoolQ CQ;
	private long fromGroup;
	public boolean isCancel = true;
	private String[] emoji = { "( •∀• )̀", "=w=", "╰(￣▽￣)╭", "ヽ(✿ﾟ▽ﾟ)ノ", "w(ﾟДﾟ)w", "(￣_,￣ )" };

	public TimeShower(CoolQ CQ, int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg,
			int font) {
		this.CQ = CQ;
		this.fromGroup = fromGroup;
		this.isCancel = true;
	}

	@Override
	public void run() {
		while (isCancel) {
			try {
				Thread.sleep(1000);
				String nowtime = new SimpleDateFormat("HH时mm分ss秒").format(System.currentTimeMillis());
				if (nowtime.equals("11时45分12秒")) {
					for (Group g : CQ.getGroupList()) {
						CQ.sendGroupMsg(g.getId(), "BOT为您报时" + emoji[Tools.Random(0, emoji.length)] + "\n现在为11时45分14秒");
					}
				}

				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
