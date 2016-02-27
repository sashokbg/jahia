package org.commons.util;

import org.apache.commons.lang.StringUtils;
import org.jahia.settings.SettingsBean;

public interface CiConstants {

	/*
	 * HTML user information parameter
	 */
	String PARAM_USER_USERNAME = "username";
	String PARAM_USER_PSEUDONAME = "pseudoname";
	String PARAM_USER_FIRSTNAME = "firstname";
	String PARAM_USER_LASTNAME = "lastname";
	String PARAM_USER_PASSWORD = "password";
	String PARAM_USER_GENDER = "gender";
	String PARAM_USER_BIRTH_DATE = "birthDate";
	String PARAM_USER_TYPE = "userType";
	String PARAM_USER_RETREAT_YEAR = "retreatYear";
	String PARAM_USER_EMAIL = "emailAddress";
	String PARAM_USER_COUNTRY = "country";
	String PARAM_USER_ZIPCODE = "zipCode";
	String PARAM_USER_ACCEPT_NOTIF = "acceptNotification";
	String PARAM_USER_ACCEPT_NEWS = "acceptNewsletter";
	String PARAM_USER_ACCEPT_NEWS_GROUP = "acceptNewsletterGroup";
	String PARAM_USER_CROPPED_IMAGE = "croppedImage";
	String PARAM_USER_CROP_COORDS = "cropCoords";
	String PARAM_USER_CROP_FRAME_MAX_SIZE = "cropFrameMaxSize";
	String PARAM_USER_ACTIVITY_CHOICE = "choixActivite";
	String[] PARAMS_TO_EXCLUDE_FROM_PROPS = { "jcrRedirectTo", "jcrErrorRedirectTo", "confirmEmailAddress", "confirmPassword",
			"avatarRequested", "cguAccepted", PARAM_USER_PASSWORD, PARAM_USER_EMAIL, PARAM_USER_CROP_FRAME_MAX_SIZE,
			PARAM_USER_CROP_COORDS, PARAM_USER_FIRSTNAME, PARAM_USER_LASTNAME };
	String PARAM_USER_PROFILE_UPDATED = "profileUpdated";

	/*
	 * HTML unsubscribe parameter
	 */
	String PARAM_UNSUBSCRIBE_REASON = "reasonSelected";
	String PARAM_UNSUBSCRIBE_REASON_DETAIL = "reasonDetails";

	/*
	 * HTML pickable node parameters
	 */
	String PARAM_PICKABLE_NODE_PATH = "nodePath";
	String PARAM_PICKABLE_MODERATOR_TOOLS_FLAG = "moderatorTools";
	String PROPERTIES_PICKABLE_IS_NOT_PICKABLE = "isNotPickable";
	String PROPERTIES_PICKABLE_IS_NOT_TOOLS = "isNotToolsDisplayed";

	/*
	 * HTML newsletter subscription
	 */
	String PARAM_NEWSLETTER_EMAIL = "emailSubscriber";
	String PARAM_NEWSLETTER_CONFIRM = "subscriptionConfirmed";
	String PARAM_NEWSLETTER_BAD_MAIL = "badEmail";

	/*
	 * HTML newsletter Unsubscription
	 */
	String PARAM_UNSUBSCRIBE_NEWSLETTER_EMAIL = "emailUnsubscriber";
	String PARAM_UNSUBSCRIBE_NEWSLETTER_CONFIRM = "unsubscriptionConfirmed";
	String PARAM_UNSUBSCRIBE_NEWSLETTER_BAD_MAIL = "badEmailUnsubcribe";

	/*
	 * JSON parameters
	 */
	// --- error type
	String JSON_UPLOADER_ERROR_TYPE = "typeError";
	String JSON_UPLOADER_ERROR_SIZE = "sizeError";
	String JSON_UPLOADER_ERROR_MIN_SIZE = "minSizeError";
	String JSON_UPLOADER_ERROR_EMPTY = "emptyError";
	// --- entry
	String JSON_UPLOADER_ENTRY_SUCCESS = "success";
	String JSON_UPLOADER_ENTRY_REASON = "reason";
	String JSON_UPLOADER_ENTRY_SIZE = "fileSize";
	String JSON_UPLOADER_ENTRY_IFRAME_POST = "sentFromIFrame";

	/*
	 * masque de notifications mail
	 */
	String MASK_MAIL_PSEUDONAME = "$pseudoname$";
	String MASK_MAIL_FIRSTNAME = "$firstname$";
	String MASK_MAIL_LASTNAME = "$lastname$";
	String MASK_MAIL_EMAIL = "$email$";
	String MASK_MAIL_PASSWORD = "$password$";
	String MASK_NAME_SITWEB = "[Preparons ma retraite.fr]";
	String MASK_MAIL_BODY = "$body$";
	String MASK_MAIL_TYPE = "$type$";
	String MASK_MAIL_TITLE = "$title$";
	String MASK_MAIL_INTRO = "$intro$";
	String MASK_MAIL_URL = "$url$";

	/*
	 * notification de forum
	 */
	String NOTIFICATION_ACTIVATED = "notificationActivated";
	String NOTIFICATION_SITE_ADDRESS = "siteAddress";
	String NOTIFICATION_EMAIL_NO_REPLY = "emailNoReply";
	String NOTIFICATION_MAIL_TEMPLATE_LOGO = "mailTemplateLogo";
	String NOTIFICATION_MAIL_TEMPLATE_LINE = "mailTemplateLine";
	String NOTIFICATION_MAIL_TEMPLATE_BORDER = "mailTemplateBorder";
	String NOTIFICATION_GOOGLE_ANALYTICS_URL_SUFFIX_EXPERTS = "gaSuffixExperts";
	String NOTIFICATION_GOOGLE_ANALYTICS_URL_SUFFIX_MEMBRES = "gaSuffixMembres";

	/*
	 * Local server parameters
	 */
	String AVATAR_PROPERTY_NAME_MAX_WIDTH = "avatarMaxWidth";
	String AVATAR_PROPERTY_VALUE_MAX_WIDTH = SettingsBean.getInstance().getPropertiesFile().getProperty(AVATAR_PROPERTY_NAME_MAX_WIDTH);
	int AVATAR_API_THUMB_MAX_WIDTH = StringUtils.isEmpty(AVATAR_PROPERTY_VALUE_MAX_WIDTH) ? 240 : Integer
			.parseInt(AVATAR_PROPERTY_VALUE_MAX_WIDTH);
	int AVATAR_API_THUMB_57_WIDTH = 57;
	String AVATAR_API_TEMP_ROOT_FOLDER = "/ciAvatars/";
	String AVATAR_API_SUFFIX_THUMB = "_THUMB";
	String AVATAR_API_SUFFIX_THUMB57 = "_THUMB57";
	String AVATAR_API_SUFFIX_CROP = "_CROP";

	/*
	 * JCR property section
	 */
	long DEFAULT_AVATAR_MAX_SIZE = 6291456L;
	int DEFAULT_CROP_FRAME_MAX_WIDTH = 618;

	/*
	 * Jahia required jcr properties for user
	 */
	String PROPERTIES_USER_MAIL = "j:email";
	String PROPERTIES_USER_PSEUDODENAME = "pseudoname";
	String PROPERTIES_USER_USERNAME = "username";
	String PROPERTIES_USER_RETREAT_YEAR = PARAM_USER_RETREAT_YEAR;
	String PROPERTIES_USER_FIRSTNAME = "j:firstName";
	String PROPERTIES_USER_LASTAME = "j:lastName";
	String PROPERTIES_USER_PASSWORD = "j:password";
	String PROPERTIES_USER_COUNTRY = PARAM_USER_COUNTRY;
	String PROPERTIES_USER_ZIPCODE = PARAM_USER_ZIPCODE;
	String PROPERTIES_USER_TYPE = PARAM_USER_TYPE;
	String PROPERTIES_USER_SELECTED_RUBRICS = "selectedRubrics";
	String PROPERTIES_USER_SELECTED_THEMATICS = "selectedThematics";
	String PROPERTIES_USER_PICTURE = "j:picture";
	String PROPERTIES_USER_GENDER = "gender";
	String PROPERTIES_USER_BIRTH_DATE = PARAM_USER_BIRTH_DATE;
	String PROPERTIES_USER_PICTURE_AVATAR_60 = "avatar_60";
	String PROPERTIES_USER_PICTURE_AVATAR_120 = "avatar_120";
	String PROPERTIES_USER_ACTIVITY_CHOICE = "choixActivite";
	String PROPERTIES_USER_ACCOUNT_LOCKED = "j:accountLocked";
	String PROPERTIES_USER_EMAIL_NOTIF_DIS = "emailNotificationsDisabled";
	String PROPERTIES_USER_NB_OF_QUESTIONS = "nbOfQuestions";
	String PROPERTIES_USER_NB_OF_REPLIES = "nbOfReplies";
	String PROPERTIES_USER_REPLIES_UUID = "repliesUuid";
	String PROPERTIES_USER_ACCEPT_NEWS = PARAM_USER_ACCEPT_NEWS;
	String PROPERTIES_USER_ACCEPT_NEWS_GROUP = PARAM_USER_ACCEPT_NEWS_GROUP;
	String PROPERTIES_USER_ACCEPT_NOTIF = PARAM_USER_ACCEPT_NOTIF;
	String PROPERTIES_USER_IS_MEMBER = "isMember";
	String PROPERTIES_USER_IS_PROFESSIONAL = "isProfessional";
	String PROPERTIES_USER_IS_MODERATOR = "isModerator";
	String PROPERTIES_USER_BIOGRAPHY = "biography";
	String PROPERTIES_USER_SIGNATURE = "signature";

	/*
	 * Jahia required jcr properties for ciArticle
	 */
	String PROPERTIES_ARTICLE_PRIMARY_TYPE = "jnt:ciArticle";
	
	/*
	 * Jahia required jcr properties for jnt:page
	 */
	String PROPERTIES_PAGE_PRIMARY_TYPE = "jnt:page";
	
	/*
	 * Jahia required jcr properties for ciQuestion
	 */
	String PROPERTIES_ARTICLE_INTRO = "intro";
	String PROPERTIES_ARTICLE_TAB_TITLTE_1 = "tabTitle1";
	String PROPERTIES_ARTICLE_TAB_TITLTE_2 = "tabTitle2";
	String PROPERTIES_ARTICLE_TAB_TITLTE_3 = "tabTitle3";

	/*
	 * Jahia required jcr properties for ciQuestion
	 */
	String PROPERTIES_FORUM_QUESTION_PRIMARY_TYPE = "jnt:ciQuestion";
	String PROPERTIES_FORUM_REPLY_PRIMARY_TYPE = "jnt:ciReply";
	String PROPERTIES_FORUM_MODIFIED_DATE = "modifiedDate";
	String PROPERTIES_FORUM_NB_OF_REPLIES = "nbOfReplies";
	String PROPERTIES_FORUM_NB_OF_PROF_REPLIES = "nbOfProfReplies";
	String PROPERTIES_FORUM_POST_TITLE = "title";
	String PROPERTIES_FORUM_POST_BODY = "body";
	char[] PROPERTIES_FORUM_TITLE_CHARS_TO_REPLACE = new char[] { ':', '/', '=', '&', '.' };

	String PROPERTIES_JCR_CREATED = "jcr:created";
	String PROPERTIES_SITE_NB_OF_VIEWS = "nbOfViews";
	/*
	 * Jahia jcr properties for newsletter subscription
	 */
	String PROPERTIES_CI_NEWSLETTER_COMPONENT = "jnt:ciSubscriptionComponent";
	String PROPERTIES_CI_NEWSLETTER_COMPONENT_MAIL = "email";
	/*
	 * 
	 * Marker page
	 */
	String PROPERTIES_PAGE_MARKER_ISFORUM = "isForum";
	String PROPERTIES_PAGE_MARKER_ISDUMMYQUESTION = "isDummyQuestion";
	String PROPERTIES_PAGE_ISRUBRIC = "isRubric";
	String PROPERTIES_PAGE_ISTHEMATIC = "isThematic";
	String PROPERTIES_PAGE_LOGO = "logo";
	String PROPERTIES_PAGE_SHORT_TITLE = "shortTitle";
	String PROPERTIES_PAGE_IMAGE = "image";
	String PROPERTIES_PAGE_DESCRIPTION = "description";

	/*
	 * Site properties
	 */
	String PROPERTIES_SITE_AVATAR_MAX_SIZE = "avatarMaxSize";
	String PROPERTIES_SITE_EMAIL_FROM = "emailFrom";
	String PROPERTIES_SITE_EMAIL_BODY = "reportMailBody";
	String PROPERTIES_SITE_EMAIL_TO = "emailTo";
	String PROPERTIES_SITE_EMAIL_NOREPLY = "emailNoReply";
	String PROPERTIES_SITE_MEMBER_PROFILE_LINK = "memberProfileLink";
	String PROPERTIES_SITE_PROFESSIONAL_PROFILE_LINK = "professionalProfileLink";
	String PROPERTIES_SITE_MODERATOR_PROFILE_LINK = "moderatorProfileLink";

	/*
	 * Mail template properties
	 */

	String PROPERTIES_MAIL_TEMPLATE_SUBJECT = "subject";
	String PROPERTIES_MAIL_TEMPLATE_RECIPIENT_BODY = "recipientMailBody";
	String PROPERTIES_MAIL_TEMPLATE_SENDER_BODY = "senderMailBody";

	String MAIL_TEMPLATE_TARGET_WELCOME = "welcomeTarget";
	String MAIL_TEMPLATE_TARGET_MEMBER_MESSAGE = "messageMemberTarget";
	String MAIL_TEMPLATE_TARGET_MEMBER_FRIENDLY_MESSAGE_ARTICLE = "messageFriendlyMemberArticleTarget";
	String MAIL_TEMPLATE_TARGET_MEMBER_FRIENDLY_MESSAGE_QUESTION = "messageFriendlyMemberQuestionTarget";
	String MAIL_TEMPLATE_TARGET_REPORT = "reportTarget";
	String MAIL_TEMPLATE_TARGET_CHANGE_PWD = "changePasswordTarget";

	/*
	 * MISC PROPERTIES
	 */
	String AVATAR_DEFAULT_MALE = "/modules/modules_retraite/img/ciInscription/userMale.png";
	String AVATAR_DEFAULT_MALE_57 = "/modules/modules_retraite/img/ciInscription/userMale57.png";
	String AVATAR_DEFAULT_MALE_65 = "/modules/modules_retraite/img/ciInscription/userMale65.png";
	String AVATAR_DEFAULT_FEMALE = "/modules/modules_retraite/img/ciInscription/userFemale.png";
	String AVATAR_DEFAULT_FEMALE_57 = "/modules/modules_retraite/img/ciInscription/userFemale57.png";
	String AVATAR_DEFAULT_FEMALE_65 = "/modules/modules_retraite/img/ciInscription/userFemale65.png";
	String GENDER_FEMALE = "female";
	String GENDER_MALE = "male";

	String USER_GROUP_MODERATOR = "moderateurs";
	String USER_GROUP_PROFESSIONNEL = "professionnels";
	String USER_CLASS_MEMBER = "member";
	String USER_CLASS_PROFESSIONAL = "professional";
	String USER_TYPE_FUTUR_RETRAITE = "radFuturRetraite";
	String USER_TYPE_RETRAITE_JUNIOR = "radRetJunior";
	String USER_ACTIVITY_SALARIE = "chkSalarie";
	String USER_ACTIVITY_FONCTIONNAIRE = "chkFonctionnaire";
	String USER_ACTIVITY_NON_SALARIE = "chkNonSalarie";

	int COMMUNITY_FIRST_DATE_DAY = 13;
	int COMMUNITY_FIRST_DATE_MONTH = 10;
	int COMMUNITY_FIRST_DATE_YEAR = 2012;
	
	String CR_TEMPLATE_PACKAGE_NAME = "ALM Modules Communaute Retraite";


}
