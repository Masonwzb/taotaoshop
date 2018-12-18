package com.taotao.service.Impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
/*
 * 图片上传服务
 */
import org.springframework.web.multipart.MultipartFile;

import com.taotao.common.utils.FtpUtil;
import com.taotao.common.utils.IDUtils;
import com.taotao.service.PictureService;
@Service
public class PictureServiceImpl implements PictureService{

	@Value("${FTP_ADDRESS}")
	private String FTP_ADDRESS;
	@Value("${FTP_PORT}")
	private Integer FTP_PORT;
	@Value("${FTP_USERNAME}")
	private String FTP_USERNAME;
	@Value("${FTP_PASSWORD}")
	private String FTP_PASSWORD;
	@Value("${FTP_BASE_PATH}")
	private String FTP_BASE_PATH;
	@Value("${IMAGE_BASE_URL}")
	private String IMAGE_BASE_URL;
	
	@Override
	public Map uploadPicture(MultipartFile uploadFile){
		Map resulMap = new HashMap<>();
		try {
			// 生成一个新的文件名
			//取原文件名
			String oldName = uploadFile.getOriginalFilename();
			//生成新文件名
			//UUID.randomUUID();
			String newName = IDUtils.genImageName();
			newName = newName + oldName.substring(oldName.lastIndexOf("."));
			//图片上传
			String imagePath = new DateTime().toString("/yyyy/mm/dd");
			boolean result = FtpUtil.uploadFile(FTP_ADDRESS, FTP_PORT, FTP_USERNAME, FTP_PASSWORD, FTP_BASE_PATH, 
				 	imagePath , newName , uploadFile.getInputStream());
			if(!result){
				resulMap.put("error", 1);
				resulMap.put("massage","文件上传失败");
				return resulMap;
			}
			resulMap.put("error", 0);
			resulMap.put("url", IMAGE_BASE_URL + imagePath + "/" +newName);
			return resulMap;
		} catch (Exception e) {
			resulMap.put("error", 1);
			resulMap.put("message", "文件上传发生异常");
			return resulMap;
		}
	}

}
