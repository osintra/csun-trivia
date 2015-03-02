/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * PseudoDatabase.java
 */

package com.comp680team2.csunmaptrivia;

import java.util.Scanner;
import android.app.Activity;

public class PseudoDatabase
{
	private String[][] content;

	public PseudoDatabase(Activity activity, int fileId)
	{
		try
		{
			Scanner input = new Scanner(activity.getResources().openRawResource(fileId));
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
}