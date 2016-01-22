/**
 * 
 */
package com.its.core.module.filescan.violation;

import java.awt.Color;
import java.io.Serializable;

/**
 * �������� 2012-12-13 ����08:31:29
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class ScannerParamBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8705390959022791192L;
	
	//ͼƬ�ļ���չ��
	private String imgFileExtension = null;

	//��Ƶ�ļ���չ��
	private String videoFileExtension = null;
	
	//��СͼƬ�ļ�Size,��λ���ֽ�
	private long minImageFileSize = 51200;

	//��С��Ƶ�ļ�Size,��λ���ֽ�
	private long minVideoFileSize = 51200;
	
	//�ӳ�ɨ��ʱ�䣬��λ���룬ȱʡ����180�룩�����ӣ���ɨ��ͼƬ֮ǰ�����ȼ��ͼƬ��lastModifiedʱ�䣬������ʱ��С�ڶ������������ɨ����ļ�����ҪΪ��ֹɨ�����ڴ����е�ͼƬ
	private long delayScanSecond = 180L;

	//����ļ���СС������ֵ��������ļ�����ʱ��,��λ����
	private long maxNoModifyTimeAllow = 1800;
	
	private String defaultWfxwCode = null;
	
	//���T_TEMP_PIC�������ظ���¼��SQL
	private String checkRepeatedViolateRecordTempSql = null;
	
	//���T_FXC_RECORD�������ظ���¼��SQL
	private String checkRepeatedViolateRecordSql = null;	
	
	//ɨ����ɺ�Դ�ļ�����Ŀ¼
	private String backupDir = null;

	private long delayDeleteDay = -1;

	private String deleteFileDir = null;

	private String invalidFileDir = null;
	
    private String backupDataDir = null;
    private String backupDataEncodingFrom = null;
    private String backupDataEncodingTo = null;	
    
    /**
	 * ��Ƶ�ļ��Ƿ��Ǳ���ģ���Щ��Ŀ�д����ͼƬû�ж�Ӧ����Ƶ�ļ�Ҳ�ǺϷ��ģ�
	 * ��������������뽫ɨ������е�require_video������Ϊfalse
	 * ȱʡΪtrue����ʾ�����ͼƬ�б��������Ӧ����Ƶƥ�䣮
	 */
	private boolean requireVideo = true;
	
	/**
	 * �Ƿ�ֱ��ɾ����Ƶ�ļ�������ò�����Ϊtrue����requireVideo������Ϊfalse����Ч.
	 * ��Ϊtrueʱ��ɨ�����ÿ����������ʱ���Ƚ���Ƶ�ļ������
	 */
	private boolean directDeleteVideo = false;
	
	//�Ƿ�ΪͼƬ���ˮӡ ȱʡΪfalse
	private boolean requireAddWaterMark = false;
	
	//��У����Ч���ļ����Ƿ��Ƶ�invalidFileDir�ļ�Ŀ¼��
	private boolean requireMoveInvalidFile = true;
	
	//ͼƬѹ����-1��ʾ��ѹ��
	private float imageCompressQuality = -1;
	
	//��ͼƬ���ڸ���ֵ����λ���ֽڣ�ʱ������ѹ����-1��0������
	private long imageCompressGeSize = -1;
	
	//Ŀ��ͼƬ��ȣ����С��0����ʾ���ı䣩
	private int imageWidth = -1;

	//Ŀ��ͼƬ�߶ȣ����С��0����ʾ���ı䣩
	private int imageHeight = -1;	
	
	//�Ƿ����MD5У����
	private boolean addMd5Verify = false;
	
	
	//ˮӡ��־����(ֻ�е�require_add_watermark����Ϊtrueʱ��Щ������������)
	private int waterMarkFontSize = 14;
	private int waterMarkFontHeight = 18;
	private Color waterMarkFontColor = Color.BLACK;
	private Color waterMarkBgColor = Color.WHITE;
	private int waterMarkBgHeight = 0;
	private int waterMarkLeftMargin = 0;
	private int waterMarkTopMargin = 0;	
	/**
	 * ˮӡλ��
	 * top:ͼƬ�Ϸ�����
	 */
	private String waterMarkPosition = null;
	
	//���ա�24Сʱͳ��T_ITS_VIOLATE_RECORD_TEMPΥ����Ϣ����t_its_traffic_day_stat��t_its_traffic_hour_stat ϸ��������һ��
	private String statTrafficDayCheckExistSql = null;
	private String statTrafficDayInsertSql = null;
	private String statTrafficDayUpdateSql = null;
	
	private String statTrafficHourCheckExistSql = null;	
	private String statTrafficHourInsertSql = null;	
	private String statTrafficHourUpdateSql = null;
	
	public String getImgFileExtension() {
		return imgFileExtension;
	}

	public void setImgFileExtension(String imgFileExtension) {
		this.imgFileExtension = imgFileExtension;
	}

	public String getVideoFileExtension() {
		return videoFileExtension;
	}

	public void setVideoFileExtension(String videoFileExtension) {
		this.videoFileExtension = videoFileExtension;
	}

	public long getMinImageFileSize() {
		return minImageFileSize;
	}

	public void setMinImageFileSize(long minImageFileSize) {
		this.minImageFileSize = minImageFileSize;
	}

	public long getMinVideoFileSize() {
		return minVideoFileSize;
	}

	public void setMinVideoFileSize(long minVideoFileSize) {
		this.minVideoFileSize = minVideoFileSize;
	}

	public long getDelayScanSecond() {
		return delayScanSecond;
	}

	public void setDelayScanSecond(long delayScanSecond) {
		this.delayScanSecond = delayScanSecond;
	}

	public long getMaxNoModifyTimeAllow() {
		return maxNoModifyTimeAllow;
	}

	public void setMaxNoModifyTimeAllow(long maxNoModifyTimeAllow) {
		this.maxNoModifyTimeAllow = maxNoModifyTimeAllow;
	}

	public String getDefaultWfxwCode() {
		return defaultWfxwCode;
	}

	public void setDefaultWfxwCode(String defaultWfxwCode) {
		this.defaultWfxwCode = defaultWfxwCode;
	}

	public String getCheckRepeatedViolateRecordTempSql() {
		return checkRepeatedViolateRecordTempSql;
	}

	public void setCheckRepeatedViolateRecordTempSql(
			String checkRepeatedViolateRecordTempSql) {
		this.checkRepeatedViolateRecordTempSql = checkRepeatedViolateRecordTempSql;
	}

	public String getCheckRepeatedViolateRecordSql() {
		return checkRepeatedViolateRecordSql;
	}

	public void setCheckRepeatedViolateRecordSql(
			String checkRepeatedViolateRecordSql) {
		this.checkRepeatedViolateRecordSql = checkRepeatedViolateRecordSql;
	}

	public String getBackupDir() {
		return backupDir;
	}

	public void setBackupDir(String backupDir) {
		this.backupDir = backupDir;
	}

	public long getDelayDeleteDay() {
		return delayDeleteDay;
	}

	public void setDelayDeleteDay(long delayDeleteDay) {
		this.delayDeleteDay = delayDeleteDay;
	}

	public String getDeleteFileDir() {
		return deleteFileDir;
	}

	public void setDeleteFileDir(String deleteFileDir) {
		this.deleteFileDir = deleteFileDir;
	}

	public String getInvalidFileDir() {
		return invalidFileDir;
	}

	public void setInvalidFileDir(String invalidFileDir) {
		this.invalidFileDir = invalidFileDir;
	}

	public String getBackupDataDir() {
		return backupDataDir;
	}

	public void setBackupDataDir(String backupDataDir) {
		this.backupDataDir = backupDataDir;
	}

	public String getBackupDataEncodingFrom() {
		return backupDataEncodingFrom;
	}

	public void setBackupDataEncodingFrom(String backupDataEncodingFrom) {
		this.backupDataEncodingFrom = backupDataEncodingFrom;
	}

	public String getBackupDataEncodingTo() {
		return backupDataEncodingTo;
	}

	public void setBackupDataEncodingTo(String backupDataEncodingTo) {
		this.backupDataEncodingTo = backupDataEncodingTo;
	}

	public boolean isRequireVideo() {
		return requireVideo;
	}

	public void setRequireVideo(boolean requireVideo) {
		this.requireVideo = requireVideo;
	}

	public boolean isDirectDeleteVideo() {
		return directDeleteVideo;
	}

	public void setDirectDeleteVideo(boolean directDeleteVideo) {
		this.directDeleteVideo = directDeleteVideo;
	}

	public boolean isRequireAddWaterMark() {
		return requireAddWaterMark;
	}

	public void setRequireAddWaterMark(boolean requireAddWaterMark) {
		this.requireAddWaterMark = requireAddWaterMark;
	}

	public boolean isRequireMoveInvalidFile() {
		return requireMoveInvalidFile;
	}

	public void setRequireMoveInvalidFile(boolean requireMoveInvalidFile) {
		this.requireMoveInvalidFile = requireMoveInvalidFile;
	}

	public float getImageCompressQuality() {
		return imageCompressQuality;
	}

	public void setImageCompressQuality(float imageCompressQuality) {
		this.imageCompressQuality = imageCompressQuality;
	}

	public long getImageCompressGeSize() {
		return imageCompressGeSize;
	}

	public void setImageCompressGeSize(long imageCompressGeSize) {
		this.imageCompressGeSize = imageCompressGeSize;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public boolean isAddMd5Verify() {
		return addMd5Verify;
	}

	public void setAddMd5Verify(boolean addMd5Verify) {
		this.addMd5Verify = addMd5Verify;
	}

	public int getWaterMarkFontSize() {
		return waterMarkFontSize;
	}

	public void setWaterMarkFontSize(int waterMarkFontSize) {
		this.waterMarkFontSize = waterMarkFontSize;
	}

	public int getWaterMarkFontHeight() {
		return waterMarkFontHeight;
	}

	public void setWaterMarkFontHeight(int waterMarkFontHeight) {
		this.waterMarkFontHeight = waterMarkFontHeight;
	}

	public Color getWaterMarkFontColor() {
		return waterMarkFontColor;
	}

	public void setWaterMarkFontColor(Color waterMarkFontColor) {
		this.waterMarkFontColor = waterMarkFontColor;
	}

	public Color getWaterMarkBgColor() {
		return waterMarkBgColor;
	}

	public void setWaterMarkBgColor(Color waterMarkBgColor) {
		this.waterMarkBgColor = waterMarkBgColor;
	}	

	public int getWaterMarkBgHeight() {
		return waterMarkBgHeight;
	}

	public void setWaterMarkBgHeight(int waterMarkBgHeight) {
		this.waterMarkBgHeight = waterMarkBgHeight;
	}

	public int getWaterMarkLeftMargin() {
		return waterMarkLeftMargin;
	}

	public void setWaterMarkLeftMargin(int waterMarkLeftMargin) {
		this.waterMarkLeftMargin = waterMarkLeftMargin;
	}

	public int getWaterMarkTopMargin() {
		return waterMarkTopMargin;
	}

	public void setWaterMarkTopMargin(int waterMarkTopMargin) {
		this.waterMarkTopMargin = waterMarkTopMargin;
	}

	public String getWaterMarkPosition() {
		return waterMarkPosition;
	}

	public void setWaterMarkPosition(String waterMarkPosition) {
		this.waterMarkPosition = waterMarkPosition;
	}

	public String getStatTrafficDayCheckExistSql() {
		return statTrafficDayCheckExistSql;
	}

	public void setStatTrafficDayCheckExistSql(String statTrafficDayCheckExistSql) {
		this.statTrafficDayCheckExistSql = statTrafficDayCheckExistSql;
	}

	public String getStatTrafficDayInsertSql() {
		return statTrafficDayInsertSql;
	}

	public void setStatTrafficDayInsertSql(String statTrafficDayInsertSql) {
		this.statTrafficDayInsertSql = statTrafficDayInsertSql;
	}

	public String getStatTrafficDayUpdateSql() {
		return statTrafficDayUpdateSql;
	}

	public void setStatTrafficDayUpdateSql(String statTrafficDayUpdateSql) {
		this.statTrafficDayUpdateSql = statTrafficDayUpdateSql;
	}

	public String getStatTrafficHourCheckExistSql() {
		return statTrafficHourCheckExistSql;
	}

	public void setStatTrafficHourCheckExistSql(String statTrafficHourCheckExistSql) {
		this.statTrafficHourCheckExistSql = statTrafficHourCheckExistSql;
	}

	public String getStatTrafficHourInsertSql() {
		return statTrafficHourInsertSql;
	}

	public void setStatTrafficHourInsertSql(String statTrafficHourInsertSql) {
		this.statTrafficHourInsertSql = statTrafficHourInsertSql;
	}

	public String getStatTrafficHourUpdateSql() {
		return statTrafficHourUpdateSql;
	}

	public void setStatTrafficHourUpdateSql(String statTrafficHourUpdateSql) {
		this.statTrafficHourUpdateSql = statTrafficHourUpdateSql;
	}	

}
