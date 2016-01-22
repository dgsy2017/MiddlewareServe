/**
 * 
 */
package com.its.core.module.task.bean;

import java.io.Serializable;

import org.apache.commons.net.ftp.FTP;

/**
 * �������� 2013-11-19 ����08:13:53
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class UploadDirBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2100585737034355338L;
	
	/**
	 * �ļ�Ŀ¼·��
	 */
	private String path = null;
	
	/**
	 * �Ƿ��ϴ���Ŀ¼
	 */
	private boolean includeSubdirectory = true;
	
	/**
	 * �Ƿ��ڷ������ϴ���ͬ����Ŀ¼
	 */
	private boolean createSubdirectoryAtFtpserver = false;
	
	/**
	 * �ļ��ϴ��ɹ����Ƿ�ɾ�������ļ�
	 */
	private boolean deleteLocalFile = true;
	
	/**
	 * �ļ��ϴ��ɹ����Ƿ�ɾ�����صĿ��ļ�Ŀ¼
	 */
	private boolean deleteLocalEmptySubdirectory = true;
	
	private  int fileType = FTP.BINARY_FILE_TYPE;

	public boolean isCreateSubdirectoryAtFtpserver() {
		return createSubdirectoryAtFtpserver;
	}

	public void setCreateSubdirectoryAtFtpserver(
			boolean createSubdirectoryAtFtpserver) {
		this.createSubdirectoryAtFtpserver = createSubdirectoryAtFtpserver;
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public boolean isIncludeSubdirectory() {
		return includeSubdirectory;
	}

	public void setIncludeSubdirectory(boolean includeSubdirectory) {
		this.includeSubdirectory = includeSubdirectory;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isDeleteLocalEmptySubdirectory() {
		return deleteLocalEmptySubdirectory;
	}

	public void setDeleteLocalEmptySubdirectory(boolean deleteLocalEmptySubdirectory) {
		this.deleteLocalEmptySubdirectory = deleteLocalEmptySubdirectory;
	}

	public boolean isDeleteLocalFile() {
		return deleteLocalFile;
	}

	public void setDeleteLocalFile(boolean deleteLocalFile) {
		this.deleteLocalFile = deleteLocalFile;
	}

}
