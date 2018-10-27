package task.table_renderer.domain;

import java.util.Optional;

/**
 * Table column. Immutable.
 * */
public class Column {

	private String title;
	private int width;
	
	public Column() { }
	
	public Optional<String> getTitle() {
		return Optional.ofNullable(title);
	}
	
	public int getWidth() {
		return width;
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Column [title=");
		builder.append(title);
		builder.append(", width=");
		builder.append(width);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Column other = (Column) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (width != other.width)
			return false;
		return true;
	}
	
	
}
