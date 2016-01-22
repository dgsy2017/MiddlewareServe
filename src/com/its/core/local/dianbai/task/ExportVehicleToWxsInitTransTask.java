/**
 * 
 */
package com.its.core.local.dianbai.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.database.ConnectionProviderFactory;
import com.its.core.local.dianbai.ws.TransProxy;
import com.its.core.module.task.ATask;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.XMLProperties;

/**
 * �������� 2014-7-23 ����08:47:56
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ExportVehicleToWxsInitTransTask extends ATask {
	private static final Log log = LogFactory.getLog(ExportVehicleToWxsInitTransTask.class);
	
	private String wsEndPoint = "http://10.47.98.188:9080/jcbktrans/services/Trans";	
	private String selectDeviceInfoSql = null;

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		// TODO Auto-generated method stub
		this.wsEndPoint = props.getProperty(propertiesPrefix, no, "ws_info.ws_endpoint");
		this.selectDeviceInfoSql = props.getProperty(propertiesPrefix, no, "sql.select_device_info_sql");

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		Connection conn = null;	
		
		try {
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			conn.setAutoCommit(true);	
			
			log.debug(this.getWsEndPoint());
			TransProxy proxy = new TransProxy();
			proxy.setEndpoint(wsEndPoint);
			
			this.initTrans(proxy, conn);		
			
			//��ȡ�ӿڵ��ô�����־
			this.getLastMessage(proxy);
			
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
	
	private int initTrans(TransProxy proxy,Connection conn) throws Exception {		
		PreparedStatement preStatement = null;
		ResultSet rs = null;		
		int initResult = 0;
		try {	
//			log.debug("ִ�У�"+this.getSelectDeviceInfoSql());
			preStatement = conn.prepareStatement(this.getSelectDeviceInfoSql());
			rs = preStatement.executeQuery();
			while(rs.next()) {		
				log.debug("�豸��ţ�" + rs.getString("device_id"));
				initResult = proxy.initTrans(rs.getString("device_id"), "");
				log.debug("ע�᷵�ؽ����" + initResult + " (0:�ɹ���1���ܾ���)");
				if(initResult == 1) return initResult;
			}						
		} catch(Exception ex){				
			log.error("�豸ע��ʧ�ܣ�"+ex.getMessage(),ex);			
			initResult = 1;
		}finally{
			DatabaseHelper.close(rs,preStatement);
		}
		return initResult;
	}

	public String getWsEndPoint() {
		return wsEndPoint;
	}

	public void setWsEndPoint(String wsEndPoint) {
		this.wsEndPoint = wsEndPoint;
	}

	public String getSelectDeviceInfoSql() {
		return selectDeviceInfoSql;
	}

	public void setSelectDeviceInfoSql(String selectDeviceInfoSql) {
		this.selectDeviceInfoSql = selectDeviceInfoSql;
	}	

}
