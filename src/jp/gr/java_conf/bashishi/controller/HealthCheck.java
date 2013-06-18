package jp.gr.java_conf.bashishi.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HealthCheck {
    /**
     * ヘルスチェックページとシステムパラメータを HttpServletResponse に出力する。
     * @param request
     * @param response
     * @return
     */
    public static void writePage(HttpServletRequest request, HttpServletResponse response) {
        // 表示を許可するパラメータ
        List<String> dispKeys  = new ArrayList<>();
        dispKeys.add("HOSTNAME");
        dispKeys.add("REMOTE_HOST");
        dispKeys.add("REMOTE_ADDR");
        dispKeys.add("SERVER_NAME");
        dispKeys.add("REQUEST_URL");
        dispKeys.add("REFERER");
        dispKeys.add("USER_AGENT");

        // リクエスト情報取得
        Map<String, String> propMap = new LinkedHashMap<>();
        propMap.put("HOSTNAME", System.getenv("HOSTNAME"));
        propMap.put("REMOTE_HOST", request.getRemoteHost());
        propMap.put("REMOTE_ADDR", request.getRemoteAddr());
        propMap.put("SERVER_NAME", request.getServerName());
        propMap.put("REQUEST_URL", request.getRequestURL().toString());
        propMap.put("REFERER", request.getHeader("referer"));
        propMap.put("USER_AGENT", request.getHeader("user-agent"));

        // クッキー取得
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                propMap.put(c.getName(), c.getValue());
                dispKeys.add(c.getName());
            }
        }

        // システムプロパティ取得
        Properties prop = System.getProperties();
        for (Enumeration<?> e = prop.propertyNames(); e.hasMoreElements();) {
            String name = (String)e.nextElement();
            String value = prop.getProperty(name, "");
            propMap.put(name, value);
        }


        // ヘルスチェックページ出力
        PrintWriter out = null;
        try {
            response.setContentType("text/html;charset=UTF-8");
            out = response.getWriter();
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Health Check</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("Hello World!<br />");
            out.println(new Date().toString());
            out.println("<table border=\"1\"><tr><th>propertyName</th><th>value</th></tr>");
            for (Iterator<String> iterator = propMap.keySet().iterator(); iterator.hasNext();) {
                String key = iterator.next();
                String value = "**********";
                if (dispKeys.isEmpty() || dispKeys.contains(key)) {
                    value = propMap.get(key);
                }
                out.print(String.format("<tr><td>%s</td><td>%s</td></tr>", key, value));
            }
            out.println("</table>");
            out.println("</body>");
            out.println("</html>");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (out != null) out.close();
        }
    }


    
    
}
