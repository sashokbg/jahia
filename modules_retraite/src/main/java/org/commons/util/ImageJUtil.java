package org.commons.util;

import ij.ImagePlus;
import ij.WindowManager;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Those image utilities work with implementation of ImageJ API. In fact, image
 * transparency is not well managed. Some images could result with black
 * background.
 * 
 * @author foo
 * 
 */
public class ImageJUtil {

	private transient static Log logger = LogFactory.getLog(ImageJUtil.class);

	/**
	 * 
	 * @param imageFileInput
	 * @param imageFileOutput
	 * @param top
	 * @param left
	 * @param height
	 * @param width
	 * @return
	 */
	public static boolean cropImage(File imageFileInput, File imageFileOutput, int top, int left, int height, int width) {
		Opener op = new Opener();
		ImagePlus ip = op.openImage(imageFileInput.getAbsolutePath());
		ImageProcessor processor = ip.getProcessor();
		processor.setRoi(left, top, width, height);
		processor = processor.crop();
		ip.setProcessor(null, processor);
		int type = op.getFileType(imageFileInput.getAbsolutePath());
		return save(type, ip, imageFileOutput);
	}

	/**
	 * 
	 * @param imageFileInput
	 * @param imageFileOutput
	 * @param height
	 * @param width
	 * @return
	 */
	public static boolean resizeImage(File imageFileInput, File imageFileOutput, int height, int width) {
		// Never used but if yes - TODO - check width > real imageFileInput
		// width
		Opener op = new Opener();
		ImagePlus ip = op.openImage(imageFileInput.getAbsolutePath());
		ImageProcessor processor = ip.getProcessor();
		processor = processor.resize(width, height);
		ip.setProcessor(null, processor);
		int type = op.getFileType(imageFileInput.getAbsolutePath());
		return save(type, ip, imageFileOutput);
	}

	/**
	 * 
	 * @param imageFileInput
	 * @param imageFileOutput
	 * @param size
	 * @param forceEnlarge
	 * @param square
	 * @return
	 * @throws IOException
	 */
	public static boolean createThumb(File imageFileInput, File imageFileOutput, int size, boolean forceEnlarge) throws IOException {
		FileInputStream fis = new FileInputStream(imageFileInput);
		Opener op = new Opener();
		ImagePlus ip = op.openImage(imageFileInput.getAbsolutePath());

		if (ip == null) {
			return false;
		}

		if (ip.getWidth() < size && ip.getHeight() < size && !forceEnlarge) {
			FileUtils.copyFile(imageFileInput, imageFileOutput);
			return true;
		}

		boolean isThumbed = createThumb(fis, imageFileOutput, size);
		fis.close();

		return isThumbed;

	}

	/**
	 * Creates a JPEG thumbnail from inputFile and saves it to disk in
	 * outputFile. scaleWidth is the width to scale the image to
	 */
	public static boolean createThumb(InputStream istream, File outputFile, int size) throws IOException {

		File parDir = outputFile.getParentFile();
		if (!parDir.exists())
			parDir.mkdir(); // create directory for thumbnails

		// Load the input image.

		File tmp = File.createTempFile("image", null);
		FileOutputStream out = new FileOutputStream(tmp);
		try {
			IOUtils.copy(istream, out);
		} finally {
			IOUtils.closeQuietly(out);
		}
		Opener op = new Opener();
		ImagePlus ip = op.openImage(tmp.getPath());
		if (ip == null) {
			return false;
		}
		int type = op.getFileType(tmp.getPath());
		tmp.delete();
		ImageProcessor processor = ip.getProcessor();
		if (ip.getWidth() > ip.getHeight()) {
			processor = processor.resize(size, ip.getHeight() * size / ip.getWidth());
		} else {
			processor = processor.resize(ip.getWidth() * size / ip.getHeight(), size);
		}
		ip.setProcessor(null, processor);

		return save(type, ip, outputFile);
	}

	public static boolean save(int type, ImagePlus ip, File outputFile) {
		switch (type) {
		case Opener.TIFF:
			return new FileSaver(ip).saveAsTiff(outputFile.getPath());
		case Opener.GIF:
			return new FileSaver(ip).saveAsGif(outputFile.getPath());
		case Opener.JPEG:
			return new FileSaver(ip).saveAsJpeg(outputFile.getPath());
		case Opener.TEXT:
			return new FileSaver(ip).saveAsText(outputFile.getPath());
		case Opener.LUT:
			return new FileSaver(ip).saveAsLut(outputFile.getPath());
		case Opener.ZIP:
			return new FileSaver(ip).saveAsZip(outputFile.getPath());
		case Opener.BMP:
			return new FileSaver(ip).saveAsBmp(outputFile.getPath());
		case Opener.PNG:
			ImagePlus tempImage = WindowManager.getTempCurrentImage();
			WindowManager.setTempCurrentImage(ip);
			PlugIn p = null;
			try {
				p = (PlugIn) Class.forName("ij.plugin.PNG_Writer").newInstance();
			} catch (InstantiationException e) {
				logger.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				logger.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage(), e);
			}
			p.run(outputFile.getPath());
			WindowManager.setTempCurrentImage(tempImage);
			return true;
		case Opener.PGM:
			return new FileSaver(ip).saveAsPgm(outputFile.getPath());
		}
		return false;
	}
}
