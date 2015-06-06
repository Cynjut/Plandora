package com.pandora.bus.alert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.pandora.AdditionalFieldTO;
import com.pandora.FieldValueTO;
import com.pandora.MetaFieldTO;
import com.pandora.NotificationFieldTO;
import com.pandora.PlanningTO;
import com.pandora.ProjectTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.bus.EventBUS;
import com.pandora.delegate.MetaFieldDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;
import com.sun.mail.smtp.SMTPTransport;

public class FileToFTPNotification extends Notification {
	private static final String SMTP_MAILER = "smtpsend";
	private static final String CONTENT_TYPE = "text/html";
	private static final String EMAIL_HOST = "EMAIL_HOST";
	private static final String EMAIL_USER = "EMAIL_USER";
	private static final String EMAIL_PASSWORD = "EMAIL_PASSWORD";
	private static final String FTP_HOST = "FTP_HOST";
	private static final String FTP_USER = "FTP_USER";
	private static final String FTP_PASSWORD = "FTP_PASSWORD";
	private static final String FTP_PORT = "FTP_PORT";
	private static final String FTP_PATH = "FTP_PATH";
	private static final String CHECK_TYPE = "CHECK_TYPE";
	private static final String CHECK_PATH = "CHECK_PATH";
	private static final String COMPACT = "COMPACT";
	private static final String EMAIL_SENDER = "EMAIL_SENDER";
	private static final String SUBJECT = "SUBJECT";
	private static final String BODY = "BODY";
	private static final String UPLOADED_FILE = "#UPLOADED_FILE#";
	private static final String PROJECT = "#PROJECT#";

	@Override
	public boolean sendNotification(Vector<NotificationFieldTO> fields,
			Vector<Vector<Object>> sqlData) throws Exception {

		String sender = this.getParamByKey(EMAIL_SENDER, fields);
		String type = this.getParamByKey(CHECK_TYPE, fields);
		String path = this.getParamByKey(CHECK_PATH, fields);
		String ftpHost = this.getParamByKey(FTP_HOST, fields);
		String ftpUser = this.getParamByKey(FTP_USER, fields);
		String ftpPassword = this.getParamByKey(FTP_PASSWORD, fields);
		String ftpPort = this.getParamByKey(FTP_PORT, fields);
		String ftpPath = this.getParamByKey(FTP_PATH, fields);
		String emailUser = this.getParamByKey(EMAIL_USER, fields);
		String emailPassword = this.getParamByKey(EMAIL_PASSWORD, fields);
		String subject = this.getParamByKey(SUBJECT, fields);
		String compactFile = this.getParamByKey(COMPACT, fields);

		String msg = "";

		String host = this.getParamByKey(EMAIL_HOST, fields);
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");
		Session session = Session.getInstance(props, null);
		SimpleDateFormat dtFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat dtFormatFile = new SimpleDateFormat("yyyyMMdd_HHmmss");
		MetaFieldDelegate mfdel = new MetaFieldDelegate();

		Iterator<Vector<Object>> itSqlData = sqlData.iterator();
		if (itSqlData.hasNext()) {
			itSqlData.next();
		}

		while (itSqlData.hasNext()) {
			Date dtSistema = DateUtil.getNow();
			Vector<Object> data = itSqlData.next();
			AdditionalFieldTO afto = new AdditionalFieldTO();
			afto.setPlanning(new PlanningTO(data.get(0).toString()));
			afto.setMetaField(new MetaFieldTO(data.get(1).toString()));

			String fileName = data.get(6).toString();
			ProjectTO pto = this.getProject(data.get(2).toString());
			msg = "[" + dtFormat.format(dtSistema) + "] [" + fileName + "]";
			if (pto != null) {
				msg += "[" + pto.getName() + "]";
			}
			try {
				if (data.get(2) != null && !data.get(2).toString().equals("0")) {

					if (data.get(3) != null
							&& data.get(3).toString().length() > 0
							&& data.get(3).toString().indexOf("@") > 0) {
						try {
							byte[] response = null;
							if (type.equals("HTTP")) {
								response = this.downloadFile(fileName, path);
							} else if (type.equals("FILE")) {
								response = this.getFile(path, fileName);
							}

							if (compactFile.equalsIgnoreCase("yes")) {
								response = this.zipBytes(fileName, response);
								String name = (fileName.indexOf(".") > 0) ? fileName
										.substring(0, fileName.indexOf("."))
										: fileName;
								fileName = "arq_"
										+ dtFormatFile.format(dtSistema) + "_"
										+ name + ".zip";
							} else {
								fileName = "arq_"
										+ dtFormatFile.format(dtSistema) + "_"
										+ fileName;
							}

							if (response != null) {
								this.sendFile(response, ftpHost, ftpPath,
										Integer.parseInt(ftpPort), ftpUser,
										ftpPassword, fileName);

								msg += " file sent successfully. \n";
								afto.setValue(data.get(5).toString() + msg);
								mfdel.updateAdditionalField(afto);

								String body = this.getParamByKey(BODY, fields);
								body = body.replaceAll(UPLOADED_FILE, fileName);

								if (pto != null) {
									body = body.replaceAll(PROJECT,
											pto.getName());
								}

								this.sendMessage(session, data.get(3)
										.toString(), body, sender, subject,
										host, emailUser, emailPassword);
								if (data.get(4) != null
										&& data.get(4).toString().length() > 0
										&& data.get(4).toString().indexOf("@") > 0) {
									this.sendMessage(session, data.get(4)
											.toString(), body, sender, subject,
											host, emailUser, emailPassword);
								}
							}
						} catch (FileNotFoundException e) {
							msg += " file not found. \n";
							afto.setValue(data.get(5).toString() + msg);
							mfdel.updateAdditionalField(afto);
						} catch (IOException e) {
							msg += " an error occurred while reading the file.\n"
									+ e.getMessage();
							afto.setValue(data.get(5).toString() + msg);
							mfdel.updateAdditionalField(afto);
						}
					} else {
						msg += " E-mail is mandatory.\n";
						afto.setValue(data.get(5).toString() + msg);
						mfdel.updateAdditionalField(afto);
					}
				} else {
					msg += " Project is mandatory.\n";
					afto.setValue(data.get(5).toString() + msg);
					mfdel.updateAdditionalField(afto);
				}
			} catch (Exception e) {
				e.printStackTrace();
				msg += " an error occurred while sending the file: "
						+ e.getMessage();
				afto.setValue(data.get(5).toString() + msg);
				mfdel.updateAdditionalField(afto);
			}
		}

		return super.sendNotification(fields, sqlData);
	}

	private ProjectTO getProject(String id) throws BusinessException {
		ProjectDelegate pdel = new ProjectDelegate();
		return pdel.getProjectObject(new ProjectTO(id), true);
	}

	private byte[] zipBytes(String filename, byte[] input) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		ZipEntry entry = new ZipEntry(filename);
		entry.setSize(input.length);
		zos.putNextEntry(entry);
		zos.write(input);
		zos.closeEntry();
		zos.close();
		return baos.toByteArray();
	}

	private byte[] getFile(String path, String fileName)
			throws FileNotFoundException, IOException {
		InputStream is = null;
		byte[] buffer = null;
		File inFile = new File(path + fileName);
		is = new FileInputStream(inFile);
		buffer = new byte[is.available()];
		is.read(buffer);
		is.close();
		return buffer;
	}

	private void sendMessage(Session session, String destination, String text,
			String sender, String subject, String host, String smtpUser,
			String smtpPass) throws Exception {
		EventBUS bus = new EventBUS();
		try {
			String logMsg = "sender:[" + sender + "] destination:["
					+ destination + "] subject:[" + subject + "] host:[" + host
					+ "] smtpUser:[" + smtpUser + "]";
			bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, logMsg,
					RootTO.ROOT_USER, null);

			// Create the message to be sent
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender));

			if (destination != null) {
				String[] multRecip = destination.split(",");
				for (int i = 0; i < multRecip.length; i++) {
					message.addRecipient(Message.RecipientType.TO,
							new InternetAddress(multRecip[i]));
				}
			}

			message.setSubject(subject);
			message.setHeader("X-Mailer", SMTP_MAILER);
			message.setSentDate(DateUtil.getNow());

			message.setContent(text, CONTENT_TYPE);

			// Create the SMTP Transport
			SMTPTransport transport = (SMTPTransport) session
					.getTransport("smtp");
			try {
				transport.connect(host, smtpUser, smtpPass);
				// Send the message
				transport.sendMessage(message, message.getAllRecipients());
			} finally {
				transport.close();
			}

		} catch (MessagingException ex) {
			throw new Exception("Error sending email to: " + destination, ex);
		}
	}

	private void sendFile(byte[] file, String server, String path, int port,
			String user, String pass, String fileName) throws JSchException,
			SftpException {
		JSch.setConfig("StrictHostKeyChecking", "no");

		JSch jsch = new JSch();
		com.jcraft.jsch.Session session = jsch.getSession(user, server, port);
		session.setPassword(pass);
		session.connect();

		ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
		sftp.connect();
		sftp.cd(path);
		sftp.put(new ByteArrayInputStream(file), fileName);

		sftp.exit();
		sftp.disconnect();
		session.disconnect();
	}

	private byte[] downloadFile(String file, String server)
			throws FileNotFoundException, IOException {
		ByteArrayOutputStream tmpOut = null;
		InputStream in = null;

		byte[] response = null;

		try {

			URL url = new URL(server + "/" + file);
			URLConnection conn = url.openConnection();
			in = conn.getInputStream();
			int contentLength = conn.getContentLength();

			if (contentLength != -1) {
				tmpOut = new ByteArrayOutputStream(contentLength);
			} else {
				tmpOut = new ByteArrayOutputStream(16384);
			}

			byte[] buf = new byte[512];
			while (true) {
				int len = in.read(buf);
				if (len == -1) {
					break;
				}
				tmpOut.write(buf, 0, len);
			}

			response = tmpOut.toByteArray();

		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (tmpOut != null) {
				try {
					tmpOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return response;
	}

	@Override
	public Vector<FieldValueTO> getFields() {
		Vector<FieldValueTO> response = new Vector<FieldValueTO>();
		Vector<TransferObject> typeList = new Vector<TransferObject>();
		typeList.add(new TransferObject("HTTP", "HTTP"));
		typeList.add(new TransferObject("FILE", "FILE"));
		response.add(new FieldValueTO(CHECK_TYPE,
				"notification.checkFile.type", typeList));

		response.add(new FieldValueTO(CHECK_PATH,
				"notification.checkFile.path", FieldValueTO.FIELD_TYPE_TEXT,
				100, 50));

		response.add(new FieldValueTO(EMAIL_SENDER, "notification.email.from",
				FieldValueTO.FIELD_TYPE_TEXT, 100, 50));
		response.add(new FieldValueTO(EMAIL_HOST, "notification.email.host",
				FieldValueTO.FIELD_TYPE_TEXT, 50, 30));
		response.add(new FieldValueTO(EMAIL_USER, "notification.email.user",
				FieldValueTO.FIELD_TYPE_TEXT, 50, 30));
		response.add(new FieldValueTO(EMAIL_PASSWORD,
				"notification.email.pass", FieldValueTO.FIELD_TYPE_PASS, 50, 20));

		response.add(new FieldValueTO(FTP_HOST, "notification.ftp.host",
				FieldValueTO.FIELD_TYPE_TEXT, 50, 30));
		response.add(new FieldValueTO(FTP_USER, "notification.ftp.user",
				FieldValueTO.FIELD_TYPE_TEXT, 50, 30));
		response.add(new FieldValueTO(FTP_PASSWORD, "notification.ftp.pass",
				FieldValueTO.FIELD_TYPE_PASS, 50, 20));
		response.add(new FieldValueTO(FTP_PORT, "notification.ftp.port",
				FieldValueTO.FIELD_TYPE_TEXT, 50, 20));
		response.add(new FieldValueTO(FTP_PATH, "notification.ftp.path",
				FieldValueTO.FIELD_TYPE_TEXT, 50, 20));

		Vector<TransferObject> boolList = new Vector<TransferObject>();
		boolList.add(new TransferObject("yes", "label.yes"));

		boolList.add(new TransferObject("no", "label.no"));
		response.add(new FieldValueTO(COMPACT, "notification.ftp.compact",
				boolList));

		response.add(new FieldValueTO(SUBJECT, "notification.email.title",
				FieldValueTO.FIELD_TYPE_TEXT, 50, 30));
		response.add(new FieldValueTO(BODY, "notification.email.body",
				FieldValueTO.FIELD_TYPE_AREA, 85, 4));

		return response;
	}

	public String getUniqueName() {
		return "FileToFTPNotification";
	}

}
