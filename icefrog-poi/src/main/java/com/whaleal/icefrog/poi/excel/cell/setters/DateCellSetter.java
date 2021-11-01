package com.whaleal.icefrog.poi.excel.cell.setters;

import com.whaleal.icefrog.poi.excel.cell.CellSetter;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Date;

/**
 * {@link Date} 值单元格设置器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class DateCellSetter implements CellSetter {

    private final Date value;

    /**
     * 构造
     *
     * @param value 值
     */
    DateCellSetter( Date value ) {
        this.value = value;
    }

    @Override
    public void setValue( Cell cell ) {
        cell.setCellValue(value);
    }
}
