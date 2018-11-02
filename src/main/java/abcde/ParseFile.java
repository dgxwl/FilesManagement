package abcde;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

public class ParseFile {
	
	private static List<List<File>> sameFiles = new LinkedList<>();  //保存重复文件
	private static Map<String, File> fileHash = new HashMap<>();  //<100M所有文件的hash值
	private static Map<String, Integer> listIndex = new HashMap<>();  //记录有重复的hash值的文件存到了哪个list中
	
	private static List<File> bigFiles = new LinkedList<>();  //>=500M
	private static List<List<File>> sameBigFiles = new LinkedList<>();  //保存>=100M重复文件
	private static Map<Long, File> fileLength = new HashMap<>();  //所有文件的字节数
	private static Map<Long, Integer> listIndex2 = new HashMap<>();  //记录有重复的字节数的文件存到了哪个list中
	
	/**
	 * 解析给定file对象
	 * @param f
	 */
	public static void parseAllFiles(File f) {
		if (f.isDirectory()) {
			System.out.println(f + " --- 文件夹");
			File[] subs = f.listFiles();
			if (subs == null) {
//				System.out.println(f + " --- 空文件夹");
				return ;
			}
			for (int i = 0; i < subs.length; i++) {
				File sub = subs[i];
				parseAllFiles(sub);
			}
		}
		
		if (f.isFile()) {
			System.out.println(f + " --- 文件");
			if (f.length() > 100 * 1024 * 1024L) {
				bigFiles.add(f);
			}
			try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));) {
				if (f.length() < 100 * 1024 * 1024L) {  //小于100M用hash值判断重复
					String hash = DigestUtils.md5Hex(in);
					if (fileHash.containsKey(hash)) {
						//出现重复文件
						Integer index = listIndex.get(hash);
						if (index != null) {
							sameFiles.get(index).add(f);
						} else {
							int i = sameFiles.size();
							sameFiles.add(new LinkedList<>());
							listIndex.put(hash, i);
							File first = fileHash.get(hash);
							sameFiles.get(i).add(first);
							sameFiles.get(i).add(f);
						}
					} else {
						fileHash.put(hash, f);
					}
				} else {  //大于100M根据大小判断(暂定)
					//记录下大于500M的大文件
					if (f.length() >= 500 * 1024 * 1024L) {
						bigFiles.add(f);
					}
					long length = f.length();
					if (fileLength.containsKey(length)) {  //可增加再取部分长度计算hash确认的步骤
						//出现重复文件
						Integer index = listIndex2.get(length);
						if (index != null) {
							sameBigFiles.get(index).add(f);
						} else {
							int i = sameBigFiles.size();
							sameBigFiles.add(new LinkedList<>());
							listIndex2.put(length, i);
							File first = fileLength.get(length);
							sameBigFiles.get(i).add(first);
							sameBigFiles.get(i).add(f);
						}
					}
				}
			} catch (Exception e) {
				System.out.println("解析" + f + "出现异常: " + e.getMessage());
			}
		} else {
//			System.out.println(f + " --- 不知道是文件还是文件夹");
		}
	}
	
	public static void outputResult() {
		try (PrintWriter pw = new PrintWriter(new BufferedOutputStream(new FileOutputStream("D:/report1.txt")), true);) {
			pw.println("可能是重复文件: \n");
			for (List<File> sf : sameFiles) {
				pw.println("---------------------------------------------------");
				for (File file : sf) {
					pw.println(file);
				}
				pw.println("---------------------------------------------------");
			}
			for (List<File> sf : sameBigFiles) {
				pw.println("---------------------------------------------------");
				for (File file : sf) {
					pw.println(file);
				}
				pw.println("---------------------------------------------------");
			}
		} catch (Exception e) {
			System.out.println("生成报告时出现异常: " + e.getMessage());
		}
		
		try (PrintWriter pw = new PrintWriter(new BufferedOutputStream(new FileOutputStream("D:/report2.txt")), true);) {
			pw.println("大文件: \n");
			for (File file : bigFiles) {
				pw.println(file);
			}
		} catch (Exception e) {
			System.out.println("生成报告时出现异常: " + e.getMessage());
		}
	}
}
