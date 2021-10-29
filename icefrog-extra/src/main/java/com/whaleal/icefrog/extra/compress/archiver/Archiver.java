package com.whaleal.icefrog.extra.compress.archiver;

import com.whaleal.icefrog.core.util.StrUtil;

import java.io.Closeable;
import java.io.File;

/**
 * 数据归档封装，归档即将几个文件或目录打成一个压缩包
 *
 * @author Looly
 * @author wh
 */
public interface Archiver extends Closeable {

	/**
	 * 将文件或目录加入归档，目录采取递归读取方式按照层级加入
	 *
	 * @param file 文件或目录
	 * @return this
	 */
	default Archiver add(File file) {
		return add(file, null);
	}

	/**
	 * 将文件或目录加入归档，目录采取递归读取方式按照层级加入
	 *
	 * @param file   文件或目录
	 * @param predicate 文件过滤器，指定哪些文件或目录可以加入，当{@link Predicate#apply(Object)}为true时加入。
	 * @return this
	 */
	default Archiver add(File file, Predicate<File> predicate) {
		return add(file, StrUtil.SLASH, predicate);
	}

	/**
	 * 将文件或目录加入归档包，目录采取递归读取方式按照层级加入
	 *
	 * @param file   文件或目录
	 * @param path   文件或目录的初始路径，null表示位于根路径
	 * @param predicate 文件过滤器，指定哪些文件或目录可以加入，当{@link Predicate#apply(Object)}为true时加入。
	 * @return this
	 */
	Archiver add(File file, String path, Predicate<File> predicate);

	/**
	 * 结束已经增加的文件归档，此方法不会关闭归档流，可以继续添加文件
	 *
	 * @return this
	 */
	Archiver finish();

	/**
	 * 无异常关闭
	 */
	@Override
	void close();
}
