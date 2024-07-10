package com.yuan.api;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.yuan.config.ConfigKeyValue;
import com.yuan.util.IpUtil;
import com.yuan.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

/**
 * @author yl
 * @date 2021/12/18 22:26
 */
@Slf4j
@RestController
public class TempApi {

    public static Object[][] IMAGES;

    static {

        IMAGES = new Object[6][];

        IMAGES[0] = new Object[]{"0.jpg", new int[]{0, 0, 0}};
        IMAGES[1] = new Object[]{"1.jpg", new int[]{60, 34, 19}};
        IMAGES[2] = new Object[]{"2.jpg", new int[]{142, 101, 105}};
        IMAGES[3] = new Object[]{"3.jpg", new int[]{202, 165, 163}};
        IMAGES[4] = new Object[]{"4.jpg", new int[]{87, 138, 59}};
        IMAGES[5] = new Object[]{"5.jpg", new int[]{90, 145, 123}};
    }

    private static BufferedImage generateImage(String selectedImage, int[] selectedColor, String cityInfo, String ip, String os, String browser) throws FontFormatException {
        try {
            BufferedImage im = ImageIO.read(new File(selectedImage));
            Graphics2D g = im.createGraphics();
            Color color = new Color(selectedColor[0], selectedColor[1], selectedColor[2]);

            File directory = new File("..");

            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(directory.getCanonicalPath() + "/IP-PHP/HarmonyOS_Sans_SC_Medium.ttf")).deriveFont(16f);
            g.setFont(font);

            Date now = new Date();
            String nowStr = DateUtil.format(now, DatePattern.CHINESE_DATE_TIME_PATTERN);

            g.setColor(color);
            g.drawString("欢迎来您 " + cityInfo, 18, 40);
            g.drawString("日期: " + nowStr, 18, 72);
            g.drawString("IP: " + ip, 10, 104);
            g.drawString("操作系统: " + os, 10, 140);
            g.drawString("浏览器: " + browser, 10, 175);
            g.dispose();
            return im;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] convertToByteArray(BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();
            return imageBytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/ip-img")
    public void t2(@RequestHeader Map<String, Object> headers, HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "type", required = false) Integer type) throws IOException, FontFormatException {


        if (ObjUtil.isNull(type) || type < 0 || type > 5) {
            type = RandomUtil.randomInt(0, 6);
        }

        Object[] selectedImageObject = IMAGES[type];
        File directory = new File("..");
        // 假设的函数调用，你需要替换为你自己的逻辑
        String selectedImage = directory.getCanonicalPath() + "/IP-PHP/" + selectedImageObject[0];
        int[] selectedColor = (int[]) selectedImageObject[1];
        String clientIP = ServletUtil.getClientIP(request);

        String cityInfo = IpUtils.getCityInfo(clientIP);

        String userAgentStr = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
        // 获取浏览器类型
        String browserName = userAgent.getBrowser().getName();
        // 获取操作系统类型
        String osName = userAgent.getOs().getName();

        System.out.println("Browser: " + browserName);
        System.out.println("OS: " + osName);

        BufferedImage image = generateImage(selectedImage, selectedColor, cityInfo, clientIP, osName, browserName);
        byte[] imageBytes = convertToByteArray(image);


        // 设置响应的内容类型为图片
        response.setContentType("image/png"); // 或者其他图片类型，如 "image/jpeg"

        // 设置 Content-Length 头部，告诉客户端图片的大小
        response.setContentLength(imageBytes.length);

        // 获取响应的输出流
        OutputStream out = response.getOutputStream();

        // 写入图片数据到输出流
        out.write(imageBytes);

        // 关闭输出流
        out.flush();
        out.close();
    }

    @RequestMapping("/**")
    public Map t1(@RequestHeader Map<String, Object> headers, HttpServletRequest request) {
        String uuid = UUID.fastUUID().toString();
        log.info("uuid:{}", uuid);
        String ipAddress = IpUtil.getIpAddr(request);
        headers.put("uuid", uuid);
        headers.put("xxx-ipAddress", ipAddress);
        String clientIP = ServletUtil.getClientIP(request);
        headers.put("clientIP", clientIP);
        headers.put("xxx-getRequestURI", request.getRequestURL());
        headers.put("xxx-getServletPath", request.getServletPath());
        headers.put("xxx-getRequestURL", request.getRequestURL());
        headers.put("xxx-getRemoteAddr", request.getRemoteAddr());
        headers.put("xxx-getRemoteUser", request.getRemoteUser());
        headers.put("xxx-getUserPrincipal", request.getUserPrincipal());
        headers.put("xxx-getQueryString", request.getQueryString());
        return headers;
    }

    @Autowired
    private ConfigKeyValue configKeyValue;
    @RequestMapping("/t3")
    public Object t3() {
        return configKeyValue;
    }
}
