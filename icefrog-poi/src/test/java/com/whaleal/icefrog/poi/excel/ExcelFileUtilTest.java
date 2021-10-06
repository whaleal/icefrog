package com.whaleal.icefrog.poi.excel;

import com.whaleal.icefrog.core.io.FileUtil;
import com.whaleal.icefrog.core.io.IoUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class ExcelFileUtilTest {

	@Test
	public void xlsTest(){
		InputStream in = FileUtil.getInputStream("aaa.xls");
		try{
			Assert.assertTrue(ExcelFileUtil.isXls(in));
			Assert.assertFalse(ExcelFileUtil.isXlsx(in));
		} finally {
			IoUtil.close(in);
		}
	}

	@Test
	public void xlsxTest(){
		InputStream in = FileUtil.getInputStream("aaa.xlsx");
		try{
			Assert.assertFalse(ExcelFileUtil.isXls(in));
			Assert.assertTrue(ExcelFileUtil.isXlsx(in));
		} finally {
			IoUtil.close(in);
		}
	}
}
