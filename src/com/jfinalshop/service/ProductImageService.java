package com.jfinalshop.service;

import com.baidu.ueditor.UeditorConfigKit;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.manager.AbstractFileManager;
import com.jfinal.core.JFinal;
import com.jfinalshop.bean.ProductImage;
import com.jfinalshop.bean.SystemConfig;
import com.jfinalshop.util.CommonUtil;
import com.jfinalshop.util.ImageUtil;
import com.jfinalshop.util.SystemConfigUtil;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Service实现类 - 商品图片
 * 
 */

public class ProductImageService {
	
	public static final ProductImageService service = new ProductImageService();
	/**
	 * 生成商品图片（包括原图、大图、小图、缩略图）
	 * 
	 * @param uploadProductImageFile
	 *            上传图片文件
	 * 
	 */
	public ProductImage buildProductImage(File uploadProductImageFile) {
		SystemConfig systemConfig = SystemConfigUtil.getSystemConfig();
		String sourceProductImageFormatName = ImageUtil.getImageFormatName(uploadProductImageFile);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
		String dateString = simpleDateFormat.format(new Date());
		String uuid = CommonUtil.getUUID();
		
		String sourceProductImagePath = SystemConfig.UPLOAD_IMAGE_DIR + dateString + "/" + uuid + "." + sourceProductImageFormatName;
		try {
			AbstractFileManager manager = UeditorConfigKit.getFileManager();
			State state = manager.saveFile(new FileInputStream(uploadProductImageFile), SystemConfig.UPLOAD_IMAGE_DIR, sourceProductImagePath);
			if (!state.isSuccess()) {
				throw new RuntimeException("save file error:" + state.toJSONString());
			}
		} catch (IOException e) {
			throw new RuntimeException("save file error:", e);
		}
		ProductImage productImage = new ProductImage();
		productImage.setId(uuid);
		productImage.setSourceProductImagePath(sourceProductImagePath);
		return productImage;
	}
	
	/**
	 * 根据ProductImage对象删除图片文件
	 * 
	 * @param productImage
	 *            ProductImage
	 * 
	 */
	public void deleteFile(ProductImage productImage) {
		File sourceProductImageFile = new File(JFinal.me().getServletContext().getRealPath(productImage.getSourceProductImagePath()));
		if (sourceProductImageFile.exists()) {
			sourceProductImageFile.delete();
		}
		File bigProductImageFile = new File(JFinal.me().getServletContext().getRealPath(productImage.getBigProductImagePath()));
		if (bigProductImageFile.exists()) {
			bigProductImageFile.delete();
		}
		File smallProductImageFile = new File(JFinal.me().getServletContext().getRealPath(productImage.getSmallProductImagePath()));
		if (smallProductImageFile.exists()) {
			smallProductImageFile.delete();
		}
		File thumbnailProductImageFile = new File(JFinal.me().getServletContext().getRealPath(productImage.getThumbnailProductImagePath()));
		if (thumbnailProductImageFile.exists()) {
			thumbnailProductImageFile.delete();
		}
	}

}