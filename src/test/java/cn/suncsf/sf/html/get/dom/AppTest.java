package cn.suncsf.sf.html.get.dom;

import static org.junit.Assert.assertTrue;

import cn.suncsf.framework.core.utils.OkHttpUtil;
import okhttp3.OkHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sun.net.www.http.HttpClient;

import java.io.IOException;

/**
 * Unit test for simple App.
 */
@RunWith(JUnit4.class)
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
       String html = new OkHttpUtil
                .Builder()
                .build()
                .get("https://s.weibo.com/user?q=%E5%A8%B1%E4%B9%90&auth=org_vip");

        Document document = Jsoup.parse(html);
        Element element = document.getElementsByTag("html").first();
        document = Jsoup.parse(element.html());
//        element = document.getElementsByTag("body").first();
//        document = Jsoup.parse(element.html());
        element = document.getElementsByClass("m-main").first();
        document = Jsoup.parse(element.html());
        element = document.getElementById("pl_feed_main").firstElementSibling();
        document = Jsoup.parse(element.html());
        element = document.getElementsByClass("m-con-l").first();
        document = Jsoup.parse(element.html());
        element = document.getElementById("pl_user_feedList");
        document = Jsoup.parse(element.html());
        System.out.printf(document.text());
    }



}
