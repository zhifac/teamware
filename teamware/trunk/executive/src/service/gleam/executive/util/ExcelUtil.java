/*
 *  ExcelUtil.java
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

import gleam.executive.Constants;
import gleam.executive.model.Address;
import gleam.executive.model.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.util.StringUtils;

public class ExcelUtil {

	private static Log log = LogFactory.getLog(ExcelUtil.class);

	public static List<User> populateUser(String fileName) throws ParseException, IOException {

		FileInputStream myInput = new FileInputStream(fileName);
		return populateUser(myInput);
	}

	public static List<User> populateUser(InputStream is) throws ParseException, IOException {

		List<User> users = new ArrayList<User>();

		/** Create a POIFSFileSystem object **/
		POIFSFileSystem myFileSystem = new POIFSFileSystem(is);

		/** Create a workbook using the File System **/
		HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

		/** Get the first sheet from workbook **/
		HSSFSheet mySheet = myWorkBook.getSheetAt(0);

		/** We now need something to iterate through the cells. **/
		Iterator<Row> rowIter = mySheet.rowIterator();
		int count = 0;
		String username = "";
		String password = "";
		String firstName = "";
		String lastName = "";
		String email = "";
		String roleCSVString = "";
		
		while (rowIter.hasNext()) {
			
			HSSFRow row = (HSSFRow) rowIter.next();
			
			if (count > 0) {
				if (row != null) {

					username = getCellValue(row, "username", 0, true, null);
					password = getCellValue(row, "password", 1, false, Constants.DEFAULT_PASSWORD);
					firstName = getCellValue(row, "firstName", 2, true, null);
					lastName = getCellValue(row, "lastName", 3, true, null);
					email = getCellValue(row, "email", 4, true, null);
					roleCSVString = getCellValue(row, "roles", 5, true, null);

					User user = new User();
					user.setUsername(username);
					user.setPassword(password);
					user.setFirstName(firstName);
					user.setLastName(lastName);
					user.setEmail(email);
					// default empty strings
					user.setPhoneNumber("");
					user.setWebsite("");
					user.setPasswordHint("something");
					Address address = new Address();
					address.setCity("");
					address.setCountry("");
					address.setPostalCode("");
					address.setProvince("");
					user.setAddress(address);
					user.setAccountExpired(false);
					user.setAccountLocked(false);
					user.setEnabled(true);
					Set<String> roleNames = StringUtils.commaDelimitedListToSet(roleCSVString);
					user.setRoleNames(roleNames);
					users.add(user);
				} else {
					break;
				}
			}
			count++;
		}

		return users;
	}

	private static String getCellValue(HSSFRow row, String field, int i, 
			boolean isRequired, String defaultValue) throws ParseException {

		String value = "";
		HSSFCell cell = row.getCell(i);
		if (cell == null || "".equals(cell.toString().trim())) {
			if(isRequired) {
				StringBuffer message = new StringBuffer("Empty value for required field ");
				message.append(field).append(" at row " ).append(i);
				log.debug(message.toString());
				throw new ParseException(message.toString(), i);
			} else { 
				value = defaultValue;
			}
		} else {
			if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				value = "" + Math.round(new Float(cell.toString().trim()));
			} else {
				value = cell.toString().trim();
			}
		}
		return value;
	}

}
