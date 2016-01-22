/**
 * 
 */
package com.its.core.module.broadcast;

import com.its.core.module.device.MessageBean;

/**
 * �������� 2012-9-24 ����09:45:51
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class BroadcastProtocolBean {
	
	//�㲥��Ϣ�����õ�ע��Э��/
	private String registerProtocol;
	
	//����Ϣ�������ĸ��豸ID/
	private String deviceId;
	
	//����Ϣ�������ĸ��豸������ĸ�����/
	private String directionCode;
	
	//Э������
	private String content;
	
	//ԭʼЭ�����ݣ��豸������������ԭʼ��Ϣ��δ�����ӹ�������
	private MessageBean oriMessageBean;

	/**
	 * @return the registerProtocol
	 */
	public String getRegisterProtocol() {
		return registerProtocol;
	}

	/**
	 * @param registerProtocol the registerProtocol to set
	 */
	public void setRegisterProtocol(String registerProtocol) {
		this.registerProtocol = registerProtocol;
	}	

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @return the directionCode
	 */
	public String getDirectionCode() {
		return directionCode;
	}

	/**
	 * @param directionCode the directionCode to set
	 */
	public void setDirectionCode(String directionCode) {
		this.directionCode = directionCode;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the oriMessageBean
	 */
	public MessageBean getOriMessageBean() {
		return oriMessageBean;
	}

	/**
	 * @param oriMessageBean the oriMessageBean to set
	 */
	public void setOriMessageBean(MessageBean oriMessageBean) {
		this.oriMessageBean = oriMessageBean;
	}		
	
}
