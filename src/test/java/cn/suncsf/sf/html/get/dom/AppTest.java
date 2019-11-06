package cn.suncsf.sf.html.get.dom;

import static org.junit.Assert.assertTrue;

import cn.suncsf.framework.core.common.KeyValueStr;
import cn.suncsf.framework.core.utils.DateUtil;
import cn.suncsf.framework.core.utils.OkHttpUtil;
import cn.suncsf.framework.core.utils.extr.BasicAuthInterceptor;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sun.misc.BASE64Encoder;
import sun.net.www.http.HttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unit test for simple App.
 */
@RunWith(JUnit4.class)
public class AppTest {


    @Test
    public void shouldAnswerWithTrue() {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("_s_tentry", "login.sina.com.cn");
            map.put("Apache", "3072989182787.392.1480858059338");
            map.put("login_sid_t", "89596622af1c55ce27d4e2014ce266f2");
            map.put("SCF", "Am1Px4Tom_6UrbPEEMj1BFx_b9I_R-SIe79a8uwyZZ1wAq1G9lz70IGe372BGUn-1P2iSOpnpHs1mG__cljsA08.");
            map.put("SINAGLOBAL", "1793024353389.4553.1479174837212");
            map.put("SUB", "_2AkMvGLRGdcNhrABVnfEXxG7maI1H-jzEiebBAn7uJhMyAxh77goMqSWXdZhKmkaeRY4olOi-mJAYtV-Mzg..");
            map.put("SUBP", "0033WrSXqPxfM72wWs9jqgMF55529P9D9WhiexOd4VLhUIk_kVInXZ9I5JpV2heRe02E1K.cSXWpMC4odcXt");
            map.put("SUHB", "0qjkCp7v-5C29u");
            map.put("ULV", "1480858059441:4:2:2:3072989182787.392.1480858059338:1480834549292");
            map.put("un", "383129967@qq.com");
            map.put("UOR", "s.share.baidu.com,service.weibo.com,login.sina.com.cn");
            map.put("TC-Page-G0", "cdcf495cbaea129529aa606e7629fea7");
            map.put("TC-Ugrow-G0", "e66b2e50a7e7f417f6cc12eec600f517");
            map.put("TC-V5-G0", "b8dff68fa0e04b3c8f0ba710d783479a");
            map.put("wb_bub_find", "1");
            map.put("wb_bub_find_3459285440", "1");
            map.put("wb_bub_find_5601838195", "1");
            map.put("WBStorage", "2c466cc84b6dda21|undefined");
            Document document = Jsoup.connect("https://s.weibo.com/user?q=服饰&auth=org_vip&page=1")
                    .cookies(map)
                    .get();
            String body = document.select("body").toString();


            String filePath = "F:" + File.separator + "t.xls";
//            Document document = Jsoup.connect("https://s.weibo.com/user?q=%E5%A8%B1%E4%B9%90&auth=org_vip").get();
//            File file = new File("F:"+File.separator +"t.xls");
//            if(!file.exists()){
//                file.createNewFile();
//            }
            HSSFWorkbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("名称");
            row.createCell(1).setCellValue("网址");
            row.createCell(2).setCellValue("电话");
            Elements elements = document.getElementsByAttributeValue("class", "card card-user-b s-pg16 s-brt1");
            for (int i = 0; i < elements.size(); i++) {
                row = sheet.createRow(i + 1);
                Element element = elements.get(i);


//                row.createCell(0).setCellValue(avatorHref.attr("href"));
                Element info = element.getElementsByAttributeValue("class", "info").first();
                Element urlElm = info.getElementsByTag("div").first().getElementsByTag("a").first();
                String url =  urlElm.attr("href");
                String name =  urlElm.text();
//                System.out.println(urlElm.html());
//                System.out.println(url);

                row.createCell(0).setCellValue(name);
                row.createCell(1).setCellValue(url);
                Document meHome  = Jsoup.connect("http:"+url+"/about").cookies(map).get();
//                Document meHome  = Jsoup.connect("https://weibo.com/meimeikongjian/about").cookies(map).get();
                String sc = meHome.select("script").html();
                int index = sc.indexOf("电话：");
                if(index < 0){
                    continue;
                }
                System.out.println(sc);
                String phone = getTelnum(sc.substring(index));
                System.out.println(phone);
                System.out.println("---------------------------");
//                Element bodyPages = meHome.getElementsByClass("WB_miniblog").first()
//                        .getElementsByClass("WB_miniblog_fb").first();
//                System.out.println(bodyPages.html());
                row.createCell(2).setCellValue(getTelnum(sc));
            }
            FileOutputStream out = new FileOutputStream(filePath);
            workbook.write(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public String getTelnum(String sParam){

        if(sParam.length()<=0)
            return "";
        Pattern pattern = Pattern.compile("(1|861)(3|5|8)\\d{9}$*");
        Matcher matcher = pattern.matcher(sParam);
        StringBuffer bf = new StringBuffer();
        while (matcher.find()) {
            bf.append(matcher.group()).append(",");
        }
        int len = bf.length();
        if (len > 0) {
            bf.deleteCharAt(len - 1);
        }
        return bf.toString();
    }

}
