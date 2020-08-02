package com.kagg886.robot;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.meowy.cqp.jcq.entity.CoolQ;
import org.meowy.cqp.jcq.entity.Group;

public class BOTManager implements Runnable
{
	private CoolQ CQ;
	private long fromGroup;
	private String msg;

	private FilenameFilter f = new FilenameFilter() {

		@Override
		public boolean accept(File arg0, String arg1) {
			if (arg1.matches("[0-9]+")) {
				return true;
			}
			return false;
		}
	};
	private long fromQQ;

	public BOTManager(CoolQ CQ, long fromGroup, long fromQQ, String msg) {
		this.CQ = CQ;
		this.fromGroup = fromGroup;
		this.msg = msg;
		this.fromQQ = fromQQ;
	}

	@Override
	public void run() {
		
		if (fromQQ == 485184047L) {
			if (msg.indexOf(".allsend ") != -1) {
				List<Group> list = CQ.getGroupList();
				for (Group group : list) {
					if (group.getId() != fromGroup) {
						int a = CQ.sendGroupMsg(group.getId(), "[公告]\n" + msg.replace(".allsend ", ""));
						if (a <= 0) {
							CQ.setGroupLeave(fromGroup, false);
						}
					}
				}
				CQ.sendGroupMsg(fromGroup, "发送成功\n共发送:" + list.size() + "个群");
			}
			
			

			if (msg.matches(".del")) {
				File file = new File(CQ.getAppDirectory() + "\\data\\");
				ArrayList<Long> A = new ArrayList<Long>();
				for (String a : file.list(f)) // 文件列表
				{
					A.add(Long.parseLong(a));
				}
				ArrayList<Long> B = new ArrayList<Long>(); // 群数据
				for (Group g : CQ.getGroupList()) {
					B.add(g.getId());
				}
				A.removeAll(B);
				if (A.isEmpty())
				{
					CQ.sendGroupMsg(fromGroup, "暂无群可清理");
					return;
				}
				StringBuffer buf = new StringBuffer();
				for (Long a : A) {
					buf.append(String.valueOf(a) + "\n");
					del(new File(CQ.getAppDirectory() + "\\data\\" + a));
				}
				CQ.sendGroupMsg(fromGroup,"已清理如下群的群数据:\n" +  buf.toString());
				
			}
		}
		
	}
	
	public static void del(File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				del(f);
			}
		}
		file.delete();
	}

}
