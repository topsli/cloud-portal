package de.papke.cloud.portal.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.papke.cloud.portal.constants.Constants;
import de.papke.cloud.portal.dao.ProvisionLogDao;
import de.papke.cloud.portal.pojo.ProvisionLog;
import de.papke.cloud.portal.pojo.User;
import de.papke.cloud.portal.util.ZipUtil;

@Service
public class ProvisionLogService {

	private static final Logger LOG = LoggerFactory.getLogger(ProvisionLogService.class);

	private static final String TMP_FILE_PREFIX = "provision-log";
	private static final String TMP_FILE_SUFFIX = ".zip";

	@Value("${application.dev.mode}")
	private boolean devMode;	
	
	@Value("${application.expiration.auto.minutes}")
	private int expirationAutoMinutes;	

	@Autowired
	private SessionUserService sessionUserService;

	@Autowired
	private ProvisionLogDao provisionLogDao;

	public List<ProvisionLog> getList(String provider) {

		List<ProvisionLog> provisionLogList = new ArrayList<>();

		User user = sessionUserService.getUser();
		if (user != null) {
			List<String> groups = user.getGroups();
			provisionLogList = provisionLogDao.findByGroupInAndProvider(groups, provider);
		}

		return provisionLogList;
	}
	
	public ProvisionLog get(String id) {
		return provisionLogDao.findById(id);
	}
	
	public List<ProvisionLog> getExpired() {
		return provisionLogDao.findByCommandAndExpirationDate(Constants.ACTION_APPLY, new Date());
	}

	public ProvisionLog create(String state, String provider, String group, Boolean success, Map<String, Object> variableMap, File privateKeyFile, File tmpFolder) {

		ProvisionLog provisionLog = null;
		File zipFile = null;

		try {

			// get username
			User user = sessionUserService.getUser();
			String username = user.getUsername();

			// zip temp folder
			zipFile = File.createTempFile(TMP_FILE_PREFIX, TMP_FILE_SUFFIX);
			ZipUtil.zip(tmpFolder, zipFile);
			byte[] data = IOUtils.toByteArray(new FileInputStream(zipFile));

			// get private key as byte array
			byte[] privateKey = null;
			if (privateKeyFile != null) {
				privateKey = IOUtils.toByteArray(new FileInputStream(privateKeyFile));
			}
			
			// get expiration date
			Date expirationDate = getExpirationDate(variableMap);
			
			// create provision log
			provisionLog = provisionLogDao.save(new ProvisionLog(new Date(), expirationDate, username, group, state, provider, success, variableMap, privateKey, data));
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		finally {
			FileUtils.deleteQuietly(zipFile);
		}

		return provisionLog;
	}
	
	public ProvisionLog update(ProvisionLog provisionLog) {
		
		// set date
		provisionLog.setDate(new Date());
		
		// save
		return provisionLogDao.save(provisionLog);
	}

	public void delete(String id) {
		provisionLogDao.delete(id);
	}
	
	private Date getExpirationDate(Map<String, Object> variableMap) {
		
		Date expirationDate = null;
		long now = System.currentTimeMillis();
		
		if (devMode) {
			expirationDate = new Date(now + (expirationAutoMinutes * DateUtils.MILLIS_PER_MINUTE));
		}
		else {
			String expirationDaysString = (String) variableMap.get(Constants.VM_EXPIRATION_DAYS_STRING);
			if (StringUtils.isNotEmpty(expirationDaysString)) {
				int expirationDays = Integer.parseInt(expirationDaysString);
				if (expirationDays != -1) {
					expirationDate = new Date(now + (expirationDays * DateUtils.MILLIS_PER_DAY));
				}
			}
		}
		
		return expirationDate;
	}
}
