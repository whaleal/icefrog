package com.whaleal.icefrog.poi.excel.sax.handler;

import com.whaleal.icefrog.core.lang.Precondition;
import com.whaleal.icefrog.core.lang.func.Func1;

import java.util.List;

/**
 * 抽象行数据处理器，通过实现{@link #handle(int, long, List)} 处理原始数据<br>
 * 并调用{@link #handleData(int, long, Object)}处理经过转换后的数据。
 *
 * @param <T> 转换后的数据类型
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public abstract class AbstractRowHandler<T> implements RowHandler {

    /**
     * 读取起始行（包含，从0开始计数）
     */
    protected final int startRowIndex;
    /**
     * 读取结束行（包含，从0开始计数）
     */
    protected final int endRowIndex;
    /**
     * 行数据转换函数
     */
    protected Func1<List<Object>, T> convertFunc;

    /**
     * 构造
     *
     * @param startRowIndex 读取起始行（包含，从0开始计数）
     * @param endRowIndex   读取结束行（包含，从0开始计数）
     */
    public AbstractRowHandler( int startRowIndex, int endRowIndex ) {
        this.startRowIndex = startRowIndex;
        this.endRowIndex = endRowIndex;
    }

    @Override
    public void handle( int sheetIndex, long rowIndex, List<Object> rowList ) {
        Precondition.notNull(convertFunc);
        if (rowIndex < this.startRowIndex || rowIndex > this.endRowIndex) {
            return;
        }
        handleData(sheetIndex, rowIndex, convertFunc.applyWithRuntimeException(rowList));
    }

    /**
     * 处理转换后的数据
     *
     * @param sheetIndex 当前Sheet序号
     * @param rowIndex   当前行号，从0开始计数
     * @param data       行数据
     */
    public abstract void handleData( int sheetIndex, long rowIndex, T data );
}
