/**
 * 
 */
package com.its.core.module.device.vehicle.filter.helper;

/**
 * �������� 2012-12-6 ����03:23:55
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class SectionBean {
	
	//������
	private String direction = null;
	
	//���������
	private String name = null;
	
	// ����������豸ID
	private String deviceId = null;
	
	private CheckPointBean firstCheckPointBean = null;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public CheckPointBean getFirstCheckPointBean() {
		return firstCheckPointBean;
	}
	public void setFirstCheckPointBean(CheckPointBean firstCheckPointBean) {
		this.firstCheckPointBean = firstCheckPointBean;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

}
