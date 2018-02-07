import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

public class ToAscii {
	private static String Get_file_name(){
		int maxline = 20;
		byte[] strline = new byte[maxline];
		int strlen;  

		System.out.println("Enter filename with extension (up to " + 
			maxline + " bytes):");
		try { 
			strlen = System.in.read(strline, 0, maxline); 
		}
		catch (IOException ioe) { 
			System.out.println("IOException caught in input filename"); 
			strlen = -2; 
		}
		String file_name = new String(strline, 0, strlen-1);
		return file_name;
	}

	private static void File_validate_extract(String in, String out){
		File from_file = new File(in);
		File to_file = new File(out);
		FileInputStream from_filestream = null;
		FileOutputStream to_filestream = null;
		byte[] buffer = new byte[4096];
		int bytes_read = 0;

		try {
			from_filestream = new FileInputStream(from_file);
			to_filestream = new FileOutputStream(to_file); 

			try {
				while((bytes_read = from_filestream.read(buffer)) != -1){}
				int i=0;
				while(buffer[i] != 0) {
				    to_filestream.write(buffer[i]);
				    to_filestream.write(' ');
				    to_filestream.write(String.valueOf(buffer[i]).getBytes());
				    to_filestream.write('\n');
				    ++i;
				}
			}
			catch(IOException e) { 
				System.out.println("IOException caught in reading from filestream");
			}
		}
		catch (FileNotFoundException e){
			System.out.println("FileNotFoundException caught, does the file(s) exist in this location?");
		}
		catch (SecurityException e){
			System.out.println("SecurityException caught, denied access to read file. Check security");
		}
/*		try{
			for(int i=0; i < bytes_read; ++i){
				String first_char = new String(buffer, 0, 1);
				byte[] temp = Arrays.copyOfRange(buffer, i, i+1);
				to_filestream.write(temp);
				to_filestream.flush();
			}
		}
		catch(IndexOutOfBoundsException e) { 
			System.out.println("IndexOutOfBoundsException caught in converting buff to string");
		}
		catch(IOException e) { 
			System.out.println("IOException caught in writing to filestream");
		}*/
		
		finally {
			if (from_filestream != null) {
				try { 
					from_filestream.close();
					to_filestream.close(); 
				} 
				catch(IOException e) { 
					System.out.println("IOException caught " + e.getMessage() + e.getCause());
				}
			}
			else {
				System.out.println("from_filestream not null");
			}
		}
		return;
	}
	public static void main(String[] args) {
		System.out.println("This program copy file content from one file to another, adding " +
			"ascii values for every character. First enter a file to copy from...");
		String in_name = Get_file_name();
		System.out.println("Now enter a file name to copy to...");
		String out_name = Get_file_name();

		File_validate_extract(in_name, out_name);
		return;
	}

}
