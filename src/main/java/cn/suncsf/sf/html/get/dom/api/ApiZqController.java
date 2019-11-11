package cn.suncsf.sf.html.get.dom.api;

import cn.suncsf.framework.core.entity.EntityDatabase;
import cn.suncsf.framework.core.utils.ArrayUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/api/zq")
public class ApiZqController {
    static String phoneRegex = "(1|861)(3|5|8)\\d{9}$*";
    static String regex = "^[0][1-9]{2,3}-[0-9]{5,10}$";
    static String dtregex = "^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$";

    //    private static ThreadPoolExecutor pool = new ThreadPoolExecutor(1,2,1000,new BlockingDeque<>(10));
    @GetMapping("/zqdt")
    public List<Person> shouldAnswerWithTrue(String field, int pageIndex, int pages, HttpServletResponse response) {


        List<Person> list = new ArrayList<>();

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
            map.put("un", StringUtils.join(String.valueOf(new Date().getTime()), "@qq.com"));
            map.put("UOR", "s.share.baidu.com,service.weibo.com,login.sina.com.cn");
            map.put("TC-Page-G0", "cdcf495cbaea129529aa606e7629fea7");
            map.put("TC-Ugrow-G0", "e66b2e50a7e7f417f6cc12eec600f517");
            map.put("TC-V5-G0", "b8dff68fa0e04b3c8f0ba710d783479a");
            map.put("wb_bub_find", "1");
            map.put("wb_bub_find_3459285440", "1");
            map.put("wb_bub_find_5601838195", "1");
            map.put("WBStorage", "2c466cc84b6dda21|undefined");
            String agent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0";
            System.out.println("field=" + field + "，pageIndex=" + pageIndex + "，pages=" + pages);
            final CountDownLatch countDownLatch = new CountDownLatch(pages);
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            while (pageIndex < pages + 1) {


                final int mPageIndex = pageIndex;
                pageIndex++;
                Document document = Jsoup.connect("https://s.weibo.com/user?q=" + field + "&auth=org_vip&page=" + mPageIndex)
                        .timeout(10000)
                        .userAgent(agent)
                        .ignoreHttpErrors(true)
                        .cookies(map)
                        .get();
                System.out.println("关键词：" + field + "，第" + String.valueOf(mPageIndex) + "页，共" + String.valueOf(pages) + "页。");
                Elements elements = document.getElementsByAttributeValue("class", "card card-user-b s-pg16 s-brt1");
                for (int i = 0; i < elements.size(); i++) {
                    Person person = new Person();
                    Element element = elements.get(i);
                    element = element.getElementsByAttributeValue("class", "info").first();
                    element = element.getElementsByTag("div").first().getElementsByTag("a").first();
                    String url = element.attr("href");
                    String name = element.text();
                    String dates = "";
                    Document meHome = null;
                    try {
                        Document home = Jsoup.connect("http:" + url)
                                .timeout(2000)
                                .userAgent(agent)
                                .cookies(map)
                                .ignoreHttpErrors(true)
                                .get();
                        String hmStr = home.html();
                        boolean f = true;
                        List<Date> dateList = new ArrayList<>();
                        while (f) {
                            String dts = getPhoneStr(hmStr);
                            if (StringUtils.isNotBlank(dts)) {
                                dateList.add(dateFormat.parse(dts));
                                hmStr = hmStr.replace(dts, "");
                                continue;
                            }
                            f = false;
                        }
                        Optional<Date> date = dateList.stream().max(Date::compareTo);
                        if (date.isPresent()) {
                            dates = dateFormat.format(date);
                        }
                        System.out.println(dates);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.println("http:" + url + "/about");
                    try {
                        Document dt = Jsoup.connect("http:" + url)
                                .timeout(2000)
                                .cookies(map)
                                .userAgent(agent)
                                .ignoreHttpErrors(true).get();

                        Elements dtmt = dt.select("script");
                        String dts = getTelnum(dtmt.html(), dtregex);
//                        System.out.println(dtmt.html());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    try {
                        meHome = Jsoup.connect("http:" + url + "/about")
                                .timeout(2000).cookies(map)
                                .userAgent(agent)
                                .ignoreHttpErrors(true).get();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    try {
                        if (meHome == null) {
                            continue;
                        }
                        String sc = meHome.select("script").html();
                        int index = sc.indexOf("电话");
                        if (index < 0) {
                            String tempStr = "";
                            index = sc.indexOf("微信");
                            if (index > -1) {
                                String ct = sc.substring(index);
                                index = ct.indexOf("FM.view");
                                if (index > -1) {
                                    ct = ct.substring(0, index);
                                }
                                tempStr += ct;
                            }
                            sc = tempStr;
                            if (StringUtils.isBlank(sc)) {
                                continue;
                            }
                        } else {
                            sc = sc.substring(index);
                            sc = sc.substring(0, sc.indexOf("<\\/li>"));
                            index = sc.indexOf("FM.view");
                            if (index > -1) {
                                sc = sc.substring(0, index);
                            }
                        }


                        String content = getTextFromHtml(Jsoup.parse(sc).body().text());
                        if (StringUtils.isNotBlank(content)) {
                            content = content.replace(" ", "")
                                    .replace("\\", "")
                                    .replace("t", "")
                                    .replace("r", "")
                                    .replace("n", "");
                        }

                        person.setName(name);
                        person.setUrl("http:" + url);
                        person.setTel(content);
                        person.setDates(dates);
                        list.add(person);
                        System.out.println(person.toJson());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                }


            }
//            String filePath = "F:" + File.separator + "t.xls";
            HSSFWorkbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("名称");
            row.createCell(1).setCellValue("网址");
            row.createCell(2).setCellValue("电话");
            row.createCell(3).setCellValue("相关时间");
            HSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(true);
            for (int i = 0; i < list.size(); i++) {
                Person person = list.get(i);
                row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(person.getName());
                row.createCell(1).setCellValue(person.getUrl());

                row.createCell(2).setCellValue(person.getTel());
                Cell cell = row.createCell(3);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(person.getDates());
            }

            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition"
                    , "attachment;filename="
                            + URLEncoder.encode(field + "（" + (pageIndex - 1) + "-" + pages + "）数据导出("
                                    + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ").xls"
                            , "utf-8"));
            OutputStream stream = response.getOutputStream();
            workbook.write(stream);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    public String getPhoneStr(String hmStr) {
        String regEx = "\\d{4}-\\d{2}-\\d{2}";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(hmStr);
        if (m.find()) {
            return m.group();
        }
        return null;
    }

    public String getTelnum(String sParam, String p) {

        if (sParam.length() <= 0)
            return "";
        Pattern pattern = Pattern.compile(p);
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

    /**
     * 去除html代码中含有的标签
     *
     * @param htmlStr
     * @return
     */
    public static String delHtmlTags(String htmlStr) {
        //定义script的正则表达式，去除js可以防止注入
        String scriptRegex = "<script[^>]*?>[\\s\\S]*?<\\/script>";
        //定义style的正则表达式，去除style样式，防止css代码过多时只截取到css样式代码
        String styleRegex = "<style[^>]*?>[\\s\\S]*?<\\/style>";
        //定义HTML标签的正则表达式，去除标签，只提取文字内容
        String htmlRegex = "<[^>]+>";
        //定义空格,回车,换行符,制表符
        String spaceRegex = "\\s*|\t|\r|\n";

        // 过滤script标签
        htmlStr = htmlStr.replaceAll(scriptRegex, "");
        // 过滤style标签
        htmlStr = htmlStr.replaceAll(styleRegex, "");
        // 过滤html标签
        htmlStr = htmlStr.replaceAll(htmlRegex, "");
        // 过滤空格等
        htmlStr = htmlStr.replaceAll(spaceRegex, "");
        return htmlStr.trim(); // 返回文本字符串
    }

    /**
     * 获取HTML代码里的内容
     *
     * @param htmlStr
     * @return
     */
    public static String getTextFromHtml(String htmlStr) {
        //去除html标签
        htmlStr = delHtmlTags(htmlStr);
        //去除空格" "
        htmlStr = htmlStr.replaceAll(" ", "");
        return htmlStr;
    }

    class Person extends EntityDatabase {
        private String name;
        private String url;
        private String tel;
        private String dates;

        public String getDates() {
            return dates;
        }

        public void setDates(String dates) {
            this.dates = dates;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }
    }
}
