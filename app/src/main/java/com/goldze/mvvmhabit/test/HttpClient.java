package com.goldze.mvvmhabit.test;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

/**
 * @Author: zhouxiaolin
 * @CreateDate: 2020/6/4 12:08
 * @Description:
 */
public class HttpClient {
    public static String getHealthCode(String urls, JsonObject body) {

        try {
            //创建连接
            String defURL = "http://www.maiweiyun.com/personjkm.jsp";
            URL url = new URL(defURL);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");//请求post方式
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setDoInput(true);// 设置是否从HttpURLConnection输入，默认值为 true
            connection.setDoOutput(true);// 设置是否使用HttpURLConnection进行输出，默认值为 false
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            // 得到请求的输出流对象
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(body.toString());
            writer.flush();

            // 读取响应
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String strRead;
            StringBuffer sbf = new StringBuffer();
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            // 断开连接
            connection.disconnect();
            return sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String test() {
        try {
            JSONObject param = new JSONObject();
            param.put("password", "cf79ae6addba60ad018347359bd144d2");
            param.put("username", "mwsq");
            param.put("xm", "周晓琳");
            param.put("zjhm", "441421199204152426");
            String defURL = "http://www.maiweiyun.com/personjkm.jsp";
            URL url = new URL(defURL);
            // 打开和URL之间的连接
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");//请求post方式
            con.setUseCaches(false); // Post请求不能使用缓存
            con.setDoInput(true);// 设置是否从HttpURLConnection输入，默认值为 true
            con.setDoOutput(true);// 设置是否使用HttpURLConnection进行输出，默认值为 false
            con.setRequestProperty("Content-Type", "application/json");
            // 建立实际的连接
            con.connect();
            // 得到请求的输出流对象
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
            writer.write(param.toString());
            writer.flush();
            // 获取服务端响应，通过输入流来读取URL的响应
            InputStream is = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sbf = new StringBuffer();
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            con.disconnect();     // 打印读到的响应结果

            return sbf.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String testArea() {
        try {
            String url1 = "http://192.168.5.230:21664/api/VillageStructures?TenantCode=T0001&parentStructureID=965855";
            URL url = new URL(url1);
            //得到connection对象。
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //设置请求方式
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("ContentType", "application/json");
            connection.setRequestProperty("Authorization", "Basic QUNTV2ViQXBpOjEyMzQ1Ng==");
            //连接
            connection.connect();
            //得到响应码
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //得到响应流
                InputStream inputStream = connection.getInputStream();
                //将响应流转换成字符串
                String result = is2String(inputStream);//将流转换为字符串。
                inputStream.close();
                connection.disconnect();
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String is2String(InputStream inputStream) {
        try {
            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while (true) {
                if (!((lines = reader.readLine()) != null)) break;
                lines = URLDecoder.decode(lines, "utf-8");
                sb.append(lines);
            }
            System.out.println(sb);
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
