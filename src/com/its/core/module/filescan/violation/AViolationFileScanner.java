/**
 * 
 */
package com.its.core.module.filescan.violation;

import java.awt.Color;
import java.io.File;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.DeviceDirectionBean;
import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.common.sequence.SequenceFactory;
import com.its.core.constant.SystemConstant;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.filescan.AFileScanner;
import com.its.core.util.ColorHelper;
import com.its.core.util.CryptoHelper;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.FilenameFilterByPostfix;
import com.its.core.util.FilenameFilterByPostfixAndSize;
import com.its.core.util.ImageHelper;
import com.its.core.util.PropertiesHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * �������� 2012-12-13 ����08:21:18
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public abstract class AViolationFileScanner extends AFileScanner {
	private static final Log log = LogFactory.getLog(AViolationFileScanner.class);
	
	//ɨ��������
	private ScannerParamBean scannerParam = new ScannerParamBean();
	
	//���ø�����Ŀ�ı�������
	public abstract void configureLocalProperties(XMLProperties props, String propertiesPrefix, int no);

	//����Υ����Ϣ return 0:��ʾ�ɹ�������ֵ����ʾʧ��
	public abstract int processViolationInfoBean(Connection conn, ViolationInfoBean violationInfoBean);

	//����Υ����Ϣ�ɹ���ĺ��ڴ���
	public abstract boolean postProcessViolationInfoBean(ViolationInfoBean violationInfoBean);
	
	//�ӱ����ļ��лָ�Υ����Ϣ�ɹ���ĺ��ڴ���
	public abstract boolean postRestoreViolationInfoBean(ViolationInfoBean violationInfoBean);

	/* (non-Javadoc)
	 * @see com.its.core.module.filescan.AFileScanner#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		scannerParam.setBackupDir(props.getProperty(propertiesPrefix, no, "violation_param.backup_dir"));
		scannerParam.setInvalidFileDir(props.getProperty(propertiesPrefix, no, "violation_param.invalid_file_dir"));	
		scannerParam.setDeleteFileDir(props.getProperty(propertiesPrefix, no, "violation_param.delete_file_dir"));
		scannerParam.setBackupDataDir(props.getProperty(propertiesPrefix,no,"violation_param.backup_data_dir"));
		scannerParam.setBackupDataEncodingFrom(props.getProperty(propertiesPrefix,no,"violation_param.backup_data_encoding.from"));
		scannerParam.setBackupDataEncodingTo(props.getProperty(propertiesPrefix,no,"violation_param.backup_data_encoding.to"));		
		scannerParam.setDelayDeleteDay(PropertiesHelper.getLong(propertiesPrefix, no, "violation_param.delay_delete_day", props, -1));
		scannerParam.setMaxNoModifyTimeAllow(PropertiesHelper.getLong(propertiesPrefix, no, "violation_param.max_no_modify_time_allow", props, scannerParam.getMaxNoModifyTimeAllow()));
		scannerParam.setDefaultWfxwCode(props.getProperty(propertiesPrefix, no, "violation_param.default_wfxw_code"));
		
		String strRequireMoveInvalidFile = props.getProperty(propertiesPrefix, no, "violation_param.require_move_invalid_file");
		if(StringHelper.isEmpty(strRequireMoveInvalidFile) || "true".equalsIgnoreCase(strRequireMoveInvalidFile.trim())){
			scannerParam.setRequireMoveInvalidFile(true);
		}
		else{
			scannerParam.setRequireMoveInvalidFile(false);
		}

		//violation_param.image
		scannerParam.setImgFileExtension(props.getProperty(propertiesPrefix, no, "violation_param.image.img_file_extension"));
		
		scannerParam.setDelayScanSecond(PropertiesHelper.getLong(propertiesPrefix, no, "violation_param.image.delay_scan_second", props, scannerParam.getDelayScanSecond()));
		scannerParam.setMinImageFileSize(PropertiesHelper.getLong(propertiesPrefix, no, "violation_param.image.min_image_file_size", props, scannerParam.getMinImageFileSize()));
		String strRequireAddWaterMark = props.getProperty(propertiesPrefix, no, "violation_param.image.require_add_watermark");
		if(StringHelper.isEmpty(strRequireAddWaterMark) || "false".equalsIgnoreCase(strRequireAddWaterMark.trim())){
			scannerParam.setRequireAddWaterMark(false);
		}
		else{
			scannerParam.setRequireAddWaterMark(true);
		}
		scannerParam.setImageCompressQuality(PropertiesHelper.getFloat(propertiesPrefix, no, "violation_param.image.image_compress_quality", props, scannerParam.getImageCompressQuality()));
		scannerParam.setImageCompressGeSize(PropertiesHelper.getLong(propertiesPrefix, no, "violation_param.image.image_compress_ge_size", props, scannerParam.getImageCompressGeSize()));
		
		//ͼƬĿ���ȣ����Ϊ�ջ�С�ڵ��ڣ�����ʾ���ı䣩
		String strImageWidth = props.getProperty(propertiesPrefix, no, "violation_param.image.img_width");		
		if(StringHelper.isNotEmpty(strImageWidth)){
			scannerParam.setImageWidth(Integer.parseInt(strImageWidth));
		}
		else{
			scannerParam.setImageWidth(-1);
		}
		
		//ͼƬĿ��߶ȣ����Ϊ�ջ�С�ڵ��ڣ�����ʾ���ı䣩
		String strImageHeight = props.getProperty(propertiesPrefix, no, "violation_param.image.img_height");		
		if(StringHelper.isNotEmpty(strImageHeight)){
			scannerParam.setImageHeight(Integer.parseInt(strImageHeight));
		}
		else{
			scannerParam.setImageHeight(-1);
		}
		
		//�Ƿ����MD5����ˮӡ֤��
		String strMd5Verify = props.getProperty(propertiesPrefix, no, "violation_param.image.add_md5_verify");		
		scannerParam.setAddMd5Verify(StringHelper.getBoolean(strMd5Verify));
		
		//ˮӡ��־
		scannerParam.setWaterMarkPosition(props.getProperty(propertiesPrefix, no, "violation_param.watermark.position"));
		scannerParam.setWaterMarkFontSize(PropertiesHelper.getInt(propertiesPrefix,no,"violation_param.watermark.font_size",props,scannerParam.getWaterMarkFontSize()));
		scannerParam.setWaterMarkFontHeight(PropertiesHelper.getInt(propertiesPrefix,no,"violation_param.watermark.font_height",props,scannerParam.getWaterMarkFontHeight()));
		scannerParam.setWaterMarkLeftMargin(PropertiesHelper.getInt(propertiesPrefix,no,"violation_param.watermark.left_margin",props,scannerParam.getWaterMarkLeftMargin()));
		scannerParam.setWaterMarkTopMargin(PropertiesHelper.getInt(propertiesPrefix,no,"violation_param.watermark.top_margin",props,scannerParam.getWaterMarkTopMargin()));
		scannerParam.setWaterMarkBgHeight(PropertiesHelper.getInt(propertiesPrefix,no,"violation_param.watermark.bg_height",props,scannerParam.getWaterMarkBgHeight()));
		Color fontColor = ColorHelper.getColor(props.getProperty(propertiesPrefix, no, "violation_param.watermark.font_color"));
		if(fontColor!=null) scannerParam.setWaterMarkFontColor(fontColor);		
		Color bgColor = ColorHelper.getColor(props.getProperty(propertiesPrefix, no, "violation_param.watermark.bg_color"));
		if(bgColor!=null) scannerParam.setWaterMarkBgColor(bgColor);
		
		//violation_param.video
		String strRequireVideo = props.getProperty(propertiesPrefix, no, "violation_param.video.require_video");
		if(StringHelper.isEmpty(strRequireVideo) || "true".equalsIgnoreCase(strRequireVideo.trim())){
			scannerParam.setRequireVideo(true);
		}
		else{
			scannerParam.setRequireVideo(false);
		}	
		
		String strDirectDeleteVideo = props.getProperty(propertiesPrefix, no, "violation_param.video.direct_delete_video");
		if(StringHelper.isEmpty(strDirectDeleteVideo) || "false".equalsIgnoreCase(strDirectDeleteVideo.trim())){
			scannerParam.setDirectDeleteVideo(false);
		}
		else{
			scannerParam.setDirectDeleteVideo(true);
		}			
		
		scannerParam.setVideoFileExtension(props.getProperty(propertiesPrefix, no, "violation_param.video.video_file_extension"));
		
		scannerParam.setMinVideoFileSize(PropertiesHelper.getLong(propertiesPrefix, no, "violation_param.video.min_video_file_size", props, scannerParam.getMinVideoFileSize()));
		
		scannerParam.setCheckRepeatedViolateRecordTempSql(props.getProperty(propertiesPrefix, no, "violation_param.check_repeated.violate_record_temp"));
		scannerParam.setCheckRepeatedViolateRecordSql(props.getProperty(propertiesPrefix, no, "violation_param.check_repeated.violate_record"));

		//����ͳ��T_ITS_VIOLATE_RECORD_TEMPΥ����Ϣ����t_its_traffic_day_stat
		scannerParam.setStatTrafficDayCheckExistSql(props.getProperty(propertiesPrefix, no, "violation_param.day_stat_sql.check_exist"));
		scannerParam.setStatTrafficDayInsertSql(props.getProperty(propertiesPrefix, no, "violation_param.day_stat_sql.insert"));
		scannerParam.setStatTrafficDayUpdateSql(props.getProperty(propertiesPrefix, no, "violation_param.day_stat_sql.update"));
		
		//��24Сʱͳ��T_ITS_VIOLATE_RECORD_TEMPΥ����Ϣ����t_its_traffic_hour_stat
		scannerParam.setStatTrafficHourCheckExistSql(props.getProperty(propertiesPrefix, no, "violation_param.hour_stat_sql.check_exist"));
		scannerParam.setStatTrafficHourInsertSql(props.getProperty(propertiesPrefix, no, "violation_param.hour_stat_sql.insert"));
		scannerParam.setStatTrafficHourUpdateSql(props.getProperty(propertiesPrefix, no, "violation_param.hour_stat_sql.update"));
		
		try {
			FileHelper.createDir(scannerParam.getBackupDir());
			FileHelper.createDir(scannerParam.getInvalidFileDir());
			FileHelper.createDir(scannerParam.getDeleteFileDir());
		} catch (Exception ex) {
			log.error(ex);
		}
		
		this.configureLocalProperties(props, propertiesPrefix, no);

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.filescan.AFileScanner#execute()
	 */
	@Override
	public void execute() {
		if(scannerParam.isDirectDeleteVideo() && !scannerParam.isRequireVideo()){
			int cleanNum = this.cleanVideoFile();
			log.debug("��������Ƶ�ļ���"+cleanNum+"����");
		}
		
		//������ļ�
		if (scannerParam.getDelayDeleteDay() > 0L) {			
			this.cleanOldFile();
		}			
		
		Connection conn = null;
		boolean isUseDbConn = SystemConstant.getInstance().isUseDbConnection();
		boolean hasRefreshed = false;
		try {
			boolean hasConn = false;
			if(isUseDbConn){
				try {
					conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
					hasConn = true;
				} catch (Exception ex) {
					log.error("����ʧ�ܣ�" + ex.getMessage(), ex);
					hasRefreshed = true;
					ConnectionProviderFactory.getInstance().refreshConnProperties();
				}
			}
			
			long startTime = System.currentTimeMillis();
			
			int dirNum = this.getScanDirs().length;
			int scanFileCount = 0;
			for(int i=0;i<dirNum && scanFileCount<this.getMaxScanFileNum();i++){
				String strScanDir = this.getScanDirs()[i];
				log.debug("��ʼɨ�贳����ļ�Ŀ¼��" + strScanDir);
				File scanDir = new File(strScanDir);
				if (!scanDir.exists() || !scanDir.isDirectory()) {
					log.error("������ļ�Ŀ¼��" + scanDir + " �����ڣ�");
					continue;
				}	
				
				this.cleanSmallFile(scanDir);
				
				scanFileCount = this.processorDir(scanDir,scanFileCount,conn,hasConn,isUseDbConn);
			}
			
			long currentTime = System.currentTimeMillis();
			log.info("����ɨ�蹲��ʱ��" + ((currentTime - startTime) / 1000F) + "�룬����Υ����¼��" + scanFileCount + "����");
			
			//����ǰ���ʧ�ܵı�����������һ����������
			try {
				if(!isUseDbConn){
					this.restoreBackupData(conn,isUseDbConn);
				}
				else if(hasConn){
					this.restoreBackupData(conn,isUseDbConn);
				}				
			} catch (Exception ex) {}				
		} catch (SQLException sqlEx) {
			ConnectionProviderFactory.getInstance().refreshConnProperties();
			hasRefreshed = true;
			log.error(sqlEx);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		} finally {
			//this.setRunning(false);
			if (conn != null) {
				if (hasRefreshed) {
					try {
						conn.close();
					} catch (Exception ex1) {
					}
				} else {
					try {
						ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
					} catch (Exception ex1) {
					}
				}
			}
		}

	}
	
	/**
	 * ������ɨ��Ŀ¼���ݹ�ɨ����Ŀ¼
	 * @param scanDir
	 * @param scanFileCount
	 * @param conn
	 * @param hasConn
	 * @return
	 * @throws Exception
	 */
	private int processorDir(File scanDir,int scanFileCount,Connection conn,boolean hasConn,boolean isUseDbConn) throws Exception{
		log.debug("��ʼɨ���ļ�Ŀ¼��" + scanDir.getAbsolutePath());
		if (!scanDir.exists() || !scanDir.isDirectory()) {
			log.error("�ļ�Ŀ¼��" + scanDir + " �����ڣ�");
			return scanFileCount;
		}

		File[] jpgFileArr = scanDir.listFiles(new FilenameFilterByPostfixAndSize(scannerParam.getImgFileExtension(), true, true, scannerParam.getMinImageFileSize()));
		int fileNum = jpgFileArr.length;
		for (int j = 0; j < fileNum && scanFileCount<this.getMaxScanFileNum(); j++) {
			File jpgFile = jpgFileArr[j];
			if(jpgFile.isDirectory()){
				scanFileCount = this.processorDir(jpgFile, scanFileCount,conn,hasConn,isUseDbConn);
			}else{
				log.debug("ɨ���ļ���"+jpgFile.getAbsolutePath());		
				long lastModified = jpgFile.lastModified();
				if ((System.currentTimeMillis() - lastModified) < (scannerParam.getDelayScanSecond()*1000)){
					log.debug("ͼƬ����޸�ʱ��С�ڹ涨ʱ�ޣ�"+scannerParam.getDelayScanSecond()+"�룬��Ҫ�ȴ���");	
					continue;
				} 
//				else {
//					String targetFileDir = scannerParam.getInvalidFileDir() + DateHelper.dateToString(new Date(), "yyyyMMdd");
//					FileHelper.createDir(targetFileDir);
//							
//					String targetFileName = targetFileDir + "/" + jpgFile.getName();
//					boolean moveSuccess = FileHelper.moveFile(jpgFile, new File(targetFileName));
//					if (moveSuccess)
//						log.info("�����涨ʱ��δ�ҵ�ƥ���ͼƬ���ļ���" + jpgFile.getName() + " ת�Ƴɹ���");
//					else
//						log.info("�����涨ʱ��δ�ҵ�ƥ���ͼƬ���ļ���" + jpgFile.getName() + " ת��ʧ�ܣ��ļ���������ʹ���У�");
//				}
				
				//ͨ���ļ�������Υ����Ϣ
				ViolationInfoBean violationInfoBean = null;
				try {
					violationInfoBean = this.parseViolationInfo(scanDir.getAbsolutePath(), jpgFile);
				} catch (VideoFileNotFoundException vfnfExce) {
					log.warn("�ļ���" + jpgFile.getName() + " δ�ҵ���Ӧ����Ƶ�ļ���δ��⣡");
					
					if ((System.currentTimeMillis() - lastModified) > scannerParam.getMaxNoModifyTimeAllow() * 1000) {
						String targetFileDir = scannerParam.getInvalidFileDir() + DateHelper.dateToString(new Date(), "yyyyMMdd");
						FileHelper.createDir(targetFileDir);
								
						String targetFileName = targetFileDir + "/" + jpgFile.getName();
						boolean moveSuccess = FileHelper.moveFile(jpgFile, new File(targetFileName));
						if (moveSuccess)
							log.warn("�ļ���" + jpgFile.getName() + " ת�Ƴɹ���");
						else
							log.warn("�ļ���" + jpgFile.getName() + " ת��ʧ�ܣ��ļ���������ʹ���У�");						
					}			
					continue;
				} catch (Exception ex) {
					log.error("�����ļ���'"+jpgFile.getName()+"'ʧ�ܣ�"+ex.getMessage(),ex);
					if(scannerParam.isRequireMoveInvalidFile()){
						try {
							String targetFileDir = scannerParam.getInvalidFileDir() + DateHelper.dateToString(new Date(), "yyyyMMdd");
							FileHelper.createDir(targetFileDir);
							String targetFileName = targetFileDir + "/" + jpgFile.getName();
							log.warn("�����ļ�ת�Ƶ���" + targetFileName, ex);
							boolean moveSuccess = FileHelper.moveFile(jpgFile, new File(targetFileName));
							if (moveSuccess)
								log.warn("�ļ���" + jpgFile.getName() + " ת�Ƴɹ���");
							else
								log.warn("�ļ���" + jpgFile.getName() + " ת��ʧ�ܣ��ļ���������ʹ���У�");
						} catch (Exception ex2) {
							log.error(ex2);
						}
					}
					continue;
				}
				
				log.debug("violationInfoBean="+violationInfoBean);
				if(violationInfoBean==null) continue;
				
				//�豸IDת��
//				String targetId = DeviceInfoConvertFactory.getInstance().getTargetDeviceId(violationInfoBean.getDeviceId(), violationInfoBean.getDirectionNo());
//				if(StringHelper.isNotEmpty(targetId)) violationInfoBean.setDeviceId(targetId);
				
				violationInfoBean.setFileDir(scanDir.getAbsolutePath()+"/");
				
				if(!this.verifyVideoFile(violationInfoBean)){
					continue;
				}	
				
				Object obj = DeviceInfoLoaderFactory.getInstance().getDeviceMap().get(violationInfoBean.getDeviceId());
				if (obj == null) {
					log.warn("δ�ҵ��豸ID��" + violationInfoBean.getDeviceId() + "��Ӧ���豸��Ϣ��");
					
					//��Ҫ����װ���豸��Ϣ
					DeviceInfoLoaderFactory.getInstance().setRequireReload(true);					
					
					//������Ч�ļ����п�������Ϊ�豸��Ϣ��δ���ö���ɵģ����豸��Ϣ������ɺ���Ҫ����Ч�ļ�COPY���ϴ�Ŀ¼����ɨ��
					if((!isUseDbConn || hasConn) && scannerParam.isRequireMoveInvalidFile()){
						//ת����Ч���豸��Ų���ȷ���ļ�����Ч�ļ�Ŀ¼
						try {
							String targetFileDir = scannerParam.getInvalidFileDir() + DateHelper.dateToString(new Date(), "yyyyMMdd");
							FileHelper.createDir(targetFileDir);
							String sourceFileName,targetFileName;
							for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
								String imageFileName = violationInfoBean.getImageFiles()[i];
								if(StringHelper.isNotEmpty(imageFileName)){
									sourceFileName = violationInfoBean.getFileDir() + "/" + imageFileName;
									targetFileName = targetFileDir + "/" + imageFileName;
									log.debug("��ʼ�ƶ��ļ���" + sourceFileName + " �� " + targetFileName);
									FileHelper.moveFile(sourceFileName, targetFileName);					
								}
							}
							
							if(StringHelper.isNotEmpty(violationInfoBean.getVideoFile())){
								sourceFileName = violationInfoBean.getFileDir() + "/" + violationInfoBean.getVideoFile();
								targetFileName = targetFileDir + "/" + violationInfoBean.getVideoFile();		
								log.debug("��ʼ�ƶ��ļ���" + sourceFileName + " �� " + targetFileName);
								FileHelper.moveFile(sourceFileName, targetFileName);
							}
						} catch (Exception ex2) {
							log.error(ex2);
						}
					}		
					continue;
				}
				
				DeviceInfoBean dib = (DeviceInfoBean) obj;
				violationInfoBean.setDeviceInfoBean(dib);
				
				if(scannerParam.getImageCompressQuality()>0){
					this.compress(violationInfoBean);
				}
				
				if(scannerParam.getImageWidth()>0 && scannerParam.getImageHeight()>0){
					this.changeSize(violationInfoBean);
					//ImageHelper.changeSize(jpgFile, scannerParam.getImageWidth(), scannerParam.getImageHeight());
				}
				
				if(scannerParam.isRequireAddWaterMark()){
					this.createMark(violationInfoBean);
				}
				
				//���MD5У����
				if(scannerParam.isAddMd5Verify()){
					for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
						String imageFileName = violationInfoBean.getImageFiles()[i];
						if(StringHelper.isNotEmpty(imageFileName)){
							String sourceFileName = violationInfoBean.getFileDir() + "/" + imageFileName;
							byte[] resultFileByte = FileHelper.getBytes(sourceFileName);
							byte[] md5 = CryptoHelper.getUniHzImageEncryptString(resultFileByte).getBytes();
							List<byte[]> byteList = new ArrayList<byte[]>();
							byteList.add(resultFileByte);
							byteList.add(md5);
							FileHelper.writeFile(sourceFileName,byteList);
						}
					}
										
				}
				
				if(isUseDbConn) this.process(conn, hasConn, violationInfoBean);
				else this.process(violationInfoBean);
				
				//log.debug("�����룺"+returnCode);
				scanFileCount++;
				
				try {
					Thread.sleep(10);
				} catch (Exception ex) {}
			}
		}				
			
		return scanFileCount;	
	}	
	
	/**
	 * ����������С�ߴ���ļ�
	 */
	protected void cleanSmallFile(File scanDir){
		if (!scanDir.exists() || !scanDir.isDirectory()) {
			return;
		}		
		log.debug("��ʼ����Ŀ¼'"+scanDir.getAbsolutePath()+"'�²����ϴ�С���ļ���");
		File[] fileArr = scanDir.listFiles(new FilenameFilterByPostfixAndSize(scannerParam.getImgFileExtension(), true, true, 0L, scannerParam.getMinImageFileSize()));
		int fileNum = fileArr.length;
		for (int i = 0; i < fileNum; i++) {
			File file = fileArr[i];
			if(file.isDirectory()){
				this.cleanSmallFile(file);
			}else{
				//log.debug("ɨ���ļ���"+jpgFile.getAbsolutePath());		
				
				//ͨ���ļ�������Υ����Ϣ
				ViolationInfoBean violationInfoBean = null;
				try {
					violationInfoBean = this.parseViolationInfo(scanDir.getAbsolutePath(), file);
					if(violationInfoBean!=null) violationInfoBean.setFileDir(scanDir.getAbsolutePath()+"/");
				} catch (Exception ex) {
					log.error(ex.getMessage(),ex);
				}
				
				//��С�ļ�����ʱ��
				if ((System.currentTimeMillis() - file.lastModified()) > scannerParam.getMaxNoModifyTimeAllow() * 1000) {
					if(violationInfoBean!=null){
						for(int j=0;j<violationInfoBean.getImageFiles().length;j++){
							String imageFileName = violationInfoBean.getImageFiles()[j];
							if(StringHelper.isNotEmpty(imageFileName)){
								imageFileName = violationInfoBean.getFileDir() + imageFileName;								
								log.debug("ɾ���ļ���" + imageFileName);
								try {
									FileHelper.delFile(new File(imageFileName));
								} catch (Exception e) {
									log.error(e);
								}					
							}
							if(StringHelper.isNotEmpty(violationInfoBean.getVideoFile())){
								String videoFileName = violationInfoBean.getFileDir() + violationInfoBean.getVideoFile();
								log.debug("ɾ���ļ���" + videoFileName);
								try {
									FileHelper.delFile(new File(videoFileName));
								} catch (Exception e) {
									log.error(e);
								}	
							}					
						}					
					}
					else{
						try {
							log.debug("ɾ���ļ���" + file.getAbsolutePath());
							FileHelper.delFile(file);
						} catch (Exception e) {
							log.error(e);
						}						
					}
				}
			}
		}
	}
	
	/**
	 * ��⵱ǰͼƬ�Ƿ�Ϊ�ظ���¼���Ѵ�����T_ITS_VIOLATE_RECORD_TEMP��T_ITS_VIOLATE_RECORD�У�
	 * @param violationInfoBean
	 * @return
	 */
	protected boolean isRepeatedData(Connection conn,ViolationInfoBean violationInfoBean) throws Exception{
		boolean repeated = this.isRepeatedDataInTempPic(conn, violationInfoBean);
		
		if(!repeated){
			repeated = this.isRepeatedDataInFxcRecord(conn, violationInfoBean);
		}
		
		return repeated;
	}
	
	/**
	 * ����ͳ��T_ITS_VIOLATE_RECORDΥ����Ϣ����T_ITS_TRAFFIC_DAY_STAT
	 * @param conn
	 * @param violationInfoBean
	 * @return
	 */
	protected boolean statTrafficDay(Connection conn,ViolationInfoBean violationInfoBean){
		boolean success = true;
		PreparedStatement preStatement = null;
		ResultSet resultSet = null;		
		String checkExistSql = this.getScannerParam().getStatTrafficDayCheckExistSql();
		if(StringHelper.isNotEmpty(checkExistSql)){
			try{
//				log.debug(checkExistSql);
				
				//�Ƿ��г�������
				boolean hasLane = false;
				if(checkExistSql.toUpperCase().indexOf("LANE_NO")!=-1){
					hasLane = true;
				}
				
				Timestamp violateDay = new Timestamp(DateHelper.parseDateString(DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd"),"yyyyMMdd").getTime());
				long statId = -1L;
				long roadId = Long.valueOf(violationInfoBean.getDeviceInfoBean().getRoadId());
				String directionCode = violationInfoBean.getDirectionNo();
				if(StringHelper.isEmpty(directionCode)){
					List<DeviceDirectionBean> ddbList = violationInfoBean.getDeviceInfoBean().getDirectionList();
					if(ddbList!=null && ddbList.size()>0){
						DeviceDirectionBean ddb = (DeviceDirectionBean)ddbList.get(0);
						directionCode = ddb.getDirectionCode();
					}
				}
				//select id from T_ITS_TRAFFIC_DAY_STAT where ROAD_ID=? and DEVICE_ID=? and DIRECTION_CODE=? and VIOLATE_DAY=?
				preStatement = conn.prepareStatement(checkExistSql);
				preStatement.setLong(1,roadId);	
				preStatement.setString(2, violationInfoBean.getDeviceId());
				preStatement.setString(3, directionCode);		
				if(hasLane){
					preStatement.setString(4, violationInfoBean.getLine());
					preStatement.setTimestamp(5, violateDay);
				}
				else{
					preStatement.setTimestamp(4, violateDay);
				}
				
				resultSet = preStatement.executeQuery();
				if(resultSet.next()){
					statId = resultSet.getLong("id");					
				}
				DatabaseHelper.close(resultSet, preStatement);
				if(statId==-1L){
					//����һ����¼:insert into T_ITS_TRAFFIC_DAY_STAT (ID,ROAD_ID,DEVICE_ID,DIRECTION_CODE,VIOLATE_DAY,VIOLATE_SUM) values (?,?,?,?,?,?)
					statId = (long)SequenceFactory.getInstance().getStatTrafficDayStatSequence();
					preStatement = conn.prepareStatement(this.getScannerParam().getStatTrafficDayInsertSql());
					preStatement.setLong(1,statId);	
					preStatement.setLong(2,roadId);	
					preStatement.setString(3, violationInfoBean.getDeviceId());
					preStatement.setString(4, directionCode);	
					if(hasLane){
						preStatement.setString(5, violationInfoBean.getLine());
						preStatement.setTimestamp(6, violateDay);	
						preStatement.setLong(7, 1L);							
					}
					else{
						preStatement.setTimestamp(5, violateDay);	
						preStatement.setLong(6, 1L);	
					}

//					log.debug("statId = "+statId+" created!");
				}
				else{
					//���¼�¼:update T_ITS_TRAFFIC_DAY_STAT set VIOLATE_SUM=VIOLATE_SUM+1 where ID=?
					preStatement = conn.prepareStatement(this.getScannerParam().getStatTrafficDayUpdateSql());
					preStatement.setLong(1,statId);	
//					log.debug("statId = "+statId+" updated!");
				}
				preStatement.executeUpdate();
			}catch(Exception ex){
				success = false;
				log.error(ex.getMessage(),ex);
			}finally{
				DatabaseHelper.close(resultSet, preStatement);
			}
		}
		
		if(StringHelper.isNotEmpty(this.getScannerParam().getStatTrafficHourCheckExistSql())){
			try{
//				log.debug(this.getScannerParam().getStatTrafficHourCheckExistSql());				
				
				String violateHour = DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyy-MM-dd hh");
				long id = -1L;
				long roadId = Long.valueOf(violationInfoBean.getDeviceInfoBean().getRoadId());
				String directionCode = violationInfoBean.getDirectionNo();
				if(StringHelper.isEmpty(directionCode)){
					List<DeviceDirectionBean> ddbList = violationInfoBean.getDeviceInfoBean().getDirectionList();
					if(ddbList!=null && ddbList.size()>0){
						DeviceDirectionBean ddb = (DeviceDirectionBean)ddbList.get(0);
						directionCode = ddb.getDirectionCode();
					}
				}
				//select id from T_ITS_TRAFFIC_HOUR_STAT where ROAD_ID=? and DEVICE_ID=? and DIRECTION_CODE=? and LANE_NO=? and RELEASE_TIME=?
				preStatement = conn.prepareStatement(this.getScannerParam().getStatTrafficHourCheckExistSql());
				preStatement.setLong(1,roadId);	
				preStatement.setString(2, violationInfoBean.getDeviceId());
				preStatement.setString(3, directionCode);						
				preStatement.setString(4, violationInfoBean.getLine());
				preStatement.setString(5, violateHour);				
				
				resultSet = preStatement.executeQuery();
				if(resultSet.next()){
					id = resultSet.getLong("id");					
				}
				DatabaseHelper.close(resultSet, preStatement);
				if(id==-1L){
					//����һ����¼:insert into T_ITS_TRAFFIC_HOUR_STAT (ID,ROAD_ID,DEVICE_ID,DIRECTION_CODE,LANE_NO,RELEASE_TIME,FLUX) values (?,?,?,?,?,?,?)
					id = (long)SequenceFactory.getInstance().getStatTrafficHourStatSequence();
					preStatement = conn.prepareStatement(this.getScannerParam().getStatTrafficHourInsertSql());
					preStatement.setLong(1,id);	
					preStatement.setLong(2,roadId);	
					preStatement.setString(3, violationInfoBean.getDeviceId());
					preStatement.setString(4, directionCode);	
					preStatement.setString(5, violationInfoBean.getLine());
					preStatement.setString(6, violateHour);	
					preStatement.setLong(7, 1L);						
					

//					log.debug("id = "+id+" created!");
				}
				else{
					//���¼�¼:update T_ITS_TRAFFIC_HOUR_STAT set FLUX=FLUX+1 where ID=?
					preStatement = conn.prepareStatement(this.getScannerParam().getStatTrafficHourUpdateSql());
					preStatement.setLong(1,id);	
//					log.debug("id = "+id+" updated!");
				}
				preStatement.executeUpdate();
			}catch(Exception ex){
				success = false;
				log.error(ex.getMessage(),ex);
			}finally{
				DatabaseHelper.close(resultSet, preStatement);
			}
		}
		return success;
	}
	
	/**
	 * �Ƿ��Ѵ�����T_ITS_VIOLATE_RECORD_TEMP����
	 * @param conn
	 * @param violationInfoBean
	 * @return
	 * @throws Exception
	 */
	protected boolean isRepeatedDataInTempPic(Connection conn,ViolationInfoBean violationInfoBean) throws Exception{
		boolean repeated = false;
		String checkRepeatedTempPicSql = this.getScannerParam().getCheckRepeatedViolateRecordTempSql();
		if(StringHelper.isNotEmpty(checkRepeatedTempPicSql)){
			log.debug(checkRepeatedTempPicSql);
			PreparedStatement preStatement = null;
			ResultSet resultSet = null;
			try{
				preStatement = conn.prepareStatement(checkRepeatedTempPicSql);
				preStatement.setString(1, "%/"+violationInfoBean.getImageFiles()[0]);				
				resultSet = preStatement.executeQuery();
				if(resultSet.next()){
					String id = resultSet.getString("id");
					log.info(violationInfoBean.getImageFiles()[0]+" �ظ���T_ITS_VIOLATE_RECORD_TEMP:ID="+id+"����");
					repeated = true;
				}
				DatabaseHelper.close(resultSet, preStatement);
			}catch(Exception ex){
				log.error(ex.getMessage(),ex);
				throw ex;
			}finally{
				DatabaseHelper.close(resultSet, preStatement);
			}
		}		
		
		return repeated;
	}	
	
	/**
	 * �Ƿ��Ѵ�����T_ITS_VIOLATE_RECORD����
	 * @param conn
	 * @param violationInfoBean
	 * @return
	 * @throws Exception
	 */
	protected boolean isRepeatedDataInFxcRecord(Connection conn,ViolationInfoBean violationInfoBean) throws Exception{
		boolean repeated = false;
		String checkRepeatedFxcRecordSql = this.getScannerParam().getCheckRepeatedViolateRecordSql();
		if(StringHelper.isNotEmpty(checkRepeatedFxcRecordSql)){
			log.debug(checkRepeatedFxcRecordSql);
			PreparedStatement preStatement = null;
			ResultSet resultSet = null;
			try{
				preStatement = conn.prepareStatement(checkRepeatedFxcRecordSql);
				preStatement.setString(1, "%/"+violationInfoBean.getImageFiles()[0]);				
				resultSet = preStatement.executeQuery();
				if(resultSet.next()){
					String id = resultSet.getString("id");
					log.info(violationInfoBean.getImageFiles()[0]+" �ظ���T_ITS_VIOLATE_RECORD:ID="+id+"����");
					repeated = true;
				}
				DatabaseHelper.close(resultSet, preStatement);
			}catch(Exception ex){
				log.error(ex.getMessage(),ex);
				throw ex;
			}finally{
				DatabaseHelper.close(resultSet, preStatement);
			}				
		}
		return repeated;
	}		
	
	/**
	 * ����ظ�
	 * @param violationInfoBean
	 * @return
	 * @throws Exception
	 */
	protected boolean isRepeatedData(ViolationInfoBean violationInfoBean) throws Exception{
		return false;
	}
	
	/**
	 * �����ظ����� (ȱʡ��ɾ��)
	 * @param violationInfoBean
	 */
	protected void processRepeatedData(ViolationInfoBean violationInfoBean){
		String fileName = null;
		for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
			String imageFileName = violationInfoBean.getImageFiles()[i];
			if(StringHelper.isNotEmpty(imageFileName)){
				fileName = violationInfoBean.getFileDir() + "/" + imageFileName;
				log.debug("ɾ���ظ��ļ���" + fileName);
				try {
					FileHelper.delFile(new File(fileName));
				} catch (Exception e) {
					log.error(e);
				}					
			}
		}
		
		if(StringHelper.isNotEmpty(violationInfoBean.getVideoFile())){
			fileName = violationInfoBean.getFileDir() + "/" + violationInfoBean.getVideoFile();
			log.debug("ɾ���ظ��ļ���" + fileName);
			try {
				FileHelper.delFile(new File(fileName));
			} catch (Exception e) {
				log.error(e);
			}		
		}		
	}
	
	/**
	 * ��ȡ�ļ�����ID�ֶε�ֵ
	 * @param fileName
	 * @return
	 */
	protected String getFileNameIdStr(String fileName) {
		int idIndex = fileName.indexOf("ID");
		String idStr = fileName.substring(idIndex + 2, idIndex + 6);
		return idStr;
	}	
	
	/**
	 * ����ͼƬ�ļ�������ͼƬ�ļ����Ƶĸ�ʽ����ȡ��Ӧ����Ƶ�ļ���
	 * @param imageFile
	 * @return
	 */
	protected String getVideoFileName(String jpgOriFileName,String scanDir) {
		File dirScan = new File(scanDir);
		String prefix = jpgOriFileName;
		String postfix = scannerParam.getVideoFileExtension();
		log.debug(prefix+"\t"+postfix);
		File videoFileArr[] = dirScan.listFiles(new FilenameFilterByPostfix(prefix,postfix, true));
		if (videoFileArr != null) {
			int len = videoFileArr.length;
			if(len>0) return videoFileArr[0].getName();
		}
		return null;
	}
	
	/**
	 * �����ļ�������Υ����Ϣ
	 * @param scanDir
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	protected ViolationInfoBean parseViolationInfo(String scanDir,File imageFile) throws Exception{
		String oriFileName = imageFile.getName();
		String fileName = oriFileName.trim().toUpperCase();
		
		//����ȫ��ͼƬ��ɨ�裬ͨ����дͼƬ�����ƥ�� (��Ҫ���������ͼƬ�ų�)
		if((fileName.endsWith(".P.JPG") || fileName.endsWith(".P.K.JPG")) && !fileName.endsWith(".F.P.K.JPG")){
			return null;
		}
		
		ViolationInfoBean bean = new ViolationInfoBean();
		
		int deviceIndex = fileName.indexOf("R") + 1;
		String deviceId = fileName.substring(deviceIndex, deviceIndex + 5);
		bean.setDeviceId(deviceId);
		
		int timeIndex = fileName.indexOf("T") + 1;
		String timeStr = fileName.substring(timeIndex, timeIndex + 14);
		Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(timeStr);
		bean.setViolateTime(date);
		
		String line = "00";
		if(fileName.indexOf("L")!=-1){
			int lineIndex = fileName.indexOf("L") + 1;
			line = fileName.substring(lineIndex, lineIndex + 2);			
		}
		bean.setLine(line);	
		
		String[] picFiles = null;
		int idIndexOf = fileName.indexOf("ID");		
		if (idIndexOf != -1) {			
			if(!(scannerParam.isDirectDeleteVideo() && !scannerParam.isRequireVideo())){		
				String fileId = this.getFileNameIdStr(fileName);
				if (fileId != null && !fileId.trim().equals("")) {
					String yearMonthDayTime = timeStr.substring(0,12);
					String videoFileName = this.getVideoFileName(oriFileName,scanDir);
					if (scannerParam.isRequireVideo() && (videoFileName == null || videoFileName.trim().equals("")))
						throw new VideoFileNotFoundException(fileId, "δ�ҵ���Ƶ�ļ���");
					bean.setVideoFile(videoFileName);
				}				
			}
		} else {            
			int limitSpeedIndex = fileName.indexOf("I");
			int realSpeedIndex = fileName.indexOf("V");
			if (limitSpeedIndex != -1 && realSpeedIndex != -1 && limitSpeedIndex<realSpeedIndex) {
				String limitSpeed = fileName.substring(limitSpeedIndex + 1, limitSpeedIndex + 4);
				bean.setLimitSpeed(limitSpeed);
				String realSpeed = fileName.substring(realSpeedIndex + 1, realSpeedIndex + 4);
				bean.setSpeed(realSpeed);
				
				//����ǿ��ڳ���ͼƬ����ʹ������ͼƬ��ȫ��ͼ����дͼ
				if(fileName.endsWith(".F.JPG")){
					String panoramaPic = oriFileName.substring(0,oriFileName.indexOf("."))+".P.JPG";
					if((new File(scanDir+"/"+panoramaPic)).exists()){
						picFiles = new String[2];
						picFiles[0] = oriFileName;
						picFiles[1] = panoramaPic;
					}
				}
				else if(fileName.endsWith(".F.K.JPG")){
					String panoramaPic = oriFileName.substring(0,oriFileName.indexOf("."))+".P.K.JPG";
					if((new File(scanDir+"/"+panoramaPic)).exists()){
						picFiles = new String[2];
						picFiles[0] = oriFileName;
						picFiles[1] = panoramaPic;
					}					
				}
			}
		}
		
		//���ʱ��
		int redLightTime = fileName.indexOf("S");
		//if(redLightTime!=-1 && (idIndexOf==-1 || redLightTime<idIndexOf)){
		if(redLightTime!=-1 && redLightTime<idIndexOf){
			String strRedLightTime = fileName.substring(redLightTime+1,redLightTime+4).toUpperCase();
			strRedLightTime = StringHelper.replace(strRedLightTime, "I", "");
			strRedLightTime = StringHelper.replace(strRedLightTime, ".", "");
			bean.setRedLightTime(strRedLightTime);
		}	
		
		//�г�����Ϣ
		int plateFirstIndexOf = fileName.indexOf("&");
		int plateColorLastIndexOf = fileName.lastIndexOf("&");
		if (plateFirstIndexOf != -1 && plateColorLastIndexOf != -1 && plateFirstIndexOf != plateColorLastIndexOf) {
			String plateNo = fileName.substring(plateFirstIndexOf + 1, plateColorLastIndexOf);
			if (plateNo.indexOf("-") != -1)
				plateNo = plateNo.replaceAll("[-]", "");
			if (plateNo.indexOf("@") != -1)
				plateNo = plateNo.replaceAll("[@]", "");
			bean.setPlateNo(plateNo);
			bean.setPlateColor(fileName.substring(plateColorLastIndexOf + 1, plateColorLastIndexOf + 2));
		}
		
		//�з�����Ϣ
		boolean hasDirection = false;
		int directionIndexOf = fileName.indexOf("D");
		if(directionIndexOf!=-1){		 
			 if(idIndexOf==-1 || idIndexOf>directionIndexOf) hasDirection = true;
			 if(hasDirection){
				 String directionNo = fileName.substring(directionIndexOf+1,directionIndexOf+2);
				 bean.setDirectionNo(directionNo);
			 }				 
		}
		
		//���û�з�����Ϣ����ʹ���豸�еķ�����Ϣ
		if(!hasDirection){
			Object obj = DeviceInfoLoaderFactory.getInstance().getDeviceMap().get(deviceId);
			if (obj != null) {
				DeviceInfoBean dib = (DeviceInfoBean)obj;
				String directionNo = SystemConstant.getInstance().getDirectionNoByName(dib.getFrom());
				if(StringHelper.isNotEmpty(directionNo)){
					bean.setDirectionNo(directionNo);
				}
				else{
					//Ϊ���ݾɵ��豸��ֱ�Ӵӷ������ȡ��һ��������루һ������豸ֻ��һ������
					List<DeviceDirectionBean> directionList = dib.getDirectionList();
					if(directionList!=null && directionList.size()>0){
						DeviceDirectionBean ddb = (DeviceDirectionBean)directionList.get(0);
						bean.setDirectionNo(ddb.getDirectionCode());
					}
				}	
			}			
		}
				
        //����Υ������
		int violateTypeIndex = fileName.indexOf("A");		
		if(violateTypeIndex!=-1){
			boolean hasViolateType = false;
			if(plateFirstIndexOf!=-1 && violateTypeIndex<plateFirstIndexOf){
				hasViolateType = true;
			}
			else{			
				if(idIndexOf!=-1){
					if(violateTypeIndex<idIndexOf){
						hasViolateType = true;
					}
				}
				//û��ID��
				else{
					int dotIndexOf = fileName.indexOf(".");
					if(dotIndexOf!=-1 && violateTypeIndex<dotIndexOf){
						hasViolateType = true;
					}					
				}				
			}
			if(hasViolateType){
				String violateType = fileName.substring(violateTypeIndex+1, violateTypeIndex+2);			
				bean.setViolateType(violateType);			
			}
		}
		
		//log.debug("violate type = "+bean.getViolateType());
		if(picFiles==null){
			picFiles = new String[1];
			picFiles[0] = oriFileName;
		}		
		bean.setImageFiles(picFiles);	
		
		//���Υ����Ϊ����
		this.fillWfxwCode(bean);
		
		return bean;		
	}
	
	/**
	 * �����������Υ����Ϊ����
	 * @param bean
	 */
	protected void fillWfxwCode(ViolationInfoBean bean) {
		String defaultWfxwCode = scannerParam.getDefaultWfxwCode();
		if(StringHelper.isNotEmpty(defaultWfxwCode)){
			//�����˲�ͬ�ĳ��ٱ���
			if(defaultWfxwCode.indexOf(",")!=-1 && defaultWfxwCode.indexOf("=")!=-1){
				if(StringHelper.isNotEmpty(bean.getSpeed()) && StringHelper.isNotEmpty(bean.getLimitSpeed())){
					int speed = Integer.parseInt(bean.getSpeed());
					int limitSpeed = Integer.parseInt(bean.getLimitSpeed());
					if(limitSpeed>0 && speed>limitSpeed){
						float overSpeedRate = ((speed-limitSpeed)/(float)limitSpeed)*100.0f;						
						String[] itemArr = StringHelper.splitExcludeEmpty(defaultWfxwCode, ",");
						int len = itemArr.length;
						String[] previousWfxwCodeArr = null;
						
						for(int i=0;i<len;i++){
							String[] wfxwCodeArr = StringHelper.splitExcludeEmpty(itemArr[i], "=");
							if(i==0){
								if(overSpeedRate<=Float.parseFloat(wfxwCodeArr[0])){
									bean.setWfxwCode(wfxwCodeArr[1]);
									break;
								}
							}
							else if(i==len-1){
								if(overSpeedRate>Float.parseFloat(wfxwCodeArr[0])){
									bean.setWfxwCode(wfxwCodeArr[1]);
									break;
								}
							}
							else{
								if(overSpeedRate>Float.parseFloat(previousWfxwCodeArr[0]) && overSpeedRate<=Float.parseFloat(wfxwCodeArr[0])){
									bean.setWfxwCode(wfxwCodeArr[1]);
									break;
								}
							}
							previousWfxwCodeArr = wfxwCodeArr;
						}		
					}
				}
			}
			else{
				bean.setWfxwCode(defaultWfxwCode);
			}
		}		
	}
	
	/**
	 * У����Ƶ�ļ��Ƿ�ϸ�
	 * @param scanDir
	 * @param violationInfoBean
	 * @return
	 */
	protected boolean verifyVideoFile(ViolationInfoBean violationInfoBean){
		boolean result = true;
		String scanDir = violationInfoBean.getFileDir();
		if (StringHelper.isNotEmpty(violationInfoBean.getVideoFile())) {

			File videoFile = new File(scanDir + "/" + violationInfoBean.getVideoFile());

			long videoFileSize = videoFile.length();
			if (videoFileSize < scannerParam.getMinVideoFileSize()) {
				log.debug("�ļ�:" + violationInfoBean.getVideoFile() + "̫С��" + videoFileSize + "����");

				//�����Ƶ�ļ�̫С������ļ����������жϣ�����ļ�������޸������뵱ǰʱ��֮�������ֵ��
				//��ת��ͼƬ�ļ�����Ƶ�ļ������Ϸ��ļ�Ŀ¼��
				if ((System.currentTimeMillis() - videoFile.lastModified()) > scannerParam.getMaxNoModifyTimeAllow() * 1000) {
					try {
						String targetFileDir = scannerParam.getInvalidFileDir() + DateHelper.dateToString(new Date(), "yyyyMMdd");
						FileHelper.createDir(targetFileDir);
						
						for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
							String imageFileName = violationInfoBean.getImageFiles()[i];
							if(StringHelper.isNotEmpty(imageFileName)){
								File imageFile = new File(scanDir + "/" + imageFileName);
								
								String targetFileName = targetFileDir + "/" + imageFileName;
								log.warn("��Ƶ�ļ���СС������ֵ�����ļ���" + imageFileName + " ת�Ƶ���" + targetFileName);
								boolean moveSuccess = FileHelper.moveFile(imageFile, new File(targetFileName));
								if (moveSuccess)
									log.warn("�ļ���" + imageFileName + " ת�Ƴɹ���");
								else
									log.warn("�ļ���" + imageFileName + " ת��ʧ�ܣ��ļ���������ʹ���У�");								
							}
						}
						
						String targetFileName = targetFileDir + "/" + videoFile.getName();
						log.warn("��Ƶ�ļ���СС������ֵ�Ѿ�һ��ʱ�䣬���ļ���" + videoFile.getName() + " ת�Ƶ���" + targetFileName);
						boolean moveSuccess = FileHelper.moveFile(videoFile, new File(targetFileName));
						if (moveSuccess)
							log.warn("�ļ���" + videoFile.getName() + " ת�Ƴɹ���");
						else
							log.warn("�ļ���" + videoFile.getName() + " ת��ʧ�ܣ��ļ���������ʹ���У�");
					} catch (Exception ex2) {
					}
				}
				result = false;
			}
		}
		return result;
	}
	
	/**
	 * ѹ��ͼƬ
	 * @param violationInfoBean
	 */
	protected void compress(ViolationInfoBean violationInfoBean){
		String scanDir = violationInfoBean.getFileDir();
		for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
			String imageFileName = violationInfoBean.getImageFiles()[i];
			if(StringHelper.isNotEmpty(imageFileName)){
				File imageFile = new File(scanDir + "/" + imageFileName);						
				if(scannerParam.getImageCompressGeSize()>0){
					if(imageFile.length()>=scannerParam.getImageCompressGeSize()){
						log.debug("ѹ��ͼƬ��"+imageFile.getAbsolutePath());		
						ImageHelper.compress(imageFile,true,null,scannerParam.getImageCompressQuality());
					}
				}
				else{
					log.debug("ѹ��ͼƬ��"+imageFile.getAbsolutePath());		
					ImageHelper.compress(imageFile,true,null,scannerParam.getImageCompressQuality());
				}				
			}
		}		
	}
	
	/**
	 * �ı�ͼƬ�ߴ磨�ֱ��ʣ�
	 * @param violationInfoBean
	 */
	protected void changeSize(ViolationInfoBean violationInfoBean){
		String scanDir = violationInfoBean.getFileDir();
		for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
			String imageFileName = violationInfoBean.getImageFiles()[i];
			if(StringHelper.isNotEmpty(imageFileName)){
				File imageFile = new File(scanDir + "/" + imageFileName);
				try {
					ImageHelper.changeSize(imageFile, scannerParam.getImageWidth(), scannerParam.getImageHeight());
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			}
		}		
	}	
	
	
	
	/**
	 * ��ͼƬ�ļ��ϴ��ϱ�ǣ�������Ը��Ǹ÷����ṩ�����ˮӡ����־���ķ���
	 * @param violationInfoBean
	 * @param scanDir
	 */
	protected void createMark(ViolationInfoBean violationInfoBean){
		String[] waterMarkArr = null;
		String scanDir = violationInfoBean.getFileDir();
		String line = violationInfoBean.getLine();		
		
		if(StringHelper.isNotEmpty(violationInfoBean.getSpeed()) && StringHelper.isNotEmpty(violationInfoBean.getLimitSpeed())){
			if("top".equalsIgnoreCase(this.getScannerParam().getWaterMarkPosition())){
				waterMarkArr = new String[]{
						"�ص㣺"+violationInfoBean.getDeviceInfoBean().getRoadName()+
						" ʱ�䣺"+DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyy-MM-dd HH:mm:ss")+
						" �޶ȣ�"+Integer.parseInt(violationInfoBean.getLimitSpeed())+"KM/H"+
						" ʵ�٣�"+Integer.parseInt(violationInfoBean.getSpeed())+"KM/H"+
						" �� �� ��"+line						
				};				
			}
			else{
				waterMarkArr = new String[]{
						"�ص㣺"+violationInfoBean.getDeviceInfoBean().getRoadName(),
						"ʱ�䣺"+DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyy-MM-dd HH:mm:ss"),
						"���٣�"+Integer.parseInt(violationInfoBean.getLimitSpeed())+"KM/H",
						"ʵ�٣�"+Integer.parseInt(violationInfoBean.getSpeed())+"KM/H",
						"�� �� ��"+line						
				};					
			}
		}
		else{
			if(StringHelper.isNotEmpty(violationInfoBean.getRedLightTime()) && Integer.parseInt(violationInfoBean.getRedLightTime())>0){
				if("top".equalsIgnoreCase(this.getScannerParam().getWaterMarkPosition())){
					waterMarkArr = new String[]{
							"�ص㣺"+violationInfoBean.getDeviceInfoBean().getRoadName()+
							" ʱ�䣺"+DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyy-MM-dd HH:mm:ss")+
							" ��Ƴ���ʱ�䣺"+Integer.parseInt(violationInfoBean.getRedLightTime())+"��"+
							" �� �� ��"+line							
					};		
				}
				else{
					waterMarkArr = new String[]{
							"�ص㣺"+violationInfoBean.getDeviceInfoBean().getRoadName(),
							"ʱ�䣺"+DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyy-MM-dd HH:mm:ss"),
							"��Ƴ���ʱ�䣺"+Integer.parseInt(violationInfoBean.getRedLightTime())+"��",
							"�� �� ��"+line							
					};							
				}
			}
			else{
				if("top".equalsIgnoreCase(this.getScannerParam().getWaterMarkPosition())){
					waterMarkArr = new String[]{
							"�ص㣺"+violationInfoBean.getDeviceInfoBean().getRoadName()+
							" ʱ�䣺"+DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyy-MM-dd HH:mm:ss")+
							" �� �� ��"+line							
					};	
				}
				else{
					waterMarkArr = new String[]{
							"�ص㣺"+violationInfoBean.getDeviceInfoBean().getRoadName(),
							"ʱ�䣺"+DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyy-MM-dd HH:mm:ss"),
							"�� �� ��"+line							
					};						
				}
			}			
		}		
		
		for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
			String imageFileName = violationInfoBean.getImageFiles()[i];
			if(StringHelper.isNotEmpty(imageFileName)){
				String fullPicName = scanDir + "/" + imageFileName;
				File imageFile = new File(fullPicName);
				//ImageHelper.createWaterMark(imageFile,waterMarkArr,14,18,Color.BLACK,Color.WHITE,0);	
				if("top".equalsIgnoreCase(this.getScannerParam().getWaterMarkPosition())){
					int[] size = new int[]{2288,2288};
					try {
						byte[] fileByte = FileHelper.getBytes(imageFile);
						size = ImageHelper.getImageSize(fileByte);
						byte[] waterMarkPic = ImageHelper.createColorImage(this.getScannerParam().getWaterMarkBgColor(), size[0], this.getScannerParam().getWaterMarkFontHeight()+10);
						waterMarkPic = ImageHelper.createWaterMark(
								waterMarkPic,
								waterMarkArr,
								this.getScannerParam().getWaterMarkFontSize(),
								this.getScannerParam().getWaterMarkFontHeight(),
								this.getScannerParam().getWaterMarkFontColor(),
								null,
								this.getScannerParam().getWaterMarkLeftMargin(),
								this.getScannerParam().getWaterMarkTopMargin());			
						//�ϲ�ˮӡ��ԭʼͼƬ
						fileByte = ImageHelper.compose(waterMarkPic, fileByte, true);	
						FileHelper.writeFile(fileByte, fullPicName);
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					}					
				}
				else{
					ImageHelper.createWaterMark(
							imageFile,
							waterMarkArr,
							scannerParam.getWaterMarkFontSize(),
							scannerParam.getWaterMarkFontHeight(),
							scannerParam.getWaterMarkFontColor(),
							scannerParam.getWaterMarkBgColor(),
							scannerParam.getWaterMarkLeftMargin(),
							scannerParam.getWaterMarkTopMargin());
				}
			}
		}
	}
	
	/**
	 * ����Υ��ͼƬ����Ƶ�ļ�
	 * @param violationInfoBean
	 * @throws Exception
	 */
	public boolean backupViolationInfoFile(ViolationInfoBean violationInfoBean) {
		boolean result = true;
		try {		
			String backDir = scannerParam.getBackupDir() + "/" + violationInfoBean.getDeviceId() + "/" + DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd") +"/" + DateHelper.dateToString(violationInfoBean.getViolateTime(), "HH") +"/";
			
			for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
				String imageFileName = violationInfoBean.getImageFiles()[i];
				if(StringHelper.isNotEmpty(imageFileName)){				
					File imageFile = new File(violationInfoBean.getFileDir() + "/" + imageFileName);
					
					FileHelper.createDir(backDir);				
					File targetFile = new File(backDir + "/" + imageFileName);
					
					log.debug("��ʼ�ƶ��ļ���" + imageFile.getAbsolutePath() + " �� " + targetFile.getAbsolutePath());
					boolean backupSuccess = FileHelper.moveFile(imageFile, targetFile);
					if (backupSuccess)
						log.debug("�ƶ��ļ��ɹ���");
					else
						log.debug("�ƶ��ļ�ʧ��,������ļ��Ƿ���������ʹ�ã�");				
				}
			}		
			
			if (StringHelper.isNotEmpty(violationInfoBean.getVideoFile())) {
				String sourceFileName = violationInfoBean.getFileDir() + "/" + violationInfoBean.getVideoFile();
				String targetFileName = backDir + "/" + violationInfoBean.getVideoFile();
				log.debug("��ʼ�ƶ��ļ���" + sourceFileName + " �� " + targetFileName);
				
				boolean backupSuccess = FileHelper.moveFile(sourceFileName, targetFileName);
				
				if (backupSuccess) {
					log.debug("�ƶ��ļ��ɹ���");
				} else {
					log.debug("�ƶ��ļ�ʧ��,������ļ��Ƿ���������ʹ�ã�");	
				}
			}		
		} catch (Exception ex) {
			result = false;
			log.error(ex.getMessage(), ex);
		}		
		
		return result;
	}	
	
	/**
	 * ���ɱ����嵥
	 * @param violationInfoBean
	 * @return
	 */
	public boolean buildBackupData(ViolationInfoBean violationInfoBean) {
		boolean buildResult = true;
		RandomAccessFile raf = null;
		try{
			FileHelper.createDir(scannerParam.getBackupDataDir());
			String backupFileName = scannerParam.getBackupDataDir() + DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyyMMdd") + ".txt";
			File backupFile = new File(backupFileName);
			if(!backupFile.exists()){
				backupFile.createNewFile();
			}
			StringBuffer backupDataBuff = new StringBuffer();
			backupDataBuff.append(violationInfoBean.getDeviceId());
			backupDataBuff.append(",");
			backupDataBuff.append(DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyy-MM-dd HH:mm:ss"));
			backupDataBuff.append(",");
			
			int i=0;
			for(;i<violationInfoBean.getImageFiles().length;i++){
				String imageFileName = violationInfoBean.getImageFiles()[i];
				backupDataBuff.append(imageFileName);
				backupDataBuff.append(",");												
			}
			for(;i<4;i++){
				backupDataBuff.append("null,");					
			}

			backupDataBuff.append(violationInfoBean.getVideoFile());
			backupDataBuff.append(",");
			backupDataBuff.append(violationInfoBean.getSpeed());
			backupDataBuff.append(",");
			backupDataBuff.append(violationInfoBean.getLimitSpeed());
			backupDataBuff.append(",");
			backupDataBuff.append(violationInfoBean.getLine());			
			backupDataBuff.append(",");
			backupDataBuff.append(violationInfoBean.getPlateNo());		
			backupDataBuff.append(",");
			backupDataBuff.append(violationInfoBean.getPlateColor());		
			backupDataBuff.append(",");
			backupDataBuff.append(violationInfoBean.getDirectionNo());				
			backupDataBuff.append(",");
			backupDataBuff.append(violationInfoBean.getViolateType());				
			backupDataBuff.append("\n");
			
			String backupData = backupDataBuff.toString();
			if(scannerParam.getBackupDataEncodingFrom()!=null && !scannerParam.getBackupDataEncodingFrom().trim().equals("")){			
				backupData = new String(backupData.getBytes(scannerParam.getBackupDataEncodingFrom()),scannerParam.getBackupDataEncodingTo());
			}
			raf = new RandomAccessFile(backupFileName,"rw");
			raf.seek(raf.length());
			raf.writeBytes(backupData);
		}
		catch(Exception ex){
			buildResult = false;
			log.error("���ɱ�������ʱ����"+ex.getMessage(),ex);
		}
		finally{
			if(raf!=null){
				try{
					raf.close();
				}
				catch(Exception ioEx){}				 
			}
		}
		
		return buildResult;		
	}
		
		
	/* 
	 * �ָ���������
	 */
	public void restoreBackupData(Connection conn,boolean isUseDbConn) throws Exception{
		File backupDir = new File(scannerParam.getBackupDataDir());
		File backupFileArr[] = backupDir.listFiles(new FilenameFilterByPostfix(".txt", true));
		if(backupFileArr==null) return;
		int len = backupFileArr.length;
		for(int i = 0; i < len; i++){
			if(conn!=null) conn.setAutoCommit(false);
			boolean restoreSuccess = true;
			File backupFile = backupFileArr[i];
			log.debug("��ʼ�ָ��ļ���" + backupFile.getAbsolutePath() + "�е����ݣ�");
			try{
				int processResult = this.restoreBackupFile(conn, isUseDbConn, backupFile);
				if(processResult == 0)
					restoreSuccess = backupFile.delete();
				else
					restoreSuccess = false;
			}
			catch(Exception otherExce){
				restoreSuccess = false;
			}
			if(restoreSuccess){
				if(conn!=null) conn.commit();
				log.debug("�ָ��ļ���" + backupFile.getAbsolutePath() + "�е����ݳɹ���");
			} 
			else {
				if(conn!=null) conn.rollback();
				log.debug("�ָ��ļ���" + backupFile.getAbsolutePath() + "�е�����ʧ�ܣ�");
			}
		}
	}
	
	/**
	 * �ָ����������ļ�
	 * @param conn
	 * @param backupFile
	 * @return
	 * @throws Exception
	 */
	public int restoreBackupFile(Connection conn,boolean isUseDbConn,File backupFile) throws Exception{
		int processResult = 0;
		RandomAccessFile raf = null;
		try{
			raf = new RandomAccessFile(backupFile, "r");	
			String content = null;
			while(true){
				content = raf.readLine();
				if(content == null) break;
				content = content.trim();
				if("".equals(content)) break;
				
				if(scannerParam.getBackupDataEncodingFrom()!=null && !scannerParam.getBackupDataEncodingFrom().trim().equals("")){			
					content = new String(content.getBytes(scannerParam.getBackupDataEncodingTo()),scannerParam.getBackupDataEncodingFrom());
				}
				
				String arrContent[] = content.split("[,]");
				if(arrContent.length < 13){
					log.warn("�������ݣ�'" + content + "'�����Ϲ淶��");
					continue;
				}
				String deviceId = arrContent[0].trim();
				if(!DeviceInfoLoaderFactory.getInstance().getDeviceMap().containsKey(deviceId)){
					log.warn("�������ݣ�'" + content + "',δ�ҵ�����豸��" + deviceId);
					continue;
				}
				String violateTime = arrContent[1].trim();
				String picFileName1 = arrContent[2].trim();
				String picFileName2 = arrContent[3].trim();
				String picFileName3 = arrContent[4].trim();
				String picFileName4 = arrContent[5].trim();
				
				String videoFileName = arrContent[6].trim();
				String speed = arrContent[7].trim();
				String limitSpeed = arrContent[8].trim();
				String line = arrContent[9].trim();
				String plateNo = arrContent[10].trim();
				String plateColor = arrContent[11].trim();
				String directionNo = arrContent[12].trim();
				String violateType = arrContent[13].trim();
				
				ViolationInfoBean violationInfoBean = new ViolationInfoBean();
				violationInfoBean.setDeviceId(deviceId);
				
				violationInfoBean.setImageFiles(new String[] {
					picFileName1,picFileName2,picFileName3,picFileName4
				});
				violationInfoBean.setViolateTime(DateHelper.parseDateString(violateTime, "yyyy-MM-dd HH:mm:ss"));
				
				if(StringHelper.isEmpty(videoFileName))
					violationInfoBean.setVideoFile(null);
				else
					violationInfoBean.setVideoFile(videoFileName);
				
				if(StringHelper.isEmpty(speed))
					violationInfoBean.setSpeed(null);
				else
					violationInfoBean.setSpeed(speed);
				
				if(StringHelper.isEmpty(limitSpeed))
					violationInfoBean.setLimitSpeed(null);
				else
					violationInfoBean.setLimitSpeed(limitSpeed);
				
				if(StringHelper.isEmpty(line))
					violationInfoBean.setLine(null);
				else
					violationInfoBean.setLine(line);
				
				if(StringHelper.isEmpty(plateNo))
					violationInfoBean.setPlateNo(null);
				else
					violationInfoBean.setPlateNo(plateNo);
				
				if(StringHelper.isEmpty(plateColor))
					violationInfoBean.setPlateColor(null);
				else
					violationInfoBean.setPlateColor(plateColor);
				
				if(StringHelper.isEmpty(directionNo))
					violationInfoBean.setDirectionNo(null);
				else
					violationInfoBean.setDirectionNo(directionNo);		
				
				if(StringHelper.isEmpty(violateType))
					violationInfoBean.setViolateType(null);
				else
					violationInfoBean.setViolateType(violateType);
				
				violationInfoBean.setDeviceInfoBean((DeviceInfoBean)DeviceInfoLoaderFactory.getInstance().getDeviceMap().get(deviceId));
				String fileDir = scannerParam.getBackupDir() + "/" + deviceId + "/" + DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd") + "/"+ DateHelper.dateToString(violationInfoBean.getViolateTime(), "HH") + "/";
				violationInfoBean.setFileDir(fileDir);
				
				//����ظ�����
				boolean isRepeated = false;
				if(isUseDbConn){
					isRepeated = this.isRepeatedData(conn,violationInfoBean);
				}
				else{
					isRepeated = this.isRepeatedData(violationInfoBean);
				}
				
				//���ظ���ָ����ݣ�
				if(!isRepeated){
					log.debug("��ʼ�ָ������ļ���"+backupFile.getAbsolutePath()+"�еģ�"+content+"�����ݣ�");	            
					processResult = this.processViolationInfoBean(conn, violationInfoBean);
					if(processResult != 0){
						log.warn("�������ļ���"+backupFile.getAbsolutePath()+"�еģ�"+content+"��ʱ����������Ϊ��"+processResult);
						break;
					}
					else{
						this.statTrafficDay(conn, violationInfoBean);
						boolean afterProcessSuccess = this.postRestoreViolationInfoBean(violationInfoBean);
						if (!afterProcessSuccess) {
							processResult = -1;
							log.debug("���ڴ���ʧ�ܣ�");
							break;
						}
					}				
				}
				//�ظ���ֱ�ӽ��к��ڴ���
				else{
					this.postRestoreViolationInfoBean(violationInfoBean);
				}
			}
		}
		catch(Exception ex){
			processResult = -1;
			log.error("�ָ���������ʱ����" + ex.getMessage(), ex);
			throw ex;
		}
		finally{
			if(raf != null){
				try{
					raf.close();
				}
				catch(Exception ioEx) { }
			}
		}
    
		return processResult;
	}				
	
	/**
	 * �������
	 * @param conn
	 * @param hasConn
	 * @param violationInfoBean
	 * @param scanDir
	 * @return
	 */
	protected int process(Connection conn,boolean hasConn,ViolationInfoBean violationInfoBean){
		int returnCode = -1;
		try {
			if (hasConn) {				
				conn.setAutoCommit(false);
				
				boolean isRepeated = this.isRepeatedData(conn,violationInfoBean);
				if(isRepeated){
					this.processRepeatedData(violationInfoBean);
					return -1;
				}
				
				returnCode = this.processViolationInfoBean(conn, violationInfoBean);
				if (returnCode == 0) {
					boolean afterProcessSuccess = this.postProcessViolationInfoBean(violationInfoBean);
					if (afterProcessSuccess) {
						this.statTrafficDay(conn, violationInfoBean);
						conn.commit();
						log.debug("���ύ��⣡");
					} else {
						returnCode = -1;
						conn.rollback();
						log.debug("�ύʧ�ܣ����ݻع���");
					}
				}
			}
			if (!hasConn || returnCode != 0) {
				boolean backupSuccess = this.backupViolationInfoFile(violationInfoBean);
				if (backupSuccess)
					backupSuccess = this.buildBackupData(violationInfoBean);
			}
		} catch (SQLException sqlEx) {
			if (hasConn) {
				try {
					conn.rollback();
				} catch (Exception ex1) {
					log.error(ex1);
				}
			}
		} catch (Exception ex) {
			if (hasConn) {
				try {
					conn.rollback();
				} catch (Exception ex1) {
					log.error(ex1);
				}
			}
			log.error("��������ʧ�ܣ�" + ex.getMessage(), ex);
		}		
		return returnCode;
	}
	
	/**
	 * �������
	 * @param conn
	 * @param hasConn
	 * @param violationInfoBean
	 * @param scanDir
	 * @return
	 */
	protected int process(ViolationInfoBean violationInfoBean){
		int returnCode = -1;
		try {
			boolean isRepeated = this.isRepeatedData(violationInfoBean);
			if(isRepeated){
				this.processRepeatedData(violationInfoBean);
				return -1;
			}
			
			returnCode = this.processViolationInfoBean(null, violationInfoBean);
			if (returnCode == 0) {
				boolean afterProcessSuccess = this.postProcessViolationInfoBean(violationInfoBean);
				if (afterProcessSuccess) {
					log.debug("����ɹ���");
				} else {
					returnCode = -1;
					log.debug("����ʧ�ܣ�");
				}
			}
			if (returnCode != 0) {
				boolean backupSuccess = this.backupViolationInfoFile(violationInfoBean);
				if (backupSuccess)
					backupSuccess = this.buildBackupData(violationInfoBean);
			}
		} catch (Exception ex) {
			log.error("����ʧ�ܣ�" + ex.getMessage(), ex);
		}		
		return returnCode;
	}	
	
	/**
	 * ������Ƶ�ļ�
	 * @return
	 */
	private int cleanVideoFile(){
		int cleanNum = 0;
		int len = this.getScanDirs().length;
		for(int i=0;i<len;i++){
			String strScanDir = this.getScanDirs()[i];
			File scanDir = new File(strScanDir);
			cleanNum += this.cleanVideoFile(scanDir);
		}
		return cleanNum;
	}
	
	/**
	 * �ݹ�������Ŀ¼
	 * @param scanDir
	 * @return
	 */
	private int cleanVideoFile(File scanDir){
		int cleanNum = 0;
		if (!scanDir.exists() || !scanDir.isDirectory()) {
			return 0;
		}
		
		File[] fileArr = scanDir.listFiles(new FilenameFilterByPostfix(scannerParam.getVideoFileExtension(),true,true));
		int videoNum = fileArr.length;
		for(int j=0;j<videoNum;j++){
			if(fileArr[j].isDirectory()){
				cleanNum += this.cleanVideoFile(fileArr[j]);
			}
			else{
				log.debug("ɾ����Ƶ�ļ���"+fileArr[j].getAbsolutePath());
				if(fileArr[j].delete()) cleanNum++;
			}
		}		
		return cleanNum;
	}
	
	/**
	 * �����ļ�ת�����ļ�Ŀ¼��
	 * @return
	 */
	protected int cleanOldFile() {
		log.debug("��ʼ������ļ�...");
		int cleanFileNum = 0;
		int dirNum = this.getScanDirs().length;
		log.debug("��ʱ������" + scannerParam.getDelayDeleteDay());
		long interval = scannerParam.getDelayDeleteDay() * 24L * 60L * 60L * 1000L;
		log.debug("��ʱ��������" + interval);
		
		if (interval <= 0) {
			log.warn("��ʱ�Ƴ�ʱ����������");
			return cleanFileNum;
		}
		
		for(int i=0;i<dirNum;i++){
			String strScanDir = this.getScanDirs()[i];
			File scanDir = new File(strScanDir);
			if(!scanDir.exists()){
				log.warn(strScanDir+"�����ڣ�");
				continue;
			}
			
			if(scanDir.isDirectory()){
				cleanFileNum += this.cleanOldFile(scanDir,interval);
			}
			else{
				log.warn("'"+strScanDir+"'����Ŀ¼��");
			}
		}
		
		log.debug("���ɹ�������ļ���" + cleanFileNum + "��,��Щ�����ļ���ת�Ƶ�Ŀ¼��" + scannerParam.getDeleteFileDir());		
		return cleanFileNum;
	}	

	/**
	 * �����ļ�ת�����ļ�Ŀ¼�У��ݹ�������Ŀ¼
	 * @return
	 */
	protected int cleanOldFile(File scanDir,long interval) {
		int cleanFileNum = 0;
		log.debug("��ʼ����Ŀ¼��"+scanDir.getAbsolutePath());
		
		File files[] = scanDir.listFiles();
		int len = files.length;
		
		//ɾ����Ŀ¼
		if(len==0 && (System.currentTimeMillis() - scanDir.lastModified()) > interval){
			log.debug("ɾ����Ŀ¼��"+scanDir.getAbsolutePath());
			scanDir.delete();
			return cleanFileNum;
		}
		
		for (int j = 0; j < len; j++) {
			if (files[j].isDirectory()){
				cleanFileNum += this.cleanOldFile(files[j], interval);
			}
			else{
				String fileName = files[j].getName().toUpperCase().trim();
				/*
				if(fileName.endsWith(".INI")){
					continue;
				}
				*/
				int timeIndex = fileName.indexOf("T") + 1;
				if (timeIndex == -1){
					log.debug("ɾ���ļ���"+files[j].getAbsolutePath());
					files[j].delete();
					continue;
				}
				
				String timeStr = null;
				Date date = null;
				try {
					timeStr = fileName.substring(timeIndex, timeIndex + 14);
//					String[] fileInfo = StringHelper.split(fileName, "_");
//					timeStr = fileInfo[8];
					date = new SimpleDateFormat("yyyyMMddHHmmss").parse(timeStr);
				} catch (Exception ex) {
					log.warn("�ļ���" + files[j].getAbsolutePath() + "��Ч��(" + ex.getMessage() + "),ɾ������");
					files[j].delete();
					continue;
				}

				if ((System.currentTimeMillis() - date.getTime()) > interval) {
					String targetFileDir = scannerParam.getDeleteFileDir() + DateHelper.dateToString(new Date(), "yyyyMMdd");
					try {
						FileHelper.createDir(targetFileDir);
					} catch (Exception ex) {
						log.warn("�������ļ����Ŀ¼��" + targetFileDir + "ʱ���� -- " + ex.getMessage());
						continue;
					}
					boolean moveSuccess = true;
					try {
						moveSuccess = FileHelper.moveFile(files[j], new File(targetFileDir + "/" + files[j].getName()));
					} catch (Exception moveExce) {
						moveSuccess = false;
					}
					if (moveSuccess)
						cleanFileNum++;
					else
						log.debug("�����ļ���" + files[j].getAbsolutePath() + "ʱ�������ļ���������ʹ���У�");
				}
			}
		}			
		return cleanFileNum;
	}	
	
	public ScannerParamBean getScannerParam() {
		return scannerParam;
	}

	public void setScannerParam(ScannerParamBean scannerParam) {
		this.scannerParam = scannerParam;
	}

}
