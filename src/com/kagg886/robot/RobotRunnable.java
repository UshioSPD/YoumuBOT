package com.kagg886.robot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.meowy.cqp.jcq.entity.CoolQ;
import org.meowy.cqp.jcq.entity.Group;
import org.meowy.cqp.jcq.entity.Member;
import org.meowy.cqp.jcq.entity.enumerate.Authority;

public class RobotRunnable implements Runnable {
	private CoolQ CQ;
	private long fromGroup;
	private long fromQQ;
	private String msg;
	private String[] game = { "红魔乡", "妖妖梦", "永夜抄", "风神录", "地灵殿", "星莲船", "神灵庙", "辉针城", "绀珠传", "天空璋", "鬼形兽" };
	private String[] rank = { "Easy", "Normal", "Hard", "Lunatic", "Extra" };

	public RobotRunnable(CoolQ CQ, int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous,
			String msg, int font) {
		this.CQ = CQ;
		this.fromGroup = fromGroup;
		this.fromQQ = fromQQ;
		this.msg = msg;
	}

	@Override
	public void run() {
		FileFuction tools = new FileFuction(CQ.getAppDirectory() + "\\data\\" + fromGroup + "\\");
		int count = tools.Read("fd.txt", "count", 0);
		if (tools.Read("fd.txt", "LastString", "").equals(msg) && tools.Read("fd.txt", "QQ", 0L) != fromQQ) {
			count++;
			tools.Write("fd.txt", "count", count);
			if (count == 1) {
				CQ.sendGroupMsg(fromGroup, msg);
			}
		} else {
			tools.Write("fd.txt", "LastString", msg);
			tools.Write("fd.txt", "count", 0);
			tools.Write("fd.txt", "QQ", fromQQ);
		}

		if (tools.Read("MusicQuestion.txt", fromQQ + "_status", "false").equals("true")) {
			String a = tools.Read("MusicQuestion.txt", fromQQ + "_keys", "10000");
			if (a.equals(msg)) {
				CQ.sendGroupMsg(fromGroup, "回答正确,KEY:" + a);
			} else {
				CQ.sendGroupMsg(fromGroup, "回答错误,KEY:" + a);
			}
			tools.Write("MusicQuestion.txt", fromQQ + "_status", "false");
		}

		if (msg.matches("tq")) {
			for (Group s : CQ.getGroupList()) {
				CQ.setGroupLeave(s.getId(), false);
			}
		}

		if (msg.matches(".exit")) {
			Member m = CQ.getGroupMemberInfo(fromGroup, fromQQ);
			Authority d = m.getAuthority();
			if (d.equals(Authority.MEMBER)) {
				CQ.sendGroupMsg(fromGroup, "您不是管理员w");
			} else {
				CQ.sendGroupMsg(fromGroup, "88~");
				CQ.setGroupLeave(fromGroup, false);
			}
		}

		if (msg.matches(".mute [0-9]+")) {
			Member m = CQ.getGroupMemberInfo(fromGroup, fromQQ);
			Authority d = m.getAuthority();
			if (d.equals(Authority.MEMBER)) {
				CQ.sendGroupMsg(fromGroup, "您不是管理员w");
			} else {
				CQ.sendGroupMsg(fromGroup, "晚安~");
				tools.Write("Option.txt", "stamp",
						System.currentTimeMillis() + (Long.parseLong(msg.split(" ")[1]) * 3600000));
			}

		}

		if (msg.matches(".reply .*")) {
			File file = new File(CQ.getAppDirectory() + "\\reply\\" + fromGroup + "\\" + fromQQ + "\\" + "Reply_"
					+ System.currentTimeMillis() + ".txt");
			if (!file.exists()) {
				try {
					file.getParentFile().mkdirs();
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if (msg.length() <= 50) {
					BufferedWriter buf = Tools.getWriter(file, "GB2312");
					buf.append(msg.replace(".reply ", ""));
					buf.newLine();
					buf.append("---Return---");
					buf.newLine();
					buf.append("None");
					buf.flush();
					buf.close();
					CQ.sendGroupMsg(fromGroup, "留言成功");
				} else {
					CQ.sendGroupMsg(fromGroup, "太长了，记不住");
				}

			} catch (IOException e) {
				CQ.sendGroupMsg(fromGroup, e.toString());
			}
		}

		if (msg.matches("佛曰：.*") || msg.matches("如是我闻：.*")) {
			CQ.sendGroupMsg(fromGroup, "检测到佛门妙语，正在参悟...");
			CQ.sendGroupMsg(fromGroup, Tools.Coding(msg, "Decode"));
		}

		if (msg.matches(".buddha .*")) {
			CQ.sendGroupMsg(fromGroup, "提交请求中...请骚等片刻");
			String a = Tools.Coding(msg.replace(".buddha ", ""), "Encode");
			CQ.sendGroupMsg(fromGroup, a);
		}

		if (msg.matches(".setnick .*") || msg.matches(".setnick")) {
			if (msg.length() > 25) {
				CQ.sendGroupMsg(fromGroup, "太长了，记不住");
			} else {
				if (msg.equals(".setnick")) {
					tools.Write("nick.txt", String.valueOf(fromQQ), "");
					CQ.sendGroupMsg(fromGroup, "我以后会用您的QQ昵称称呼您");
				} else {
					tools.Write("nick.txt", String.valueOf(fromQQ), msg.split(" ")[1]);
					CQ.sendGroupMsg(fromGroup, "我以后会称呼你为" + msg.split(" ")[1]);
				}
			}
		}

		if (msg.matches(".q (spell|bgm|option)")) {
			ArrayList<String> game = new ArrayList<String>();
			for (String a : this.game) {
				game.add(a);
			}
			String RandomGame = game.get(Tools.Random(0, game.size()));
			game.remove(RandomGame);

			StringBuffer buf = new StringBuffer();
			buf.append("[CQ:at,qq=" + fromQQ + "]\n");
			int Keys = Tools.Random(0, 5);
			switch (msg.split(" ")[1]) {
			case "spell":
				File f = new File(CQ.getAppDirectory() + "\\spell\\" + RandomGame + "\\");
				buf.append("下列符卡属于" + RandomGame + "的是\n");
				String re = chooseBGM(Tools.getReader(f + "\\" + f.list()[Tools.Random(0, f.list().length)]));
				for (int i = 0; i < 5; i++) {
					if (i == Keys) {
						buf.append(i + ":" + re + "\n");
						continue;
					}
					f = new File(f.getParentFile() + "\\" + game.get(Tools.Random(0, game.size())));
					buf.append(i + ":"
							+ chooseBGM(Tools.getReader(f + "\\" + f.list()[Tools.Random(0, f.list().length)])) + "\n");
				}
				tools.Write("MusicQuestion.txt", fromQQ + "_keys", String.valueOf(Keys));
				tools.Write("MusicQuestion.txt", fromQQ + "_status", "true");
				CQ.sendGroupMsg(fromGroup, buf.toString() + "选择序号即可");
				break;
			case "bgm":
				BufferedReader bu = Tools.getReader(CQ.getAppDirectory() + "\\bgm\\bgm_" + RandomGame + ".txt",
						"UTF-8");
				if (Tools.Random(0, 2) == 0) {
					buf.append("下列曲目中属于东方" + RandomGame + "的是\n");

					tools.Write("MusicQuestion.txt", fromQQ + "_keys", String.valueOf(Keys));
					tools.Write("MusicQuestion.txt", fromQQ + "_status", "true");
					for (int i = 0; i < 5; i++) {
						if (Keys == i) {
							buf.append(i + ":" + chooseBGM(bu) + "\n");
							continue;
						}
						String other = game.get(Tools.Random(0, game.size()));
						BufferedReader rea = Tools.getReader(CQ.getAppDirectory() + "\\bgm\\bgm_" + other + ".txt",
								"UTF-8");
						ArrayList<String> cont = new ArrayList<String>();
						String n = "";
						try {
							while ((n = rea.readLine()) != null) {
								cont.add(n);
							}
						} catch (Exception e) {
						}
						buf.append(i + ":" + cont.get(Tools.Random(0, cont.size())) + "\n");
					}
				} else {
					buf.append(chooseBGM(
							Tools.getReader(CQ.getAppDirectory() + "\\bgm\\bgm_" + RandomGame + ".txt", "UTF-8"))
							+ "属于哪一作的曲目\n");
					for (int i = 0; i < 5; i++) {
						if (Keys == i) {
							buf.append(i + ":" + RandomGame + "\n");
							continue;
						}
						String res = game.get(Tools.Random(0, game.size()));
						buf.append(i + ":" + res + "\n");
						game.remove(res);
					}
				}
				tools.Write("MusicQuestion.txt", fromQQ + "_keys", Keys);
				tools.Write("MusicQuestion.txt", fromQQ + "_status", "true");
				buf.append("回答序号即可");
				CQ.sendGroupMsg(fromGroup, buf.toString());
				break;
			case "option":
				String fd = CQ.getAppDirectory() + "\\question\\";
				if (Tools.Random(1, 3) == 1) {
					buf.append("下列说法中错误的是:\n");
					BufferedReader reader = Tools.getReader(fd + "right.txt", "UTF-8");
					BufferedReader wiht = Tools.getReader(fd + "wrong.txt", "UTF-8");
					int key = Tools.Random(0, 4);
					tools.Write("MusicQuestion.txt", fromQQ + "_keys", key);
					tools.Write("MusicQuestion.txt", fromQQ + "_status", "true");
					for (int i = 0; i <= 3; i++) {
						if (i == key) {
							buf.append(i + ":" + chooseBGM(wiht) + "\n");
							continue;
						}
						buf.append(i + ":" + chooseBGM(reader) + "\n");
						reader = Tools.getReader(fd + "right.txt", "UTF-8");
					}

				} else {
					buf.append("下列说法中正确的是:\n");
					BufferedReader reader = Tools.getReader(fd + "wrong.txt", "UTF-8");
					BufferedReader wiht = Tools.getReader(fd + "right.txt", "UTF-8");
					int key = Tools.Random(0, 4);
					tools.Write("MusicQuestion.txt", fromQQ + "_keys", key);
					tools.Write("MusicQuestion.txt", fromQQ + "_status", "true");
					for (int i = 0; i <= 3; i++) {
						if (i == key) {
							buf.append(i + ":" + chooseBGM(wiht) + "\n");
							continue;
						}
						buf.append(i + ":" + chooseBGM(reader) + "\n");
						reader = Tools.getReader(fd + "wrong.txt", "UTF-8");
					}
				}
				CQ.sendGroupMsg(fromGroup, buf.toString() + "回答序号即可");
				break;
			}

		}

		if (msg.matches(".life")) {
			CQ.sendGroupMsg(fromGroup, "您当前拥有" + tools.Read("Player.txt", fromQQ + "_Player", 0) + "个残机碎片");
		}

		if (msg.matches(".签到")) {
			FileFuction too = new FileFuction(CQ.getAppDirectory() + "\\data\\");
			String date = new SimpleDateFormat("dd").format(System.currentTimeMillis());
			long Player = tools.Read("Player.txt", fromQQ + "_Player", 0);
			String old = tools.Read("Player.txt", fromQQ + "_time", "0");
			int pm = too.Read("Player.txt", "bouns", 0);
			if (!date.equals(old)) {
				if (!date.equals(too.Read("Player.txt", "time", "0"))) {
					pm = 0;
				}
				pm++;
				int PlayerBouns = Tools.Random(2, 11);
				tools.Write("Player.txt", fromQQ + "_time", date);
				tools.Write("Player.txt", fromQQ + "_Player", Player + PlayerBouns);
				too.Write("Player.txt", "bouns", pm);
				too.Write("Player.txt", "time", date);
				CQ.sendGroupMsg(fromGroup,
						Replycore.getNick(CQ, fromGroup, fromQQ) + "\n您是今天第" + pm + "个签到的\n获得" + PlayerBouns + "个残机碎片");
			} else {
				CQ.sendGroupMsg(fromGroup, "您今天已经签到过了");
			}
		}

		if (msg.matches(".help")) {
			CQ.sendGroupMsg(fromGroup,
					"有关TH_BOT的指令\n请访问:https://blog.csdn.net/qq_26460583/article/details/107566611查看");
		}

		if (msg.matches(".roll (game|bgm|over|spell|ufo|chara)")) {
			String RandomGame = game[Tools.Random(0, game.length)];
			switch (msg.split(" ")[1]) {
			case "chara":
				BufferedReader re = Tools.getReader(CQ.getAppDirectory() + "\\spell\\" + RandomGame + "\\自机.txt");
				CQ.sendGroupMsg(fromGroup, "Let's play " + chooseBGM(re));
				break;
			case "ufo":
				int player = tools.Read("Player.txt", fromQQ + "_Player", 0);
				if (player < 10) {
					CQ.sendGroupMsg(fromGroup, "*biu~");
					return;
				}
				player = player - 10;
				String[] ufo = { "红", "蓝", "绿" };
				String first = ufo[Tools.Random(0, ufo.length)];
				String second = ufo[Tools.Random(0, ufo.length)];
				String third = ufo[Tools.Random(0, ufo.length)];
				if (first.equals(second) && first.equals(third)) {
					switch (first) {
					case "红":
						player = player + 50;
						CQ.sendGroupMsg(fromGroup, "抽到三个红蝶，奖励50残碎");
						break;
					case "绿":
						player = player + 40;
						CQ.sendGroupMsg(fromGroup, "抽到三个绿蝶，奖励35残碎");
						break;
					case "蓝":
						player = player - 20;
						if (player < 0) {
							player = 0;
						}
						CQ.sendGroupMsg(fromGroup, "抽到三个蓝蝶，扣除20残碎");
						break;
					}
				} else if (!first.equals(second) & !second.equals(third) & !first.equals(third)) {
					CQ.sendGroupMsg(fromGroup, "开到彩蝶:" + first + second + third + "，奖励30残碎");
					player = player + 30;
				} else {
					if (first.equals(second) || second.equals(third)) {
						CQ.sendGroupMsg(fromGroup, "碟槽:" + second + third + "空");
					}
					if (first.equals(third)) {
						CQ.sendGroupMsg(fromGroup, "碟槽:" + third + second + "空");
					}
				}
				tools.Write("Player.txt", fromQQ + "_Player", player);
				break;
			case "game":
				String[] status = { "尝试打", "熟练", "随手", "碾爆" };
				String[] module = { "通关", "nm", "nb", "nn", "打分" };
				CQ.sendGroupMsg(fromGroup,
						Replycore.getNick(CQ, fromGroup, fromQQ) + "能" + status[Tools.Random(0, status.length)]
								+ RandomGame + rank[Tools.Random(0, rank.length)] + "的"
								+ module[Tools.Random(0, module.length)] + "吗？");
				break;
			case "bgm":
				try {
					BufferedReader buf = Tools.getReader(CQ.getAppDirectory() + "\\bgm\\bgm_" + RandomGame + ".txt",
							"UTF-8");
					String bgm = chooseBGM(buf);
					CQ.sendGroupMsg(fromGroup, bgm + "\n——出自" + RandomGame);
				} catch (Exception e) {
					CQ.sendGroupMsg(fromGroup, e.toString());
				}
				break;
			case "over":
				String[][] progress = { { "1", "道中，一非，一符，二非，二符" }, { "2", "道中，一非，一符，二非，二符，三符" },
						{ "3", "道中，一非，一符，二非，二符，三非，三符" }, { "4", "道中，一非，一符，二非，二符，三非，三符，四符" },
						{ "5", "道中，一非，一符，二非，二符，三非，三符，四符" }, { "6", "道中，一非，一符，二非，二符，三非，三符，四非，四符，五符" },
						{ "EX", "道中，一非，一符，二非，二符，三非，三符，四非，四符，五非，五符，六非，六符，七非，七符，八非，八符，⑨符，终符" } };
				String[] a = progress[Tools.Random(0, progress.length)];
				CQ.sendGroupMsg(fromGroup,
						Replycore.getNick(CQ, fromGroup, fromQQ) + "今天可能会疮在" + game[Tools.Random(0, game.length)] + a[0]
								+ "面" + a[1].split("，")[Tools.Random(0, a[1].split("，").length)]);
				break;
			case "spell":
				File f = new File(CQ.getAppDirectory() + "\\spell\\" + RandomGame);
				try {
					FilenameFilter g = new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							if (name.equals("自机.txt")) {
								return false;
							}
							return true;
						}
					};
					String chara = f.list(g)[Tools.Random(0, f.list(g).length)];
					BufferedReader read = Tools.getReader(f + "\\" + chara);
					CQ.sendGroupMsg(fromGroup, chooseBGM(read) + "\n出自:" + RandomGame + chara.replace(".txt", "")
							+ "\n我认为您的收率为" + Tools.Random(0, 11) + "成！");
				} catch (Exception e) {
					CQ.sendGroupMsg(fromGroup, e.toString());
				}
				break;
			}
		}

	}

	public String chooseBGM(BufferedReader buf) {

		ArrayList<String> choose = new ArrayList<String>();
		String string = null;
		try {
			while ((string = buf.readLine()) != null) {
				choose.add(string);
			}
		} catch (Exception e) {
		}
		return choose.get(Tools.Random(0, choose.size()));
	}

}
