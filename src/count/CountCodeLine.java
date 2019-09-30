package count;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CountCodeLine {
	public static int lineNum=0;
	public static void countLineNums(String fileName) throws IOException{
		File file=new File(fileName);
		if(fileName.equals("./src/count/CountCodeLine.java")){
			return;
		}
		if(file.isFile()){
			BufferedReader br=new BufferedReader(new FileReader(fileName));
			int sum=0;
			String line="";
			while((line=br.readLine())!=null){
				sum++;
			}
			lineNum+=sum;
			return;
		}else if(file.isDirectory()){
			File[] files=file.listFiles();
			for(File f:files){
				countLineNums(fileName+"/"+f.getName());
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		countLineNums("./src");
		String num = String.valueOf(lineNum);
		int len = num.length();
		StringBuffer buffer = new StringBuffer();
		buffer.append(num.subSequence(0, len%3));
		for (int i = 0; i < (len/3); i++) {
			buffer.append(",");
			buffer.append(num.subSequence(len%3+i*3, len%3+i*3+3));
		}
		System.out.println("代码总行数:"+buffer.toString());
	}

}
