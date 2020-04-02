package com.hf.common.utils;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class CheckCode {
    private static int width = 100;// 定义图片的width
    private static int height = 40;// 定义图片的height
    private static int codeCount = 4;// 定义图片上显示验证码的个数
    private static int xx = 18;
    private static int fontHeight = 22;
    private static  int codeY = 27;
    private static String check= "234qwertyuiopasdf67ghjklzxcvbnm589";

    public static String getCode( HttpServletResponse response)throws ServletException, IOException {
        //服务器通知浏览器不要缓存
        response.setHeader("pragma","no-cache");
        response.setHeader("cache-control","no-cache");
        response.setHeader("expires","0");

        //在内存中创建一个长80，宽30的图片，默认黑色背景
        //参数一：长
        //参数二：宽
        //参数三：颜色
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        //获取画笔
        Graphics g = image.getGraphics();
        //设置画笔颜色为白色
        g.setColor(Color.white);
        //填充图片
        g.fillRect(0,0, width,height);

        // 创建一个随机数生成器类
        Random random = new Random();

        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.BOLD, fontHeight + 2 );
        // 设置字体。
        g.setFont(font);

        // 随机产生40条干扰线，使图象中的认证码不易被其它程序探测到。
        g.setColor(Color.BLACK);
        for (int i = 0; i < 30; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(16);
            int yl = random.nextInt(16);
            g.drawLine(x, y, x + xl, y + yl);
        }



        String code = "";
        //向图片上写入验证码
        int red = 0, green = 0, blue = 0;
        for (int i = 0; i < codeCount; i++) {
            // 每循环一次生成一个验证码
            int l = random.nextInt(check.length());
            char c = check.charAt(l);
            code += String.valueOf(c);


            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);

            // 用随机产生的颜色将验证码绘制到图像中。
            g.setColor(new Color(red, green, blue));
            g.drawString(String.valueOf(c), (i + 1) * xx, codeY);
        }

        //将内存中的图片输出到浏览器
        //参数一：图片对象
        //参数二：图片的格式，如PNG,JPG,GIF
        //参数三：图片输出到哪里去
        ImageIO.write(image,"PNG",response.getOutputStream());

        return code;
    }

}
