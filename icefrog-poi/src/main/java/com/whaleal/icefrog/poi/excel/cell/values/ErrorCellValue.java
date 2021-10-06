package com.whaleal.icefrog.poi.excel.cell.values;

import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.poi.excel.cell.CellValue;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;

/**
 * ERROR类型单元格值
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class ErrorCellValue implements CellValue<String> {

	private final Cell cell;

	/**
	 * 构造
	 *
	 * @param cell {@link Cell}
	 */
	public ErrorCellValue(Cell cell){
		this.cell = cell;
	}

	@Override
	public String getValue() {
		final FormulaError error = FormulaError.forInt(cell.getErrorCellValue());
		return (null == error) ? StrUtil.EMPTY : error.getString();
	}
}
