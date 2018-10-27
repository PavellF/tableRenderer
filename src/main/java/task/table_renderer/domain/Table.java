package task.table_renderer.domain;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Represents a table, immutable.
 * 
 * */
public class Table {

	private Optional<Settings> settings;
	private List<Map<Column, String>> rows;

	private Table(Builder builder) {
		this.settings = builder.settings;
		this.rows = builder.rows;
	}

	private Table () { }
	
	public Optional<Settings> getSettings() {
		return settings;
	}

	public List<Map<Column, String>> getRows() {
		return rows.stream().map(Collections::unmodifiableMap).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		
		if (rows.isEmpty()) {
			
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		
		final int width = settings.flatMap(Settings::getPage).map(Page::getWidth).orElse(0);
		final int height = settings.flatMap(Settings::getPage).map(Page::getHeight).orElse(0);
		final int noOfColumns = settings.map(Settings::getColumns).map(cols -> cols.size()).orElse(0);
		final int owerallColWidth = settings.map(Settings::getColumns)
				.map(cols -> cols.stream().mapToInt(Column::getWidth).sum()).orElse(0);
		final int padding = (width - owerallColWidth) / (noOfColumns * 2);
			
		final WordHeightTuple delimeter = getRowDelimeter(width);
		
		Map<Column, Deque<String>> columnsSplitted = rows.get(0).keySet()
				.stream().collect(Collectors.toMap((Column col) -> {
			return col;
		}, (Column col) -> {
			return split(col.getTitle().orElse("") ,col.getWidth());
		}));
		
		final WordHeightTuple header = getRow(padding, columnsSplitted);
		Deque<WordHeightTuple> tableRows = rows.stream().map((Map<Column, String> row) -> {
			Map<Column, Deque<String>> stringRow = new HashMap<>();
			row.forEach((Column col, String cell) -> {
				stringRow.put(col, split(cell, col.getWidth()));
			});
			return getRow(padding, stringRow);
		}).collect(Collectors.toCollection(ArrayDeque::new));
		
		
		while (!tableRows.isEmpty()) {
			int currentHeight = 0;
			builder.append(header.word);
			currentHeight += header.finalHeigth;
			
			if (currentHeight > height) {
				Logger.getGlobal().log(Level.WARNING, "Required length too small to build a table.");
				return "";
			}
			
			int remainingHeight = height - currentHeight;
			while (currentHeight < height && !tableRows.isEmpty()) {
				WordHeightTuple toAppend = tableRows.peek();
				
				if (toAppend.finalHeigth > remainingHeight) {
					toAppend = tableRows.pop();
					Logger.getGlobal().log(Level.WARNING, "Row ignored: not enough space.\n" + toAppend.word);
					continue;
				}
				
				if (toAppend.finalHeigth >= height - currentHeight) {
					break;
				}
				
				toAppend = tableRows.pop();
				builder.append(delimeter.word);
				builder.append(toAppend.word);
				currentHeight =  toAppend.finalHeigth + delimeter.finalHeigth + currentHeight;
			}
			
			builder.append("~\n");
		}
		
		return builder.toString();
	}
	
	
	private WordHeightTuple getRow(int padding, Map<Column, Deque<String>> columnsSplitted) {
		
		final int maxHeigth = columnsSplitted.values().stream()
				.mapToInt(h -> h.size()).max().orElse(0);
		final StringBuilder output = new StringBuilder(128);
			
		for (int i = 0; i < maxHeigth; i++) {
			output.append('|');
			
			columnsSplitted.forEach((Column col, Deque<String> cell) -> {
				for (int p = 0; p < padding; p++) {
					output.append(' ');
				}
				
				int appendSpaces = padding;
				
				if (!cell.isEmpty()) {
					String toAppend = cell.pollLast();
					output.append(toAppend);
					appendSpaces += col.getWidth() - toAppend.length();
				} else {
					appendSpaces += col.getWidth();
				}
				
				for (int p = 0; p < appendSpaces; p++) {
					output.append(' ');
				}
					
				output.append('|');
				
			});
			output.append('\n');
		}
		
		return new WordHeightTuple(output.toString(), maxHeigth);
	}
	
	private WordHeightTuple getRowDelimeter(int width) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < width; i++) {
			builder.append('-');
		}
		builder.append('\n');
		return new WordHeightTuple(builder.toString(), 1);
	}
	
	private static class WordHeightTuple {
		
		public WordHeightTuple(String word, int finalHeigth) {
			this.word = word;
			this.finalHeigth = finalHeigth;
		}
		
		final String word;
		final int finalHeigth;
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("WordHeigthTuple [word=");
			builder.append(word);
			builder.append(", finalHeigth=");
			builder.append(finalHeigth);
			builder.append("]");
			return builder.toString();
		}
		
	} 
	
	/**
	 * Splits string by space when possible, minimal possible width is 1.
	 * @return collection with string as a lines, length represents height.
	 *  */
	private Deque<String> split(String letters, int maxWidth) {
		
		if (maxWidth < 1) {
			throw new IllegalArgumentException("Max width must be greater than 0.");
		}
		
		int lastSpace = -1;
		String currentSeq = letters;
		Deque<String> splitted = new ArrayDeque<>(1);
		splitted.add(letters);
		
		for (int i = 0; i < currentSeq.length(); i++) {
			
			if (currentSeq.charAt(i) == ' ') {
				lastSpace = i;
			}	
			
			if (i == maxWidth) {
				
				String toSplit = splitted.pop();
				
				if (lastSpace == -1) {
					splitted.push(toSplit.substring(0, i));
					splitted.push(toSplit.substring(i, toSplit.length()));
				} else {
					splitted.push(toSplit.substring(0, lastSpace));
					splitted.push(toSplit.substring(lastSpace + 1, toSplit.length()));
					lastSpace = -1;
				}
				
				currentSeq = splitted.peek();
				i = 0;
			}
			
		}
		//normalize strings
		splitted.stream().map((String s) -> {
			StringBuilder builder = new StringBuilder(s);
			for (int i = 0; i < maxWidth - s.length(); i++) {
				builder.append(' ');
			}
			return builder.toString();
		}).collect(Collectors.toCollection(ArrayDeque::new));
		
		return splitted;
	}
		
	/**
	 * Creates builder to build {@link Table}.
	 * @return created builder
	 */
	public static ISettingsStage builder() {
		return new Builder();
	}

	public interface ISettingsStage {
		public IRowsStage withSettingsFromXML(File location)  throws IOException;
	}

	public interface IRowsStage {
		public IBuildStage withRowsFromTSV(File location) throws IOException;
	}

	public interface IBuildStage {
		public Table build();
	}

	/**
	 * Builder to build {@link Table}.
	 */
	public static final class Builder
			implements ISettingsStage, IRowsStage, IBuildStage {
		private Optional<Settings> settings = Optional.empty();
		private List<Map<Column, String>> rows = Collections.emptyList();

		private Builder() {
		}

		@Override
		public IRowsStage withSettingsFromXML(File location) throws IOException {
			
			if (location == null || !location.exists()) {
				throw new IllegalArgumentException("Xml file can not be found");
			}
			
			XmlMapper mapper = new XmlMapper();
			settings = Optional.ofNullable(mapper.readValue(location, Settings.class));
			return this;
		}

		@Override
		public IBuildStage withRowsFromTSV(File location) throws IOException {
			
			if (location == null || !location.exists()) {
				throw new IllegalArgumentException("TVS file can not be found");
			}
			
			TsvParserSettings parserSettings = new TsvParserSettings();
			parserSettings.getFormat().setLineSeparator("\n");
			
			TsvParser parser = new TsvParser(parserSettings);
			List<Column> columns = settings.orElseThrow(() -> new IllegalArgumentException("Settings must not be null"))
								.getColumns();
			
			rows = parser.parseAll(location, Charset.forName("UTF-16")).stream()
					.map(row -> Arrays.asList(row).iterator())
					.map((Iterator<String> values) -> {
						
						Map<Column, String> columnValueMap = new HashMap<>();
						Iterator<Column> icolumns = columns.iterator();
						
						while (icolumns.hasNext() && values.hasNext()) {
							columnValueMap.put(icolumns.next(), values.next());
						}
						
						if (icolumns.hasNext() || values.hasNext()) {
							Logger.getGlobal().log(Level.WARNING, "Inconsistency has found.");
						}
						
						return columnValueMap;
					}).collect(Collectors.toList());
			
			return this;
		}

		@Override
		public Table build() {
			return new Table(this);
		}
	}

}
