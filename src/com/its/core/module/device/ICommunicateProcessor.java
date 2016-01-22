/**
 * 
 */
package com.its.core.module.device;

import com.its.core.util.XMLProperties;

/**
 * �豸ͨѶ�������ӿ�
 * ���дӣ�ICommunicateProcessor ��չ���඼�����Ƕ����̵߳ķ�ʽִ��
 * �������� 2012-9-19 ����05:47:22
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public interface ICommunicateProcessor extends Runnable {
	
	/**
	 * ���豸֮�����ͨѶ��Э�鴦��������
	 */
	public static final int PROCESSOR_TYPE_DEVICE = 0x1;
	
	/**
	 * ���豸֮���ϵͳ����ͨѶ��Э�鴦�������ͣ��磺����Ӧ�÷��������͹�������Ϣ���д���
	 */
	public static final int PROCESSOR_TYPE_TRANSFER = 0x2;
	
	/**
	 * ���ô���������
	 * @param processorType
	 */
	public void setProcessorType(int processorType);
	
	/**
	 * ����ͷ����
	 * @param headWord
	 */
	public void setHeadWord(String headWord);
	
	/**
	 * �����豸ͨѶģ������
	 * @param deviceCommunicateModule
	 */
	public void setDeviceCommunicateModule(DeviceCommunicateModule deviceCommunicateModule);
	
	/**
	 * ���ò�������XML�����ļ���
	 * @param props
	 * @throws Exception
	 */
	public void configure(XMLProperties props,String propertiesPrefix,int no) throws Exception;

}
