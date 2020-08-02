package com.kagg886.robot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.meowy.cqp.jcq.entity.CoolQ;
import org.meowy.cqp.jcq.entity.ICQVer;
import org.meowy.cqp.jcq.entity.IMsg;
import org.meowy.cqp.jcq.entity.IRequest;
import org.meowy.cqp.jcq.entity.Member;
import org.meowy.cqp.jcq.entity.enumerate.Authority;
import org.meowy.cqp.jcq.event.JcqAppAbstract;

public class Replycore extends JcqAppAbstract implements ICQVer, IMsg, IRequest {
	private ThreadPoolExecutor pool;
	private boolean beat = true;
	private ReplySender sender;
	private TimeShower shower;
	private long groupid;

	public static void main(String[] args) {
	}

	@Override
	public int groupMsg(int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg,
			int font) {
		if (msg.matches(".unmute")) {
			Member m = CQ.getGroupMemberInfo(fromGroup, fromQQ);
			Authority d = m.getAuthority();
			if (d.equals(Authority.MEMBER)) {
				CQ.sendGroupMsg(fromGroup, "您不是管理员w");
			} else {
				CQ.sendGroupMsg(fromGroup, "早上好！~");
				FileFuction tools = new FileFuction(CQ.getAppDirectory() + "\\data\\" + fromGroup + "\\");
				tools.Write("Option.txt", "stamp", 0L);
			}
			
		}
		if (fliter(fromGroup)) {
			return 0;
		}
		
		if (fliter2(fromGroup)) {
			return 0;
		}

		if (beat) {
			sender = new ReplySender(CQ, subType, msgId, fromGroup, fromQQ, fromAnonymous, msg, font);
			shower = new TimeShower(CQ, subType, msgId, fromGroup, fromQQ, fromAnonymous, msg, font);
			pool.execute(sender);
			pool.execute(shower);
			beat = false;
		}

		if (msg.matches(".runstatus")) {
			CQ.sendGroupMsg(fromGroup,
					"活跃线程数:" + pool.getActiveCount() + "\n已处理任务:" + pool.getCompletedTaskCount());
		}
		pool.execute(new RobotRunnable(CQ, subType, msgId, fromGroup, fromQQ, fromAnonymous, msg, font));
		pool.execute(new MultiChat(CQ, subType, msgId, fromGroup, fromQQ, fromAnonymous, msg, font));
		pool.execute(new MCorRan(CQ, fromGroup, fromQQ, msg));
		if (fromGroup == 973510746L) {
			pool.execute(new BOTManager(CQ, fromGroup, fromQQ, msg));
		}
		return 0;
	}

	private boolean fliter2(long fromGroup) {
		FileFuction tools = new FileFuction(CQ.getAppDirectory() + "\\data\\" + fromGroup + "\\");
		long exit = tools.Read("Option.txt", "stamp", 0L);
		if (System.currentTimeMillis() - exit >= 3600000) {
			return false;
		}
		CQ.logDebug("屏蔽提示", "此群的调用被阻挡");
		return true;
	}

	private boolean fliter(long fromGroup) {
		String a = "";
		BufferedReader buf = Tools.getReader(CQ.getAppDirectory() + "\\fliter.txt");
		try {
			while ((a = buf.readLine()) != null) {
				if (fromGroup == Long.parseLong(a)) {
					CQ.logDebug("[消息过滤]", "过滤掉来自" + fromGroup + "的消息");
					return true;
				}
			}
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public int disable() {
		return 0;
	}

	@Override
	public int discussMsg(int arg0, int arg1, long arg2, long arg3, String arg4, int arg5) {
		return 0;
	}

	@Override
	public int enable() {
		return 0;
	}

	@Override
	public int exit() {
		this.sender.isCancel = false;
		this.shower.isCancel = false;
		return 0;
	}

	@Override
	public int friendAdd(int arg0, int arg1, long arg2) {

		return 0;
	}

	@Override
	public int groupAdmin(int subtype, int sendTime, long fromGroup, long beingOperateQQ) {
		if (fliter(fromGroup)) {
			return 0;
		}
		if (fliter2(fromGroup)) {
			return 0;
		}
		String type = null;
		switch (subtype) {
		case 1:
			type = "没";
			break;
		case 2:
			type = "有";
			if (beingOperateQQ == CQ.getLoginQQ()) {
				CQ.sendGroupMsg(fromGroup, "您已给予BOT管理员权限，可以进行\n1.退群统计\n2.新成员尝试进群提醒");
				return 0;
			}
			break;
		}
		CQ.sendGroupMsg(fromGroup,
				getNick(CQ, fromGroup, beingOperateQQ) + "(" + beingOperateQQ + ")的绿帽子" + type + "了！");
		return 0;
	}

	@Override
	public int groupMemberDecrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
		if (fliter(fromGroup)) {
			return 0;
		}
		if (fliter2(fromGroup)) {
			return 0;
		}
		String sign = null;
		switch (subtype) {
		case 1:
			sign = "溜走了";
			break;
		case 2:
			sign = "被" + getNick(CQ, fromGroup, fromQQ) + "玩坏了";
			break;
		}
		CQ.sendGroupMsg(fromGroup, getNick(CQ, fromGroup, beingOperateQQ) + "(" + beingOperateQQ + ")" + sign);
		FileFuction tools = new FileFuction(CQ.getAppDirectory() + "\\data\\" + fromGroup + "\\");
		int s = tools.Read("Decrase.txt", String.valueOf(beingOperateQQ), 0);
		tools.Write("Decrase.txt", String.valueOf(beingOperateQQ), s + 1);
		return 0;
	}

	@Override
	public int groupMemberIncrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
		if (fliter(fromGroup)) {
			return 0;
		}
		if (fliter2(fromGroup)) {
			return 0;
		}
		if (beingOperateQQ == CQ.getLoginQQ()) {
			int a = CQ.sendGroupMsg(fromGroup, "这里是妖梦BOT！\n我的使用文档在这里:https://blog.csdn.net/qq_26460583/article/details/107566611\n注意！请不要禁言或者踢掉我！");
			if (a <= 0) {
				CQ.setGroupLeave(fromGroup, false);
			}
		}
		CQ.sendGroupMsg(fromGroup, "怀孕新人:[CQ:at,qq=" + beingOperateQQ + "](" + beingOperateQQ + ")\n发送.help查看机器人全部指令");
		FileFuction tools = new FileFuction(CQ.getAppDirectory() + "\\data\\" + fromGroup + "\\");
		int s = tools.Read("Decrase.txt", String.valueOf(beingOperateQQ), 0);
		if (s != 0) {
			CQ.sendGroupMsg(fromGroup, "哎呦，您怎么又进来了\n您知不知道您已经退过" + s + "次群啦");
		}
//		try
//		{
//			
//			JSONObject obj = new JSONObject(Tools.getHTML("https://api.66mz8.com/api/qq.level.php?qq=" + fromQQ));
//			if (obj.getInt("qqage") <= 0|| ((obj.getInt("level") / obj.getInt("qqage")) <= 1))
//			{
//				CQ.sendGroupMsg(fromGroup,"[警告]:此人疑似小号");
//			}
//		} catch (JSONException e)
//		{
//			e.printStackTrace();
//		}
		return 0;
	}

	@Override
	public int groupUpload(int subType, int sendTime, long fromGroup, long fromQQ, String file) {
		return 0;
	}

	@Override
	public int requestAddFriend(int subtype, int sendTime, long fromQQ, String msg, String responseFlag) {
		CQ.setFriendAddRequest(responseFlag, IRequest.REQUEST_REFUSE, null);
		return 0;
	}

	@Override
	public int requestAddGroup(int subtype, int sendTime, long fromGroup, long fromQQ, String msg,
			String responseFlag) {
		switch (subtype) {
		case 1:
			if (msg == null) {
				msg = "\n暂无";
			}
			CQ.sendGroupMsg(fromGroup, getNick(CQ, fromGroup, fromQQ) + "(" + fromQQ + ")想进群玩一玩\n申请信息:\n" + msg);
			break;
		case 2:
			CQ.setGroupAddRequest(responseFlag, IRequest.REQUEST_GROUP_INVITE, IRequest.REQUEST_ADOPT, null);
			break;
		}
		return 0;
	}

	@Override
	public int startup() {
		pool = new ThreadPoolExecutor(5, 7, 2, TimeUnit.SECONDS, new ArrayBlockingQueue<>(512), // 使用有界队列，避免OOM
				new RejectedExecutionHandler() {
					@Override
					public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
						CQ.logDebug(String.valueOf(groupid), groupid + "的调用被阻挡");
					}
				});
		return 0;
	}

	@Override
	public String appInfo() {
		return CQAPIVER + "," + "com.kagg886.robot";
	}

	public static String getNick(CoolQ CQ, long fromGroup, long p0) {
		FileFuction tools = new FileFuction(CQ.getAppDirectory() + "\\data\\" + fromGroup + "\\");
		String a = null;
		String b = tools.Read("nick.txt", String.valueOf(p0), "");
		if (b.equals("")) {
			a = CQ.getStrangerInfo(p0).getNick();
//			try
//			{
//				a = Tools.getHTML("http://r.qzone.qq.com/fcg-bin/cgi_get_portrait.fcg?g_tk=1518561325&uins=" + p0).split("\"")[5];
//			} catch (ArrayIndexOutOfBoundsException e)
//			{
//				a = "此人过于谔谔，无法提供群名片";
//			}
		} else {
			a = b;
		}
		return a;
	}

	@Override
	public int groupBan(int subType, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ, long duration) {
		if (fliter(fromGroup)) {
			return 0;
		}
		switch (subType) {
		case 1:
			if (beingOperateQQ != 0) {
				CQ.sendGroupMsg(fromGroup, getNick(CQ, fromGroup, fromQQ) + "取下了"
						+ getNick(CQ, fromGroup, beingOperateQQ) + "(" + beingOperateQQ + ")" + "的口球");
			} else {
				CQ.sendGroupMsg(fromGroup, getNick(CQ, fromGroup, fromQQ) + "取下了所有人的口球");
			}

			break;
		case 2:
			if (beingOperateQQ != 0) {
				if (beingOperateQQ == 3405637452L) {
					CQ.sendPrivateMsg(fromQQ, "你口我呜呜呜，不理你了呜呜呜");
					CQ.setGroupLeave(fromGroup, false);
				}
				CQ.sendGroupMsg(fromGroup, getNick(CQ, fromGroup, beingOperateQQ) + "(" + beingOperateQQ + ")被"
						+ getNick(CQ, fromGroup, fromQQ) + "赠送了" + (int) (duration / 60) + "分钟的口球");
			} else {
				CQ.sendGroupMsg(fromGroup, getNick(CQ, fromGroup, fromQQ) + "赠送了所有人口球");
			}
			break;
		}
		return 0;
	}

	@Override
	public int privateMsg(int subType, int msgId, long fromQQ, String msg, int font) {
		if (subType == 2) {
			CQ.sendPrivateMsg(fromQQ, "BOT无私聊指令\n若您对此有异议，请进入Q群:973510746向群主进行询问");
		}
		return 0;
	}
}
