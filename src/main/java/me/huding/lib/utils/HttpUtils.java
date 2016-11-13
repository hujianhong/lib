package me.huding.lib.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * HTTP 工具类 基于标准Java接口HttpURLConnection实现
 *
 *
 * @author JianhongHu
 * @version 1.0
 * @date 2016年11月13日
 */
public final class HttpUtils {
	
	public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

	public static final String DEFAULT_ECODING = "UTF-8";
	

	private HttpUtils() {
	}

	public static String get(String url) {
		return delegate.get(url);
	}

	public static String get(String url, Map<String, String> queryParas) {
		return delegate.get(url, queryParas);
	}

	public static String get(String url, Map<String, String> queryParas,
			Map<String, String> headers) {
		return delegate.get(url, queryParas);
	}

	public static String post(String url, Map<String, String> queryParas) {
		return delegate.post(url, queryParas);
	}

	public static String post(String url, Map<String, String> queryParas,
			Map<String, String> headers) {
		return delegate.post(url, queryParas);
	}

	public static String post(String url, String data) {
		return delegate.post(url, data);
	}

	public static String postSSL(String url, String data, String certPath,
			String certPass) {
		return delegate.postSSL(url, data, certPath, certPass);
	}

	public static InputStream download(String url, String params) {
		return delegate.download(url, params);
	}

	public static String upload(String url, File file, String params) {
		return delegate.upload(url, file, params);
	}

	private interface HttpDelegate {
		String get(String url);

		String get(String url, Map<String, String> queryParas);

		String get(String url, Map<String, String> queryParas,
				Map<String, String> headers);

		String post(String url, Map<String, String> queryParas);

		String post(String url, Map<String, String> queryParas,
				Map<String, String> headers);

		String post(String url, String data);

		String postSSL(String url, String data, String certPath, String certPass);

		InputStream download(String url, String params);

		String upload(String url, File file, String params);
	}

	// http请求工具代理对象
	private static final HttpDelegate delegate;

	static {
		HttpDelegate delegateToUse = new DefaultHttpDelegate();
		delegate = delegateToUse;
	}

	/**
	 */
	private static class DefaultHttpDelegate implements HttpDelegate {

		

		/**
		 * 字符串不为 null 而且不为 "" 时返回 true
		 */
		public static boolean notBlank(String str) {
			return str != null && !"".equals(str.trim());
		}

		private static String toString(InputStream input) throws IOException {
			StringBuffer out = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input, DEFAULT_ECODING));
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				out.append(temp).append("\n");
			}
			IOUtils.closeQuietly(input);
			return out.toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see me.huding.lib.utils.HttpUtils.HttpDelegate#get(java.lang.String)
		 */
		public String get(String url) {
			return get(url, null, null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see me.huding.lib.utils.HttpUtils.HttpDelegate#get(java.lang.String,
		 * java.util.Map)
		 */
		public String get(String url, Map<String, String> queryParas) {
			return get(url, queryParas, null);
		}

		private static String encodeForm(Map<String, String> queryParas)
				throws UnsupportedEncodingException {
			if (queryParas == null || queryParas.isEmpty()) {
				return "";
			}
			StringBuffer buffer = new StringBuffer();
			boolean b = false;
			for (Entry<String, String> e : queryParas.entrySet()) {
				String k = e.getKey();
				String v = e.getValue();
				if (!notBlank(k)) {
					continue;
				}
				if (b) {
					buffer.append("&");
				}
				String es = URLEncoder.encode(v, DEFAULT_ECODING);
				buffer.append(k).append("=").append(es);
				b = true;
			}
			return buffer.toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see me.huding.lib.utils.HttpUtils.HttpDelegate#get(java.lang.String,
		 * java.util.Map, java.util.Map)
		 */
		public String get(String url, Map<String, String> queryParas,
				Map<String, String> headers) {
			try {
				if (url.endsWith("?")) {
					url = url + encodeForm(queryParas);
				} else {
					url = url + "?" + encodeForm(queryParas);
				}
				// 创建URL对象
				URL pathURL = new URL(url);
				// 打开一个HttpURLConnetion连接
				HttpURLConnection conn = (HttpURLConnection) pathURL
						.openConnection();
				// 设置连接超时
				conn.setConnectTimeout(3000);
				// 增加请求头
				addRequestProperty(conn, headers);
				conn.connect();
				return toString(conn.getInputStream());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private static void addRequestProperty(HttpURLConnection conn,
				Map<String, String> headers) {
			if (headers == null || headers.isEmpty()) {
				return;
			}
			for (Entry<String, String> e : headers.entrySet()) {
				String k = e.getKey();
				String v = e.getValue();
				if (notBlank(k)) {
					conn.addRequestProperty(k, v);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * me.huding.lib.utils.HttpUtils.HttpDelegate#post(java.lang.String,
		 * java.util.Map)
		 */
		public String post(String url, Map<String, String> queryParas) {
			return post(url, queryParas,null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * me.huding.lib.utils.HttpUtils.HttpDelegate#post(java.lang.String,
		 * java.util.Map, java.util.Map)
		 */
		public String post(String url, Map<String, String> queryParas,
				Map<String, String> headers) {
			try {
				// 创建URL对象
				URL pathURL = new URL(url);
				// 打开一个HttpURLConnetion连接
				HttpURLConnection conn = (HttpURLConnection) pathURL
						.openConnection();
				// 设置连接超时
				conn.setConnectTimeout(3000);
				// POST请求必须设置允许输出
				conn.setDoOutput(true);
				// POST请求不能使用缓存
				conn.setUseCaches(false);
				// 设置POST方式请求
				conn.setRequestMethod("POST");
				// 设置跟随重定向
				conn.setInstanceFollowRedirects(true);
				conn.setRequestProperty("Content-type", CONTENT_TYPE_FORM);
				// 增加请求头
				addRequestProperty(conn, headers);
				conn.connect();
				String params = encodeForm(queryParas);
				if (notBlank(params)) {
					DataOutputStream dos = new DataOutputStream(
							conn.getOutputStream());
					dos.write(params.getBytes());
					dos.flush();
					dos.close();
				}
				// 请求成功
				// TODO more handle
				/*
				 * if(conn.getResponseCode() == 200){
				 * 
				 * }
				 */
				return toString(conn.getInputStream());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * me.huding.lib.utils.HttpUtils.HttpDelegate#post(java.lang.String,
		 * java.lang.String)
		 */
		public String post(String url, String data) {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * me.huding.lib.utils.HttpUtils.HttpDelegate#postSSL(java.lang.String,
		 * java.lang.String, java.lang.String, java.lang.String)
		 */
		public String postSSL(String url, String data, String certPath,
				String certPass) {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * me.huding.lib.utils.HttpUtils.HttpDelegate#download(java.lang.String,
		 * java.lang.String)
		 */
		public InputStream download(String url, String params) {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * me.huding.lib.utils.HttpUtils.HttpDelegate#upload(java.lang.String,
		 * java.io.File, java.lang.String)
		 */
		public String upload(String url, File file, String params) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
