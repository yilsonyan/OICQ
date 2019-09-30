package com.qq.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageUtil {
	/**
	 * 图片文件的读取
	 * @param srcImgPath
	 * @return
	 */
	private static BufferedImage InputImage(String srcImgPath) {
		BufferedImage srcImage = null;
		try {
			FileInputStream in = new FileInputStream(srcImgPath);
			srcImage = ImageIO.read(in);
		} catch (IOException e) {
			System.out.println("读取图片文件出错！" + e.getMessage());
			e.printStackTrace();
		}
		return srcImage;
	}

	
	
	/**
	 * 把文件转换 为字节数组
	 * @param file
	 * @return
	 */
	 public static byte[] getBytesFromFile(File file) {  
	        byte[] ret = null;  
	        try {  
	            if (file == null) {  
	                return null;  
	            }  
	            FileInputStream in = new FileInputStream(file);  
	            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);  
	            byte[] b = new byte[4096];  
	            int n;  
	            while ((n = in.read(b)) != -1) {  
	                out.write(b, 0, n);  
	            }  
	            in.close();  
	            out.close();  
	            ret = out.toByteArray();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        return ret;  
	    }  
	
	
	
	/**
	 * 将图片按照指定的大小来压缩
	 * @param srcImgPath
	 * @param outImgPath
	 * @param new_w
	 * @param new_h
	 */
	public static void compressImage(String srcImgPath,
			int new_w, int new_h) {
		BufferedImage src = InputImage(srcImgPath);
		disposeImage(src, new_w, new_h);
	}

	/**
	 * 将给定图片按照一定的大小压缩到目的地
	 * @param file
	 * @param outImgPath
	 * @param new_w
	 * @param new_h
	 */
	public static BufferedImage compressImage(File file,
			int new_w, int new_h) {
		BufferedImage src = null;
		try {
			src = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return disposeImage(src, new_w, new_h);
	}
	
	
	
	/**
	 * 处理图片
	 * @param src
	 * @param outImgPath
	 * @param new_w
	 * @param new_h
	 */
	private synchronized static BufferedImage disposeImage(BufferedImage src,
			 int new_w, int new_h) {
		// 得到图片
		int old_w = src.getWidth();
		// 得到源图宽
		int old_h = src.getHeight();
		// 得到源图长
		BufferedImage newImg = null;
		// 判断输入图片的类型
		switch (src.getType()) {
		case 13:
			// png,gifnewImg = new BufferedImage(new_w, new_h,
			// BufferedImage.TYPE_4BYTE_ABGR);
			break;
		default:
			newImg = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
			break;
		}
		Graphics2D g = newImg.createGraphics();
		// 从原图上取颜色绘制新图
		g.drawImage(src, 0, 0, old_w, old_h, null);
		g.dispose();
		// 根据图片尺寸压缩比得到新图的尺寸
		newImg.getGraphics().drawImage(
				src.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0, 0,
				null);
		// 调用方法输出图片文件
		return newImg;
		//OutImage(outImgPath, newImg);
	}

	
	/**
	 * 将图片输出到指定的路径
	 * @param outImgPath
	 * @param newImg
	 */
	private static void OutImage(String outImgPath, BufferedImage newImg) {
		// 判断输出的文件夹路径是否存在，不存在则创建
		File file = new File(outImgPath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}// 输出到文件流
		try {
			ImageIO.write(newImg,
					outImgPath.substring(outImgPath.lastIndexOf(".") + 1),
					new File(outImgPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<Integer, String> readfile(String filepath,
			Map<Integer, String> pathMap) throws Exception {
		if (pathMap == null) {
			pathMap = new HashMap<Integer, String>();
		}

		File file = new File(filepath);
		// 文件
		if (!file.isDirectory()) {
			pathMap.put(pathMap.size(), file.getPath());

		} else if (file.isDirectory()) { // 如果是目录， 遍历所有子目录取出所有文件名
			String[] filelist = file.list();
			for (int i = 0; i < filelist.length; i++) {
				File readfile = new File(filepath + "/" + filelist[i]);
				if (!readfile.isDirectory()) {
					pathMap.put(pathMap.size(), readfile.getPath());

				} else if (readfile.isDirectory()) { // 子目录的目录
					readfile(filepath + "/" + filelist[i], pathMap);
				}
			}
		}
		return pathMap;
	}

	/**
	 * 将彩色图片转换为灰色
	 * @param originalPic
	 * @return
	 */
	public static BufferedImage convert2GrayPicture(BufferedImage originalPic) {  
        int imageWidth = originalPic.getWidth();  
        int imageHeight = originalPic.getHeight();  
  
        BufferedImage newPic = new BufferedImage(imageWidth, imageHeight,  
                BufferedImage.TYPE_3BYTE_BGR);  
  
        ColorConvertOp cco = new ColorConvertOp(ColorSpace  
                .getInstance(ColorSpace.CS_GRAY), null);  
        cco.filter(originalPic, newPic);  
       return newPic;
    }  

}
