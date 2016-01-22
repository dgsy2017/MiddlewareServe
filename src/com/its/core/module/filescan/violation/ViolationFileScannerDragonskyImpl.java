/**
 * 
 */
package com.its.core.module.filescan.violation;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.common.sequence.SequenceFactory;
import com.its.core.constant.SystemConstant;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.FilenameFilterByPostfix;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * �������� 2012-12-13 ����02:38:58
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class ViolationFileScannerDragonskyImpl extends AViolationFileScanner {
	private static final Log log = LogFactory.getLog(ViolationFileScannerDragonskyImpl.class);
	
    private String insertSql = null;
    private String httpUrlPrefix = null;
    
    private static Map<String, String> sbbhMap = new HashMap<String, String>();
	
	static {
		sbbhMap.put("192.168.88.112", "440622010000000001");
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.filescan.violation.AViolationFileScanner#configureLocalProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureLocalProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.httpUrlPrefix = props.getProperty(propertiesPrefix,no,"standard_version.http_url_prefix");		
		this.insertSql = props.getProperty(propertiesPrefix,no,"standard_version.insert_sql");
	}		
	
	/**
	 * ���ݵ�һ��ͼƬ�ļ�������ȡ�ڶ���ͼƬ�ļ�
	 * @param imageFile
	 * @return
	 */
	protected File getSecondFile(String scanDir,String firstFileName) {
		File dirScan = new File(scanDir);
		String prefix = firstFileName.substring(0,firstFileName.indexOf("_")+13);			
		String postfix = "_02.jpg";			
		File fileArr[] = dirScan.listFiles(new FilenameFilterByPostfix(prefix,postfix, true));
		if (fileArr != null) {
			int len = fileArr.length;	
			log.info("len = " + len);
			if(len>0) return fileArr[0];
		}
		return null;
	}
	
	/**
	 * ���ݵ�һ��ͼƬ�ļ�������ȡ�ڶ�������ͼƬ�ļ�
	 * @param imageFile
	 * @return
	 */
	protected File getThirdFile(String scanDir,String firstFileName) {
		File dirScan = new File(scanDir);
		String prefix = firstFileName.substring(0,firstFileName.indexOf("_")+13);			
		String postfix = "_03.jpg";		
		File fileArr[] = dirScan.listFiles(new FilenameFilterByPostfix(prefix,postfix, true));
		if (fileArr != null) {
			int len = fileArr.length;	
			log.info("len = " + len);
			if(len>0) return fileArr[0];
		}
		return null;
	}

	@Override
	protected ViolationInfoBean parseViolationInfo(String scanDir, File imageFile) throws Exception {
		String oriFileName = imageFile.getName();
		String fileName = oriFileName.trim().toUpperCase();
		log.info("fileName = " + fileName);
		ViolationInfoBean bean = new ViolationInfoBean();
		
		//�豸���
		int deiviceIPIndex = fileName.indexOf("_");
		String deviceIP = fileName.substring(0, deiviceIPIndex);
		bean.setDeviceId(sbbhMap.get(deviceIP));
		
		//��ʻ����
		Map dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
		DeviceInfoBean dib = (DeviceInfoBean)dibMap.get(bean.getDeviceId());
		log.info("DirectionNo = " + dib.getDirectionCode());
		bean.setDirectionNo(dib.getDirectionCode());
		
		//����
		int deviceIndex = fileName.lastIndexOf("_");
		String drivewayNo = fileName.substring(deviceIndex-1, deviceIndex);
		if(drivewayNo.length()==1) drivewayNo = "0" + drivewayNo;
		log.info("drivewayNo = " + drivewayNo);
		bean.setLine(drivewayNo);
		
		//Υ��ʱ��
		int timeIndex = fileName.indexOf("_")+1;
		String timeStr = fileName.substring(timeIndex, timeIndex + 14);
		Date time = new SimpleDateFormat("yyyyMMddHHmmss").parse(timeStr);
		bean.setViolateTime(time);
		
		String[] imageFiles = null;
		
		//�ڶ���
		File file2 = this.getSecondFile(scanDir, oriFileName);
		if(file2==null){
			log.warn("δ�ҵ�ƥ��ĵڶ���ͼƬ�����ڣ�"+imageFile.getAbsolutePath());
			return null;
		}
		String fileName2 = file2.getName();
		log.info("fileName2 = " + fileName2);
		//������
		File file3 = this.getThirdFile(scanDir, oriFileName);
		if(file3==null){
			log.warn("δ�ҵ�ƥ��ĵ�����ͼƬ�����ڣ�"+imageFile.getAbsolutePath());
			return null;
		}
		String fileName3 = file3.getName();
		log.info("fileName3 = " + fileName3);
		
		if((new File(scanDir+"/"+fileName2)).exists() && (new File(scanDir+"/"+fileName3)).exists()){
			imageFiles = new String[3];				
			imageFiles[0] = oriFileName;
			imageFiles[1] = fileName2;
			imageFiles[2] = fileName3;
		}
		
		if(imageFiles==null){
			imageFiles = new String[1];
			imageFiles[0] = oriFileName;
		}		
		bean.setImageFiles(imageFiles);
		
		return bean;
	}



	/* (non-Javadoc)
	 * @see com.its.core.module.filescan.violation.AViolationFileScanner#processViolationInfoBean(java.sql.Connection, com.its.core.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public int processViolationInfoBean(Connection conn,ViolationInfoBean violationInfoBean) {
		if(StringHelper.isEmpty(this.getInsertSql())){
			log.warn("δ����'insert_sql'������");
			return 0;
		}
        int result = -1;
        PreparedStatement preStatement = null;
       	try{
       		//id,violate_time,road_id,device_id,direction_code,line,speed,limit_speed,create_time,status,plate,plate_type_id,image_path_1,image_path_2,image_path_3,image_path_4,violate_type
			preStatement = conn.prepareStatement(this.getInsertSql());
			preStatement.setLong(1, (long)SequenceFactory.getInstance().getViolateRecordTempSequence());
			preStatement.setTimestamp(2, new Timestamp(violationInfoBean.getViolateTime().getTime()));
			preStatement.setString(3, violationInfoBean.getDeviceInfoBean().getRoadId());
			preStatement.setString(4, violationInfoBean.getDeviceId());
			log.info("######1" + violationInfoBean.getDirectionNo());
			preStatement.setString(5, violationInfoBean.getDirectionNo());
			log.info("######2" + violationInfoBean.getLine());
			preStatement.setString(6, violationInfoBean.getLine());
			preStatement.setString(7, "000");
			preStatement.setString(8, "000");
			preStatement.setTimestamp(9, new Timestamp(new java.util.Date().getTime()));
			preStatement.setString(10, violationInfoBean.getPlateNo());
			
			String plateTypeId = SystemConstant.getInstance().getPlateTypeIdByColor(violationInfoBean.getPlateColor());
			if(StringHelper.isEmpty(plateTypeId)){
				//ȱʡ��С������
				plateTypeId = SystemConstant.getInstance().PLATE_TYPE_ID_ROADLOUSE;
			}
			else{
				plateTypeId = plateTypeId.trim();
			}
			
			preStatement.setString(11, plateTypeId);
			
			String pathPrefix = this.getHttpUrlPrefix() + violationInfoBean.getDeviceId() + "/" + DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd") + "/";
			int i=0;
			for(;i<violationInfoBean.getImageFiles().length;i++){
				String imageFileName = violationInfoBean.getImageFiles()[i];
				if(StringHelper.isNotEmpty(imageFileName)){
					preStatement.setString(12+i, pathPrefix + imageFileName);
				}
				else{
					preStatement.setString(12+i, null);
				}
			}
			for(;i<4;i++){
				preStatement.setString(12+i, null);
			}				
			
			if(StringHelper.isNotEmpty(violationInfoBean.getVideoFile()))
				preStatement.setString(16, pathPrefix + violationInfoBean.getVideoFile());
			else
				preStatement.setString(16, null);			

			preStatement.execute();
			result = 0;       		
       	}
       	catch(Exception ex){
			log.error("�������ʧ�ܣ�" + ex.getMessage(), ex);
			result = -1;		
       	}
       	finally{
			if(preStatement != null){
				try
				{
					preStatement.close();
				}
				catch(Exception ex) { }      
			}       		
       	}
        return result;
	}
	
	/* (non-Javadoc)
	 * @see com.its.core.module.filescan.violation.AViolationFileScanner#postProcessViolationInfoBean(com.its.core.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public boolean postProcessViolationInfoBean(ViolationInfoBean violationInfoBean) {
		boolean backupSuccess = true;
		try {
			String backDir = this.getScannerParam().getBackupDir() + "/" + violationInfoBean.getDeviceId() + "/" + DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd");
			FileHelper.createDir(backDir);
			
			for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
				String imageFileName = violationInfoBean.getImageFiles()[i];
				if(StringHelper.isNotEmpty(imageFileName)){
					String sourceFileName = violationInfoBean.getFileDir() + "/" + imageFileName;
					String targetFileName = backDir + "/" + imageFileName;
					log.debug("��ʼ�ƶ��ļ���" + sourceFileName + " �� " + targetFileName);
					FileHelper.moveFile(sourceFileName, targetFileName);					
				}
			}
			
			if(StringHelper.isNotEmpty(violationInfoBean.getVideoFile())){
				String sourceFileName = violationInfoBean.getFileDir() + "/"  + violationInfoBean.getVideoFile();
				String targetFileName = backDir + "/" + violationInfoBean.getVideoFile();		
				FileHelper.moveFile(sourceFileName, targetFileName);
			}
		} catch (Exception ex) {
			backupSuccess = false;
			log.error(ex.getMessage(), ex);
		}
		return backupSuccess;
	}
	
	/* (non-Javadoc)
	 * @see com.its.core.module.filescan.violation.AViolationFileScanner#postRestoreViolationInfoBean(com.its.core.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public boolean postRestoreViolationInfoBean(ViolationInfoBean violationInfoBean) {
		// TODO Auto-generated method stub
		return true;
	}

	public String getInsertSql() {
		return insertSql;
	}

	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}

	public String getHttpUrlPrefix() {
		return httpUrlPrefix;
	}

	public void setHttpUrlPrefix(String httpUrlPrefix) {
		this.httpUrlPrefix = httpUrlPrefix;
	}	

}
