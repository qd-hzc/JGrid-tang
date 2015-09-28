/*
Copyright 2009 Xijian (Jim) Tang
This version of Software is free for using in non-commercial applications. For commercial use please contact txijian@yahoo.com to obtain license
*/

package com.tang.taglib.grid;

import java.sql.*;
import javax.sql.*;
import java.rmi.RemoteException;
import javax.naming.*;
import java.util.Vector;
import java.util.Collection;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.io.File;
import oracle.sql.*;
import oracle.jdbc.*;

public class SingleRecordCreation implements java.io.Serializable
{

  private String DATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
  private String DATEFORMAT = "yyyy-MM-dd";
  
  public SingleRecordCreation()
  {
  }
  
  public ArrayList getResultsetArrayList(PreparedStatement notamStmt) throws Exception
  {
    int totalCoulum=0;
    ResultSet rs = null;
    SimpleDateFormat formatter;
    
    rs = notamStmt.executeQuery();
    
    if (rs == null) return null;
    try{
      ArrayList mList = new ArrayList();
      int i = 0;
      ResultSetMetaData rsmd =rs.getMetaData();
      
      int columnCount = rsmd.getColumnCount();
      String[] fieldValues = new String[columnCount];
      String[] fieldNames=new String[columnCount];  
      String[] fieldNameTypes=new String[columnCount];  
      int row = 0;
      while (rs.next())
      {
        fieldValues = new String[columnCount];
        for(i=0;i<columnCount;i++)
        {
          fieldValues[i]=rs.getString(i+1);
          fieldNames[i] = rsmd.getColumnName(i+1).toUpperCase();
          fieldNameTypes[i]= checkDataType(rsmd.getColumnTypeName(i+1));
          if (rsmd.getColumnTypeName(i+1).equalsIgnoreCase("CLOB")== true)
          {
            CLOB myClob = ((OracleResultSet)rs).getCLOB(rsmd.getColumnName(i+1));
            fieldValues[i]=getCLOBContent(myClob);
          }

          if (fieldNameTypes[i].startsWith("DATE"))
          {
            java.util.Date dDate = rs.getTimestamp(i+1);
            if (dDate != null) 
            {
	      if (fieldNameTypes[i].equals("DATETIME"))
		formatter = new SimpleDateFormat(DATETIMEFORMAT);
              else
		formatter = new SimpleDateFormat(DATEFORMAT);

	      fieldValues[i] = formatter.format(dDate);
                
            }
            else
              fieldValues[i] = "";
          }
        } 
        
        mList.add(new SingleRecord(fieldValues, fieldNames, fieldNameTypes));
        row++;
      }
      return mList;
      
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return null;         
    }
    finally
    {
      if(notamStmt != null) notamStmt.close();
    }
  }

  
  public ArrayList getResultsetArrayList(Connection myConnection, String SQLState) throws Exception
  {
    
    PreparedStatement notamStmt = null;
    int totalCoulum=0;
    ResultSet rs = null;
    
    notamStmt = myConnection.prepareStatement(SQLState) ;
    rs = notamStmt.executeQuery(SQLState);
    
    if (rs == null) return null;
    try{
      ArrayList mList = new ArrayList();
      int i = 0;
      ResultSetMetaData rsmd =rs.getMetaData();
      
      int columnCount = rsmd.getColumnCount();
      String[] fieldValues = new String[columnCount];
      String[] fieldNames=new String[columnCount];  
      String[] fieldNameTypes=new String[columnCount];  
      int row = 0;
      while (rs.next())
      {
        fieldValues = new String[columnCount];
        for(i=0;i<columnCount;i++)
        {
          fieldValues[i]=rs.getString(i+1);
          fieldNames[i] = rsmd.getColumnName(i+1).toUpperCase();
          fieldNameTypes[i]= checkDataType(rsmd.getColumnTypeName(i+1));
          if (rsmd.getColumnTypeName(i+1).equalsIgnoreCase("CLOB")== true)
          {
            CLOB myClob = ((OracleResultSet)rs).getCLOB(rsmd.getColumnName(i+1));
            fieldValues[i]=getCLOBContent(myClob);
          }

          if (fieldNameTypes[i].equals("DATE"))
          {
            java.util.Date dDate = rs.getTimestamp(i+1);
            if (dDate != null) 
            {
              SimpleDateFormat formatter = new SimpleDateFormat(DATETIMEFORMAT);
              fieldValues[i] = formatter.format(dDate);
                
            }
            else
              fieldValues[i] = "";
          }
        } 
        
        mList.add(new SingleRecord(fieldValues, fieldNames, fieldNameTypes));
        row++;
      }
      return mList;
      
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return null;         
    }
  }

  public String checkDataType(String sType)
  {
    if (sType == null) return "STRING";

    String sTemp = sType.toLowerCase();
    if (sTemp.equals("int")||sTemp.equals("short")||sTemp.equals("long")||sTemp.equals("byte")||sTemp.equals("integer")||sTemp.equals("autonumber")||sTemp.equals("smallint")||sTemp.equals("mediumint")||sTemp.equals("year"))
      return "INTEGER";
    if (sTemp.equals("boolean") || sTemp.equals("bool") ||sTemp.equals("bit")||sTemp.equals("yes/no"))
      return "INTEGER";
      
    if (sTemp.equals("float")||sTemp.equals("double")||sTemp.equals("decimal")||sTemp.equals("number")||sTemp.equals("currency"))
      return "NUMBER";
    if (sTemp.equals("raw")||sTemp.equals("long raw")||sTemp.equals("fixed")||sTemp.equals("real"))
      return "NUMBER";
    if (sTemp.equals("tinyint")||sTemp.equals("bigint")||sTemp.equals("dec")||sTemp.equals("double precision")||sTemp.equals("numeric"))
      return "NUMBER";
    
    if (sTemp.equals("java.util.date")|| sTemp.equals("java.sql.timestamp")||sTemp.equals("java.sql.date"))
      return "DATETIME"; 
    if (sTemp.equals("datetime")|| sTemp.equals("date/time")||sTemp.equals("timestamp")||sTemp.equals("time"))
      return "DATETIME"; 
    if (sTemp.equals("java.sql.date")||sTemp.equals("date"))
      return "DATE"; 
    if (sTemp.startsWith("date"))
    {
      if(sTemp.length()>4)
        return "DATE"+sType.substring(4); 
      else
        return "DATE";
    }
    
    return "STRING";
  }
  
  public String getCLOBContent(CLOB myClob) throws SQLException
  {
    try{
      int chunkSize = myClob.getChunkSize();
      char [] textBuffer = new char[chunkSize];
      long clobLength = myClob.length();
      String strClob = "";
      String strTemp;
      for( long position = 1; position <= clobLength; position += chunkSize)
      {
        int charsRead = myClob.getChars(position, chunkSize, textBuffer);
        strTemp = new String(textBuffer);
        strClob = strClob + strTemp;
      }
      return strClob;
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return null;         
    }
  }

}