package com.whaleal.icefrog.poi.word;

import com.whaleal.icefrog.core.io.FileUtil;
import com.whaleal.icefrog.core.io.IORuntimeException;
import com.whaleal.icefrog.poi.exceptions.POIException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.IOException;

/**
 * Word Document工具
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class DocUtil {

	/**
	 * 创建{@link XWPFDocument}，如果文件已存在则读取之，否则创建新的
	 *
	 * @param file docx文件
	 * @return {@link XWPFDocument}
	 */
	public static XWPFDocument create(File file) {
		try {
			return FileUtil.exist(file) ? new XWPFDocument(OPCPackage.open(file)) : new XWPFDocument();
		} catch (InvalidFormatException e) {
			throw new POIException(e);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}
}
