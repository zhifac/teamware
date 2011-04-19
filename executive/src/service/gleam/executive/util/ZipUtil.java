/*
 *  ZipUtil.java
 *
 *  Copyright (c) 2006-2011, The University of Sheffield.
 *
 *  This file is part of GATE Teamware (see http://gate.ac.uk/teamware/), 
 *  and is free software, licenced under the GNU Affero General Public License,
 *  Version 3, November 2007 (also included with this distribution as file 
 *  LICENCE-AGPL3.html).
 *
 *  A commercial licence is also available for organisations whose business
 *  models preclude the adoption of open source and is subject to a licence
 *  fee charged by the University of Sheffield. Please contact the GATE team
 *  (see http://gate.ac.uk/g8/contact) if you require a commercial licence.
 *
 *  $Id$
 */
package gleam.executive.util;

import java.util.Set;
import java.util.zip.ZipEntry;

public class ZipUtil {
	
	public static boolean isValidZipEntry(ZipEntry ze) {
		boolean supported = false;
		String path = ze.getName();
		// ignore Mac OS resource fork entries
			Set<String> supportedFormats = GATEUtil.supportedFormats;

			for (String format : supportedFormats) {
				String suffix = "." + format;
				if (!ze.isDirectory() && path.endsWith(suffix)) {
					supported = true;
					break;
				}
			}

		return supported;
	}
	
	public static boolean isMACSpecificZipEntry(ZipEntry ze) {
		boolean flag = false;
		String path = ze.getName();
		// ignore Mac OS resource fork entries
		if (path.contains("__MACOSX/") || path.contains(".DS_Store")) {
			flag = true;
		}
		return flag;
	}

	/**
	 * Returns true if the filename has a valid zip extension. i.e. jar, war,
	 * ear, zip etc.
	 *
	 * @param filename
	 *            The name of the file to check.
	 * @return true if it has a valid extension.
	 */
	public static boolean isZipFile(String filename) {
		boolean result;

		if (filename == null) {
			result = false;
		} else {
			String lowercaseName = filename.toLowerCase();
			if (lowercaseName.endsWith(".zip")) {
				result = true;
			} else if (lowercaseName.endsWith(".ear")) {
				result = true;
			} else if (lowercaseName.endsWith(".war")) {
				result = true;
			} else if (lowercaseName.endsWith(".rar")) {
				result = true;
			} else if (lowercaseName.endsWith(".jar")) {
				result = true;
			} else {
				result = false;
			}
		}

		return result;
	}
}
