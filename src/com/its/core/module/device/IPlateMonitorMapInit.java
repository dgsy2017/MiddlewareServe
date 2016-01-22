/**
 * 
 */
package com.its.core.module.device;

import java.util.List;

import com.its.core.util.XMLProperties;

/**
 * �������� 2013-1-30 ����03:23:47
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public interface IPlateMonitorMapInit {
	
	public void configure(XMLProperties props, String propertiesPrefix, int no) throws Exception;
	
	public List<BlacklistBean> load() throws Exception;

}
