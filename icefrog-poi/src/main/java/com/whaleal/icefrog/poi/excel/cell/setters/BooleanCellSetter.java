package com.whaleal.icefrog.poi.excel.cell.setters;

import com.whaleal.icefrog.poi.excel.cell.CellSetter;
import org.apache.poi.ss.usermodel.Cell;

/**
 * {@link Boolean} 值单元格设置器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class BooleanCellSetter implements CellSetter {

    private final Boolean value;

    /**
     * 构造
     *
     * @param value 值
     */
    BooleanCellSetter( Boolean value ) {
        this.value = value;
    }

    @Override
    public void setValue( Cell cell ) {
        cell.setCellValue(value);
    }
}
