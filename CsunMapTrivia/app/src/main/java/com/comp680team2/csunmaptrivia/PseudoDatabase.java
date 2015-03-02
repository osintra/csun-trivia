/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * PseudoDatabase.java
 */

package com.comp680team2.csunmaptrivia;

import java.io.*;
import java.util.*;

public class PseudoDatabase
{
	private String file;
	private String[][] content;

	public PseudoDatabase(String file)
	{
		this.file = file;
		loadData();
	}

	private void loadData()
	{
		try
		{
			Scanner input = new Scanner(new FileInputStream(file));
			int width = input.nextInt();
			int height = input.nextInt();
			input.nextLine();

			content = new String[height][width];
			for (int i = 0; i < height; i++)
			{
				for (int j = 0; j < width; j++)
				{
					content[i][j] = input.nextLine();
				}
			}

			input.close();
		}
		catch (Exception e)
		{
			content = new String[0][0];
		}
	}

	public String getFile()
	{
		return file;
	}

	public int getWidth()
	{
		int result;

		if (getHeight() > 0)
		{
			result = content[0].length;
		}
		else
		{
			result = 0;
		}

		return result;
	}

	public int getHeight()
	{
		return content.length;
	}

	public String[][] getMatrix()
	{
		return content.clone();
	}

	public String[] getColumn(int x)
	{
		String[] result;

		if (x >= 0 && x < getWidth())
		{
			result = new String[getHeight()];
			for (int i = 0; i < getHeight(); i++)
			{
				result[i] = content[i][x];
			}
		}
		else
		{
			result = new String[0];
		}

		return result;
	}

	public String[] getRow(int y)
	{
		String[] result;

		if (y >= 0 && y < getHeight())
		{
			result = content[y].clone();
		}
		else
		{
			result = new String[0];
		}

		return result;
	}

	public String getValue(int x, int y)
	{
		String result;

		if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight())
		{
			result = content[y][x];
		}
		else
		{
			result = "";
		}

		return result;
	}

	public void changeFile(String newFile)
	{
		file = newFile;
		loadData();
	}

	private void saveData()
	{
		try
		{
			PrintWriter printer = new PrintWriter(new FileOutputStream(file));

			printer.println(getWidth() + " " + getHeight());

			for (String[] row : content)
			{
				for (String string : row)
				{
					printer.println(string);
				}
			}

			printer.close();
		}
		catch (Exception ignored) {}
	}

	public void changeSize(int newWidth, int newHeight)
	{
		if (newWidth < 0)
		{
			newWidth = 0;
		}
		if (newHeight < 0)
		{
			newHeight = 0;
		}

		String[][] newData = new String[newHeight][newWidth];

		for (int i = 0; i < getHeight() && i < newHeight; i++)
		{
			for (int j = 0; j < getWidth() && j < newWidth; j++)
			{
				newData[i][j] = content[i][j];
			}
		}

		for (int i = getHeight(); i < newHeight; i++)
		{
			for (int j = 0; j < getWidth() && j < newWidth; j++)
			{
				newData[i][j] = "";
			}
		}

		for (int i = 0; i < getHeight() && i < newHeight; i++)
		{
			for (int j = getWidth(); j < newWidth; j++)
			{
				newData[i][j] = "";
			}
		}

		for (int i = getHeight(); i < newHeight; i++)
		{
			for (int j = getWidth(); j < newWidth; j++)
			{
				newData[i][j] = "";
			}
		}

		content = newData;
		saveData();
	}

	public void changeWidth(int newWidth)
	{
		changeSize(newWidth, getHeight());
	}

	public void changeHeight(int newHeight)
	{
		changeSize(getWidth(), newHeight);
	}

	public void setMatrix(String[][] value)
	{
		content = value;
		saveData();
	}

	public void setColumn(int x, String[] value)
	{
		if (x >= 0 && x < getWidth() && value.length == getHeight())
		{
			for (int i = 0; i < getHeight(); i++)
			{
				content[i][x] = value[i];
			}
		}
		saveData();
	}

	public void setRow(int y, String[] value)
	{
		if (y >= 0 && y < getHeight() && value.length == getWidth())
		{
			content[y] = value;
		}
		saveData();
	}

	public void setValue(int x, int y, String value)
	{
		if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight())
		{
			content[y][x] = value;
		}
		saveData();
	}

	public void insertColumn(int x, String[] value)
	{
		if (x >= 0 && x <= getWidth() && value.length == getHeight())
		{
			changeWidth(getWidth() + 1);
			for (int i = 0; i < getHeight(); i++)
			{
				for (int j = getWidth() - 1; j > x; j--)
				{
					content[i][j] = content[i][j - 1];
				}
			}
			setColumn(x, value);
		}
	}

	public void insertRow(int y, String[] value)
	{
		if (y >= 0 && y <= getHeight() && value.length == getWidth())
		{
			changeHeight(getHeight() + 1);
			for (int i = getHeight() - 1; i > y; i--)
			{
				content[i] = content[i - 1];
			}
			setRow(y, value);
		}
	}

	public void deleteColumn(int x)
	{
		if (x >= 0 && x < getWidth())
		{
			for (int i = 0; i < getHeight(); i++)
			{
				for (int j = x; j < getWidth() - 1; j++)
				{
					content[i][j] = content[i][j + 1];
				}
			}
			changeWidth(getWidth() - 1);
		}
	}

	public void deleteRow(int y)
	{
		if (y >= 0 && y < getHeight())
		{
			for (int i = y; i < getHeight() - 1; i++)
			{
				content[i] = content[i + 1];
			}
			changeHeight(getHeight() - 1);
		}
	}

	public void switchColumn(int x1, int x2)
	{
		if (x1 >= 0 && x1 < getWidth() && x2 >= 0 && x2 < getWidth())
		{
			String[] temporary = getColumn(x1);
			setColumn(x1, getColumn(x2));
			setColumn(x2, temporary);
		}
	}

	public void switchRow(int y1, int y2)
	{
		if (y1 >= 0 && y1 < getHeight() && y2 >= 0 && y2 < getHeight())
		{
			String[] temporary = getRow(y1);
			setRow(y1, getRow(y2));
			setRow(y2, temporary);
		}
	}

	public void switchValue(int x1, int y1, int x2, int y2)
	{
		if (x1 >= 0 && x1 < getWidth() && y1 >= 0 && y1 < getHeight())
		{
			if (x2 >= 0 && x2 < getWidth() && y2 >= 0 && y2 < getHeight())
			{
				String temporary = getValue(x1, y1);
				setValue(x1, y1, getValue(x2, y2));
				setValue(x2, y2, temporary);
			}
		}
	}
}