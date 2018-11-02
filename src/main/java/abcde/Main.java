package abcde;

import java.io.File;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("盘符: ");
		String root = scanner.nextLine().toUpperCase();
		scanner.close();
		
		File[] files = File.listRoots();
		File file = null;
		boolean hasFile = false;
		for(int i = 0; i < files.length; i++) {
			if (files[i].getPath().contains(root)) {
				file = files[i];
				hasFile = true;
			}
		}
		
		if (hasFile) {
			System.out.println("开始解析...");
			ParseFile.parseAllFiles(file);
		} else {
			System.out.println("没找到这个盘");
		}
		
		ParseFile.outputResult();
		System.out.println("结束.");
	}
	
}
