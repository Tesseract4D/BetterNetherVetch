package cn.tesseract.bnv;

public class Identifier implements Comparable<Identifier> {
    public final String id;

    public Identifier(String id) {
        this.id = id;
    }

    public String toString() {
        return this.id;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other instanceof Identifier otherId) {
            if (this.toString().equals(otherId.toString())) {
                throw new IllegalStateException(String.format("Encountered a duplicate instance of Identifier %s!", this.id));
            } else {
                return false;
            }
        } else {
            return this.toString().equals(other);
        }
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public int compareTo(Identifier o) {
        return this.id.compareTo(o.id);
    }
}
