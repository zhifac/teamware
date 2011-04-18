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
