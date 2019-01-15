package Dummy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class MergeSub {

	

	static Record1[][] records = null;
	private static FileOutputStream fos;
	static BufferedReader[] readers;
	static short[] pointers;
	static byte[] Permanant_pointers;
	static byte marked = 0;
	static ByteBuffer out =  ByteBuffer.allocate( 100 * 340 ) ;
	static FileChannel fc;
	
	static short limit = 300;
    static int g = 0;
	public static void loadBuffers(String location, byte numFiles, String f) throws IOException {
		//System.out.println(numFiles);
		try {
			fos = new FileOutputStream( location+f );
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        // allocate a channel to write that file
        fc = fos.getChannel();  
 
		records = new Record1[numFiles][limit+2];
		pointers =  new short[numFiles];
		readers = new BufferedReader[numFiles];
		Permanant_pointers = new byte[numFiles];
		try {
			fillAll(numFiles, location);
			//cleanAll();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		short k = 0;
		int mini = 0;
		short cursor = 0;
		boolean isDone = true; 
		boolean isSet  = false;
		main:
		while(isDone){			
			isSet = false;			
			z :
			for(byte i=0;i<numFiles;i++){				
                cursor = pointers[i];                   
                if(Permanant_pointers[i] == -1)
                {
                	continue;	
                }
                
                if(records[i][cursor] == null){
                	boolean t = tryFill(i);
                	if(!t){
                		continue z;	
                	}else{
                		cursor = 0;
                		pointers[i] = 0;
                	}
                }
                
				if(!isSet){
					mini = records[i][cursor].key ;
					isSet = true;
				}
				
				if(records[i][cursor].key <= mini){
					mini = records[i][cursor].key; 
				}
			
			}
			
			if(!isSet){
				break main;
			}
			
			
 			for(byte i=0;i<numFiles;i++){
				cursor = pointers[i]; 
				if(Permanant_pointers[i] == -1)
                	continue ;	
				
				if(records[i][cursor].key == mini){
			       if(k >= limit){
			    	   writeOut();
			    	   k = 0;
			       }
			       
			        //   g++;
			    	//System.out.println("at record "+records[i][cursor].key);
				    
			        out.put((records[i][cursor].key+"").getBytes());
					out.put(records[i][cursor].a);
				    out.put("\n".getBytes());
				    pointers[i]++;
				    ++k;
				}

			}
		}
		
		if(k != 0)
		  writeOut();
		 
		
		try {
			//fc.close();
			out.clear();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		//System.out.println("minimum is "+mini);
		
		
	}

	
	
	
	private static boolean tryFill(int position) {
		
		
		
		//System.out.println("in file "+position+" rec "+g);
		if(Permanant_pointers[position] == -1){
			return false;
		}
		
		clean(records[position]);
		
		
		String line;
		int j =0;
		boolean is = false;
		try {
			while((line = readers[position].readLine()) != null){
				if(j<limit){
				 records[position][j] = new Record1(Integer.parseInt(line.substring(0,8)), line.substring(8).getBytes());				
			    }else{
					if(line != null){
						 records[position][j] = new Record1(Integer.parseInt(line.substring(0,8)), line.substring(8).getBytes());				
					 }
					is = true;
					break;
				}
				j++;  					
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
		if(!is && j == 0){
			Permanant_pointers[position] = -1;
			++marked;
			return false;			
		}
				return true;
		
	}




	private static void clean(Record1[] record1s) {
	
		for(short i=0;i<record1s.length;i++){
			record1s[i] = null;
		}
		
	}




	private static void writeOut() {
		
		
		out.flip();
        						try {
									fc.write(out);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
		out.clear();
		
	}

	public static void fillAll(byte n, String location) throws IOException {
		
		String line;
		
		for (byte i = 0; i < n; i++) {
		    line = null;   
			readers[i] = new BufferedReader(new FileReader(location + i + ".txt"));
			int j =0;
			while((line = readers[i].readLine()) != null){
				if(j<limit){
				 records[i][j] = new Record1(Integer.parseInt(line.substring(0,8)), line.substring(8).getBytes());
				 }else{
					if(line != null){
						 records[i][j] = new Record1(Integer.parseInt(line.substring(0,8)), line.substring(8).getBytes());			 
					}
					break;
				}
				j++;
			}
		}	
		
		
	
		}

	
}

class Record1 {

	int key;
	byte[] a;

	Record1(int id, byte[] d) {
		key = id;
		a = d.clone();
	}

}
