package Dummy;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class Main_String {

	static String basePath = "C:\\Users\\ADB\\db\\" ;
	//practically 51200 but due to java objects just 18000
	//Took 202770 ms
	// cannot more than 19,000 because GC overhead limit exceeded 
	// https://stackoverflow.com/questions/1393486/error-java-lang-outofmemoryerror-gc-overhead-limit-exceeded
	static int numOfRecords = 29000;
	static Record[] arr = new Record[numOfRecords];
	//static ArrayList<Record> arr = new ArrayList<Record>(numOfRecords);
	
	public static void main(String[] args) throws IOException {
		args = null;
		long startTime = System.nanoTime();
		
		readFile(basePath+"bag2.txt");
		long endTime = System.nanoTime();
		System.out.println("Generated sublists, took "+(endTime - startTime)/1000000 + " ms"); 
        
		arr = null; 
		startTime = System.nanoTime();
		MergeSub.loadBuffers(basePath, sublists, "out2.txt");
        endTime = System.nanoTime();                
        System.out.println("Merged the sublists, took "+(endTime - startTime)/1000000 + " ms"); 
        	 	
	}

	
	
	static int ig = 0;
	static void readFile(String filePath) {
		 byte[] id = new byte[8];
		 byte[] data = new byte[92];
		 ig = 0;
		
		for (int k = 0; k < data.length; k++) {
			data[k] = ' ';
		}
		try {
			
			FileInputStream fis = new FileInputStream(filePath);
			byte buf[] = new byte[4096 * 2];
			int b;
			int tmp;
			byte charp = 0;

			main: while ((b = fis.read(buf)) != -1) {
				for (int p = 0; p < b; p++) {
					if (buf[p] == '\n') {
						if (ig >= numOfRecords) {
							sorIt();
							//System.out.println("Done sorting ); ");
							writeIt();
					        
					        ig = 0;
							//System.out.println("Sorted a sublist, :) "+sublists);
						}
						tmp = (id[0] - 48) * 10000000;
						tmp += (id[1] - 48) * 1000000;
						tmp += (id[2] - 48) * 100000;
						tmp += (id[3] - 48) * 10000;
						tmp += (id[4] - 48) * 1000;
						tmp += (id[5] - 48) * 100;
						tmp += (id[6] - 48) * 10;
						tmp += (id[7] - 48) * 1;
						arr[ig] = new Record(tmp, data  );
						//arr.add(new Record(tmp, data  ));
						ig++;
						charp = 0;

					} else {
						if (charp > 7 && charp < 93) {
							data[charp - 8] = buf[p];
							charp++;
						} else if (charp < 8) {
							id[charp++] = buf[p];
						}
					}

				}
			}
			
			if(ig != 0) {
				Arrays.sort(arr,0,ig);
				writeIt();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private static void sorIt() {
	    Arrays.sort(arr);
	}

    static byte sublists = 0;
	private static FileOutputStream fos;
	static ByteBuffer buffer = null;
	private static void writeIt() {
	
		
		   try {
			     if(buffer == null){
	        	  buffer = ByteBuffer.allocate( 100 * 2400 );    
			     }
	        	fos = new FileOutputStream( basePath + sublists + ".txt" );
	            // allocate a channel to write that file
	            FileChannel fc = fos.getChannel();
	            // allocate a buffer, as big a chunk as we are willing to handle at a pop.
	            //  Unlike the buffer on a stream, item is up to you not to overflow the buffer.
	            int tmp = 0;
	            for(int i = 0; i<ig; i++){
	                
	                buffer.put( ( arr[i].key+"" ).getBytes() );               
	            	
	                buffer.put( arr[i].a );	
	                arr[i] =  null;
	            	//arr.remove(i);
	                buffer.put("\n".getBytes());
	            	tmp++;
	            	if(tmp > 2310){
	            		buffer.flip();
	            		fc.write(buffer);
	            		//System.out.println("sent 2900 to disk ");
	            		buffer.clear();
	            	    tmp = 0;
	            	}
	            }
	            // write if something else            
	    		buffer.flip();
	    		fc.write(buffer);
	    		buffer.clear();

	            fos.close();
	            fc.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   sublists++;
		//arr.clear();
	}
	
	
	  
	
}


class Record implements Comparable<Record>{
	
	int key;
	byte[] a;
	Record(int id, byte[] d){
		key = id;
		a = d.clone();
	}
	
	 @Override
	 public int compareTo(Record o) {
		   if(o == null)
			   return 0;
		   
	       return this.key - o.key ;
	 }
}

