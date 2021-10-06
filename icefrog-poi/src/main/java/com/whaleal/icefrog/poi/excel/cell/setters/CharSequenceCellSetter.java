package com.whaleal.icefrog.poi.excel.cell.setters;

import com.whaleal.icefrog.poi.excel.cell.CellSetter;
import org.apache.poi.ss.usermodel.Cell;

/**
 * {@link CharSequence} 值单元格设置器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class CharSequenceCellSetter implements CellSetter {

	private final CharSequence value;

	/**
	 * 构造
	 *
	 * @param value 值
	 */
	CharSequenceCellSetter(CharSequence value) {
		this.value = value;
	}

	@Override
	public void setValue(Cell cell) {
		cell.setCellValue(value.toString());
	}
}
