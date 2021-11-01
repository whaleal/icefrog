package com.whaleal.icefrog.poi.excel.cell.setters;

import com.whaleal.icefrog.core.util.NumberUtil;
import com.whaleal.icefrog.poi.excel.cell.CellSetter;
import org.apache.poi.ss.usermodel.Cell;

/**
 * {@link Number} 值单元格设置器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class NumberCellSetter implements CellSetter {

    private final Number value;

    /**
     * 构造
     *
     * @param value 值
     */
    NumberCellSetter( Number value ) {
        this.value = value;
    }

    @Override
    public void setValue( Cell cell ) {
        // issue https://github.com/whaleal/icefrog/issues/I43U9G
        // 避免float到double的精度问题
        cell.setCellValue(NumberUtil.toDouble(value));
    }
}
