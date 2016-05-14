package com.jfinalshop.util;

import com.jfinalshop.bean.SystemConfig.WatermarkPosition;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类 - 图片处理
 */

public class ImageUtil {

    // 获取img标签正则
    private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
    // 获取src路径的正则
    private static final String IMGSRC_REG = "(http://7xqgf8.com1.z0.glb.clouddn.com\"?(.*?))(\"|>|\\s+)";

    /**
     * 图片缩放(图片等比例缩放为指定大小，空白部分以白色填充)
     *
     * @param srcBufferedImage 源图片
     * @param destFile         缩放后的图片文件
     */
    public static void zoom(BufferedImage srcBufferedImage, File destFile, int destHeight, int destWidth) {
        try {
            int imgWidth = destWidth;
            int imgHeight = destHeight;
            int srcWidth = srcBufferedImage.getWidth();
            int srcHeight = srcBufferedImage.getHeight();
            if (srcHeight >= srcWidth) {
                imgWidth = (int) Math.round(((destHeight * 1.0 / srcHeight) * srcWidth));
            } else {
                imgHeight = (int) Math.round(((destWidth * 1.0 / srcWidth) * srcHeight));
            }
            BufferedImage destBufferedImage = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = destBufferedImage.createGraphics();
            graphics2D.setBackground(Color.WHITE);
            graphics2D.clearRect(0, 0, destWidth, destHeight);
            graphics2D.drawImage(srcBufferedImage.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH), (destWidth / 2) - (imgWidth / 2), (destHeight / 2) - (imgHeight / 2), null);
            graphics2D.dispose();
            ImageIO.write(destBufferedImage, "JPEG", destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加图片水印
     *
     * @param srcBufferedImage 需要处理的源图片
     * @param destFile         处理后的图片文件
     * @param watermarkFile    水印图片文件
     */
    public static void imageWatermark(BufferedImage srcBufferedImage, File destFile, File watermarkFile, WatermarkPosition watermarkPosition, int alpha) {
        try {
            int srcWidth = srcBufferedImage.getWidth();
            int srcHeight = srcBufferedImage.getHeight();
            BufferedImage destBufferedImage = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = destBufferedImage.createGraphics();
            graphics2D.setBackground(Color.WHITE);
            graphics2D.clearRect(0, 0, srcWidth, srcHeight);
            graphics2D.drawImage(srcBufferedImage.getScaledInstance(srcWidth, srcHeight, Image.SCALE_SMOOTH), 0, 0, null);

            if (watermarkFile != null && watermarkFile.exists() && watermarkPosition != null && watermarkPosition != WatermarkPosition.no) {
                BufferedImage watermarkBufferedImage = ImageIO.read(watermarkFile);
                int watermarkImageWidth = watermarkBufferedImage.getWidth();
                int watermarkImageHeight = watermarkBufferedImage.getHeight();
                graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha / 100.0F));
                int x = 0;
                int y = 0;
                if (watermarkPosition == WatermarkPosition.topLeft) {
                    x = 0;
                    y = 0;
                } else if (watermarkPosition == WatermarkPosition.topRight) {
                    x = srcWidth - watermarkImageWidth;
                    y = 0;
                } else if (watermarkPosition == WatermarkPosition.center) {
                    x = (srcWidth - watermarkImageWidth) / 2;
                    y = (srcHeight - watermarkImageHeight) / 2;
                } else if (watermarkPosition == WatermarkPosition.bottomLeft) {
                    x = 0;
                    y = srcHeight - watermarkImageHeight;
                } else if (watermarkPosition == WatermarkPosition.bottomRight) {
                    x = srcWidth - watermarkImageWidth;
                    y = srcHeight - watermarkImageHeight;
                }
                graphics2D.drawImage(watermarkBufferedImage, x, y, watermarkImageWidth, watermarkImageHeight, null);
            }
            graphics2D.dispose();
            ImageIO.write(destBufferedImage, "JPEG", destFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片缩放并添加图片水印(图片等比例缩放为指定大小，空白部分以白色填充)
     *
     * @param srcBufferedImage 需要处理的图片
     * @param destFile         处理后的图片文件
     * @param watermarkFile    水印图片文件
     */
    public static void zoomAndWatermark(BufferedImage srcBufferedImage, File destFile, int destHeight, int destWidth, File watermarkFile, WatermarkPosition watermarkPosition, int alpha) {
        try {
            int imgWidth = destWidth;
            int imgHeight = destHeight;
            int srcWidth = srcBufferedImage.getWidth();
            int srcHeight = srcBufferedImage.getHeight();
            if (srcHeight >= srcWidth) {
                imgWidth = (int) Math.round(((destHeight * 1.0 / srcHeight) * srcWidth));
            } else {
                imgHeight = (int) Math.round(((destWidth * 1.0 / srcWidth) * srcHeight));
            }

            BufferedImage destBufferedImage = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = destBufferedImage.createGraphics();
            graphics2D.setBackground(Color.WHITE);
            graphics2D.clearRect(0, 0, destWidth, destHeight);
            graphics2D.drawImage(srcBufferedImage.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH), (destWidth / 2) - (imgWidth / 2), (destHeight / 2) - (imgHeight / 2), null);
            if (watermarkFile != null && watermarkFile.exists() && watermarkPosition != null && watermarkPosition != WatermarkPosition.no) {
                BufferedImage watermarkBufferedImage = ImageIO.read(watermarkFile);
                int watermarkImageWidth = watermarkBufferedImage.getWidth();
                int watermarkImageHeight = watermarkBufferedImage.getHeight();
                graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha / 100.0F));
                int x = 0;
                int y = 0;
                if (watermarkPosition == WatermarkPosition.topLeft) {
                    x = 0;
                    y = 0;
                } else if (watermarkPosition == WatermarkPosition.topRight) {
                    x = destWidth - watermarkImageWidth;
                    y = 0;
                } else if (watermarkPosition == WatermarkPosition.center) {
                    x = (destWidth - watermarkImageWidth) / 2;
                    y = (destHeight - watermarkImageHeight) / 2;
                } else if (watermarkPosition == WatermarkPosition.bottomLeft) {
                    x = 0;
                    y = destHeight - watermarkImageHeight;
                } else if (watermarkPosition == WatermarkPosition.bottomRight) {
                    x = destWidth - watermarkImageWidth;
                    y = destHeight - watermarkImageHeight;
                }
                graphics2D.drawImage(watermarkBufferedImage, x, y, watermarkImageWidth, watermarkImageHeight, null);
            }
            graphics2D.dispose();
            ImageIO.write(destBufferedImage, "JPEG", destFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片文件的类型.
     *
     * @param uploadFile 图片文件对象.
     * @return 图片文件类型
     */
    public static String getImageFormatName(File uploadFile) {
        try {
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(uploadFile);
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInputStream);
            if (!iterator.hasNext()) {
                return null;
            }
            ImageReader imageReader = (ImageReader) iterator.next();
            imageInputStream.close();
            return imageReader.getFormatName().toLowerCase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertToWeChatSize(String origin) {
        String result = origin;
        Pattern p = Pattern.compile(IMGURL_REG, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(origin);
        String imgUrl = null;
        String imgSrc = null;
        while (m.find()) {
            imgUrl = m.group();
            Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(imgUrl);
            String newUrl = null;
            while (matcher.find()) {
                imgSrc = matcher.group(1);
                System.out.println(matcher.group(1));
                newUrl = matcher.replaceAll(imgSrc + "-o\"");
            }
            if (newUrl != null) {
                result = m.replaceAll(newUrl);
            }
        }
        return result;
    }


}