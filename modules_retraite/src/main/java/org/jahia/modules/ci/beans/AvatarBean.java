package org.jahia.modules.ci.beans;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.commons.util.CiConstants;

/**
 * 
 * @author lakreb
 * 
 */
public class AvatarBean implements CiConstants {

	private String avatarPath;
	private String filename;
	private String contentType;
	private File sessionFolder;
	private byte[] imageData;
	private int imageCropWidth;
	private int imageCropTop;
	private int imageCropLeft;
	private int imageCropHeight;
	private int cropFrameMaxWidth;
	private long imageMaxSize;
	private boolean avatar2BCropped;

	public AvatarBean() {
	}

	public String getAvatarPath() {
		return avatarPath;
	}

	public void setAvatarPath(String avatarPath) {
		if (StringUtils.isNotEmpty(avatarPath))
			this.avatarPath = avatarPath;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		if (StringUtils.isNotEmpty(filename))
			this.filename = filename;
	}

	public File getSessionFolder() {
		return sessionFolder;
	}

	public void setSessionFolder(File sessionFolder) {
		this.sessionFolder = sessionFolder;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		if (StringUtils.isNotEmpty(contentType))
			this.contentType = contentType;
	}

	public int getImageCropWidth() {
		return imageCropWidth;
	}

	public void setImageCropWidth(int imageCropWidth) {
		this.imageCropWidth = imageCropWidth;
	}

	public int getImageCropTop() {
		return imageCropTop;
	}

	public void setImageCropTop(int imageCropTop) {
		this.imageCropTop = imageCropTop;
	}

	public int getImageCropLeft() {
		return imageCropLeft;
	}

	public void setImageCropLeft(int imageCropLeft) {
		this.imageCropLeft = imageCropLeft;
	}

	public int getImageCropHeight() {
		return imageCropHeight;
	}

	public void setImageCropHeight(int imageCropHeight) {
		this.imageCropHeight = imageCropHeight;
	}

	public int getCropFrameMaxWidth() {
		return cropFrameMaxWidth;
	}

	public void setCropFrameMaxWidth(int cropFrameMaxWidth) {
		this.cropFrameMaxWidth = cropFrameMaxWidth;
	}

	public long getImageMaxSize() {
		return imageMaxSize;
	}

	public void setImageMaxSize(long imageMaxSize) {
		this.imageMaxSize = imageMaxSize;
	}

	public boolean isAvatar2BCropped() {
		return avatar2BCropped;
	}

	public void setAvatar2BCropped(boolean avatar2bCropped) {
		avatar2BCropped = avatar2bCropped;
	}

	@Override
	public String toString() {
		return "AvatarBean [avatarPath=" + avatarPath + ", filename=" + filename + ", contentType=" + contentType + ", sessionFolder="
				+ sessionFolder + ", imageData=" + Arrays.toString(imageData) + ", imageCropWidth=" + imageCropWidth + ", imageCropTop="
				+ imageCropTop + ", imageCropLeft=" + imageCropLeft + ", imageCropHeight=" + imageCropHeight + ", cropFrameMaxWidth="
				+ cropFrameMaxWidth + ", imageMaxSize=" + imageMaxSize + ", avatar2BCropped=" + avatar2BCropped + "]";
	}

}