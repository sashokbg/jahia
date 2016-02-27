package org.jahia.modules.ci.helpers;

import ij.ImagePlus;
import ij.io.Opener;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.taglibs.standard.functions.Functions;
import org.commons.util.CiConstants;
import org.commons.util.EmailUtil;
import org.commons.util.ImageJUtil;
import org.commons.util.Validator;
import org.jahia.api.Constants;
import org.jahia.modules.ci.beans.AvatarBean;
import org.jahia.modules.ci.beans.MailTemplateBean;
import org.jahia.modules.ci.beans.UserData;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.*;
import org.jahia.services.image.Image;
import org.jahia.services.image.ImageJAndJava2DImageService;
import org.jahia.services.image.JahiaImageService;
import org.jahia.services.query.QueryResultWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.jahia.services.usermanager.jcr.JCRUser;
import org.jahia.settings.SettingsBean;
import org.jahia.tools.files.FileUpload;
import org.jahia.utils.FileUtils;
import org.slf4j.Logger;

import javax.jcr.ItemNotFoundException;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLConnection;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserHelper implements CiConstants {

    private static class AvatarFile {
        enum SIZE {
            size40(40, "avatar_40"), size51(51, "avatar_51"), size65(65, "avatar_65"), size70(70, "avatar_70");
            private int size;
            private String name;

            SIZE(int size, String name) {
                this.size = size;
                this.name = name;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        private String name;
        private File file;
        private boolean isAvatar = false;

        public AvatarFile() {
        }

        public AvatarFile(JCRNodeWrapper node, SIZE size, File folder) throws IOException {
            String fileExtension = FilenameUtils.getExtension(node.getName());

            final File f = File.createTempFile("thumb", StringUtils.isNotEmpty(fileExtension) ? "." + fileExtension : null, folder);
            JCRContentUtils.downloadFileContent(node, f);
            f.deleteOnExit();

            StringBuilder sb = new StringBuilder();

            if (size!=null) {
                sb.append(size.getName());
            } else {
                sb.append("avatar");
            }
            sb.append(".").append(fileExtension);
            this.name = sb.toString();
            this.file = new File(folder, this.name);
            Integer width = AVATAR_API_THUMB_MAX_WIDTH;
            if (size != null) {
                width=size.getSize();
            }
            try {
                this.file.createNewFile();
                ImageJUtil.createThumb(f, this.file, width, false);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public AvatarFile(File file, SIZE size, File folder) {


            String ext = FileUtils.getExtension(file.getName());
            StringBuilder sb = new StringBuilder();
            if (size!=null) {
                sb.append(size.getName());
            } else {
                sb.append("avatar");
            }
            sb.append(".").append(ext);
            this.name = sb.toString();
            this.file = new File(folder, this.name);
            Integer width = AVATAR_API_THUMB_MAX_WIDTH;
            if (size != null) {
                width=size.getSize();
            }
            try {
                this.file.createNewFile();
                ImageJUtil.createThumb(file, this.file, width, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public boolean isAvatar() {
            return isAvatar;
        }

        public void setIsAvatar(boolean isAvatar) {
            this.isAvatar = isAvatar;
        }
    }


    private static Map<String, String> AVATAR_DEFAULT_MALE_MAP = new HashMap<String, String>();
    private static Map<String, String> AVATAR_DEFAULT_FEMALE_MAP = new HashMap<String, String>();
    private transient static Logger logger = org.slf4j.LoggerFactory.getLogger(UserHelper.class);

    static {
        AVATAR_DEFAULT_MALE_MAP.put("40", "/modules/modules_retraite/img/ciInscription/userMale_40.png");
        AVATAR_DEFAULT_MALE_MAP.put("51", "/modules/modules_retraite/img/ciInscription/userMale_51.png");
        AVATAR_DEFAULT_MALE_MAP.put("65", "/modules/modules_retraite/img/ciInscription/userMale_65.png");
        AVATAR_DEFAULT_MALE_MAP.put("70", "/modules/modules_retraite/img/ciInscription/userMale_70.png");
        AVATAR_DEFAULT_FEMALE_MAP.put("40", "/modules/modules_retraite/img/ciInscription/userFemale_40.png");
        AVATAR_DEFAULT_FEMALE_MAP.put("51", "/modules/modules_retraite/img/ciInscription/userFemale_51.png");
        AVATAR_DEFAULT_FEMALE_MAP.put("65", "/modules/modules_retraite/img/ciInscription/userFemale_65.png");
        AVATAR_DEFAULT_FEMALE_MAP.put("70", "/modules/modules_retraite/img/ciInscription/userFemale_70.png");

    }

    public static String getTmpCroppedFilename(String filename) {
        StringBuilder croppedFilename = new StringBuilder();
        String ext = FileUtils.getExtension(filename);
        String base = filename.replace("." + ext, "");
        croppedFilename.append(base).append(AVATAR_API_SUFFIX_CROP).append(".").append(ext);
        return croppedFilename.toString();
    }

    public static String getTmpThumbFilename(String filename) {
        StringBuilder croppedFilename = new StringBuilder();
        String ext = FileUtils.getExtension(filename);
        String base = filename.replace("." + ext, "");
        croppedFilename.append(base).append(AVATAR_API_SUFFIX_THUMB).append(".").append(ext);
        return croppedFilename.toString();
    }

    public static String getTmpMiniThumbFilename(String filename) {
        StringBuilder croppedFilename = new StringBuilder();
        String ext = FileUtils.getExtension(filename);
        String base = filename.replace("." + ext, "");
        croppedFilename.append(base).append(AVATAR_API_SUFFIX_THUMB57).append(".").append(ext);
        return croppedFilename.toString();
    }

    public static String getUserAvatarUrl(final JCRNodeWrapper user, final String thumbnailName) {
        String avatarUrl = AVATAR_DEFAULT_MALE;
        if (user != null) {
            try {
                avatarUrl = JCRTemplate.getInstance().doExecuteWithSystemSession("root", "live", new JCRCallback<String>() {
                    public String doInJCR(JCRSessionWrapper session) throws RepositoryException {
                        JCRNodeWrapper userNode = session.getNode(user.getPath());
                        JCRPropertyWrapper jPicture = null;

                        if (userNode.hasProperty(PROPERTIES_USER_PICTURE))
                            jPicture = userNode.getProperty(PROPERTIES_USER_PICTURE);

                        if (jPicture != null) {
                            try {
                                return ((JCRNodeWrapper) jPicture.getNode()).getThumbnailUrl(thumbnailName);
                            } catch (RepositoryException e) {
                                if (logger.isInfoEnabled())
                                    logger.info("getDefaultUserAvatar - Unable to retrieve avatar from user picture [" + user + "]");
                            }
                        } else {
                            String gender = user.getPropertyAsString(PROPERTIES_USER_GENDER);
                            if (GENDER_FEMALE.equals(gender))
                                return AVATAR_DEFAULT_FEMALE;
                        }
                        return AVATAR_DEFAULT_MALE;
                    }
                });
            } catch (RepositoryException e) {
                if (logger.isInfoEnabled())
                    logger.info("getDefaultUserAvatar - Unable to retrieve avatar from user gender [" + user + "]");
            }
            if (logger.isDebugEnabled())
                logger.debug("getDefaultUserAvatar - Avatar retrieved from user properties [" + user + "]");
        }
        if (logger.isDebugEnabled())
            logger.debug("getDefaultUserAvatar - Loading url avatar : " + avatarUrl + " for user [" + user + "]");
        return avatarUrl;
    }

    public static void populateParams(HttpServletRequest req, UserData data, Map<String, List<String>> parameters) {
        Properties prop = new Properties();

        if (data.getUser() == null) {
            prop.put(PROPERTIES_USER_EMAIL_NOTIF_DIS, "true");
            prop.put(PROPERTIES_USER_IS_MEMBER, "true");
            /*
			 * is that really necessary as "acceptNotification" may probably be
			 * set in parameters loop ?
			 */
            prop.put(PROPERTIES_USER_ACCEPT_NOTIF, "true");
        }

        data.setPseudoname(getParameter(parameters, PARAM_USER_PSEUDONAME, null));
        data.setPassword(getParameter(parameters, PARAM_USER_PASSWORD, null));
        data.setFirstname(getParameter(parameters, PARAM_USER_FIRSTNAME, ""));
        data.setLastname(getParameter(parameters, PARAM_USER_LASTNAME, ""));
        data.setEmail(getParameter(parameters, PARAM_USER_EMAIL, ""));

        prop.put(PROPERTIES_USER_FIRSTNAME, data.getFirstname());
        prop.put(PROPERTIES_USER_LASTAME, data.getLastname());
        prop.put(PROPERTIES_USER_MAIL, data.getEmail());

        // parameters auto fill loop
        for (Map.Entry<String, List<String>> param : parameters.entrySet()) {
            if (!Arrays.asList(PARAMS_TO_EXCLUDE_FROM_PROPS).contains(param.getKey())) {
                String value = getParameter(parameters, param.getKey(), null);
                if (value != null) {
                    prop.put(param.getKey(), value);
                }
            }
        }

        List<String> choixActivite = parameters.get("choixActivite");
        if (choixActivite != null && choixActivite.size() > 0) {
            String activiteValue = choixActivite.get(0);
            for (int i = 1; i < choixActivite.size(); i++) {
                activiteValue += "," + choixActivite.get(i);
            }
            prop.put("choixActivite", activiteValue);
        }

        data.setProperties(prop);
        data.setAvatarRequested(false);

        FileUpload fu = (FileUpload) req.getAttribute(FileUpload.FILEUPLOAD_ATTRIBUTE);
        if (fu != null) {
            DiskFileItem inputFile = fu.getFileItems().get(PARAM_USER_CROPPED_IMAGE);
            if (inputFile != null) {
                data.setAvatarRequested(true);
                // FIXME - do something when avatar is too big again (means
                // hacker are playing...)
            }
        } else if (getParameter(parameters, "avatarRequested", null) != null
                && "true".equals(getParameter(parameters, "avatarRequested", null)))
            data.setAvatarRequested(true);
        else if (getParameter(parameters, "removeAvatar", null) != null && "true".equals(getParameter(parameters, "removeAvatar", null)))
            data.setRemoveAvatar(true);

        AvatarBean avatarBean = new AvatarBean();

        avatarBean.setAvatar2BCropped(false);

        if (data.isAvatarRequested()) {
            String cropCoords = getParameter(parameters, PARAM_USER_CROP_COORDS, null);
            if (StringUtils.isNotEmpty(cropCoords)) {
                StringTokenizer tokenizer = new StringTokenizer(cropCoords, ",");
                if (tokenizer.countTokens() == 4) {
                    avatarBean.setImageCropLeft((int) Float.parseFloat(tokenizer.nextToken()));
                    avatarBean.setImageCropTop((int) Float.parseFloat(tokenizer.nextToken()));
                    avatarBean.setImageCropWidth((int) Float.parseFloat(tokenizer.nextToken()));
                    avatarBean.setImageCropHeight((int) Float.parseFloat(tokenizer.nextToken()));
                    avatarBean.setAvatar2BCropped(true);
                }
            }
            String cropFrameSize = getParameter(parameters, PARAM_USER_CROP_FRAME_MAX_SIZE, null);
            if (StringUtils.isNotEmpty(cropFrameSize))
                avatarBean.setCropFrameMaxWidth(Integer.parseInt(cropFrameSize));
            else
                avatarBean.setCropFrameMaxWidth(DEFAULT_CROP_FRAME_MAX_WIDTH);
        }

        data.setAvatarBean(avatarBean);

    }

    /**
     * @param session
     * @param contentType
     * @return
     */
    public static boolean saveUserAvatar(List<AvatarFile> avatarFiles, JCRSessionWrapper session, String contentType) {
        try {
            session = getCurrentUserDefaultSession(session);
            JCRUser user = (JCRUser) session.getUser();
            JCRNodeWrapper userNode = user.getNode(session);
            String path = userNode.getPath() + "/files/profile";
            JCRNodeWrapper profileNode = session.getNode(path);
            if (logger.isInfoEnabled())
                logger.info("ciInscription - Saving avatar for user : " + session.getUser().getUsername());
            List<String> uuidsToPublish = new ArrayList<String>();
            for (AvatarFile avatarFile : avatarFiles) {
                FileInputStream fis = new FileInputStream(avatarFile.getFile());
                JCRNodeWrapper pictureNode = profileNode.uploadFile(avatarFile.getName(), fis, contentType);
                if (avatarFile.isAvatar()) {
                    userNode.setProperty("j:picture", pictureNode);
                }
                uuidsToPublish.add(pictureNode.getIdentifier());
            }
            session.save();

            JCRPublicationService.getInstance().publish(uuidsToPublish, Constants.EDIT_WORKSPACE, Constants.LIVE_WORKSPACE, null);

            return true;
        } catch (Exception e) {
            logger.error("problem when saving avatar for user " + session.getAliasedUser());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param session
     * @return
     * @throws RepositoryException
     */
    public static JCRSessionWrapper getCurrentUserDefaultSession(JCRSessionWrapper session) throws RepositoryException {
        if (session.isLive())
            session = JCRTemplate.getInstance().getSessionFactory()
                    .getCurrentUserSession("default", session.getLocale(), session.getFallbackLocale());
        return session;
    }

    /**
     * @param session
     * @return
     * @throws RepositoryException
     */
    public static JCRSessionWrapper getCurrentUserLiveSession(JCRSessionWrapper session) throws RepositoryException {
        if (!session.isLive())
            session = JCRTemplate.getInstance().getSessionFactory()
                    .getCurrentUserSession("live", session.getLocale(), session.getFallbackLocale());
        return session;
    }

    /**
     * @param renderContext
     * @param tmpLocHelper
     */
    public static void setSessionFolder(RenderContext renderContext, AvatarBean tmpLocHelper) {
        SettingsBean settings = renderContext.getSettings();
        String tempFolderPath = settings.getTmpContentDiskPath() + AVATAR_API_TEMP_ROOT_FOLDER;
        File sessionFolder = new File(tempFolderPath + renderContext.getRequest().getSession().getId());
        if (!sessionFolder.exists())
            sessionFolder.mkdirs();
        tmpLocHelper.setSessionFolder(sessionFolder);
    }

    /**
     * @param avatarBean
     * @param context
     * @throws IOException
     */
    public static boolean populateAvatar(AvatarBean avatarBean, RenderContext context, boolean loadBytes) throws IOException {
        String filename = (String) context.getRequest().getSession().getAttribute(PARAM_USER_CROPPED_IMAGE);
        if (StringUtils.isEmpty(filename))
            return false;
        avatarBean.setFilename(filename);
        avatarBean.setContentType(URLConnection.getFileNameMap().getContentTypeFor(avatarBean.getFilename()));
        if (avatarBean.getSessionFolder() == null) {
            setSessionFolder(context, avatarBean);
        }
        File file = new File(avatarBean.getSessionFolder(), avatarBean.getFilename());
        avatarBean.setAvatarPath(file.getAbsolutePath());
        if (file.exists() && loadBytes) {
            avatarBean.setImageData(org.apache.commons.io.FileUtils.readFileToByteArray(file));
        }
        return true;
    }

    public static long getImageUploadMaxSize(RenderContext renderContext) {
        long maxSize = DEFAULT_AVATAR_MAX_SIZE;
        try {
            if (renderContext.getSite().hasProperty(PROPERTIES_SITE_AVATAR_MAX_SIZE)) {
                JCRPropertyWrapper maxSizeProperty = renderContext.getSite().getProperty(PROPERTIES_SITE_AVATAR_MAX_SIZE);
                if (maxSizeProperty != null) {
                    maxSize = maxSizeProperty.getLong();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return maxSize;
    }

    public static String getParameter(Map<String, List<String>> parameters, String paramName, String defaultValue) {
        List<String> vals = parameters.get(paramName);
        return CollectionUtils.isNotEmpty(vals) && StringUtils.isNotEmpty(vals.get(0)) ? Functions.escapeXml(vals.get(0)) : defaultValue;
    }

    /**
     * Use isProfessional(RenderContext , JahiaUser)
     */
    @Deprecated
    public static boolean isProfessional(JCRUser user) {
        return Validator.isNotNull(user) && Validator.isNotEmpty(user.getProperty(PROPERTIES_USER_IS_PROFESSIONAL))
                && "true".equals(user.getProperty(PROPERTIES_USER_IS_PROFESSIONAL));
    }

    /**
     * Permet de recuperer la liste des rubriques (node) selectionnee par
     * l'utilisateur
     *
     * @param user
     * @return
     * @throws RepositoryException
     */
    public static List<JCRNodeWrapper> getSelectedRubrics(JCRNodeWrapper user) throws RepositoryException {
        return getSelectedPages(user, PROPERTIES_USER_SELECTED_RUBRICS);
    }

    /**
     * Permet de recuperer la liste des thematique (node) selectionnee par
     * l'utilisateur
     *
     * @param user
     * @return
     * @throws RepositoryException
     */
    public static List<JCRNodeWrapper> getSelectedThematics(JCRNodeWrapper user) throws RepositoryException {
        return getSelectedPages(user, PROPERTIES_USER_SELECTED_THEMATICS);
    }

    /**
     * Permet de recuperer la liste des pages (node) selectionnee par
     * l'utilisateur
     *
     * @param user
     * @param fieldName nom du champ qui contient les pages
     * @return
     * @throws RepositoryException
     */
    private static List<JCRNodeWrapper> getSelectedPages(JCRNodeWrapper user, String fieldName) throws RepositoryException {
        List<JCRNodeWrapper> pages = new ArrayList<JCRNodeWrapper>();
        String propertyValue = user.getPropertyAsString(fieldName);

        if (Validator.isNotEmpty(propertyValue)) {
            String[] array = propertyValue.split(",");

            for (String uuid : array) {
                try {
                    pages.add(user.getSession().getNodeByUUID(uuid));
                } catch (ItemNotFoundException e) {
                    logger.warn("getSelectedPages - Item [" + uuid + "] not found exception for user " + user);
                }
            }
        }

        return pages;
    }

    public static String getUserStatus(JCRNodeWrapper user) {
        String userType = user.getPropertyAsString("userType");
        String gender = user.getPropertyAsString("gender");
        String str = "";
        if ("male".equals(gender) && userType.equals(USER_TYPE_FUTUR_RETRAITE))
            str = "Futur Retrait\u00E9";
        else if ("female".equals(gender) && userType.equals(USER_TYPE_FUTUR_RETRAITE))
            str = "Future Retrait\u00E9e";
        else {
            if ("male".equals(gender) && userType.equals(USER_TYPE_RETRAITE_JUNIOR))
                str = "Retrait\u00E9 Junior";
            else if ("female".equals(gender) && userType.equals(USER_TYPE_RETRAITE_JUNIOR))
                str = "Retrait\u00E9e Junior";
        }
        return StringEscapeUtils.escapeHtml(str);
    }

    public static boolean deleteCurrentUserAvatar(JCRNodeWrapper user) {
        if (user != null) {
            try {
                JCRSessionWrapper liveSession = user.getSession();
                JCRSessionWrapper defaultSession = getCurrentUserDefaultSession(liveSession);
                user = defaultSession.getNode(user.getPath());
                JCRPropertyWrapper jPicture = null;

                if (user.hasProperty(PROPERTIES_USER_PICTURE))
                    jPicture = user.getProperty(PROPERTIES_USER_PICTURE);

                if (jPicture != null) {
                    try {
                        JCRNodeWrapper jPictureNode = ((JCRNodeWrapper) jPicture.getNode());
                        JCRNodeWrapper folderNode = jPictureNode.getParent();
                        jPictureNode.remove();
                        jPicture.remove();
                        List<JCRNodeWrapper> nodes = JCRContentUtils.getChildrenOfType(folderNode, "jmix:image");
                        for (JCRNodeWrapper node : nodes) {
                            for(AvatarFile.SIZE size : AvatarFile.SIZE.values()) {
                                if (StringUtils.contains(node.getName(), size.getName())) {
                                    node.remove();
                                }
                            }
                        }
                        defaultSession.save();
                        JCRPublicationService.getInstance().publishByMainId(folderNode.getIdentifier());
                        liveSession.save();
                        return true;
                    } catch (RepositoryException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * @param session
     * @return
     * @throws IOException
     */
    public static boolean manageUserAvatar(JCRSessionWrapper session, UserData data) throws IOException {

        File imageFileInput = new File(data.getAvatarBean().getAvatarPath());

        File tmpThumbImage = new File(data.getAvatarBean().getSessionFolder(), UserHelper.getTmpThumbFilename(data.getAvatarBean()
                .getFilename()));
        tmpThumbImage.createNewFile();

        File tmpCroppedImage = null;

        if (data.getAvatarBean().isAvatar2BCropped()) {

            tmpCroppedImage = new File(data.getAvatarBean().getSessionFolder(), UserHelper.getTmpCroppedFilename(data.getAvatarBean()
                    .getFilename()));
            tmpCroppedImage.createNewFile();

            int imageCropTop = data.getAvatarBean().getImageCropTop();
            int imageCropLeft = data.getAvatarBean().getImageCropLeft();
            int imageCropHeight = data.getAvatarBean().getImageCropHeight();
            int imageCropWidth = data.getAvatarBean().getImageCropWidth();

			/*
			 * handles case when image displayed size lower than real image size
			 */
            Opener op = new Opener();
            ImagePlus ip = op.openImage(imageFileInput.getAbsolutePath());
            if (ip.getWidth() > data.getAvatarBean().getCropFrameMaxWidth()) {
                Integer realWidth = ip.getWidth();
                Integer displayedWidth = data.getAvatarBean().getCropFrameMaxWidth();
                float frameRatio = displayedWidth.floatValue() / realWidth.floatValue();
                imageCropTop = Math.round(imageCropTop / frameRatio);
                imageCropLeft = Math.round(imageCropLeft / frameRatio);
                imageCropHeight = Math.round(imageCropHeight / frameRatio);
                imageCropWidth = Math.round(imageCropWidth / frameRatio);
            }

            boolean isCropped = ImageJUtil.cropImage(imageFileInput, tmpCroppedImage, imageCropTop, imageCropLeft, imageCropHeight,
                    imageCropWidth);

            if (logger.isInfoEnabled() && isCropped)
                logger.info("Image " + data.getAvatarBean().getFilename() + " cropped for user " + data.getUser().getUsername());
            if (!isCropped)
                logger.warn("Image " + data.getAvatarBean().getFilename() + " didn't cropped for user " + data.getUser().getUsername());
        }

        boolean isThumbed = ImageJUtil.createThumb(data.getAvatarBean().isAvatar2BCropped() ? tmpCroppedImage : imageFileInput,
                tmpThumbImage, AVATAR_API_THUMB_MAX_WIDTH, false);

        if (logger.isInfoEnabled() && isThumbed)
            logger.info("Image " + data.getAvatarBean().getFilename() + " resized for user " + data.getUser().getUsername());
        if (!isThumbed)
            logger.warn("Image " + data.getAvatarBean().getFilename() + " didn't thumbed for user " + data.getUser().getUsername());


        File tmpThumb57Image = new File(data.getAvatarBean().getSessionFolder(), UserHelper.getTmpMiniThumbFilename(data.getAvatarBean()
                .getFilename()));
        tmpThumb57Image.createNewFile();

        ImageJUtil.createThumb(data.getAvatarBean().isAvatar2BCropped() ? tmpCroppedImage : imageFileInput,
                tmpThumb57Image, AVATAR_API_THUMB_57_WIDTH, false);

        List<AvatarFile> files = new ArrayList<AvatarFile>();

        File inputFile = data.getAvatarBean().isAvatar2BCropped() ? tmpCroppedImage : imageFileInput;
        File tempFolder = data.getAvatarBean().getSessionFolder();


        AvatarFile thumb = new AvatarFile(inputFile, null, tempFolder);
        thumb.setIsAvatar(true);
        files.add(thumb);

        for( AvatarFile.SIZE size : AvatarFile.SIZE.values()) {
            AvatarFile thumbTemp = new AvatarFile(inputFile, size, tempFolder);
            files.add(thumbTemp);
        }

        boolean result = UserHelper.saveUserAvatar(files, session, data.getAvatarBean().getContentType());

        return result;
    }

    /**
     * Check if the account with user is locked.
     *
     * @param user JahiaUser
     * @return true if the user locked
     */
    public static boolean isAccountLocked(JahiaUser user) {
        return user != null && !user.isRoot() && Boolean.valueOf(user.getProperty(PROPERTIES_USER_ACCOUNT_LOCKED));
    }

    /**
     * Permet de cloturer l'inscription par l'envoie d'un mail de bienvenue
     *
     * @param renderContext
     * @param parameters
     * @param data
     */
    public static void closeUserRegistration(RenderContext renderContext, Map<String, List<String>> parameters, UserData data,
                                             boolean sendWelcomeMail) {

        if (sendWelcomeMail) {
            // Récupérer les params du mail de notification :
            String from = renderContext.getSite().getPropertyAsString(PROPERTIES_SITE_EMAIL_NOREPLY);
            // String cci = node.getPropertyAsString("cci");
            MailTemplateBean templateBean = NotificationHelper.getMailTemplate(renderContext, MAIL_TEMPLATE_TARGET_WELCOME);

            String subject = templateBean.getSubject();
            String htmlBody = templateBean.getRecipientMailBody();

            String to = getParameter(parameters, PARAM_USER_EMAIL, null);

            String[] searchList = new String[]{MASK_MAIL_PSEUDONAME, MASK_MAIL_FIRSTNAME, MASK_MAIL_PASSWORD, MASK_MAIL_EMAIL};

            String pseudo = StringUtils.EMPTY;

            if (StringUtils.isEmpty(data.getFirstname())) {
                pseudo = data.getPseudoname();
            }

            String[] replacementList = new String[]{pseudo, data.getFirstname(), data.getPassword(), to};
            // Appel envoie mail de bienvenue au membre
            if (StringUtils.isNotEmpty(to))
                EmailUtil.sendMail(from, to, subject, htmlBody, searchList, replacementList, renderContext);
        }

        if (data.getAvatarBean() != null && data.getAvatarBean().getSessionFolder() != null) {
            FileDeleteStrategy deleteStrategy = FileDeleteStrategy.FORCE;
            for (File file : data.getAvatarBean().getSessionFolder().listFiles()) {
                String fName = file.getPath();
                boolean deleted = deleteStrategy.deleteQuietly(file);
                if (logger.isInfoEnabled())
                    logger.info("Deleted file [" + fName + "] " + (deleted ? "successfully" : "failed") + ".");
            }
            String fName = data.getAvatarBean().getSessionFolder().getPath();
            boolean deleted = deleteStrategy.deleteQuietly(data.getAvatarBean().getSessionFolder());
            if (logger.isInfoEnabled())
                logger.info("Deleted file [" + fName + "] " + (deleted ? "successfully" : "failed") + ".");
        }

    }

    /**
     * Recupere un utilisateur a partir de son pseudoname
     *
     * @param pseudoname
     * @return
     */
    public static JahiaUser getUserByPseudoname(String pseudoname) {
        JahiaUser user = null;
        if (Validator.isNotEmpty(pseudoname)) {
            JahiaUserManagerService jums = ServicesRegistry.getInstance().getJahiaUserManagerService();
            Properties prop = new Properties();
            prop.setProperty(PROPERTIES_USER_PSEUDODENAME, pseudoname);
            Set<Principal> set = jums.searchUsers(prop);
            if (!set.isEmpty()) {
                Iterator<Principal> it = set.iterator();
                while (it.hasNext()) {
                    JahiaUser temp = jums.lookupUser(it.next().getName());
                    if (temp != null && StringUtils.isNotEmpty(temp.getProperty(PROPERTIES_USER_PSEUDODENAME))
                            && temp.getProperty(PROPERTIES_USER_PSEUDODENAME).equals(pseudoname)) {
                        user = temp;
                        break;
                    }
                }
            }
        }
        return user;
    }

    /**
     * renvoie vrai si l'utilisateur est un membre
     *
     * @param user
     * @return
     */
    public static boolean isMember(JCRNodeWrapper user) {
        String isMember = null;
        if (user != null)
            isMember = user.getPropertyAsString(PROPERTIES_USER_IS_MEMBER);
        return isMember != null && "true".equals(isMember);
    }

    /**
     * renvoie vrai si l'utilisateur est un professionnel
     *
     * @param user
     * @return
     * @deprecated because isProfessional is never set. Use isProfessional(RenderContext , JahiaUser)
     */
    @Deprecated
    public static boolean isProfessional(JCRNodeWrapper user) {
        String isProf = null;
        if (user != null)
            isProf = user.getPropertyAsString(PROPERTIES_USER_IS_PROFESSIONAL);
        return isProf != null && "true".equals(isProf);
    }

    /**
     * renvoie vrai si l'utilisateur est un professionnel
     *
     * @param user
     * @return
     */
    public static boolean isProfessional(RenderContext renderContext, JahiaUser user) {
        if (user == null || renderContext == null) {
            return false;
        }
        return user.isMemberOfGroup(renderContext.getSite().getID(), USER_GROUP_PROFESSIONNEL);
    }

    /**
     * renvoie vrai si l'utilisateur est un moderateur
     *
     * @param user
     * @return
     * @deprecated because isModerator is never set.
     */
    @Deprecated
    public static boolean isModerator(JCRNodeWrapper user) {
        String isModo = null;
        if (user != null)
            isModo = user.getPropertyAsString(PROPERTIES_USER_IS_MODERATOR);
        return isModo != null && "true".equals(isModo);
    }

    /**
     * renvoie vrai si l'utilisateur est un moderateur
     *
     * @param user
     * @return
     */
    public static boolean isModerator(RenderContext renderContext, JCRNodeWrapper user) {
        if (user == null || renderContext == null) {
            return false;
        }
        JahiaUserManagerService jums = ServicesRegistry.getInstance().getJahiaUserManagerService();
        return jums.lookupUser(user.getName()).isMemberOfGroup(renderContext.getSite().getID(), USER_GROUP_MODERATOR);
    }

    /**
     * renvoie vrai si l'utilisateur est un moderateur
     *
     * @param user
     * @return
     */
    public static boolean isModerator(RenderContext renderContext, JahiaUser user) {
        if (user == null || renderContext == null) {
            return false;
        }
        return user.isMemberOfGroup(renderContext.getSite().getID(), USER_GROUP_MODERATOR);
    }

    /**
     * retourne l'url de visualisation d'un profil (membre, modo ou pro)
     *
     * @param renderContext
     * @param user
     * @return
     */
    public static String getProfileUrl(RenderContext renderContext, JCRNodeWrapper user) {
        try {
            StringBuilder url = new StringBuilder();
            String userClass;
            if (isModerator(renderContext, user)) {
                userClass = PROPERTIES_SITE_MODERATOR_PROFILE_LINK;
            } else if (isMember(user)) {
                userClass = PROPERTIES_SITE_MEMBER_PROFILE_LINK;
            } else if (isProfessional(user)) {
                userClass = PROPERTIES_SITE_PROFESSIONAL_PROFILE_LINK;
            } else {
                return "#";
            }
            JCRNodeWrapper node = (JCRNodeWrapper) renderContext.getSite().getProperty(userClass).getNode();
            url.append(node.getUrl());
            url.append("?pseudo=").append(user.getPropertyAsString(PROPERTIES_USER_PSEUDODENAME));
            // apply rewrite rules on url
            return renderContext.getResponse().encodeURL(url.toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "#";
        }
    }

    /**
     * Retourne (xxmois ou xxannee et yy mois) membre depuis
     *
     * @param date
     * @return
     */
    public static String getMemberSince(Calendar date) {
        String memberSince = "";
        int months = computeMonthDelayToNow(date);
        if (months > 0) {
            int years = 0;
            if (months > 11) {
                years = months / 12;
                months = months - (years * 12);
            }
            if (years == 1) {
                memberSince += years + " an";
            } else if (years > 1) {
                memberSince += years + " ans";
            }
            if (months > 0) {
                if (years > 0) {
                    memberSince += " et ";
                }
                memberSince += months + " mois";
            }
        } else {
            memberSince = "moins d'un mois";
        }
        return memberSince;
    }

    /**
     * @param fromDay
     * @return
     */
    private static int computeMonthDelayToNow(Calendar fromDay) {
        int months = 0;
        if (fromDay != null) {
            Calendar today = Calendar.getInstance();
            months = (today.get(Calendar.YEAR) - fromDay.get(Calendar.YEAR) /*- 1*/) * 12;
            if (fromDay.get(Calendar.MONTH) != today.get(Calendar.MONTH)) {
                months += today.get(Calendar.MONTH) - fromDay.get(Calendar.MONTH);
                if (fromDay.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH)) {
                    months--;
                }
            }
        }
        return months;
    }

    /**
     * Check and remove a boolean flag which name is given in parameter. It
     * looks in session attribute because on some jahia actions we would want to
     * check a request attribute but it makes an internal redirection, so that
     * the attribute is no more available.
     *
     * @param renderContext
     * @param eventName     the name of the researched flag
     * @return true if the flag is present and is set to true
     * @{code RenderContext} to determine the current session
     */
    public static Boolean removeSessionEventFlag(RenderContext renderContext, String eventName) {
        HttpSession session = renderContext.getRequest().getSession();
        Boolean sessionAttribute = (Boolean) session.getAttribute(eventName);

        if (sessionAttribute != null && sessionAttribute) {
            session.removeAttribute(eventName);
            return true;
        }
        return false;
    }

    private static Map<String, Integer> getNumberOfNodesGroupedByMemberForLastMonths(final String nodeName, final int lastMonths) throws RepositoryException {

        final Map<String, Integer> nodeMap = new HashMap<String, Integer>();

        JCRTemplate.getInstance().doExecuteWithSystemSession("root", "live", new JCRCallback<String>() {
            public String doInJCR(JCRSessionWrapper session) throws RepositoryException {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -lastMonths);

                QueryManager queryManager = session.getWorkspace().getQueryManager();
                StringBuilder query = new StringBuilder("SELECT * FROM [" + nodeName + "] AS node ").append(
                        "INNER JOIN [jnt:user] AS user ").append(
                        "ON node.[jcr:createdBy] = user.[j:nodename]").append(
                        "WHERE node.[jcr:created] ").append(
                        ">= CAST('" + format.format(cal.getTime()) + "T00:00:00.000Z' AS DATE) ").append(
                        "AND user.[isMember] = 'true'");
                logger.info("query : " + query);
                Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
                QueryResultWrapper queryResult = (QueryResultWrapper) q.execute();
                NodeIterator iterator = queryResult.getNodes();

                while (iterator.hasNext()) {
                    try {
                        JCRNodeWrapper node = (JCRNodeWrapper) iterator.next();
                        JCRPropertyWrapper userNode = node.getProperty("user");
                        String userUUID = userNode.getNode().getIdentifier();
                        if (nodeMap.containsKey(userUUID)) {
                            nodeMap.put(userUUID, nodeMap.get(userUUID) + 1);
                        } else {
                            nodeMap.put(userUUID, 1);
                        }
                    } catch (Exception e) {
                        logger.warn(e.getMessage() + " " + e.getCause());
                    }
                }
                return null;
            }
        });
        return nodeMap;
    }

    public static List<UserStatistics> getMostActiveUsersForLastMonths(int months, int numberOfUsers) {
        //1. init
        Map<String, UserStatistics> statisticsMap = new HashMap<String, UserStatistics>();
        Map<String, Integer> questionersMap = null;
        Map<String, Integer> repliersMap = null;

        //2. extract number of questions and answers
        try {
            questionersMap = getNumberOfNodesGroupedByMemberForLastMonths("jnt:ciQuestion", months);
            repliersMap = getNumberOfNodesGroupedByMemberForLastMonths("jnt:ciReply", months);
        } catch (RepositoryException e) {
            logger.error(e.getMessage() + " " + e.getCause());
        }

        //3. fill Map with values
        //3.1 for questions
        for (String user : questionersMap.keySet()) {
            UserStatistics stats = new UserStatistics();
            stats.setUserUUID(user);//(UserHelper.getUserByPseudoname(user));
            stats.setNumberOfQuestions(questionersMap.get(user));
            statisticsMap.put(user, stats);
        }

        //3.2 for replies
        for (String user : repliersMap.keySet()) {
            UserStatistics stats;

            if (statisticsMap.containsKey(user)) {
                stats = statisticsMap.get(user);
            } else {
                stats = new UserStatistics();
                stats.setUserUUID(user);
            }
            stats.setNumberOfReplies(repliersMap.get(user));
            statisticsMap.put(user, stats);
        }

        //3.3 for replies in own questions/topics
        for (Map.Entry<String, UserStatistics> entry : statisticsMap.entrySet()) {
            String userUUID = entry.getKey();
            UserStatistics stats = entry.getValue();
            stats.setNumberOfRepliesInOwnQuestion(getNumberOfRepliesInOwnQuestionsForLastMonths(findUserByUuid(userUUID), months));
            statisticsMap.put(userUUID, stats);
        }

        //4. Prepare result - convert to list, sort, trim (...only MOST active users...)
        List<UserStatistics> result = new ArrayList<UserStatistics>(statisticsMap.values());
        Collections.sort(result);
        if (result.size() > numberOfUsers) {
            result.subList(numberOfUsers, result.size()).clear();
        }

        return result;
    }

    public static double getRankByUuid(String uuid) {
        int numberOfQuestions = 0;
        int numberOfReplies = 0;
        int numberOfRepliesInOwnQuestions = 0;

        String user = findUserByUuid(uuid);
        if (user != null) {
            numberOfQuestions = getNumberOfQuestionsForUser(user);
            numberOfReplies = getNumberOfRepliesForUser(user);
            numberOfRepliesInOwnQuestions = getNumberOfRepliesInOwnQuestionsForLastMonths(user);
        }

        return numberOfQuestions + 2 * (numberOfReplies - numberOfRepliesInOwnQuestions)
                + 0.5 * numberOfRepliesInOwnQuestions;
    }

    private static int getNumberOfRepliesForUser(String user) {
        return getNumberOfEntriesForUser(user, "jnt:ciReply");
    }

    private static int getNumberOfQuestionsForUser(String user) {
        return getNumberOfEntriesForUser(user, "jnt:ciQuestion");
    }

    private static int getNumberOfEntriesForUser(final String user, final String entry) {
        final int[] result = {0};

        try {
            JCRTemplate.getInstance().doExecuteWithSystemSession("root", "live", new JCRCallback<String>() {
                public String doInJCR(JCRSessionWrapper session) throws RepositoryException {

                    //1. get questions with answer
                    QueryManager queryManager = session.getWorkspace().getQueryManager();
                    StringBuilder query = new StringBuilder("SELECT * FROM [" + entry + " ] as node "
                            + "where node.[jcr:createdBy] = '" + user + "'");

                    Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
                    QueryResultWrapper queryResult = (QueryResultWrapper) q.execute();
                    result[0] = (int) queryResult.getNodes().getSize();
                    return null;
                }
            });
        } catch (RepositoryException e) {
            return 0;
        }
        return result[0];
    }

    /**
     * @param user   - the user returned by findUserByUuid(uuid);
     * @param months - number of months fot the statistic
     * @return
     */
    private static int getNumberOfRepliesInOwnQuestionsForLastMonths(final String user, final int months) {
        final int[] result = {0};

        try {
            JCRTemplate.getInstance().doExecuteWithSystemSession("root", "live", new JCRCallback<String>() {
                public String doInJCR(JCRSessionWrapper session) throws RepositoryException {

                    //1. get questions with answer
                    QueryManager queryManager = session.getWorkspace().getQueryManager();
                    StringBuilder query = new StringBuilder("SELECT * FROM [jnt:ciQuestion] as question ").
                            append(" where question.[nbOfReplies] > 0").
                            append(" and question.[jcr:createdBy] =  '" + user + "'");

                    Query q = queryManager.createQuery(query.toString(), Query.JCR_SQL2);
                    QueryResultWrapper queryResult = (QueryResultWrapper) q.execute();
                    NodeIterator iterator = queryResult.getNodes();
                    //2. search for own replies in every question
                    while (iterator.hasNext()) {
                        try {
                            //check if questioner is the same as the repliers (own reply)
                            JCRNodeWrapper question = (JCRNodeWrapper) iterator.next();
                            NodeIterator replyNodes = question.getNodes("ciReply*");

                            while (replyNodes.hasNext()) {
                                //only replies for the last N months
                                JCRNodeWrapper reply = (JCRNodeWrapper) replyNodes.next();
                                boolean dateValidation = Boolean.TRUE;
                                if (months > 0) {
                                    Calendar createdDate = reply.getProperty("jcr:created").getDate();
                                    Calendar compareDate = Calendar.getInstance();
                                    compareDate.add(Calendar.MONTH, -months);
                                    dateValidation = compareDate.before(createdDate);
                                }

                                if (dateValidation) {
                                    JCRPropertyWrapper replyAuthorNode = reply.getProperty("user");
                                    String replyAuthor = replyAuthorNode.getNode().getName();

                                    if (replyAuthor.equals(user)) {
                                        result[0] += 1;
                                    }
                                }
                            }


                        } catch (Exception e) {
                            logger.warn(e.getMessage() + " " + e.getCause());
                        }
                    }
                    return null;
                }
            });
        } catch (RepositoryException e) {
            return 0;
        }
        return result[0];
    }

    private static int getNumberOfRepliesInOwnQuestionsForLastMonths(String user) {
        return getNumberOfRepliesInOwnQuestionsForLastMonths(user, 0);
    }

    public static String findUserByUuid(final String uuid) {

        final String[] user = {null};

        try {
            JCRTemplate.getInstance().doExecuteWithSystemSession("root", "live", new JCRCallback<String>() {
                public String doInJCR(JCRSessionWrapper session) throws RepositoryException {
                    user[0] = session.getNodeByUUID(uuid).getProperty("j:nodename").getString();

                    return null;
                }
            });
        } catch (RepositoryException e) {
            return null;
        }
        return user[0];
    }

    /**
     * returns the number of stars (0-4) representing the Rank
     */
    public static int getNumberOfRankStars(String uuid) {
        double rank = getRankByUuid(uuid);

        if (rank > 0) {
            if (rank > 5) {
                if (rank > 15) {
                    if (rank > 30) {
                        return 4;
                    }
                    return 3;
                }
                return 2;
            }
            return 1;
        }
        return 0;
    }

    public static String getAvatarFile(final RenderContext renderContext, final JCRNodeWrapper userNode, final String size) throws RepositoryException {

        if (StringUtils.isNotBlank(size)) {
            final Integer sizeInt = Integer.parseInt(size);
            JCRSessionWrapper session = userNode.getSession();
            try {
                String profilePath = userNode.getPath() + "/files/profile";
                final JCRNodeWrapper profileNode = session.getNode(profilePath);
                List<JCRNodeWrapper> nodes = JCRContentUtils.getChildrenOfType(profileNode, "jmix:image");
                for (JCRNodeWrapper node : nodes) {
                    if (StringUtils.contains(node.getName(), "avatar_" + size)) {
                        return node.getUrl();
                    }
                }
                synchronized (userNode){
                    if (userNode.hasProperty("j:picture")) {
                        final JCRNodeWrapper picture = (JCRNodeWrapper) userNode.getProperty("j:picture").getNode();
                        String url = JCRTemplate.getInstance().doExecuteWithUserSession("root", Constants.EDIT_WORKSPACE, new JCRCallback<String>() {
                            public String doInJCR(JCRSessionWrapper session) throws RepositoryException {

                                String uuidToPublish = null;
                                String target = "avatar_" + size;
                                String fileExtension = FilenameUtils.getExtension(picture.getName());
                                if ((fileExtension != null) && (!"".equals(fileExtension))) {
                                    target += "." + fileExtension;
                                }
                                try {
                                    uuidToPublish = resizeImage(picture.getPath(), target, sizeInt, sizeInt, session);
                                } catch (Exception e) {
                                    throw new RepositoryException(e);
                                }
                                if (StringUtils.isNotBlank(uuidToPublish)) {

                                    //Since JCRPublicationService doesn't care about the user in this session, and
                                    // get the one from sessionFactory, we must swap it
                                    JCRPublicationService publicationService = JCRPublicationService.getInstance();
                                    JahiaUser oldUser = publicationService.getSessionFactory().getCurrentUser();
                                    publicationService.getSessionFactory().setCurrentUser(session.getUser());
                                    publicationService.publish(Arrays.asList(uuidToPublish), Constants.EDIT_WORKSPACE, Constants.LIVE_WORKSPACE, null);
                                    publicationService.getSessionFactory().setCurrentUser(oldUser);

                                    JCRNodeWrapper pictureNode = profileNode.getNode(target);
                                    return pictureNode.getUrl();
                                }
                                return null;
                            }
                        });
                        if (url != null) {
                            return url;
                        } else {
                            return picture.getUrl();
                        }

                    }
                }
            } catch (PathNotFoundException e) {
                //Pas de repertoire profile l'utilisateur, on passe à la suite.
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            //On bascule sur les images par défaut
            if(userNode.hasProperty("gender")) {
                if (StringUtils.equalsIgnoreCase(userNode.getProperty("gender").getString(), "female")) {
                    return AVATAR_DEFAULT_FEMALE_MAP.get(size);
                } else {
                    return AVATAR_DEFAULT_MALE_MAP.get(size);
                }
            }
            return AVATAR_DEFAULT_MALE_MAP.get(size);
        }
        if (userNode.hasProperty("j:picture")) {
            return ((JCRNodeWrapper) userNode.getProperty("j:picture").getNode()).getUrl();
        }
        if(userNode.hasProperty("gender")) {
            if (StringUtils.equalsIgnoreCase(userNode.getProperty("gender").getString(), "female")) {
                return CiConstants.AVATAR_DEFAULT_FEMALE;
            } else {
                return CiConstants.AVATAR_DEFAULT_MALE;
            }
        }
        return CiConstants.AVATAR_DEFAULT_MALE;
    }

    private static String resizeImage(String path, String target, int width, int height, JCRSessionWrapper session) throws Exception {
        try {
            JahiaImageService imageService = ImageJAndJava2DImageService.getInstance();
            JCRNodeWrapper node = session.getNode(path);
            if (node.hasNode(target)) {
                return null;
            }
            Image image = imageService.getImage(node);
            String fileExtension = FilenameUtils.getExtension(node.getName());
            if ((fileExtension != null) && (!"".equals(fileExtension))) {
                fileExtension += "." + fileExtension;

            } else {
                fileExtension = null;
            }
            File f = File.createTempFile("image", fileExtension);
            imageService.resizeImage(image, f, width, height, JahiaImageService.ResizeType.SCALE_TO_FILL);

            InputStream fis = new BufferedInputStream(new FileInputStream(f));
            try {
                node.getParent().uploadFile(target, fis, node.getFileContent().getContentType());
                session.save();
                return node.getParent().getNode(target).getIdentifier();
            } finally {
                IOUtils.closeQuietly(fis);
                f.delete();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}
