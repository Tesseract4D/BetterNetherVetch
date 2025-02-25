package cn.tesseract.bnv.world.generator.terrain;

public class CrossInterpolationCell {
	private final InterpolationCell cell1;
	private final InterpolationCell cell2;
	private final int offset;

	public CrossInterpolationCell(int cellSide) {
		cell1 = new InterpolationCell(cellSide);
		cell2 = new InterpolationCell(cellSide, 16 / cellSide + 2);
		offset = cellSide >> 1;
	}

	public void setX(int x) {
		cell1.setX(x);
		cell2.setX(x + offset);
	}

	public void setY(int y) {
		cell1.setY(y);
		cell2.setY(y + offset);
	}

	public void setZ(int z) {
		cell1.setZ(z);
		cell2.setZ(z + offset);
	}

	public float get() {
		return (cell1.get() + cell2.get()) * 0.5F;
	}

	public void fill(int x, int y, int z, TerrainSDF sdf) {
		cell1.fill(x, y, z, sdf);
		cell2.fill(x - offset, y - offset, z - offset, sdf);
	}

	public boolean isEmpty() {
		return cell1.isEmpty() && cell2.isEmpty();
	}
}
