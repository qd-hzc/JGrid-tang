/*
Copyright 2009 Xijian (Jim) Tang
This version of Software is free for using in non-commercial applications. For commercial use please contact txijian@yahoo.com to obtain license
*/

package com.tang.taglib.grid;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.Object; 
import java.lang.reflect.*; 
import java.util.Comparator; 
import java.text.ParseException;

public class SingleRecordComparator extends Object implements Comparator { 
  private int column = 0; 
  private int descAscIndicator = 1; 
  private static final String DATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss"; 
  private static final String DATEFORMAT = "yyyy-MM-dd";
  public static final int ASCENDING = 1; 
  public static final int DESCENDING = -1; 
    	 
  public SingleRecordComparator(int column, int descAscIndicator) { 
    this.descAscIndicator = descAscIndicator; 
    this.column = column;
  } 

               	 
  public int compare(Object o1, Object o2) { 
    SingleRecord sr1 = (SingleRecord) o1;
    SingleRecord sr2 = (SingleRecord) o2;

    if (column >= sr1.getTotalColumnNumber()) return 0;
    //new added by jimt 11/02/2006
    if (column <= -1) return 0;
    //add end

    String dataType = sr1.getFieldDataType(column);

    String strTemp1 = sr1.getFieldOrigialValue(column);
    String strTemp2 = sr2.getFieldOrigialValue(column);
    if( (strTemp1 == null || strTemp1.equals("")) && (strTemp2 != null && !strTemp2.equals("")))
    {
      return -1* descAscIndicator;
    }
    if( (strTemp1 != null && !strTemp1.equals("")) && (strTemp2 == null || strTemp2.equals("")))
    {
      return 1* descAscIndicator;
    }
    if( (strTemp1 == null || strTemp1.equals("")) && (strTemp2 == null || strTemp2.equals("")))
    {
      return 0;
    }
    try
    {  
      if( dataType.startsWith("DATE"))
      {
        String sFormat ="";
        int begPos = dataType.indexOf("(");
        int endPos = dataType.indexOf(")");

        if ((begPos != -1) && (endPos != -1))
        {
          sFormat = dataType.substring(begPos+1, endPos);                    
        }
        
        SimpleDateFormat formatter = null;
        if(!sFormat.equals(""))
          formatter = new SimpleDateFormat(sFormat);
        else {
	  if(dataType.equals("DATETIME"))
	    formatter = new SimpleDateFormat(DATETIMEFORMAT);
	  else
	    formatter = new SimpleDateFormat(DATEFORMAT);
	}
	
        Date Date1 = formatter.parse(sr1.getFieldOrigialValue(column));
        Date Date2 = formatter.parse(sr2.getFieldOrigialValue(column));
        return Date1.compareTo(Date2)* descAscIndicator;
      }
    
      if( dataType.equalsIgnoreCase("NUMBER") || dataType.equalsIgnoreCase("INTEGER"))
      { 
      
        Double Double1 = Double.valueOf(sr1.getFieldOrigialValue(column));
        Double Double2 = Double.valueOf(sr2.getFieldOrigialValue(column));
    
        return Double1.compareTo(Double2)* descAscIndicator;
      }
    
      String String1 = sr1.getFieldOrigialValue(column);
      String String2 = sr2.getFieldOrigialValue(column);
      return String1.compareTo(String2)* descAscIndicator;
    }
    catch (Exception e) 
    {
      System.out.println("Error message = " + e.getMessage());
      //e.printStackTrace();
      return 0;
    } 
  } 
                            	 
  public boolean equals(Object obj) { 
    return this.equals(obj); 
  } 
}
