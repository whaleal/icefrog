package com.whaleal.icefrog.poi.excel.cell.setters;

import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.poi.excel.cell.CellSetter;
import org.apache.poi.ss.usermodel.Cell;

/**
 * {@link Number} 值单元格设置器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class NullCellSetter implements CellSetter {

	public static final NullCellSetter INSTANCE = new NullCellSetter();

	@Override
	public void setValue(Cell cell) {
		cell.setCellValue(StrUtil.EMPTY);
	}
}
