/**
 * 
 */
package com.its.core.module;

import com.its.core.util.XMLProperties;

/**
 * �������� 2012-9-19 ����03:59:33
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public interface IModule {
	
	/**
	 * ����ģ������
	 * @param xmlProperties
	 * @param propertiesPrefix
	 * @param no
	 * @throws Exception
	 */
	public void config(XMLProperties xmlProperties,String propertiesPrefix,int no) throws Exception;
	
	/**
	 * ����ģ��
	 * @throws Exception
	 */
	public void start() throws Exception;
	
	/**
	 * ֹͣģ��
	 * @throws Exception
	 */
	public void stop() throws Exception;

}
