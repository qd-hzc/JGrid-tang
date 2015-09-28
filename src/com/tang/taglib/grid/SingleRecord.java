/*
Copyright 2009 Xijian (Jim) Tang
This version of Software is free for using in non-commercial applications. For commercial use please contact txijian@yahoo.com to obtain license
*/

package com.tang.taglib.grid;

import java.util.*;
import java.lang.System;
import java.lang.String;
import java.sql.*;

public class SingleRecord implements java.io.Serializable {
  private String[] fieldValues = null; 
  private String[] fieldNames = null; 
  private String[] fieldDataTypes = null;
  private int totalFields=0;
  
  //for display values
  private String[] newFieldValues = null; 
  
  //for sorting
  int index = 0;  
  
  //new added for tree grid
  int level = 0;
  boolean hasChild = false;
  String rowID = "";
  //new added end
  
  public SingleRecord(String[] recordfieldvalues)
  {
    totalFields = recordfieldvalues.length;
        
    fieldValues = new String[totalFields]; newFieldValues = new String[totalFields];
    fieldNames = new String[totalFields];
    fieldDataTypes = new String[totalFields];
    int i;
    for (i=0;i<totalFields; i++ ) {
      fieldValues[i] = recordfieldvalues[i];
      fieldNames[i] = "COL_" + String.valueOf(i);
      fieldDataTypes[i] = "STRING";
    }
    
  }
  
  public SingleRecord(String[] recordfieldvalues, String[] recordfieldnames)
  {
    totalFields = recordfieldvalues.length;
    if(recordfieldnames != null)
    {
      if (totalFields > recordfieldnames.length) totalFields = recordfieldnames.length;
    }
    fieldValues = new String[totalFields];  newFieldValues = new String[totalFields];
    fieldNames = new String[totalFields];
    fieldDataTypes = new String[totalFields];
    int i;
    for (i=0;i<totalFields; i++ ) {
      fieldValues[i] = recordfieldvalues[i];
      if(recordfieldnames != null)
        fieldNames[i] = recordfieldnames[i];
      else
        fieldNames[i] = "COL_" + String.valueOf(i);
      fieldDataTypes[i] = "STRING";
    }
    
  }

  public SingleRecord(String[] recordfieldvalues, String[] recordfieldnames, String[] recordfielddatatypes)
  {
    totalFields = recordfieldvalues.length;
    if(recordfieldnames != null)
    {
      if (totalFields > recordfieldnames.length) totalFields = recordfieldnames.length;
    }
    if(recordfielddatatypes != null)
    {
      if (totalFields > recordfielddatatypes.length) totalFields = recordfielddatatypes.length;
    }
    
    fieldValues = new String[totalFields];  newFieldValues = new String[totalFields];
    fieldNames = new String[totalFields];
    fieldDataTypes = new String[totalFields];
    int i;
    for (i=0;i<totalFields; i++ ) {
      fieldValues[i] = recordfieldvalues[i];
      if(recordfieldnames != null)
        fieldNames[i] = recordfieldnames[i];
      else
        fieldNames[i] = "COL_" + String.valueOf(i);
      
      if(recordfielddatatypes != null)
        fieldDataTypes[i] = recordfielddatatypes[i];
      else
        fieldDataTypes[i] = "STRING";
    }
    
  }
  
  public SingleRecord(String[] recordfieldvalues, String[] recordfieldnames, String[] recordfielddatatypes, String[] newrecordfieldvalues)
  {
    totalFields = recordfieldvalues.length;
    if(recordfieldnames != null)
    {
      if (totalFields > recordfieldnames.length) totalFields = recordfieldnames.length;
    }
    if(recordfielddatatypes != null)
    {
      if (totalFields > recordfielddatatypes.length) totalFields = recordfielddatatypes.length;
    }
    
    fieldValues = new String[totalFields];  newFieldValues = new String[totalFields];
    fieldNames = new String[totalFields];
    fieldDataTypes = new String[totalFields];
    int i;
    for (i=0;i<totalFields; i++ ) {
      fieldValues[i] = recordfieldvalues[i]; newFieldValues[i] = newrecordfieldvalues[i]; 
      if(recordfieldnames != null)
        fieldNames[i] = recordfieldnames[i];
      else
        fieldNames[i] = "COL_" + String.valueOf(i);
      
      if(recordfielddatatypes != null)
        fieldDataTypes[i] = recordfielddatatypes[i];
      else
        fieldDataTypes[i] = "STRING";
    }
    
  }
  
  //
  public String getFieldValue(int i)
  {
    if (i >= totalFields) return "&nbsp;";
    
    String value = "";
    if (this.newFieldValues[i]==null)
      value = this.fieldValues[i];
    else
      value = this.newFieldValues[i];
    
    if (value==null)
    {
      return "&nbsp;";
    }
    else
    {
      if (value.trim().equals(""))
        return "&nbsp;";
      else
        return (value.trim());  
    }
  }

  public String getFieldValue(String fieldname)
  {
    for (int i=0;i<totalFields; i++ ) {
      if (fieldNames[i].equalsIgnoreCase(fieldname))
      {
        return getFieldValue(i);
      }
    }
    return "&nbsp;";
  }

  public String getFieldOrigialValue(int i)
  {
    if (i >= totalFields) return null;
    return this.fieldValues[i];  
  }

  public String getFieldOrigialValue(String fieldname)
  {
    for (int i=0;i<totalFields; i++ ) {
      if (fieldNames[i].equalsIgnoreCase(fieldname))
      {
        return getFieldOrigialValue(i);
      }
    }
    return null;
  }
  
   //new added by jimt 11/02/2006
  public int getFieldIndex(String fieldname)
  {
    for (int i=0;i<totalFields; i++ ) {
      if (fieldNames[i].equalsIgnoreCase(fieldname))
      {
        return i;
      }
    }
    return -1;
  }
  
  //add end
  

  public void setFieldOrigialValue(int i, String value)
  {
    if (i >= totalFields) return;
    this.fieldValues[i] = value;  
  }

  public void setFieldOrigialValue(String fieldname, String value)
  {
    for (int i=0;i<totalFields; i++ ) {
      if (fieldNames[i].equalsIgnoreCase(fieldname))
      {
        setFieldOrigialValue(i, value);
        break;
      }
    }
  }
  
  public void setFieldValue(int i, String value)
  {
    if (i >= totalFields) return;
    this.newFieldValues[i] = value;  
  }

  public void setFieldValue(String fieldname, String value)
  {
    for (int i=0;i<totalFields; i++ ) {
      if (fieldNames[i].equalsIgnoreCase(fieldname))
      {
        setFieldValue(i, value);
        break;
      }
    }
  }
  
  //
  public String getFieldName(int i)
  {
    if (i >= totalFields) return "";
    return (this.fieldNames[i]);
  }
  
  public void setFieldName(int i, String newname)
  {
    if (i >= totalFields) return;
    this.fieldNames[i] = newname;
  }
  //
  public String getFieldDataType(int i)
  {
    if (i >= totalFields) return "";
    return (this.fieldDataTypes[i]);
  }
  
  public void setFieldDataType(int i, String newtype)
  {
    if (i >= totalFields) return;
    if (newtype == null) return;
    this.fieldDataTypes[i] = newtype.toUpperCase();
  }
  
  public String getFieldDataType(String fieldname)
  {
    for (int i=0;i<totalFields; i++ ) {
      if (fieldNames[i].equalsIgnoreCase(fieldname))
      {
        return getFieldDataType(i);
      }
    }
    return "";
  }
  //
  public int getTotalColumnNumber()
  {
    return  this.totalFields;
  }

}
  
