import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

	static String letters = "abcdefghijklmnopqrstuvwxyz,. ;\"'-()@?_=+{}1234567890!^<>/*%#&:—|“”’‘[]…~";

	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter the keyword: ");
		String key = sc.nextLine();
		System.out.print("Encode (true) or Decode (false): ");
		boolean enc = sc.nextBoolean();

		String inp = "src/Docs/Input.txt";
		String oup = "src/Docs/Output.txt";

		File i = new File(enc ? inp : oup);
		FileWriter o = new FileWriter(enc ? oup : inp);
		Scanner fs = new Scanner(i);

		o.write("");

		int count = 0;

		while (fs.hasNextLine()){
			String s = fs.nextLine();
			o.append(DiffCypher(key.toLowerCase(), s.toLowerCase(), enc));
			o.append(System.lineSeparator());
			count++;
//			System.out.println(count);
		}

		o.close();

		/*
		System.out.print("Enter the keyword: ");
		String key = sc.nextLine();
		System.out.print("Enter the phrase to convert: ");
		String phrase = sc.nextLine();
		System.out.print("Encode (true) or Decode (false): ");
		boolean enc = sc.nextBoolean();

		System.out.println();

//		System.out.println(VigenereCypher(key.toLowerCase(), phrase));
		System.out.println(DiffCypher((key.toLowerCase()), (phrase.toLowerCase()), enc));
		 */
	}

	public static String RemoveSpace(String str){
		StringBuilder r = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			String curL = str.substring(i, i+1);
			if (curL.equals(" ")) continue;
			r.append(curL);
		}

		return r.toString();
	}

	public static String VigenereCypher(String key, String phrase){
		StringBuilder result = new StringBuilder();

		int locInKey = 0;

		for (int i = 0; i < phrase.length(); i++) {
			String curL = phrase.substring(i, i+1).toLowerCase();

			if (!letters.contains(curL)){
				result.append(curL);
				continue;
			}

			String curK = key.substring(locInKey%key.length(), (locInKey%key.length())+1);

			int k = letters.indexOf(curK);
			int p = letters.indexOf(curL);

			// (k + p) % 26

			if (Character.isUpperCase(phrase.charAt(i)))
				result.append(letters.substring((k + p) % 26, ((k + p) % 26) + 1).toUpperCase());
			else result.append(letters.charAt((k + p) % 26));

			locInKey++;
		}
		return result.toString();
	}

	static String compKey = "";
	static char[][] grid;

	public static String DiffCypher(String key, String phrase, boolean encode){
		StringBuilder result = new StringBuilder();
		StringBuilder pain = new StringBuilder();

		int sX = 8;
		int sY = 9;

		if(!compKey.equals(key))
			grid = new char[sX][sY];

		int curPos = 0;
		int alpha = 0;

		if (!compKey.equals(key))
			for (int i = 0; i < sX; i++) {
				for (int j = 0; j < sY; j++) {
					if (curPos < key.length())
						while (pain.indexOf(String.valueOf(key.charAt(curPos))) != -1) {
							curPos++;
							if (curPos >= key.length()) break;
						}

					if (curPos < key.length()) {
						grid[i][j] = key.charAt(curPos);
						pain.append(key.charAt(curPos));
					}
					else {
						while (pain.indexOf(letters.substring(alpha, alpha + 1)) != -1) {
							alpha++;
						}
						pain.append(letters.charAt(alpha));
						grid[i][j] = letters.charAt(alpha);
					}

	//				System.out.print(grid[i][j]);

					curPos++;
				}
	//			System.out.println();
			}

		compKey = key;

		if (phrase.length() % 2 != 0) phrase += "x";
		List<String> pair = new ArrayList<>();

		int i = 0;

		while (i < phrase.length()) {
			char curL = phrase.charAt(i);

//			if (i == phrase.length()-1) phrase += "x";

			char neL = i == (phrase.length()-1) ? 'x' : phrase.charAt(i+1);

//			if (curL == 'j') curL = 'i';

			String newP = String.valueOf(curL);

			if (curL == neL){
				pair.add(newP + (curL == 'z' ? "x" : "z"));
				i++;
			}
			else{
				pair.add(newP + neL);
				i += 2;
			}
		}

		for (String curS : pair) {
			Vector2 posA = Position(grid, curS.charAt(0));
			Vector2 posB = Position(grid, curS.charAt(1));

			if (encode) {
				if (posA.X == posB.X) {
					result.append(grid[posA.X][(posA.Y + 1) % sY]);
					result.append(grid[posB.X][(posB.Y + 1) % sY]);
				} else if (posA.Y == posB.Y) {
					result.append(grid[(posA.X + 1) % sX][posA.Y]);
					result.append(grid[(posB.X + 1) % sX][posB.Y]);
				} else {
					result.append(grid[posA.X][posB.Y]);
					result.append(grid[posB.X][posA.Y]);
				}
			}else{
				if (posA.X == posB.X) {
					result.append(grid[posA.X][(posA.Y - 1) < 0 ? (sY-1) : (posA.Y - 1)]);
					result.append(grid[posB.X][(posB.Y - 1) < 0 ? (sY-1) : (posB.Y - 1)]);
				} else if (posA.Y == posB.Y) {
					result.append(grid[(posA.X - 1) < 0 ? (sX-1): (posA.X - 1)][posA.Y]);
					result.append(grid[(posB.X - 1) < 0 ? (sX-1) : (posB.X - 1)][posB.Y]);
				} else {
					result.append(grid[posA.X][posB.Y]);
					result.append(grid[posB.X][posA.Y]);
				}
			}
		}

		return result.toString();
	}

	public static Vector2 Position(char[][] chars, char a){
//		System.out.println(a);

		int sX = 8;
		int sY = 9;

		for (int x = 0; x < sX; x++) {
			for (int y = 0; y < sY; y++) {
				if (chars[x][y] == a) return new Vector2(x, y);
			}
		}

		return new Vector2(-1, -1);
	}


	public static class Vector2{
		public int X;
		public int Y;

		public Vector2 (int x, int y){
			X = x;
			Y = y;
		}
	}
}