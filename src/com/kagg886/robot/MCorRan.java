package com.kagg886.robot;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.meowy.cqp.jcq.entity.CoolQ;

public class MCorRan implements Runnable {
	private CoolQ CQ;
	private long fromGroup;
	private long fromQQ;
	private String msg;

	public MCorRan(CoolQ CQ, long fromGroup, long fromQQ, String msg) {
		this.CQ = CQ;
		this.fromGroup = fromGroup;
		this.fromQQ = fromQQ;
		this.msg = msg;
	}

	@Override
	public void run() {
		String[] var = msg.split(" ");
		
		if (msg.matches(".random view .*")) {
			String[] all = new String[] {"非常ao","有点ao","一点也不ao","有点谔谔","非常谔谔"};
			String a = all[Tools.Random(0, all.length)];
			CQ.sendGroupMsg(fromGroup, "\"" + var[2] + "\"这个选项我认为" + a);
		}
		
		if (msg.matches(".random boolean .*")) {
			String[] all = new String[] {"可以","不可"};
			String a = all[Tools.Random(0, all.length)];
			CQ.sendGroupMsg(fromGroup, "对于\"" + var[2] + "\"\n我局的" + a);
		}
		if (msg.indexOf(".random event\r\n") != -1) {
			ArrayList<String> list = new ArrayList<String>();
			for (String event : msg.split("\r\n")) {
				if (event.equals(".random event")) continue;
				list.add(event);
			}
			int ran = Tools.Random(0, list.size());
			CQ.sendGroupMsg(fromGroup, "咱从里面挑出了第" + (ran + 1) + "个选项,也就是:\n" + list.get(ran));
		}
		
		if (msg.matches(".rd[0-9]+")) {
			int all = Integer.parseInt(msg.replace(".rd", ""));
			if (all == 0) {
				CQ.sendGroupMsg(fromGroup, "妖梦和他的半灵骰出了...等等，骰子被uuz吃了？");
			}
			CQ.sendGroupMsg(fromGroup, "妖梦和他的半灵骰出了:D" + all + "=" + Tools.Random(0, all));
		}
		
		if (msg.matches("(http|https)://www.bilibili.com/video/.*") || msg.matches("(http|https)://b23.tv/.*")) {
			CQ.sendGroupMsg(fromGroup, "发现了一个B站链接，正在指挥半灵寻找中...");
			Document source = Jsoup.parse(Tools.getHTML(msg));
			StringBuffer buf = new StringBuffer();
			buf.append("[BiliFinder]:解析结果如下\n");
			String av = null;
			for (Element a : source.getElementsByTag("meta")) {
				if (a.attr("property").equals("og:url")) {
					av = a.attr("content").split("/")[a.attr("content").split("/").length - 1];
					break;
				}
			}
			for (Element a : source.getElementsByTag("meta")) {
//				System.out.println(a.toString());
				if (a.attr("itemprop").equals("name")) {
					buf.append("标题:" + a.attr("content").replace("哔哩哔哩 (゜-゜)つロ 干杯~-bilibili", "(") + av + ")\n");
				}
				if (a.attr("itemprop").equals("image")) {
					buf.append("封面链接:" + a.attr("content") + "\n");
				}
				if (a.attr("itemprop").equals("description")) {
					buf.append("简介:" + a.attr("content") + "...\n");
				}
				if (a.attr("itemprop").equals("keywords")) {
					buf.append("关键词:" + a.attr("content") + "\n");
				}
			}
			CQ.sendGroupMsg(fromGroup, buf.toString());
		}
		
		
		if (msg.matches(".serverinfo .*") && var.length == 2) {

			getServerInfo(var[1], 25565);
		}

		if (msg.matches(".serverinfo .* [0-9]+") && var.length == 3) {
			getServerInfo(var[1], Integer.parseInt(var[2]));
		}
		
		if (msg.matches(".getitemid .*"))
		{
			CQ.sendGroupMsg(fromGroup, "提交请求中...\n总数据为76.8K，遍历时间较长，请骚等");
			boolean ischeck = false;
			try {
				String a = null;
				a = Tools.getHTML("https://mc.ganxiaozhe.com/mc/tool/lib/items116.json");
				JSONArray json = new JSONArray(a);
				a = msg.split(" ")[1];
				for (int i = 0;i < json.length();i++)
				{
					JSONObject object = json.getJSONObject(i);
					if (object.getString("name").equals(a))
					{
						CQ.sendGroupMsg(fromGroup, "物品名称:" + a + "\n物品id:" + object.getString("id"));
						ischeck = true;
					}
				}
			} catch (Exception e) {
				
			}
			if (ischeck == false)
			{
				CQ.sendGroupMsg(fromGroup, "找不到物品:" + msg.split(" ")[1]);
			}
		}
		
	}

	private void getServerInfo(String string, int i) {
		StringBuffer buf = new StringBuffer();
		CQ.sendGroupMsg(fromGroup, "提交请求中...");
		Document d = null;
		try {
			d = Jsoup.connect("https://status.mctalks.com/return.php?address=" + string + "&port=" + i).get();
		} catch (Exception e1) {
			this.CQ.sendGroupMsg(fromGroup, "我炸了");
		}
		for (Element e : d.getElementsByTag("tbody")) {
			for (Element p : e.getElementsByTag("tr")) {
				buf.append(p.text() + "\n");
			}
		}
		this.CQ.sendGroupMsg(fromGroup, buf.toString());

	}

}
