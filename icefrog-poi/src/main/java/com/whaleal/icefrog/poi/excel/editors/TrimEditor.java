package com.whaleal.icefrog.poi.excel.editors;

import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.poi.excel.cell.CellEditor;
import org.apache.poi.ss.usermodel.Cell;

/**
 * 去除String类型的单元格值两边的空格
 *
 * @author Looly
 * @author wh
 */
public class TrimEditor implements CellEditor {

    @Override
    public Object edit( Cell cell, Object value ) {
        if (value instanceof String) {
            return StrUtil.trim((String) value);
        }
        return value;
    }

}
