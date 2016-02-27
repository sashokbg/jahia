package org.jahia.modules.ci.user.actions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.commons.util.CiConstants;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.ci.beans.AvatarBean;
import org.jahia.modules.ci.helpers.UserHelper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.settings.SettingsBean;
import org.jahia.tools.files.FileUpload;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

/**
 * The avatar action loader. Possible accesses may be done to set or get a user
 * avatar during inscription or profile updating.
 * 
 * @author lakreb
 * 
 */
public class LoadAvatarAction extends Action implements CiConstants {

	private static Timer timer;
	private static TimerTask task;

	static {
		timer = new Timer();
		task = new TimerTask() {
			private transient Logger logger = org.slf4j.LoggerFactory.getLogger(TimerTask.class);

			@Override
			public void run() {

				if (logger.isInfoEnabled())
					logger.info("Lanching task timer for avatar subsequent deletion...");

				FileDeleteStrategy deleteStrategy = FileDeleteStrategy.FORCE;
				File ciAvatarFolder = new File(SettingsBean.getInstance().getTmpContentDiskPath() + AVATAR_API_TEMP_ROOT_FOLDER);
				ciAvatarFolder.mkdirs();
				File[] files = ciAvatarFolder.listFiles();
				Date date = new Date();

				for (int i = 0; i < files.length; i++) {
					if (date.getTime() - files[i].lastModified() > 1800000L) {
						File[] oldAvatar = files[i].listFiles();
						for (int j = 0; j < oldAvatar.length; j++) {
							String fName = oldAvatar[j].getPath();
							boolean deleted = deleteStrategy.deleteQuietly(oldAvatar[j]);
							if (logger.isInfoEnabled())
								logger.info("Deleted file [" + fName + "] " + (deleted ? "successfully" : "failed") + ".");
						}
					}
					if (files[i].list().length == 0) {
						String fName = files[i].getPath();
						boolean deleted = deleteStrategy.deleteQuietly(files[i]);
						if (logger.isInfoEnabled())
							logger.info("Deleted folder [" + fName + "] " + (deleted ? "successfully" : "failed") + ".");
					}
				}

				if (logger.isInfoEnabled())
					logger.info("Avatar subsequent deletion task timer finished in " + (new Date().getTime() - date.getTime()) + "ms.");
			}
		};
		timer.scheduleAtFixedRate(task, 60000L, 3600000L);
	}

	private transient static Logger logger = org.slf4j.LoggerFactory.getLogger(LoadAvatarAction.class);

	// request parameter constant
	private static final String GET_AVATAR = "get";
	private static final String SET_AVATAR = "set";
	private static final String AVATAR_ACTION = "avatarAction";

	@Override
	public ActionResult doExecute(HttpServletRequest request, RenderContext renderContext, Resource resource,
			JCRSessionWrapper jcrSessionWrapper, Map<String, List<String>> paramMap, URLResolver urlResolver) throws Exception {

		AvatarBean avatarBean = new AvatarBean();

		UserHelper.setSessionFolder(renderContext, avatarBean);

		String avatarAction = request.getParameter(AVATAR_ACTION);

		/*
		 * Determines whether the action is to set or get the avatar. It is
		 * known with the "avatarAction" request parameter.
		 */
		if (SET_AVATAR.equalsIgnoreCase(avatarAction)) {
			return setAvatarFile(renderContext, avatarBean);
		} else if (GET_AVATAR.equalsIgnoreCase(avatarAction)) {
			return doGetAvatar(renderContext, avatarBean);
		}

		return null;
	}

	/**
	 * Set temp avatar from the 'fileupload' client side javascript plugin. The
	 * file can be sent either with {@code XmlHttpRequest} or from form submit
	 * in hidden iframe part.
	 * 
	 * @param context
	 * @param avatarBean
	 * @return
	 * @throws JSONException
	 */
	private ActionResult setAvatarFile(RenderContext context, AvatarBean avatarBean) throws JSONException {

		/*
		 * set response content type as text/html when IE because of non
		 * interpretation of application/json when we post a form.
		 */
		ActionResult ar = new ActionResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		JSONObject json = new JSONObject();
		ByteArrayInputStream stream = null;
		try {
			json = extractAvatarFromRequest(avatarBean, context);

			if (avatarBean.getImageData() != null)
				stream = new ByteArrayInputStream(avatarBean.getImageData());

			if (stream != null) {
				File file = new File(avatarBean.getAvatarPath());
				IOUtils.copy(stream, FileUtils.openOutputStream(file));
				context.getRequest().getSession().setAttribute(PARAM_USER_CROPPED_IMAGE, avatarBean.getFilename());
				ar.setResultCode(HttpServletResponse.SC_OK);
			}

		} catch (Exception ex) {
			logger.error("Error when handling avatar bytes : " + ex.getMessage());
			ex.printStackTrace();
			ar.setJson(json);
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {
				logger.warn("Unable to close inputstream " + stream.toString());
			}
		}

		/*
		 * We do so for IE because it does not understand a json application
		 * return object on an html form sent from browser, see
		 * http://github.com/valums/file-uploader file uploader plugin and
		 * iframe creation section.
		 */
		if (json.toString().contains(JSON_UPLOADER_ENTRY_IFRAME_POST) && json.getBoolean(JSON_UPLOADER_ENTRY_IFRAME_POST)) {
			context.getResponse().setContentType("text/html");
			PrintWriter writer = null;
			try {
				writer = context.getResponse().getWriter();
				writer.write(json.toString());
			} catch (IOException e) {
				context.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				logger.error("Error with IE avatar response handler : " + e.getMessage());
				e.printStackTrace();
			}
			writer.flush();
			writer.close();
			return ActionResult.OK;
		}

		ar.setJson(json);
		return ar;

	}

	/**
	 * Set the avatar @{code byte[]} populating avatar bean @{code AvatarBean}.
	 * 
	 * @param avatarBean
	 *            the @{code AvatarBean}
	 * @param context
	 *            the @{code RenderContext} from jahia
	 * @return the avatar @{code InputStream}
	 * @throws JSONException
	 */
	private JSONObject extractAvatarFromRequest(AvatarBean avatarBean, RenderContext context) throws JSONException {

		// -------------------------------------------------------------
		// --- init InputStream from request data. Also set filename ---
		// -------------------------------------------------------------
		String filename = null;
		JSONObject json = new JSONObject();
		FileDeleteStrategy deleteStrategy = FileDeleteStrategy.FORCE;
		if (avatarBean.getSessionFolder() != null && avatarBean.getSessionFolder().listFiles().length > 0) {
			File[] files = avatarBean.getSessionFolder().listFiles();
			for (int i = 0; i < files.length; i++) {
				String fName = files[i].getPath();
				boolean deleted = deleteStrategy.deleteQuietly(files[i]);
				if (logger.isInfoEnabled())
					logger.info("Deleted file [" + fName + "] " + (deleted ? "successfully" : "failed") + ".");
			}
		}
		/*
		 * if request is multipart, it's certainly sent from IE. Image is sent
		 * as a form input from client side.
		 */
		try {
			if (ServletFileUpload.isMultipartContent(context.getRequest())) {
				FileUpload fu = (FileUpload) context.getRequest().getAttribute(FileUpload.FILEUPLOAD_ATTRIBUTE);
				DiskFileItem inputFile = fu.getFileItems().get(PARAM_USER_CROPPED_IMAGE);
				json.put(JSON_UPLOADER_ENTRY_IFRAME_POST, true);
				long contributedMaxSize = UserHelper.getImageUploadMaxSize(context);
				if (inputFile.getSize() > contributedMaxSize) {
					json.put(JSON_UPLOADER_ENTRY_SIZE, inputFile.getSize());
					json.put(JSON_UPLOADER_ENTRY_REASON, JSON_UPLOADER_ERROR_SIZE);
					json.put(JSON_UPLOADER_ENTRY_SUCCESS, false);
					inputFile.delete();
					return json;
				}
				if (inputFile != null) {
					String fName = inputFile.getName();
					if (fName.contains("/") || fName.contains("\\")) {
						int lio = (fName.lastIndexOf("/") != -1 ? fName.lastIndexOf("/") : fName.lastIndexOf("\\")) + 1;
						fName = fName.substring(lio, fName.length());
					}
					filename = fName;
				}
				avatarBean.setImageData(IOUtils.toByteArray(inputFile.getInputStream()));
				json.put("success", true);
				deleteStrategy.deleteQuietly(inputFile.getStoreLocation());
				/*
				 * otherwise, image is sent from XmlHttpRequest js object. We
				 * can directly retrieve it in request input stream.
				 */
			} else {
				filename = context.getRequest().getHeader("X-File-Name");
				avatarBean.setImageData(IOUtils.toByteArray(context.getRequest().getInputStream()));
				if (avatarBean.getImageData().length > 0)
					json.put("success", true);
			}
		} catch (IOException e) {
			logger.error("Error when extracting avatar bytes : " + e.getMessage());
			e.printStackTrace();
			json.put("success", false);
		}

		if (filename != null) {
			avatarBean.setFilename(filename);
			avatarBean.setAvatarPath(avatarBean.getSessionFolder().getAbsolutePath() + "/" + avatarBean.getFilename());
		}

		return json;

	}

	/**
	 * An avatar filename must have been set. So that we can retrieve the file
	 * by getting filename in session attribute.
	 * 
	 * @param context
	 *            the @{code RenderContext} from jahia
	 * @param avatarBean
	 *            an helper to work with session avatar cropping
	 * @return
	 */
	private ActionResult doGetAvatar(RenderContext context, AvatarBean avatarBean) {
		ServletOutputStream responseOutputStream;
		try {
			UserHelper.populateAvatar(avatarBean, context, true);
			responseOutputStream = context.getResponse().getOutputStream();
			if (avatarBean.getImageData() != null && avatarBean.getImageData().length > 0)
				try {
					responseOutputStream.write(avatarBean.getImageData());
				} catch (Exception e) {
					// let's ignore it (IE6 bug but it works)
				}
			context.getResponse().setContentType(avatarBean.getContentType());
			context.getResponse().setHeader("Cache-Control", "no-store");
			context.getResponse().setHeader("Pragma", "no-cache");
			context.getResponse().setDateHeader("Expires", 0L);
			try {
				responseOutputStream.flush();
			} catch (Exception e) {
				// let's ignore it (IE6 bug but it works)
			}
			responseOutputStream.close();
		} catch (IOException ex) {
			logger.error("Error when getting avatar resource : " + ex.getMessage());
			ex.printStackTrace();
			return ActionResult.INTERNAL_ERROR;
		}
		return ActionResult.OK;

	}

}
