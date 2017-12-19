package com.gearreald.tullframe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gearreald.tullframe.columns.Column;
import com.gearreald.tullframe.columns.StringColumn;
import com.gearreald.tullframe.exceptions.DataFrameException;
import com.gearreald.tullframe.utils.ColumnType;
import com.opencsv.CSVWriter;

import net.tullco.tullutils.FileUtils;

public class TullFrame {

	private Map<String, Column> columns;
	private List<String> columnNames;
	private int currentIndex;
	private List<Integer> indices;
	
	protected TullFrame(){
	}
	protected TullFrame(String[] headers, ColumnType[] columnTypes){
		columns = new HashMap<String, Column>();
		columnNames = new ArrayList<String>();
		currentIndex = 0;
		indices = new ArrayList<Integer>();
		for(int i = 0; i < headers.length; i++){
			addEmptyColumn(headers[i], columnTypes[i]);
		}
	}
	protected void addEmptyColumn(String name, ColumnType type){
		addColumn(name, Column.getColumnFromColumnType(type));
	}
	protected void addColumn(String name, Column c){
		columns.put(name, c);
		columnNames.add(name);
	}
	protected void initializeStringColumns(String[] names){
		columns.clear();
		columnNames.clear();
		for (String name: names){
			columns.put(name, new StringColumn());
			columnNames.add(name);
		}
	}
	public void addRow(String[] valueArray){
		if(valueArray.length != columns.size()){
			throw new DataFrameException("The new row has the wrong number of elements.");
		}
		for(int i = 0; i < columnNames.size(); i++){
			Column col = columns.get(columnNames.get(i));
			col.set(currentIndex,valueArray[i]);
		}
		indices.add(currentIndex);
		currentIndex++;
		
	}
	public int countRows(){
		return indices.size();
	}
	public int countColumns(){
		return columns.size();
	}
	public void toCsv(File f) throws IOException{
		try(CSVWriter writer = FileUtils.getCSVWriter(f)){
			String[] headers = columnNames.toArray(new String[0]); 
			writer.writeNext(headers);
			for(Integer i: indices){
				String[] row = new String[headers.length];
				for (int j=0;j<headers.length;j++){
					String columnName = columnNames.get(j); 
					row[j] = columns.get(columnName).getString(i);
				}
				writer.writeNext(row);
			}
		}
	}
}
