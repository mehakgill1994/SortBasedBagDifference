package Dummy;

import java.lang.System;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

//total number of tuples in the op
//blocks used to storing output on disk
//with 186000 GC starts running and increases the computation time
public class BagDiff {
	
	static String basePath = "C:\\Users\\ADB\\db\\" ;
	
	public static String print;
	static int numOfRecords = 2500;
	static short bufferOutLimit = 0;
	static FileChannel fc;
	static ByteBuffer out =  ByteBuffer.allocate( 100 * 5000 ) ;
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		ArrayList<String> test = new ArrayList<String>();
		ArrayList<String> test2 = new ArrayList<String>();
		String temp1 = null, temp2 = null;
		boolean checkBD = false, checkEOF = false, checkinitialstate = false;
		
		try {
			FileOutputStream fileOutputStream = new FileOutputStream( basePath+"file-output.txt" );
			fc = fileOutputStream.getChannel();
		} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
		}
		        // allocate a channel to write that file
		        
		try {
			
			Scanner s = new Scanner(new File(basePath+"out1.txt"));
			Scanner s2 = new Scanner(new File(basePath+"out2.txt"));

			while (s.hasNextLine()) {

				if (test.size() == 0 && checkinitialstate) {
					test.add(temp1);
					test2.add(temp2);
					temp1 = null;
					temp2 = null;
					checkinitialstate = false;
				}
				checkinitialstate = true;
				if (test.size() < numOfRecords) {
					test.add(s.nextLine());
					if (!s.hasNext()) {
						checkEOF = true;
					}
				}

				// Handling repetition of values in second AL after block size
				if (test.size() == numOfRecords || checkEOF) {

					// checking if more values equal to last value exist
					if (!checkEOF) {
						temp1 = s.nextLine();
						while (temp1.equals(test.get(test.size() - 1))) {
							test.add(temp1);
							if (s.hasNextLine())
								temp1 = s.nextLine();
							else {
								temp1 = null;
								break;
							}
						}
					}

					if (s2.hasNextLine()) {
						temp2 = s2.nextLine();
						if (!s2.hasNext()) {
							test2.add(temp2);
						}
					}
					while (s2.hasNextLine() && (Integer.parseInt(temp2.substring(0, 8)) <= Integer
							.parseInt(test.get(test.size() - 1).substring(0, 8)))) {
						test2.add(temp2);
						temp2 = s2.nextLine();
						if (!s2.hasNext()) {
							test2.add(temp2);
						}
					}

					checkBD = true;
				}

				if (checkBD) {
					bagDifference(test, test2);
					test.clear();
					test2.clear();
					checkBD = false;
				}

			}
			writeToFile("",true);
			s.close();
			s2.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        
		long stop = System.currentTimeMillis();
		System.out.println("\nTime:" + (stop - start) + "ms" + ":)");
	}

	public static void writeToFile(String output,boolean isend) {

		
		
		if(bufferOutLimit > 4500 || isend){
			out.flip();
			try {
				fc.write(out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bufferOutLimit = 0;
			out.clear();
		}
		
		if(isend)
			return;
		
		++bufferOutLimit;
		
		out.put(output.getBytes());
		out.put("\r\n".getBytes());
		
		
		
	}

	public static void bagDifference(ArrayList<String> test, ArrayList<String> test2) {
		int[] a = new int[test.size()];
		int counter = 1;
		for (int i = 1; i < test.size() + 1; i++) {
			while (i < test.size() && test.get(i - 1).equals(test.get(i))) {
				counter++;
				i++;
			}
			a[i - counter] = counter;

			counter = 1;
		}
		if (!test2.isEmpty()) {
			a[a.length - counter] = counter;
			boolean flag = false;
			int[] b = new int[test.size()];
			int k = 0;
			int j = 0;
			for (int i = 0; i < test.size(); i++) {
				b[k] = a[i];
				// start while
				while (j < test2.size()) {
					if (Integer.parseInt(test.get(i).substring(0, 8)) >= Integer
							.parseInt(test2.get(j).substring(0, 8))) {
						if (test.get(i).equals(test2.get(j))) {
							b[k] -= 1;
							flag = true;
						}
						j++;
					} else {
						flag = false;
						break;
					}
				} 
				if (!(b[k] < 0)) {
					// System.out.println(b[k] + " * " + test.get(i));
					print = b[k] + " * " + test.get(i);
					writeToFile(print,false);
				} else {
					// System.out.println("0 * " + test.get(i));
					print = "0 * " + test.get(i);
					writeToFile(print,false);
				}
				i = i + a[i] - 1;
				k++;
			}
		} else {
			for (int x = 0; x < a.length; x++) {
				// System.out.println(a[x] + " * " + test.get(x));
				print = a[x] + " * " + test.get(x);
				writeToFile(print,false);
				x = x + a[x] - 1;
			}
		}
	}
}