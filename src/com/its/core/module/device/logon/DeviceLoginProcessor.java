/**
 * 
 */
package com.its.core.module.device.logon;

import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoSession;

import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.module.device.ACommunicateProcessor;
import com.its.core.module.device.MessageBean;
import com.its.core.module.device.MessageHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLParser;
import com.its.core.util.XMLProperties;

/**
 * �������� 2012-11-22 ����03:32:52
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class DeviceLoginProcessor extends ACommunicateProcessor {
	private static final Log log = LogFactory.getLog(DeviceLoginProcessor.class);

	/* (non-Javadoc)
	 * @see com.its.core.module.device.ACommunicateProcessor#configure(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configure(XMLProperties props, String propertiesPrefix, int no) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.device.ACommunicateProcessor#process(com.its.core.module.device.MessageBean)
	 */
	@Override
	public void process(MessageBean messageBean) throws Exception {
		String sessionKey = messageBean.getSessionKey();
		IoSession session = this.getDeviceCommunicateModule().getSessionsMap().get(sessionKey);
		log.debug("session = "+session);
		
		String deviceId = this.getDeviceId(messageBean);
		
		//��һ��������豸���ϵͳ�Ƿ��Ѷ���
		if(StringHelper.isNotEmpty(deviceId)){
			Map<String, DeviceInfoBean> deviceMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
			if(deviceMap.containsKey(deviceId)){
				
				//�ڶ�������ǰ�豸����ʱ��У׼ָ��
				try{
					this.adjustTime(session,messageBean.getVersion());
				}catch(Exception ex){
					log.error("����ʱ�ӵ���ָ��ʧ�ܣ�"+ex.getMessage(),ex);
				}
			}
			else{
				log.warn("ע��ʧ�ܣ�δ�ҵ�ǰ���豸����ı�ţ�"+deviceId);
			}		
		}

	}
	
	/**
	 * ����ǰ���豸ʱ��
	 * @param currentSocket
	 * @throws Exception
	 */
	private void adjustTime(IoSession currentSession,int msgVersion) throws Exception{			
		String protocol = null;		
		Date currentTime = new Date();			
		
		StringBuffer timeProtocol = new StringBuffer("<curTime>").append(DateHelper.dateToString(currentTime,"yyyyMMddHHmmss")).append("</curTime>");
		protocol = MessageHelper.createXmlMsg(
				MessageHelper.XML_MSGCODE_SD_TIME_ADJUST, 
				"2.0.0.0", 
				false,
				null, 
				timeProtocol.toString());
		
		
		log.debug("����ʱ�ӣ�"+protocol);
		log.debug("send to:"+currentSession);
		currentSession.write(protocol);
	}
	
	/**
	 * ��ȡ�豸ID
	 * @param messageBean
	 * @return
	 * @throws Exception
	 */
	private String getDeviceId(MessageBean messageBean) throws Exception{
		String deviceId = null;
		
		XMLParser xmlParser = messageBean.getXmlParser();
		deviceId = xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "deviceId");
		
		return deviceId;
	}

}
