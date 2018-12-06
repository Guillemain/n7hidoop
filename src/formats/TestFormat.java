package formats;

import formats.Format.Type;

public class TestFormat {
	public static void main(String[] args) {
		FormatImpl fm = new FormatImpl(args[0]);
		fm.setFmt(Type.LINE);
		KV test;
		while ((test = fm.read()) != null){
			System.out.println(test.toString());
		}
	}
}
