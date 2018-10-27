package task.table_renderer.domain;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Table settings. Immutable.
 * */
public class Settings {

	private Page page;
	private List<Column> columns;
	
	public Settings() { }
	
	public Optional<Page> getPage() {
		return Optional.ofNullable(page);
	}
	
	public List<Column> getColumns() {
		return Collections.unmodifiableList(columns);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Settings [page=");
		builder.append(page);
		builder.append(", columns=");
		builder.append(columns);
		builder.append("]");
		return builder.toString();
	}
	
	
}
