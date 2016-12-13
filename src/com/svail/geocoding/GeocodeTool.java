package com.svail.geocoding;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.svail.geotext.GeoQuery;
import com.svail.geotext.Result;
import com.svail.util.FileTool;
import net.sf.json.JSONObject;


// 将文本POI数据空间化处理，并入库
public class GeocodeTool {
	
	@SuppressWarnings("resource")
	public static void putText(String txt) {
		String srv = "http://geocontext.svail.com:8080/txt?";
		String parameters = "&req=put&txt=";
		
		byte[] postData;
		try {
			postData = (parameters + java.net.URLEncoder.encode(txt,
					"UTF-8")).getBytes(Charset.forName("UTF-8"));
			int postDataLength = postData.length;
	            
			URL url = new URL(srv);
			//System.out.println(request + urlParameters);
			HttpURLConnection cox = (HttpURLConnection) url
					.openConnection();
			cox.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; rv:11.0) like Gecko");
			cox.setDoOutput(true);
			cox.setDoInput(true);
			cox.setInstanceFollowRedirects(false);
			cox.setRequestMethod("POST");
			cox.setRequestProperty("Accept-Encoding", "gzip");  
			cox.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			cox.setRequestProperty("charset", "utf-8");
			cox.setRequestProperty("Content-Length",
					Integer.toString(postDataLength));
			cox.setUseCaches(false);
			
			DataOutputStream wr = new DataOutputStream(cox.getOutputStream());
			wr.write(postData);
				
			InputStream is = cox.getInputStream();
			if (is != null) {
				byte[] header = new byte[2];
				BufferedInputStream bis = new BufferedInputStream(is);
				bis.mark(2);
				int result = bis.read(header);

				// reset输入流到开始位置
				bis.reset();
				BufferedReader reader = null;
				// 判断是否是GZIP格式
				int ss = (header[0] & 0xff) | ((header[1] & 0xff) << 8);
				if (result != -1 && ss == GZIPInputStream.GZIP_MAGIC) {
					// System.out.println("为数据压缩格式...");
					reader = new BufferedReader(new InputStreamReader(
							new GZIPInputStream(bis), "utf-8"));
				} else {
					// 取前两个字节
					reader = new BufferedReader(new InputStreamReader(bis, "utf-8"));
				}
				
				StringBuffer temp = new StringBuffer(4096);
				
				//通过JsonParser对象可以把json格式的字符串解析成一个JsonElement对象
				try {
					String line;
					while ((line = reader.readLine()) != null) {
						temp.append(line);
					}
					
					System.out.print(temp.toString());
				}catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String batchParse(String request, String parameters, String urlParameters, int n, int offset) {
		
		byte[] postData;
		try {
			Date begin = new Date();
			
			postData = (parameters + java.net.URLEncoder.encode(urlParameters,
					"UTF-8")).getBytes(Charset.forName("UTF-8"));
			int postDataLength = postData.length;
	         
			URL url = new URL(request + "&c=" + n);
			System.out.println(request + "&c=" + n);
			HttpURLConnection cox = (HttpURLConnection) url
					.openConnection();
			cox.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; rv:11.0) like Gecko");
			cox.setDoOutput(true);
			cox.setDoInput(true);
			
			cox.setConnectTimeout(50000);
			cox.setReadTimeout(50000);

			cox.setInstanceFollowRedirects(false);
			cox.setRequestMethod("POST");
			cox.setRequestProperty("Accept-Encoding", "gzip");  
			cox.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			cox.setRequestProperty("charset", "utf-8");
			cox.setRequestProperty("Content-Length",
					Integer.toString(postDataLength));
			cox.setUseCaches(false);
			
			DataOutputStream wr = new DataOutputStream(cox.getOutputStream());
			wr.write(postData);
				
			InputStream is = cox.getInputStream();
			if (is != null) {
				byte[] header = new byte[2];
				BufferedInputStream bis = new BufferedInputStream(is);
				bis.mark(2);
				int result = bis.read(header);

				// reset输入流到开始位置
				bis.reset();
				BufferedReader reader = null;
				// 判断是否是GZIP格式
				int ss = (header[0] & 0xff) | ((header[1] & 0xff) << 8);
				if (result != -1 && ss == GZIPInputStream.GZIP_MAGIC) {
					// System.out.println("为数据压缩格式...");
					reader = new BufferedReader(new InputStreamReader(
							new GZIPInputStream(bis), "utf-8"));
				} else {
					// 取前两个字节
					reader = new BufferedReader(new InputStreamReader(bis, "utf-8"));
				}
				
				String s = reader.readLine();
				System.out.println(s);
				Date end = new Date();
					
				System.out.println("地理编码费时：" + ((end.getTime() - begin.getTime())) / 1000.0 + " [" +  (n - offset + 1) +  " 条记录]");
				
				return s;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} 
		
		return null;
				
	}

	public static void geolize(String province, String filename, String storepath) {

		String path=""+filename;
		Vector<String> pois = FileTool.Load(path,"utf-8"); 	// plan\\bj_items.txt");
		String srv = "http://geocode.svail.com"; //"http://192.168.6.9";"http://192.168.6.9"; // 
		String request = srv + ":8080/p41?f=json";		// "http://geocode.svail.com:8080/p41?f=xml";
		
		String parameters = null;
		
		//File f = new File(fileName);
		//String tf = f.getName();
		
		try {
			parameters = "&within="
				+ java.net.URLEncoder.encode(province, "UTF-8")
				+ "&key=327D6A095A8111E5BFE0B8CA3AF38727&queryStr=";
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    // String parameters = "&key=F37EC86E46DD11E58EBB48F1DE870894&queryStr=";
		// http://geocode.svail.com:8080/p41?f=xml&queryStr=%E5%8C%97%E4%BA%AC%E5%B8%82%E6%9C%9D%E9%98%B3%E5%8C%BA%E5%8C%97%E8%BE%B0%E4%B8%9C%E8%B7%AF6%E5%8F%B7%E6%89%80&key=EF4070EC424911E58EBB48F1DE870894
		boolean batch = true;
		
		if (batch)
			request = srv + ":8080/p4b?f=json";
		Gson gson = new Gson();
		int offset = 0;
		StringBuffer sb = new StringBuffer();
		Vector<String> validpois = new Vector<String>();

		for (int n = 0; n < pois.size(); n++) {
			if (batch) {
				String addr = null;

				String info=pois.elementAt(n);
				if(info.endsWith("},")){
					info=info.replace("},","}");
				}
				JSONObject obj=JSONObject.fromObject(info);

				String title="";
				String abstr="";
				if(obj.containsKey("Title")){
					title=obj.getString("Title");
				}
				if(obj.containsKey("abstract")){
					abstr=obj.getString("abstract");
				}
				addr=title+abstr;

				validpois.add(info);
				if (addr == null)
					sb.append("位置未知\n");
				else
					sb.append(addr.replace("\n", "").replace("\r", "").replace("\t", "").replace("。", "").replace(",", "").replace("、", "").replace("，", "")).append("\n");

				if ((n + 1) % 800 == 0 || n + 1 == pois.size()) {
					String urlParameters = sb.toString();
					String ct = null;
					
					ct = batchParse(request, parameters, urlParameters, n, offset);

					if (ct == null)
					{
						int cnt = 0; 
						while (cnt < 10)
						{
							ct = batchParse(request, parameters, urlParameters, n, offset);
							if (ct != null)
								break;
							cnt ++;
						}
					}

					JsonParser parser = new JsonParser();

					//通过JsonParser对象可以把json格式的字符串解析成一个JsonElement对象
					try {
						FileTool.Dump(ct, "D:/text.txt", "UTF-8");
						JsonElement el = parser.parse(ct);

						//把JsonElement对象转换成JsonObject
						JsonObject jsonObj = null;
						if(el.isJsonObject())
						{
							jsonObj = el.getAsJsonObject();
							GeoQuery gq = gson.fromJson(jsonObj, GeoQuery.class);
							System.out.println(gq.getResult().size());
							String lnglat = "";
							String Admin="";
							if (gq != null && gq.getResult() != null && gq.getResult().size() > 0) {
								System.out.println("这批数据没有问题！");
								for (int m = 0; m < gq.getResult().size(); m ++) {
									if (gq.getResult().get(m) != null && gq.getResult().get(m).getLocation() != null) {
										if(gq.getResult().get(m).getLocation().getRegion()!=null){
											try {
												String provinces=gq.getResult().get(m).getLocation().getRegion().getProvince();
												String city=gq.getResult().get(m).getLocation().getRegion().getCity();
												String county=gq.getResult().get(m).getLocation().getRegion().getCounty();
												String town=gq.getResult().get(m).getLocation().getRegion().getTown();

												Admin=(provinces+","+city+","+county+","+town).replace("null","");

											}catch (NullPointerException e){
												System.out.println("admin这里出了问题？");
											}
										}else{
											Admin="暂无";
										}

										double longitude=gq.getResult().get(m).getLocation().getLng();
										double latitude=gq.getResult().get(m).getLocation().getLat();

										String poitemp= validpois.elementAt(m);
										JSONObject jobj=JSONObject.fromObject(poitemp);
										jobj.put("region",Admin);
										jobj.put("longitude",longitude);
										jobj.put("latitude", latitude);

										FileTool.Dump(jobj.toString().replace(" ", ""), storepath+filename.replace(".json", "")  + "_result.txt", "UTF-8");

									}else {
										FileTool.Dump(validpois.elementAt(m).replace(" ", ""), storepath+"nonPostalCoor\\"+filename.replace(".json", "")  + "_nonPostalCoor.txt", "UTF-8");
									}
								}
							}
						}
					}catch (JsonSyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					sb.setLength(0);
					offset = n + 1;
				} 
			}
		}

	}
	
	/* 处理TAB分隔的文本文件 */
	public static void geolizeTabFile(String province, String fileName, int index, String targetFolder) {
		Vector<String> pois = FileTool.Load(fileName, "UTF-8"); 	// plan\\bj_items.txt");
		String srv = "http://geocode.svail.com"; //"http://192.168.6.9";"http://192.168.6.9"; // 
		String request = srv + ":8080/p41?f=json";		// "http://geocode.svail.com:8080/p41?f=xml";
		
		String parameters = null;
		
		File f = new File(fileName);
		String tf = f.getName();
		
		try {
			parameters = "&within="
				+ java.net.URLEncoder.encode(province, "UTF-8")
				+ "&key=327D6A095A8111E5BFE0B8CA3AF38727&queryStr=";
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    // String parameters = "&key=F37EC86E46DD11E58EBB48F1DE870894&queryStr=";
		// http://geocode.svail.com:8080/p41?f=xml&queryStr=%E5%8C%97%E4%BA%AC%E5%B8%82%E6%9C%9D%E9%98%B3%E5%8C%BA%E5%8C%97%E8%BE%B0%E4%B8%9C%E8%B7%AF6%E5%8F%B7%E6%89%80&key=EF4070EC424911E58EBB48F1DE870894
		boolean batch = true;
		
		if (batch)
			request = srv + ":8080/p4b?f=json";
		Gson gson = new Gson();
		int offset = 0;
		StringBuffer sb = new StringBuffer();
		for (int n = 0; n < pois.size(); n++) {
			String ks[] = pois.get(n).split("\t");
			
			if (batch) {
				String addr = ks[index];

				if (addr == null)
					sb.append("位置未知\n");
				else
					sb.append(addr.replace("\n", "").replace("\r", "").replace("\t", "").replace("。", "").replace(",", "").replace("、", "").replace("，", "")).append("\n");

				if ((n + 1) % 800 == 0 || n + 1 == pois.size()) {
					String urlParameters = sb.toString();
					String ct = null;
					
					ct = batchParse(request, parameters, urlParameters, n, offset);

					if (ct == null)
					{
						int cnt = 0; 
						while (cnt < 10)
						{
							ct = batchParse(request, parameters, urlParameters, n, offset);
							if (ct != null)
								break;
							cnt ++;
						}
					}

					JsonParser parser = new JsonParser();

					//通过JsonParser对象可以把json格式的字符串解析成一个JsonElement对象
					try {
						FileTool.Dump(ct, "D:/text.txt", "UTF-8");
						JsonElement el = parser.parse(ct);

						//把JsonElement对象转换成JsonObject
						JsonObject jsonObj = null;
						if(el.isJsonObject())
						{
							jsonObj = el.getAsJsonObject();
							GeoQuery gq = gson.fromJson(jsonObj, GeoQuery.class);
							String lnglat = "";
							if (gq != null && gq.getResult() != null && gq.getResult().size() > 0)
							{
								for (int m = 0; m < gq.getResult().size(); m ++)
								{
									if (gq.getResult().get(m) != null && gq.getResult().get(m).getLocation() != null)
									{
										String o = pois.get(offset + m);
										o += "\t" + gq.getResult().get(m).getLocation().getLat();
										o += "\t" + gq.getResult().get(m).getLocation().getLng();

										FileTool.Dump(o, targetFolder + "//" + tf.replace(".txt", "_geo.txt"), "UTF-8");
									}
									else
									{

										FileTool.Dump(pois.elementAt(offset + m), targetFolder + "//" + tf.replace(".txt", "_error.txt"), "UTF-8");
									}
								}
							}
						}
					}catch (JsonSyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					sb.setLength(0);
					offset = n + 1;
				} 
			}
		}

	}

	public static void main(String[] args) {
		
		geolizeTabFile("广东", "D:/Temp/GZYC.txt", 1,  "D:/Temp"  );
		/*String cont = FileTool.Load("D:/test.json", "UTF-8").get(0);
		
		System.out.println(cont.substring(53400));
		JsonParser parser = new JsonParser();
		JsonElement el = parser.parse(cont);
		Gson gson = new Gson();
		//把JsonElement对象转换成JsonObject
		JsonObject jsonObj = null;
		if(el.isJsonObject())
		{
			jsonObj = el.getAsJsonObject();
			GeoQuery gq = gson.fromJson(jsonObj, GeoQuery.class);
			String lnglat = "";
			if (gq != null && gq.getResult() != null && gq.getResult().size() > 0)
			{
				for (int m = 0; m < gq.getResult().size(); m ++)
				{
					// Result rs = gq.getResult().get(m);
					// System.out.println(gq.getResult().get(m).getQuery_string());
					if (gq.getResult().get(m) != null && gq.getResult().get(m).getLocation() != null)
					{
						
					}
					else
					{
						
					}
				}
			}
		}
*/
	
		String folder = "D:\\Temp\\crawldb\\plan\\";
		String SHANGHAI_FILES[] = {"建设项目选址意见书.txt",
			"建设工程规划许可证（建筑类）.txt",
			"建设用地规划许可证.txt",
			"建设工程设计方案.txt",
			"建设工程设计要求.txt",
			"建设工程竣工规划验收合格证.txt"
		};
		
		String BEIJING_FILES[] = {"朝阳规划公告.txt",
				"朝阳规划公示.txt",
				"地名.txt",
				"地名公示.txt",
				"东城规划公告.txt",
				"东城规划公示.txt",
				"丰台规划公告.txt",
				"丰台规划公示.txt",
				"规划竣工验收.txt",
				"规划类公告.txt",
				"规划类公示.txt",
				"规划许可证.txt",
				"规划意见书.txt",
				"海淀规划公示.txt",
				"建筑名称核准.txt",
				"平谷规划公示.txt",
				"石景山规划公示.txt",
				"顺义规划公示.txt",
				"通州规划公示.txt",
				"西城规划公告.txt",
				"西城规划公示.txt",
				"延庆规划公示.txt",
				"用地许可证.txt",
			};
		String GUANGZHOU_FILES[] = {
				"工程规划许可证.txt",
				"规划验收合格证.txt",
				"批后公布.txt",
				"批前公示.txt",
				"受理公示.txt",
				"项目选址意见书.txt",
				"用地规划许可证.txt",
		};
		
		String SHENZHEN_FILES[] = {
				"建设工程规划验收合格证.txt",
				"建设工程规则许可证.txt",
				"建设用地规划许可证.txt",
				"选址意见书.txt",

		};
		String CHONGQING_FILES[] = {
				"建设工程规划许可证.txt",
				"建设工程选址意见书.txt",
				"建设用地规划许可证.txt",
				"竣工规划核实确认书.txt",
		};
		
		String REGION = "重庆";
		String PROVINCE = "重庆";
		String FIFLES[] = CHONGQING_FILES;
		
		// putText("1975年，陕西省延川县文安驿公社梁家河大队知青、党支部书记 1975年，清华大学化工系基本有机合成专业学习 1982年，国务院办公厅、中央军委办公厅秘书（现役） 1982－1983年，河北省正定县委副书记 1983－1985年，河北省正定县委书记，正定县武装部第一政委、党委第一书记 1985－1988年，福建省厦门市委常委、副市长 1988－1990年，福建省宁德地委书记，宁德军分区党委第一书记");
		for (int n = 0; n < FIFLES.length; n ++)
			geolize(PROVINCE, folder + REGION + "//" + FIFLES[n], "D:/Temp/crawldb/plan/geodata/" + REGION);
	}
}
