package Dummy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Dummy {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
       
			ArrayList<R> rec = new ArrayList<>();
		    File f = new File("C:\\Users\\SA\\Downloads\\db\\bag1.txt");
			FileReader fr = new FileReader(f);
			BufferedReader br  = new BufferedReader(fr);
			String s = null;

			while ((s = br.readLine())!=null) {

				rec.add(new R(Integer.parseInt(s.substring(0, 8)) , s ));
			   //r.add(new R(Integer.parseInt(s.substring(0, 8)) , s ));

			}
	        
			br.close();
	
		Collections.sort(rec);	
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\SA\\Downloads\\db\\sort1.txt"));
		
		for(R r : rec)
		{
			bw.write(r.data);
			bw.newLine();
		}
		
	}

}
class R implements Comparable<R>{

	int key;
	String data;

	R(int id, String d) {
		key = id;
		data = d;
	}
	
	
	 @Override
	 public int compareTo(R o) {
		   if(o == null)
			   return 0;
	       return this.key - o.key ;
	 }
	
}