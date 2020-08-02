package com.kagg886.robot;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@SuppressWarnings("deprecation")
class FileFuction {
	private String file;

	public FileFuction(String file) {
		this.file = file;
	}

	public int Read(String w, String y, int m) {
		return Integer.parseInt(Read(w, y, String.valueOf(m)));
	}

	public void Write(String w, String y, int x) {
		Write(w, y, String.valueOf(x));
	}

	public long Read(String w, String y, long m) {
		return Long.parseLong(Read(w, y, String.valueOf(m)));
	}

	public void Write(String w, String y, long x) {
		Write(w, y, String.valueOf(x));
	}

	public String Read(String w, String y, String m) {
		String i = "";
		String s = file + w;
		Properties p = new Properties();
		try {
			p.load(new BufferedInputStream(new FileInputStream(s)));

		} catch (IOException e) {
		}
		String str = null;
		i = p.getProperty(y, m);
		if (i.equals(str)) {
			return m;
		} else {
			return i;
		}
	}

	public void Write(String w, String y, String x) {
		String s = file + w;
		File f = new File(s);
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (IOException e) {
			}
		}
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(s));
			p.setProperty(y, x);
			p.store(new FileOutputStream(s), null);
		} catch (Exception e) {
		}
	}
}

public class Tools
{
	public static int Random(int min, int max)
	{
		return new java.util.Random().nextInt(max - min) + min;
	}

	public static String Coding(String msg, String flag)
	{
		try
		{
			msg = java.net.URLEncoder.encode(msg,"UTF-8");
		} catch (UnsupportedEncodingException e1)
		{
			return e1.toString();
		}
		Document d = Jsoup.parse(Tools.getHTML("http://www.keyfc.net/bbs/tools/tudou.aspx?orignalmsg=" + msg + "&action=" + flag));
		try
		{
			return d.getElementsByTag("Message").get(0).html().replace("<![CDATA[", "").replace("]]>", "");
		} catch (java.lang.IndexOutOfBoundsException e) {
			return "服务器炸了，请稍后重试";
		}
	}

	public static BufferedReader getReader(File f, String charset) {
		try {
			return new BufferedReader(new InputStreamReader(new FileInputStream(f), charset));
		} catch (Exception e) {
			return null;
		}
	}

	public static BufferedReader getReader(File f) {
		return getReader(f, "GB2312");
	}

	public static BufferedReader getReader(String g) {
		// TODO 自动生成的方法存根
		return getReader(new File(g));
	}

	public static BufferedReader getReader(String g, String charset) {
		// TODO 自动生成的方法存根
		return getReader(new File(g), charset);
	}

	public static BufferedWriter getWriter(File f, String charset) {
		try {
			return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), charset));
		} catch (Exception e) {
			return null;
		}
	}

	public static BufferedWriter getWriter(File f) {
		return getWriter(f, "GB2312");
	}

	public static String getHTML(String p0) {
		try {
			@SuppressWarnings({ "resource", "deprecation" })
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(p0);
			//get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.81 Safari/537.36 SE 2.X MetaSr 1.0");
			get.setHeader("User-Agent","Mozilla/5.0 (Linux; Android 5.1; koobee S103 Build/LMY47D) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/39.0.0.0 Mobile Safari/537.36");
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			String s = null;
			if (is != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[128];
				int ch = -1;
				@SuppressWarnings("unused")
				int count = 0;
				while ((ch = is.read(buf)) != -1) {
					baos.write(buf, 0, ch);
					count += ch;
				}
				s = new String(baos.toByteArray());
				baos.close();
			}
			is.close();
			// 返回结果
			return s;
		} catch (Exception s) {
			return s.toString();
		}
	}

	public static int getQLevel(long fromQQ) {
		Document a = Jsoup.parse(getHTML("http://www.175hd.com/level/" + fromQQ + ".html"));
		Elements b = a.getElementsByTag("tr");
		for (Element c : b)
		{
			if (c.text().matches("QQ等级.*"))
			{
				return Integer.parseInt(c.text().split("： ")[1].replace("级", ""));
			}
		}
		return 0;
	}

}
