import java.io.*;

public class CommandLineArgs {
	private static Integer[] validate_and_check(String[] raw_args){
		int limit_on_number = 20;
		Integer[] valid_args=new Integer[raw_args.length];
		for(int i=0; i < raw_args.length; ++i){
			try {
				valid_args[i]=Integer.parseInt(raw_args[i]);
			} 
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("Not a integer: " + raw_args[i] + " at index " + i);
			}
			if (valid_args[i] < 0) {
				throw new IllegalArgumentException("Factorial:  " + valid_args[i] +" must be non-negative.");
			}
			if (valid_args[i] > limit_on_number) { 
				throw new IllegalArgumentException("Factorial: " + valid_args[i] + " is too large, must not exceed " + limit_on_number);
			}
		}
		return valid_args;
	}

	private static int factorialize(int number) throws IllegalArgumentException {
		int factor = 1;
		for (int i=1; i<=number; i++) {
			factor = factor*i;
		}
		return factor;
	}

	public static void main(String[] args) {
		Integer[] args_as_numbers;
		try {
			args_as_numbers = validate_and_check(args);
		}
		catch(NumberFormatException e) {
			System.out.println("Could not convert args to integer array " +  e);
			return;
		}

		for (int i = 0;  i < args_as_numbers.length;  i++) {
			System.out.println("Will now compute "+ args_as_numbers[i] + "!");
			System.out.println(factorialize(args_as_numbers[i]));
		}
	}
}