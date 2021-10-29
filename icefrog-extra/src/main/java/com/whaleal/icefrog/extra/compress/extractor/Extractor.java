package com.whaleal.icefrog.extra.compress.extractor;

import com.whaleal.icefrog.core.lang.Predicate;
import org.apache.commons.compress.archivers.ArchiveEntry;

import java.io.Closeable;
import java.io.File;

/**
 * 归档数据解包封装，用于将zip、tar等包解包为文件
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public interface Extractor extends Closeable {

	/**
	 * 释放（解压）到指定目录，结束后自动关闭流，此方法只能调用一次
	 *
	 * @param targetDir 目标目录
	 */
	default void extract(File targetDir){
		extract(targetDir, null);
	}

	/**
	 * 释放（解压）到指定目录，结束后自动关闭流，此方法只能调用一次
	 *
	 * @param targetDir 目标目录
	 * @param predicate    解压文件过滤器，用于指定需要释放的文件，null表示不过滤。当{@link Predicate#apply(Object)}为true时释放。
	 */
	void extract(File targetDir, Predicate<ArchiveEntry> predicate);

	/**
	 * 无异常关闭
	 */
	@Override
	void close();
}
