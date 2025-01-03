package cn.tesseract.bnv.world;

public class BNBExplosion {
	private static boolean isBlockSafe = false;

	public static void setBlockSafe() {
		isBlockSafe = true;
	}

	public static boolean isBlockSafe() {
		boolean value = isBlockSafe;
		isBlockSafe = false;
		return value;
	}
}
