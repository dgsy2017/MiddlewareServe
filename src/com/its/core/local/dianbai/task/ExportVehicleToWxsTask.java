/**
 * 
 */
package com.its.core.local.dianbai.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.constant.SystemConstant;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.local.dianbai.ws.TransProxy;
import com.its.core.module.task.ATask;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.XMLProperties;

/**
 * �������� 2014-7-23 ����08:16:06
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ExportVehicleToWxsTask extends ATask {
	private static final Log log = LogFactory.getLog(ExportVehicleToWxsTask.class);
	
	private String wsEndPoint = "http://10.47.98.188:9080/jcbktrans/services/Trans";
	private String imageSavePrefix = null;
	private String imageUploadDir = null;
	
	private String selectDeviceInfoSql = null;
	
	private String selectVehicleRecordSql = null;
	private String updateVehicleRecordSql = null;

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		// TODO Auto-generated method stub
		this.wsEndPoint = props.getProperty(propertiesPrefix, no, "ws_info.ws_endpoint");			
		
		this.selectDeviceInfoSql = props.getProperty(propertiesPrefix, no, "sql.select_device_info_sql");
		
		this.selectVehicleRecordSql = props.getProperty(propertiesPrefix, no, "sql.select_vehicle_record");
		this.updateVehicleRecordSql = props.getProperty(propertiesPrefix, no, "sql.update_vehicle_record");

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		Connection conn = null;	
		try {
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			conn.setAutoCommit(false);		
			
			List<ExportVehicelPassRecordBean> recordList = this.getExportRecordList(conn);
			int size = recordList.size();
			log.debug("�����������ݣ�"+size+"����");	
			if(size==0) return;	
			
			log.debug(this.getWsEndPoint());
			TransProxy proxy = new TransProxy();
			proxy.setEndpoint(wsEndPoint);	
			
			short currentExcelRow = 1;
			for(int i=0;i<size;i++){
				
				boolean result = true;
				
				ExportVehicelPassRecordBean record = (ExportVehicelPassRecordBean)recordList.get(i);	
				DeviceInfoBean dib = (DeviceInfoBean)DeviceInfoLoaderFactory.getInstance().getDeviceMap().get(record.getDeviceId());
				
				String drivewayNo = record.getDrivewayNo();
				if(drivewayNo.length()==1) drivewayNo = "0" + drivewayNo;				
				
				log.debug(record.getFeatureImagePath());
				
				String violate = "0";
				if(Integer.parseInt(record.getSpeed())>60 && Integer.parseInt(record.getSpeed())<Integer.parseInt(record.getLimitSpeed())) {
					violate = "1";
				}
				log.debug("violate = " + violate + " (��0�� ���������ݼ�¼����1�� ������Υ�����ݼ�¼)");
				
				result = proxy.writeVehicleInfo2(
						dib.getThirdPartyDeviceInfoBean().getDeviceId(),//�豸���
						dib.getThirdPartyDeviceInfoBean().getDirectId(),//������						
						drivewayNo,//�������
						record.getPlate(),//���ƺ���
						SystemConstant.getInstance().getPlateTypeIdByColor(record.getPlateColorCode()),//��������
						DateHelper.dateToString(record.getCatchTime(), "yyyy-MM-dd HH:mm:ss"),//����ʱ��
						Integer.parseInt(record.getSpeed()),//�����ٶ�(�������3λ)
						Integer.parseInt(record.getLimitSpeed()),//��������(�������3λ)
						"",//Υ����Ϊ����
						(long)300,//��������
						record.getPlateColorCode(),//������ɫ
						"",//��������
						"",//β�����ƺ���
						"",//β��������ɫ
						"",//ǰ�˺�����β�������Ƿ�һ��
						"",//����Ʒ��
						"",//��������
						"",//������ɫ
						record.getFeatureImagePath(),//ͼƬ֤��1
						record.getPanoramaImagePath(),//ͼƬ֤��2
						"",//ͼƬ֤��3
						"",//ͼƬ֤��4
						violate,//1λ�ַ�����0�� ���������ݼ�¼����1�� ������Υ�����ݼ�¼
						"0");//���ͱ�־��1λ�ַ�, "0"����������"1"�����ͺ���
				
				log.debug("�㶫ʡ�ΰ����ڼ��鲼��ϵͳ���ý����" + result+" (true:�ɹ���false:ʧ��)");	
				
				//��ȡ�ӿڵ��ô�����־
				this.getLastMessage(proxy);
			
				this.updateExportStatus(conn, record.getId());					
				conn.commit();
				currentExcelRow++;
			}
			long currentTime = System.currentTimeMillis();
			log.info("���ι��ϴ���¼��" + (currentExcelRow-1) + "���� ��ʱ��" + ((currentTime- startTime) / 1000F) + "�룡");
			
		}
		catch(Exception ex){
			log.error("�ϴ�ʧ�ܣ�"+ex.getMessage(),ex);				
		}
		finally{
			if(conn!=null){
				try{
					ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
				}
				catch(Exception ex2){}
			}			
		}

	}
	
	private void getLastMessage(TransProxy proxy) throws Exception {		
		
        String message = null;
		try {				
			message = proxy.getLastMessage();					
		} catch(Exception ex){				
			log.error("��ȡ��Ϣ��־ʧ�ܣ�"+ex.getMessage(),ex);			

		}		
		log.debug("��Ϣ��ʾ��" + message);
	}
	
	private List<ExportVehicelPassRecordBean> getExportRecordList(Connection conn) throws Exception{
		List<ExportVehicelPassRecordBean> recordList = new ArrayList<ExportVehicelPassRecordBean>();
		PreparedStatement preStatement = null;
		ResultSet rs = null;
		try{		
			log.debug("ִ�У�"+this.getSelectVehicleRecordSql());
			preStatement = conn.prepareStatement(this.getSelectVehicleRecordSql());			
			rs = preStatement.executeQuery();
			
			while(rs.next()){
				ExportVehicelPassRecordBean record = new ExportVehicelPassRecordBean();	
				record.setId(rs.getLong("id"));
				record.setPlate(rs.getString("plate"));
				record.setPlateColorCode(rs.getString("plate_color_code"));	
				record.setCatchTime(rs.getTimestamp("catch_time"));
				record.setDeviceId(rs.getString("device_id"));
				record.setDirectionCode(rs.getString("direction_code"));
				record.setDrivewayNo(rs.getString("driveway_no"));
				record.setFeatureImagePath(rs.getString("feature_image_path"));
				record.setPanoramaImagePath(rs.getString("panorama_image_path"));				
				record.setSpeed(rs.getString("speed"));
				record.setLimitSpeed(rs.getString("limit_speed"));				
				recordList.add(record);						
			}
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);	
			throw ex;
		}finally{
			DatabaseHelper.close(rs,preStatement);
		}		
		return recordList;
	}
	
	private void updateExportStatus(Connection conn,long id) throws Exception{
		PreparedStatement preStatement = null;
		try{
			log.debug("ִ�У�"+this.getUpdateVehicleRecordSql());
			preStatement = conn.prepareStatement(this.getUpdateVehicleRecordSql());
			preStatement.setLong(1, id);
			preStatement.executeUpdate();		
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);	
			throw ex;
		}
		finally{
			DatabaseHelper.close(null,preStatement);
		}
	}

	public String getWsEndPoint() {
		return wsEndPoint;
	}

	public void setWsEndPoint(String wsEndPoint) {
		this.wsEndPoint = wsEndPoint;
	}

	public String getImageSavePrefix() {
		return imageSavePrefix;
	}

	public void setImageSavePrefix(String imageSavePrefix) {
		this.imageSavePrefix = imageSavePrefix;
	}

	public String getImageUploadDir() {
		return imageUploadDir;
	}

	public void setImageUploadDir(String imageUploadDir) {
		this.imageUploadDir = imageUploadDir;
	}

	public String getSelectDeviceInfoSql() {
		return selectDeviceInfoSql;
	}

	public void setSelectDeviceInfoSql(String selectDeviceInfoSql) {
		this.selectDeviceInfoSql = selectDeviceInfoSql;
	}

	public String getSelectVehicleRecordSql() {
		return selectVehicleRecordSql;
	}

	public void setSelectVehicleRecordSql(String selectVehicleRecordSql) {
		this.selectVehicleRecordSql = selectVehicleRecordSql;
	}

	public String getUpdateVehicleRecordSql() {
		return updateVehicleRecordSql;
	}

	public void setUpdateVehicleRecordSql(String updateVehicleRecordSql) {
		this.updateVehicleRecordSql = updateVehicleRecordSql;
	}

}
