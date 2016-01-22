/**
 * 
 */
package com.its.core.module.broadcast;

import com.its.core.module.broadcast.impl.DefaultProtocolGenerator;

/**
 * �������� 2012-9-24 ����09:51:25
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class ProtocolGeneratorFactory {
	
	private IProtocolGenerator protocolGenerator = new DefaultProtocolGenerator();
	
	private static ProtocolGeneratorFactory instance = new ProtocolGeneratorFactory();
	
	private ProtocolGeneratorFactory(){}
	
	public static ProtocolGeneratorFactory getInstance(){
		return instance;
	}
	
	public void setProtocolGenerator(IProtocolGenerator protocolGenerator){
		this.protocolGenerator = protocolGenerator;
	}
	
	public IProtocolGenerator getProtocolGenerator(){
		return this.protocolGenerator;
	}

}
