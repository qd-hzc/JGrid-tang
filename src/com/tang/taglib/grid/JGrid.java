/*
Copyright 2009 Xijian (Jim) Tang
This version of Software is free for using in non-commercial applications. For commercial use please contact txijian@yahoo.com to obtain license
*/

package com.tang.taglib.grid;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import java.io.PrintWriter;
import java.util.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;
import java.lang.reflect.*;


public class JGrid extends TagSupport 
{
  private PageContext pageContext; 
  
  private int totalColNumber=0;
  private int totalRowNumber=0;
  private int pageRowNumber=0;
  
  private List resultSetArray = null;
  
  /*
  tag attribute: queryString
  */
  private String queryString = "";
  /*
  tag attribute: dataSource
  */
  private String dataSource = "";
  
  private String[] captions = null;
  private String[] sortFields = null;
  private String[] fieldsWidth = null;
  
  /*
  tag attribute: gridRows
  */
  private int gridRows = 15;
  /*
  tag attribute: changeRows
  */
  private boolean changeRows = true;
  /*
  tag attribute: totalSize
  */
  private int totalSize = -1;
  
  /*
  tag attribute: pageName
  */
  private String pageName = "";
  
  /*
  tag attribute: showCaption
  */
  private boolean showCaption = true;
  /*
  tag attribute: tableSetting
  */
  private String tableSetting = "<TABLE cellSpacing=1 cellPadding=1 bgcolor='white' border=1 style='WIDTH: 98%; HEIGHT: 50px'>";
  /*
  tag attribute: headClass
  */
  private String headClass = "";
  /*
  tag attribute: rowClass
  */
  private String rowClass = "";
  /*
  tag attribute: pageClass
  */
  private String pageClass = "";
  /*
  tag attribute: refresh
  */
  private boolean refresh = true;
  
  /*
  tag attribute: name
  */
  private String name = "";
  
  private String emptyText = "无数据"; //"No Record Found";
  private String altRowStyle = "BACKGROUND-COLOR: #eeeeee";
  private String border = "1";
  private String cellSpacing = "1";
  private String cellPadding = "1";
  private String bgcolor = "white";
  private String style = "WIDTH: 100%; HEIGHT: 50px";
  
  //for paging
  private int currPageNum = 0;
  private int totalPageNum = 0;
  private String pageType = "word";
  private String PageFirstText = "第一页"; //First";
  private String PageLastText = "最后一页"; //"Last";
  private String PagePrevText = "上一页"; //Prev";
  private String PageNextText = "下一页"; //Next";
  private String PageText = "页"; //Page:";
  
  private String PageNumber="";
  private String wordPageNav1="";
  private String wordPageNav2="";
  private String wordPageNav3="";
  private String numPageNav1="";
  private String numPageNav2="";
  private String numPageNav3="";
  private String changeRowsNum = "";
  
  private String sortMethod = "server";

//new add
  private String[][] headers = null;
  private String[][] columns = null;
  
  private String[] fieldNames = null;
  private String[] columnTypes = null;
  private boolean[] columnSetup = null;
  private String[] columnAttributes = null;
  private String[] columnInnerValues = null;
  private boolean treeGrid = false;
  private boolean treeIcon = true;
  private int treeStatus = 0;
  
  private int keyIndex = -1;
  private String keyFieldName = "";
  private int parentKeyIndex = -1;
  private String parentKeyFieldName = "";
 
  private boolean bPrintNavigation = true;
  
  public void setHeaders(String[][] headers)
  {
    this.headers = headers;
  }

  public String[][] getHeaders()
  {
    return this.headers;
  }

  public void setColumns(String[][] columns)
  {
    this.columns = columns;
  }

  public String[][] getColumns()
  {
    return this.columns;
  }
  
  private void setUpIniNew(){ 
    int i;  
    
    //  meaning              caption,    sort,   width,    column index/name 
    //String[][] sHeaders = {{"STATUS",   "1",   "12%",      "0"},         {"NOTIFICATION SUBJECT","1","58%", "first_line"},{"RECEIVED", null, "10%"},{"DUE DATE","1", "10%", "3"},{"CHART","1","10%","4"}};
    // caption: can be null or "", show as "&nbsp;". 
    // sort: null or "", or "0" takes no sorting, any other means sorting
    // width: can be px number or *%. null or "" takes no setting, using html default.
    // column index/name: null, "" takes sHeaders array index. Can be resultset field index/name or any other name, but must be consistent with sColumns's column index/name, otherwise empty
    //  meaning          column index/name,  column type,    attributes,      innerValues
    //String[][] sColumns =   {{"0",           "span",    "class='fontbold'",   "${0}"},     {"first_line","tree","class='fontbold'","${5}"},{"2","span","","${2}'"}};
    // column index/name: Can be resultset field index/name or any other name, must be consistent with sHeaders's column index/name, otherwise no effect.
    // if the column index or name is set null or "", that means all other columns except the columns already set have this same column setting. Hence this kind of column set should be last one.
    // column type: null or "" takes plain text. Values are : radio, checkbox, button, submit, text, a, url, image, img, select, div, span, tree
    // The value of 'url' is the same as 'a', only 'tree' is special. Any other value takes plain text.
    // attributes: be used as column type tag's attributes. Can be null or "". Can take ${x} or %{x} variable values inside. 
    // innerValues: be used as mid value between column type tags. null value or missing takes resultset field value whose index or name is specified in column index/name.
    // "" value means nothing is showed up between column type tags. Can take ${x} or %{x} variable values inside.
    //
    // variable x in ${x}, %{x} should be resultset field index/name. ${x} takes the original value of x field and %{x} takes the display value of x field.
     //String[][] sColumns =   {{"0",  "tree",    "key='1' parentKey='5' status='1' icon='0' class='font'",   "${0}"},
    // 'tree' column attributes: there are several special attributes inside: 'key', 'parentkey', status, 'icon', 'class'.
    // 'key' and 'parentkey' should be resultset field index/name. They are used to order tree. 'icon' value is 0 (no icon) or 1 (show icon), can be missing. 
    // 'status' value is 0 (collapse tree), 1 (expand tree). 
    // One grid only allowed to have one tree column. If more than one, only take the first one, rest will be neglected.
    
    this.totalColNumber=this.headers.length;
    this.captions = new String[this.totalColNumber];
    this.sortFields = new String[this.totalColNumber];
    this.fieldsWidth = new String[this.totalColNumber];
    this.fieldNames = new String[this.totalColNumber];
    this.columnTypes = new String[this.totalColNumber];
    this.columnSetup = new boolean[this.totalColNumber];
    this.columnAttributes = new String[this.totalColNumber];
    this.columnInnerValues = new String[this.totalColNumber];
    
    for(i=0; i<this.totalColNumber;i++)
    { 
      this.captions[i] = "";
      this.sortFields[i] = "";
      this.fieldsWidth[i] = "";
      this.fieldNames[i] = String.valueOf(i);
      this.columnTypes[i] = "";
      this.columnSetup[i] = false;
      this.columnAttributes[i] = "";
      this.columnInnerValues[i] = "%{self}";
    }
    
    //parse the headers string array
    for(i=0; i<this.totalColNumber;i++)
    { 
      if(this.headers[i] != null)
      {
        for(int j=0; j<headers[i].length; j++)
        {
          if(j==0 && headers[i][j] != null)
          {
            this.captions[i] = headers[i][j];
          }
          else if(j==1 && headers[i][j] != null)
          {
            this.sortFields[i] = headers[i][j];
          }
          else if(j==2 && headers[i][j] != null)
          {
            this.fieldsWidth[i] = headers[i][j];
          }
          else if(j==3 && headers[i][j] != null)
          {
            this.fieldNames[i] = headers[i][j];
          }
        }
      }
    }

    //parse the columns string array
    if(this.columns != null)
    {
      for(i=0; i<this.columns.length; i++)
      { 
        if(this.columns[i] != null)
        {
          int j = 0, index = -1;
          String temp = "";
          if(j==0 && columns[i][j] != null && !columns[i][j].equals(""))
          {
            temp = columns[i][j];
            
            //find index from fieldNames
            for(int k=0; k<this.fieldNames.length; k++)
            {
              if(this.fieldNames[k].equals(temp))
              {
                index = k;
                this.columnSetup[k] = true;
                for( j=1; j<columns[i].length; j++)
                {
                  
                  if(j==1 && columns[i][j] != null)
                  {
                    this.columnTypes[index] = columns[i][j];
                  }
                  else if(j==2 && columns[i][j] != null)
                  {
                    this.columnAttributes[index] = columns[i][j];
                  }
                  else if(j==3 && columns[i][j] != null)
                  {
                    this.columnInnerValues[index] = columns[i][j];
                  }
                }
                
              }
            }
            //for end
          }
          else
          {
            //set for all other columns, must be last column
            for(int k=0; k<this.fieldNames.length; k++)
            {
              if(!this.columnSetup[k])
              {
                
                for( j=1; j<columns[i].length; j++)
                {
                  
                  if(j==1 && columns[i][j] != null)
                  {
                    this.columnTypes[k] = columns[i][j];
                  }
                  else if(j==2 && columns[i][j] != null)
                  {
                    this.columnAttributes[k] = columns[i][j];
                  }
                  else if(j==3 && columns[i][j] != null)
                  {
                    this.columnInnerValues[k] = columns[i][j];
                  }
                }
                
              }
            }
          }
 
        }
      }
    }
    
    //check tree column and get its attributes
    for(i=0; i<this.columnTypes.length; i++)
    { 
      if(this.columnTypes[i].equalsIgnoreCase("tree"))
      {
        this.treeGrid = true;
        String attribute = this.columnAttributes[i];
        String tempAttribute = attribute.toLowerCase();
        String sTemp = getAttributeValue(tempAttribute, "key");
        if(!sTemp.equals(""))
        {
          try{
            int iIndex = Integer.parseInt(sTemp);
            this.keyIndex = iIndex;
          }
          catch(NumberFormatException ne)
          {
            //not number, should be field name, conver to index
            this.keyFieldName = sTemp;
          }
        }
        sTemp = getAttributeValue(tempAttribute, "parentkey");
        if(!sTemp.equals(""))
        {
          try{
            int iIndex = Integer.parseInt(sTemp);
            this.parentKeyIndex = iIndex;
          }
          catch(NumberFormatException ne)
          {
            //not number, should be field name, conver to index
            this.parentKeyFieldName = sTemp;
          }
        }
        
        sTemp = getAttributeValue(tempAttribute, "icon");
        if(!sTemp.equals("")) 
        {
          try{
            int iIndex = Integer.parseInt(sTemp);
            if(iIndex != 0)
              this.treeIcon = true;
            else
              this.treeIcon = false;
          }
          catch(NumberFormatException ne)
          {
            //not number, set as default value
            this.treeIcon = true;
          }
        }
        
        sTemp = getAttributeValue(tempAttribute, "status");
        if(!sTemp.equals("")) 
        {
          try{
            int iIndex = Integer.parseInt(sTemp);
            if(iIndex != 0)
              this.treeStatus = 1;
            else
              this.treeStatus = 0;
          }
          catch(NumberFormatException ne)
          {
            //not number, set as default value
            this.treeStatus = 0;
          }
        }
        break;
      }
    }
    
    
  }
  
  private static String getAttributeValue(String attributes, String attributename)
  {
    if(attributes == null || attributename == null) return "";
    String strTemp, strTemp1, strTemp2, strRest, strResult = "";
    strTemp = attributes.toLowerCase();
    attributename = attributename.toLowerCase();
    int index = strTemp.indexOf(attributename);
    int index1, index2=-1;
    boolean bSpace = false;
    if(index>-1) //find
    {
      index1 = index;
      index = strTemp.indexOf("=", index1+attributename.length());
      if(index>-1)
      {
        //check between attributename and =
        strTemp1 = strTemp.substring(index1+attributename.length(), index);
        strTemp1 = strTemp1.trim();
        if(strTemp1.length()==0)
        {
          //try to find second =
          index1 = index; // first = index
          index = strTemp.indexOf("=", index+1);
          //strTemp1 is string between =
          if(index>-1)
          {
            strTemp1 = strTemp.substring(index1+1, index);
            strRest = strTemp.substring(index);
          }
          else
          { //no second =, go to all end
            strTemp1 = strTemp.substring(index1+1);
            strRest = "";
          }
          boolean bSQuote = true;
          index = strTemp1.indexOf("'");
          if(index == -1) 
          {
            index = strTemp1.indexOf("\"");
            bSQuote = false;
          }
          if(index>-1) 
          { //found ' or "
            //check space
            strTemp2 = strTemp1.substring(0, index);
            if(strTemp2.trim().length()==0)
            {
              if(bSQuote)
                index2 = strTemp1.indexOf("'", index+1);
              else
                index2 = strTemp1.indexOf("\"", index+1);
              if(index2 > -1)
              {//found close ' or "
                strResult = strTemp1.substring(index+1, index2);
                strResult = strResult.trim();
                
              }
              else
              {
                bSpace = true;
                
              }
            }
            else
            {
              //cannot count as ' or "
              bSpace = true;
            }
          }
          else
          {//no ' or "
            bSpace = true;
            
            
          }
          
          if(bSpace)
          {
            strTemp2 = strTemp1 + "A";
            strTemp2 = strTemp2.trim(); //remove left side spaces
            index2 = strTemp2.indexOf(" ");
            if(index2 > -1)
            {//found space
              strResult = strTemp2.substring(0, index2);
              strResult = strResult.trim();
              
              index2 = strTemp1.length() + 1 - strTemp2.length() + index2;
            }
            else
            {//no space found
              strResult = strTemp1;
            }
          }
          
        }
       
      }
      
    }
    
    return strResult;
  }
  
  
  private String getParseValueNew(String statement, SingleRecord R, int colnum)
  {
    String strTemp, strField, strResult = "";
    int iIndex, iField;
    strTemp = statement;
    if(strTemp == null) return "";
    
    while(true)
    {
      iIndex = strTemp.indexOf("${");
      if(iIndex>-1)
      {
        strResult = strResult + strTemp.substring(0, iIndex); 
        strTemp = strTemp.substring(iIndex); 

        iIndex = strTemp.indexOf("}");
        if(iIndex>-1)
        {
          strField = strTemp.substring(2, iIndex); 
          strTemp = strTemp.substring(iIndex); 
          if(strTemp.length()>0) strTemp = strTemp.substring(1);
          //strResult = strResult + strField; 
          
          if(strField.equals("self"))
          {
            strField = this.fieldNames[colnum];
          }
         
          try{
            iField = Integer.parseInt(strField);
            if(iField < R.getTotalColumnNumber())
            {
              strResult = strResult + R.getFieldOrigialValue(iField);
            }
          }
          catch(NumberFormatException ne)
          {//not number, should be field name
            strResult = strResult + R.getFieldOrigialValue(strField);
          }
          
          
        }
      }
      else
      {
        strResult = strResult + strTemp; 
        break;
      }
        
    }
    
    //for display values
    strTemp = strResult;
    strResult = "";
    while(true)
    {
      iIndex = strTemp.indexOf("%{");
      if(iIndex>-1)
      {
        strResult = strResult + strTemp.substring(0, iIndex); 
        strTemp = strTemp.substring(iIndex); 

        iIndex = strTemp.indexOf("}");
        if(iIndex>-1)
        {
          strField = strTemp.substring(2, iIndex); 
          strTemp = strTemp.substring(iIndex); 
          if(strTemp.length()>0) strTemp = strTemp.substring(1);
          //strResult = strResult + strField; 
          
          if(strField.equals("self"))
          {
            strField = this.fieldNames[colnum];
          }
         
          try{
            iField = Integer.parseInt(strField);
            if(iField < R.getTotalColumnNumber())
            {
              strResult = strResult + R.getFieldValue(iField);
            }
          }
          catch(NumberFormatException ne)
          {//not number, should be field name
            strResult = strResult + R.getFieldValue(strField);
          }
          
          
        }
      }
      else
      {
        strResult = strResult + strTemp; 
        break;
      }
        
    }
    
    //System.out.println("strResult "+strResult);
    return strResult;
  }
  
  private int getRealDataArrayIndex(SingleRecord R, int id)
  {
    int iIndex = id;
    if(id < this.totalColNumber)
    {
      String strField = this.fieldNames[id];
      
      try{
        iIndex = Integer.parseInt(strField);
      }
      catch(NumberFormatException ne)
      {
        //not number, should be field name, conver to index
        iIndex = R.getFieldIndex(strField);
      }
    }
    
    return iIndex;
  }
  
  
  public ArrayList treeResultSetArray(List rsa)
  {
    ArrayList newarray = new ArrayList();
    if(rsa == null || rsa.size() == 0) return newarray;
    
    String[] keyArray = new String[10];
    int level = 0, nlevel = 0;
    String Key = "", parentKey="";
    
    String[] nameId = new String[10];
    
    SingleRecord sr = (SingleRecord)rsa.get(0);
    SingleRecord lastSR = null;
   try{ 
    //copy resultset array
    for(int i=0; i<rsa.size(); i++)
    {
      sr = (SingleRecord)rsa.get(i);
      
      if(this.keyIndex != -1)
        Key = sr.getFieldOrigialValue(this.keyIndex);
      else
        Key = sr.getFieldOrigialValue(this.keyFieldName);
      if(this.parentKeyIndex != -1)
        parentKey = sr.getFieldOrigialValue(this.parentKeyIndex);
      else
        parentKey = sr.getFieldOrigialValue(this.parentKeyFieldName);
        
      if(Key == null) Key = "";
      if(parentKey == null) parentKey = "";
      
      if(parentKey.equals("") || i==0)
      {
        level = 0; 
        nameId[level] = String.valueOf(i);
        keyArray[level++] = Key;
        
      }
      else
      {
        if(parentKey.equals(keyArray[level-1]))
        {
          nameId[level] = String.valueOf(i);
          keyArray[level++] = Key;
          lastSR.hasChild = true;
        }
        else
        {
          int j = -1;
          nlevel = level-1;
          for(j=nlevel; j>-1; j--)
          {
            if(parentKey.equals(keyArray[j]))
            {
              break;
            }
            level--;
          }
          
          //no found
          if(j == -1)
            level = 0;
          nameId[level] = String.valueOf(i);
          keyArray[level++] = Key;
        }
      }
        
        
      sr.level = level; 
      
      sr.rowID = name+"_tree";
      for(int k=0; k<level; k++) 
      {
        if(k==0)
          sr.rowID = sr.rowID + nameId[k];
        else
          sr.rowID = sr.rowID + "_" + nameId[k];
        
      }
      
      if(rsa.size() == 1)
      {
        newarray.add(sr); 
      }
      else
      {
        if(i>0 && i<=rsa.size()-1)
          newarray.add(lastSR); 
        //if it is last one
        if( i==rsa.size()-1)
          newarray.add(sr);
      }
      lastSR = sr;
    }
    
   }
   catch(Exception e)
   {
     e.printStackTrace();
   }
    return newarray;
    
  }
  
  
  //new add end
  

  public void setPageContext(PageContext p) {
    pageContext = p;
  }

  public void release() {
  }

  

 private void SetUpArrayList(){   
    try{
      String navigation = (String)pageContext.getRequest().getParameter(name + "_CurrentPage");
      String orderBy = (String)pageContext.getRequest().getParameter(name + "_ORDERBY");
      
      if (resultSetArray == null)
      { 
              
        if(navigation == null && orderBy == null)
        {
          //String datasource = "jdbc/COVERTrain2_connDS";
          if ((!queryString.equals("")) && !dataSource.equals(""))
          {
            Connection myConnection = null;
            PreparedStatement pStmt = null;
            try 
            {
              //look up the data source and get the connection to the database
              InitialContext myInitialContext = new InitialContext();
              //for different database connection (in data-sources.xml file) we can change here.  
              DataSource myDataSource = (DataSource) (myInitialContext.lookup(dataSource));
              myConnection = myDataSource.getConnection();
              pStmt = myConnection.prepareStatement(queryString);
              SingleRecordCreation src = new SingleRecordCreation();
            
              resultSetArray = src.getResultsetArrayList(pStmt);
              
              //new added fro tree 11/07/2006
              if(this.treeGrid)
                resultSetArray = treeResultSetArray(resultSetArray);
              //new added end
          
//              pageContext.getSession().setAttribute(name + "_ResultArray", resultSetArray);
            }
            catch(Exception e) 
            {
              //e.printStackTrace();
              throw new SQLException("couldn't create JDBC connection");
              
            }
            finally
            {
              try {
      
                if (pStmt != null)    pStmt.close();
                if (myConnection != null) myConnection.close();                    
              
              } catch (SQLException sqle) {}
            }
            
          }
            
           // System.out.println("close Connection");
        
        }
        else
        {
//          resultSetArray=(ArrayList)pageContext.getSession().getAttribute(name + "_ResultArray");
        }
    
      }
      
      if (resultSetArray != null)
      {
          //new added fro tree 11/07/2006
          if(this.treeGrid)
            resultSetArray = treeResultSetArray(resultSetArray);
          //new added end
          
        if (orderBy != null)  //for sorting
        { 
          int ascdesc = 1;
          String AscDesc = (String)pageContext.getSession().getAttribute(name + "_ORDERBY_"+orderBy);
          if (AscDesc != null)
          {
            ascdesc = -1 * Integer.parseInt(AscDesc);
          }
          //jimt added 11/02/2006
          SingleRecord SR = (SingleRecord)resultSetArray.get(0);
          int realIndex = getRealDataArrayIndex(SR, Integer.parseInt(orderBy));
          Collections.sort(resultSetArray, new SingleRecordComparator(realIndex, ascdesc)); 
          //Collections.sort(resultSetArray, new SingleRecordComparator(Integer.parseInt(orderBy), ascdesc)); //original one
          //added end
          
          pageContext.getSession().setAttribute(name + "_ORDERBY_"+orderBy, Integer.toString(ascdesc));
                    
        }
        else if(navigation != null) //for navaigation
        {
          
//          resultSetArray=(ArrayList)pageContext.getSession().getAttribute(name + "_ResultArray");
        }
        else if(navigation == null && orderBy == null)
        {
                    
//          pageContext.getSession().setAttribute(name + "_ResultArray", resultSetArray);
          HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
          
          String URLqueryString = request.getQueryString();
          if(URLqueryString != null) 
          {
            pageContext.getSession().setAttribute(name + "_URLQueryString", URLqueryString);
          }
       
        }
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Method called at start of tag.
   * @return SKIP_BODY
   */
  public int doStartTag() throws JspException
  {
    try
    {       
      
      this.setUpIniNew();
      
      
      this.SetUpArrayList(); 
      
      this.SetNavigation();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    return SKIP_BODY;
  }


  /**
   * Method is invoked after every body evaluation to control whether the body will be reevaluated or not.
   * @return SKIP_BODY
   */
  public int doAfterBody() throws JspException
  {
    return SKIP_BODY;
  }



  private void SetNavigation()
  {  
    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
    String pathname = request.getServletPath(); //request.getRequestURI();
    //String path = pageContext.getServletContext().getRealPath(request.getServletPath());
//    StringBuffer realpathname = request.getRequestURL();
    int pos = pathname.lastIndexOf("/");
    if (pos > -1)
    {
      pageName = pathname.substring(pos+1, pathname.length());
    }
    else
    {
      pageName = pathname;
    }
    
    /*if(pathname.startsWith("/"))
    {
      pageName = pathname.substring(1, pathname.length());
    }
    else
      pageName = pathname; */
    
//System.out.println("pathname "+pathname +" pageName "+pageName +" realpathname "+realpathname);
//System.out.println("request.getServletPath() "+request.getServletPath());
    String URLqueryString = (String)pageContext.getSession().getAttribute(name + "_URLQueryString");
    if(URLqueryString != null) 
      pageName = pageName + "?" + URLqueryString + "&";
    else
      pageName = pageName + "?";
    
    
    String strNewRowNum = pageContext.getRequest().getParameter(name + "_NewRowNum");
    if (strNewRowNum != null) {
        if(strNewRowNum.equals(""))
            gridRows = 0;
        else {
            gridRows = Integer.parseInt(strNewRowNum);
        }
        pageContext.getSession().setAttribute(name + "_gridRows", String.valueOf(gridRows));
    } else {
        strNewRowNum = (String)pageContext.getSession().getAttribute(name + "_gridRows");
        if (strNewRowNum != null) {
            if(strNewRowNum.equals(""))
                gridRows = 0;
            else {
                gridRows = Integer.parseInt(strNewRowNum);
            }
        }
    }
    
    strNewRowNum = name + "_NewRowNum=";
    int index = pageName.indexOf(strNewRowNum);
    if(index > -1) {
        String temp1 = pageName.substring(0, index);
        String temp2 = pageName.substring(index + strNewRowNum.length());
        index = temp2.indexOf("&");
        if(index> -1)
            temp2 = temp2.substring(index+1);
        else {
            index = temp2.indexOf("?");
            if(index> -1) temp2 = temp2.substring(index);
        }
        
        pageName = temp1 + temp2;
    } 
    strNewRowNum = String.valueOf(gridRows);
        
    String strCurrentPage = pageContext.getRequest().getParameter(name + "_CurrentPage");
    if (strCurrentPage==null) strCurrentPage="1";
    String strPageDirection = pageContext.getRequest().getParameter(name + "_PageDirection");
    if(strPageDirection==null) strPageDirection="1";
    if (strCurrentPage == null) 
      currPageNum = 1;
    else
      currPageNum = Integer.parseInt(strCurrentPage);

    totalPageNum = 0;
    if (resultSetArray != null)
    {
      int totalRecord = resultSetArray.size();
      //new adding for having totalSize
      if(totalSize > -1){
          totalRecord = totalSize;
      }
      //
      if (gridRows < 0) bPrintNavigation = false;
      if (gridRows <= 0) gridRows = totalRecord;

      if (totalRecord >0 )
      {
        totalPageNum = (int)(totalRecord / gridRows);
        if ((totalPageNum * gridRows) < totalRecord)
        {
          totalPageNum = totalPageNum + 1;
        }
      }
      else
        totalPageNum = 1;
        
    }
  
    PageNumber= " "+PageText+" " + this.currPageNum + " / " +  this.totalPageNum  + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
    //for page type: word
    // No previous, No First
    wordPageNav1= "<A href='" + pageName + name + "_CurrentPage=1'></A><span style='color: gray' class='"+pageClass+"'>[&nbsp;"+PageFirstText+"&nbsp;]</span>&nbsp;&nbsp;"
    + " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(currPageNum-1) + "'></A><span style='color: gray' class='"+pageClass+"'>[&nbsp;"+PagePrevText+"&nbsp;]</span>&nbsp;&nbsp;"
    + " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(currPageNum+1) + "'><U class='"+pageClass+"'>[&nbsp;"+PageNextText+"&nbsp;]</U></A>&nbsp;&nbsp;"
    + " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(totalPageNum) + "'><U class='"+pageClass+"'>[&nbsp;"+PageLastText+"&nbsp;]</U></A>" ;

    // No Next, No Last
    wordPageNav2= "<A href='" + pageName + name + "_CurrentPage=1'><U class='"+pageClass+"'>[&nbsp;"+PageFirstText+"&nbsp;]</U></A>&nbsp;&nbsp;"
    + " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(currPageNum-1) + "'><U class='"+pageClass+"'>[&nbsp;"+PagePrevText+"&nbsp;]</U></A>&nbsp;&nbsp;"
    + " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(currPageNum+1) + "'></A><span style='color: gray' class='"+pageClass+"'>[&nbsp;"+PageNextText+"&nbsp;]</span>&nbsp;&nbsp;"
    + " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(totalPageNum) + "'></A><span style='color: gray' class='"+pageClass+"'>[&nbsp;"+PageLastText+"&nbsp;]</span>" ;
    //All Have 
    wordPageNav3= "<A href='" + pageName + name + "_CurrentPage=1'><U class='"+pageClass+"'>[&nbsp;"+PageFirstText+"&nbsp;]</U></A>&nbsp;&nbsp;"
    + " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(currPageNum-1) + "'><U class='"+pageClass+"'>[&nbsp;"+PagePrevText+"&nbsp;]</U></A>&nbsp;&nbsp;"
    + " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(currPageNum+1) + "'><U class='"+pageClass+"'>[&nbsp;"+PageNextText+"&nbsp;]</U></A>&nbsp;&nbsp;"
    + " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(totalPageNum) + "'><U class='"+pageClass+"'>[&nbsp;"+PageLastText+"&nbsp;]</U></A>" ;


    //for page type: number
    
    // First
    numPageNav1= "[<A href='" + pageName + name + "_CurrentPage=1'><U>"+PageFirstText+"</U></A>&nbsp;";
    //+ " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(currPageNum-1) + "'></A><span style='color: gray'>[&nbsp;"+PagePrevText+"&nbsp;]</span>&nbsp;&nbsp;"
    for(int i=1; i<=totalPageNum; i++)
    {
      if(i==1)
        numPageNav1=numPageNav1+ " "+ String.valueOf(i) + "&nbsp;";
      else if(i<6 && i>1)
        numPageNav1=numPageNav1+ " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(i) + "'&"+name + "_PageDirection=1><U>"+String.valueOf(i)+"</U></A>&nbsp;";
      else if(i==6)
        numPageNav1=numPageNav1+ " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(i) + "&"+name + "_PageDirection=Prev'><U>"+"..."+"</U></A>&nbsp;";
    }
    numPageNav1=numPageNav1+ " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(totalPageNum) + "'><U>"+PageLastText+"</U></A>]" ;

    // Last
    numPageNav2= "[<A href='" + pageName + name + "_CurrentPage=1'><U>"+PageFirstText+"</U></A>&nbsp;";
    //+ " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(currPageNum-1) + "'>[&nbsp;"+PagePrevText+"&nbsp;]</A>&nbsp;&nbsp;"
    for(int i=totalPageNum-6; i<=totalPageNum; i++)
    {
      if(i==totalPageNum-6 && i>1)
        numPageNav2=numPageNav2+ " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(i) + "&"+name + "_PageDirection=Next'><U>"+"..."+"</U></A>&nbsp;";
      else if(i<totalPageNum && i>0)
        numPageNav2=numPageNav2+ " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(i) + "&"+name + "_PageDirection="+String.valueOf(totalPageNum-6)+"'><U>"+String.valueOf(i)+"</U></A>&nbsp;";
      else if(i==totalPageNum && i>0)
        numPageNav2=numPageNav2+ " "+String.valueOf(i)+"&nbsp;";
    }
    numPageNav2=numPageNav2+ " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(totalPageNum) + "'><U>"+PageLastText+"</U></A>]" ;

    // middle 
    int iBegan = 1;
    numPageNav3= "[<A href='" + pageName + name + "_CurrentPage=1'><U>"+PageFirstText+"</U></A>&nbsp;";
    //+ " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(currPageNum-1) + "'>[&nbsp;"+PagePrevText+"&nbsp;]</A>&nbsp;&nbsp;"
    if(strPageDirection.equals("Prev"))
      iBegan= currPageNum-6;
    else if(strPageDirection.equals("Next")) 
      iBegan= currPageNum-1;
    else
      iBegan= Integer.parseInt(strPageDirection);    
    for(int i=iBegan; i<=iBegan+7; i++)
    {
      if(i==currPageNum)
        numPageNav3=numPageNav3+ " "+String.valueOf(i)+"&nbsp;";
      else
      {
        if(i==iBegan && i>1 && i<=totalPageNum)
          numPageNav3=numPageNav3+ " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(i) + "&"+name + "_PageDirection=Next'><U>"+"..."+"</U></A>&nbsp;";
        else if(i<iBegan+7 && i>0 && i<=totalPageNum)
          numPageNav3=numPageNav3+ " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(i) + "&"+name + "_PageDirection="+String.valueOf(iBegan)+"'><U>"+String.valueOf(i)+"</U></A>&nbsp;";
        else if(i==iBegan+7 && i>0 && i<=totalPageNum)
          numPageNav3=numPageNav3+ " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(i) + "&"+name + "_PageDirection=Prev'><U>"+"..."+"</U></A>&nbsp;";
      }
    }    
    numPageNav3=numPageNav3+ " <A href='" + pageName + name + "_CurrentPage="+ String.valueOf(totalPageNum) + "'><U>"+PageLastText+"</U></A>]" ;

    //change grid rows number per page
//    changeRowsNum = "Rows Number: <input type='text' id='" + name + "_rowNum' value='" + strNewRowNum + "' onkeyup='" + name + "_limitNumber(this);' disabled='true' size='3'>&nbsp;<input type='button' id='" + name + "_btnChange' value='Change' onclick='" + name + "_changRowNum(this)'>";
    if(changeRows) {
        changeRowsNum = "行数: <input type='text' id='" + name + "_rowNum' value='" + strNewRowNum + "' onkeyup='" + name + "_limitNumber(this);' size='3'>&nbsp;<input type='button' id='" + name + "_btnChange' value='提交' onclick='" + name + "_changRowNum(this)'>";
    } else {
        changeRowsNum = "";
    }
  }


  //Print grid headings
  private void PrintLable(JspWriter out, String[] datTypes)throws JspException{
    try{
      
      //out.println("<TABLE cellSpacing=1 cellPadding=1 bgcolor='white' border=1 style='WIDTH: 98%; HEIGHT: 100px'>");
      tableSetting = "<TABLE id='tbl_"+name+"' cellSpacing='"+cellSpacing+"' cellPadding='"+cellPadding+"' bgcolor='"+bgcolor+"' border='"+border+"' style='"+style+"'>";
      out.println(tableSetting );
      if(showCaption)
      {
        out.println("<THEAD>");
        out.println("<TR id='"+name+"_row_head'>");
        //begin column
       
        //middle columns
        String orderBy = (String)pageContext.getRequest().getParameter(name + "_ORDERBY");
        if(orderBy == null) orderBy = "";
        String AscDesc = (String)pageContext.getSession().getAttribute(name + "_ORDERBY_"+orderBy);
        if (AscDesc != null)
        {
          if(AscDesc.equals("1"))
            AscDesc = "&nbsp;&uarr;";
          else
            AscDesc = "&nbsp;&darr;";
        }
        else
          AscDesc = "";
        String strCaption = "";
        for (int i=0; i < this.captions.length; i++){
          if(!columnTypes[i].equalsIgnoreCase("hidden"))
          {
            strCaption = this.captions[i];
            if (strCaption.trim().equals("")) {  strCaption = "&nbsp;"; }
            
            
              if (this.sortFields[i].equals("") || this.sortFields[i].equals("0")){
                //no sorting
                out.println("<th valign='top' class ='"+ headClass +"' width =\""+ this.fieldsWidth[i] + "\" ><span id='"+name+"_header_col"+String.valueOf(i)+"' class =''>" + strCaption + "</span></th>");
              }
              else{
                //sorting
                if(sortMethod.equalsIgnoreCase("server"))
                {
                  if(orderBy.equals(String.valueOf(i)))
                    out.println(" <th valign='top' class ='"+ headClass +"' width =\""+ this.fieldsWidth[i] + "\" ><A href='" +  pageName + name + "_ORDERBY=" + String.valueOf(i) + "'><span id='"+name+"_header_col"+String.valueOf(i)+"' class =''>" + strCaption  + "</span></A>"+AscDesc+"</TH>");
                  else
                    out.println(" <th valign='top' class ='"+ headClass +"' width =\""+ this.fieldsWidth[i] + "\" ><A href='" +  pageName + name + "_ORDERBY=" + String.valueOf(i) + "'><span id='"+name+"_header_col"+String.valueOf(i)+"' class =''>" + strCaption  + "</span></A></TH>");
                }
                else
                {
                  out.println(" <th valign='top' class ='"+ headClass +"' width =\""+ this.fieldsWidth[i] + "\"><a href='#' onclick=\""+name+"_sort_table('"+name+"',"+String.valueOf(i)+","+this.totalColNumber+","+this.pageRowNumber+");return false;\"><span id='"+name+"_header_col"+String.valueOf(i)+"' class =''>" + strCaption  + "</span></A><span id='"+name+"_sort_col"+String.valueOf(i)+"'>&nbsp;&nbsp;</span></TH>");
                }
              }
            } 
            
            
          //end column
        
        }
        out.println("</TR>");
	out.println("</THEAD>");
      }
      out.println("<TBODY>");
    }
    catch(Exception e) {
      System.out.println("Error "+  e.getMessage()  );
      e.printStackTrace() ;
    }     
  }

  //Print grid rows
   private void PrintGridRows(JspWriter out, int alternate, SingleRecord R, int RowNumber)throws JspException{
    
    String sFieldNumber="";
    String sTempName="";
        
    String strSelection = "";
    String sTemp;
    
    
    //jimt new update
    try {
      //middle columns    
      if (alternate==0){
        if(R.rowID != "")
          if(this.treeStatus == 0 && R.level>1)
            out.println(" <TR id='"+R.rowID+"' status='"+this.treeStatus+"' style='display:none;' onclick='"+name+"_JGridRowClick(this)' >");
          else
            out.println(" <TR id='"+R.rowID+"' status='"+this.treeStatus+"' onclick='"+name+"_JGridRowClick(this)'>");
        else
          out.println(" <TR id='"+name+"_row"+RowNumber+"' onclick='"+name+"_JGridRowClick(this)'>");
      }
      else {
        if(R.rowID != "")
          if(this.treeStatus == 0 && R.level>1)
            out.println("<TR id='"+R.rowID+"' status='"+this.treeStatus+"' style='display:none; "+altRowStyle+"' onclick='"+name+"_JGridRowClick(this)'>");
          else
            out.println("<TR id='"+R.rowID+"' status='"+this.treeStatus+"' style='"+altRowStyle+"' onclick='"+name+"_JGridRowClick(this)'>");
        else
          out.println("<TR id='"+name+"_row"+RowNumber+"' style='"+altRowStyle+"' onclick='"+name+"_JGridRowClick(this)'>");
      }			
      out.println(strSelection);
      String strAtrribute = "";
      String sColumnName = "";
      String sInnerValue = "";
      String sFieldName = "";
      for (int i=0; i < (this.captions.length); i++){
        
        sColumnName = name + "_" + "col" + i;
        sFieldName = name + "_row" + RowNumber+ "_" + "col" + i;
        
        strAtrribute = getParseValueNew(this.columnAttributes[i], R, i);
        sInnerValue = getParseValueNew(this.columnInnerValues[i], R, i);
        
        
          
          if(columnTypes[i].equalsIgnoreCase("radio"))
          {
            //out.println(" <TD class='"+ rowClass +"'><input type='"+columnTypes[i]+"' id ='" + sFieldName +"' name ='" + sColumnName + "' " + strAtrribute + " > " + sInnerValue +" </input></TD>");
            out.println(" <TD class='"+ rowClass +"'><input type='"+columnTypes[i]+"' id ='" + sFieldName +"' name ='" + sColumnName + "' " + strAtrribute + " /></TD>");
          }
          else if(columnTypes[i].equalsIgnoreCase("checkbox") || columnTypes[i].equalsIgnoreCase("button") || columnTypes[i].equalsIgnoreCase("submit") || columnTypes[i].equalsIgnoreCase("text") )
          {
          //  out.println(" <TD class='"+ rowClass +"'><input type='"+columnTypes[i]+"' id ='" + sFieldName +"' name ='" + sFieldName + "' " + strAtrribute + " > " + sInnerValue +" </input></TD>");
            out.println(" <TD class='"+ rowClass +"'><input type='"+columnTypes[i]+"' id ='" + sFieldName +"' name ='" + sFieldName + "' " + strAtrribute + " /></TD>");
          }
          else if(columnTypes[i].equalsIgnoreCase("a") || columnTypes[i].equalsIgnoreCase("url") )
          {
            out.println(" <TD class='"+ rowClass +"'><a id ='" + sFieldName +"' name ='" + sFieldName + "' " + strAtrribute + " > " + sInnerValue +" </a></TD>");
          }
          else if(columnTypes[i].equalsIgnoreCase("image") || columnTypes[i].equalsIgnoreCase("img") )
          {
            //out.println(" <TD class='"+ rowClass +"'><image id ='" + sFieldName +"' name ='" + sFieldName + "' " + strAtrribute + " > " + sInnerValue +" </image></TD>");
            out.println(" <TD class='"+ rowClass +"'><image id ='" + sFieldName +"' name ='" + sFieldName + "' " + strAtrribute + " /></TD>");
          }
          else if(columnTypes[i].equalsIgnoreCase("select"))
          {
            out.println(" <TD class='"+ rowClass +"'><select id ='" + sFieldName +"' name ='" + sFieldName + "' " + strAtrribute + " > " + sInnerValue +" </select></TD>");
          }
          else if(columnTypes[i].equalsIgnoreCase("div"))
          {
            out.println(" <TD class='"+ rowClass +"'><div id ='" + sFieldName +"' name ='" + sFieldName + "' " + strAtrribute + " > " + sInnerValue +" </div></TD>");
          }
          else if(columnTypes[i].equalsIgnoreCase("span"))
          {
            out.println(" <TD class='"+ rowClass +"'><span id ='" + sFieldName +"' name ='" + sFieldName + "' " + strAtrribute + " > " + sInnerValue +" </span></TD>");
          }
          else if(columnTypes[i].equalsIgnoreCase("tree"))
          {
            String sTable = "<table cellspacing='0' cellpadding='0' border='0'><tr> ";
            
            for(int j=0; j<R.level-1; j++)
            {
              sTable += "<td><image src='imgs/blank.gif' border='0'></td>";
            }
            if(R.hasChild)
            {
              if(this.treeStatus == 1)
                sTable += "<td><a href='javascript:clickTreeGridNode(\"tbl_"+name+"\", \""+R.rowID+"\")'><image id='"+R.rowID+"img' src='imgs/minus.gif' border='0'></a></td>";
              else
                sTable += "<td><a href='javascript:clickTreeGridNode(\"tbl_"+name+"\", \""+R.rowID+"\")'><image id='"+R.rowID+"img' src='imgs/plus.gif' border='0'></a></td>";
                
              if(treeIcon)
                sTable += "<td><a href='javascript:clickTreeGridNode(\"tbl_"+name+"\", \""+R.rowID+"\")'><image id='"+R.rowID+"icon' src='imgs/folder.gif' border='0'></a></td>";
            }
            else
            {
	      sTable += "<td><image src='imgs/blank.gif' border='0'></td>";
              if(treeIcon)
                sTable += "<td><image src='imgs/leaf.gif' border='0'></td>";
            }
            
            sTable += "<td ><span id ='" + sFieldName +"' name ='" + sFieldName + "' " + strAtrribute + " > " + sInnerValue +" </span></td>";
            sTable += " </tr></table>";
            out.println(" <TD class='"+ rowClass +"' style='text-align: left' >" + sTable +"</TD>");
          }

          else if(columnTypes[i].equalsIgnoreCase("hidden"))
          {
            out.println(" <input type='"+columnTypes[i]+"' id ='" + sFieldName +"' name ='" + sFieldName + "' " + strAtrribute + " />");
          }
          else
          {
            out.println(" <TD class='"+ rowClass +"'> " + sInnerValue +" </TD>");
          }
          
      }    
      

      //end column
      

      out.println("</TR>");
    }
    catch(Exception e) {
      System.out.println("Error "+  e.getMessage()  );
      e.printStackTrace() ;
    }
  } 
  
  // Print table bottom
  private void hideTotalRowColNumber(JspWriter out)throws JspException{
    String strTotalRow="";
    String strTotalCol="";
    
    strTotalRow="<input type='hidden' id='"+ name +"_TotalRowNumber' name='"+ name +"_TotalRowNumber' value='" + this.pageRowNumber +"'>";
    strTotalCol="<input type='hidden' id='"+ name +"_TotalColNumber' name='"+ name +"_TotalColNumber' value='" + this.totalColNumber +"'>";
   
    try{
      out.println(strTotalRow);
      out.println(strTotalCol);
    }
    catch(Exception e)
    {
      System.out.println("Error "+  e.getMessage()  );
      e.printStackTrace() ;
    }
  }
  
  private void PrintBottom(JspWriter out)throws JspException{
    try {    
      out.println("</TBODY>");
                  
      //new code begin
      //out.println ("<br>");
      if(bPrintNavigation) {
      
	String sAlign = "";
	if(pageClass == null || pageClass.trim().equals("")) sAlign = "center";
	out.println("<TFOOT>"); 
	out.println("<TR id='" + name + "_gridBottom'>");    
        out.println ("<TD colspan='"+(this.captions.length)+ "' class='"+ pageClass +"' align='"+sAlign+"'>");
        out.println (  PageNumber );
        
        String sNavigation = "";
        if (currPageNum == 1)
        {
          if(pageType.equalsIgnoreCase("word"))
            sNavigation = wordPageNav1;
          if(pageType.equalsIgnoreCase("number"))
            sNavigation = numPageNav1;
        }
        else if (currPageNum == totalPageNum){
          if(pageType.equalsIgnoreCase("word"))
            sNavigation = wordPageNav2;
          if(pageType.equalsIgnoreCase("number"))
            sNavigation = numPageNav2;
        }
        else if ((currPageNum > 1)&&(currPageNum < totalPageNum)){
          if(pageType.equalsIgnoreCase("word"))
            sNavigation = wordPageNav3;
          if(pageType.equalsIgnoreCase("number"))
            sNavigation = numPageNav3;
        }
        if(totalRowNumber > gridRows) {
            sNavigation = sNavigation + "&nbsp;&nbsp;" + changeRowsNum;
        } else {
            sNavigation = changeRowsNum;
        }
        out.println (sNavigation);
        out.println ("</TD>");
	out.println("</TR>");    
	out.println("</TFOOT>");    
    }
      //new code end
      out.println("</TABLE>");    
    }
    catch(Exception e) {
    }
  }


  private void PrintJavaScripts(JspWriter out, ArrayList sortArray)throws JspException{
    SingleRecord R;
    SingleRecord SR = (SingleRecord)sortArray.get(0);
    try {    
      out.println("<script type='text/javascript'>");    
     
      for (int i=0; i < this.totalColNumber; i++)
      {
          if (!this.sortFields[i].equals("")) 
          {
            //sort up
            String indexUpData = "";
            //jimt modified 2/11/2006
            int realIndex = getRealDataArrayIndex(SR, i);
            Collections.sort(sortArray, new SingleRecordComparator(realIndex, 1)); 
            //Collections.sort(sortArray, new SingleRecordComparator(i, 1));  //original one
            //modified end
            for (int j=0; j<sortArray.size(); j++)
            {
              //added for tree column sorting   
              if(columnTypes[i].equalsIgnoreCase("tree"))
              {
                if(indexUpData.equals(""))
                  indexUpData = indexUpData + "'"+String.valueOf(j)+"'";
                else
                  indexUpData = indexUpData + ",'"+String.valueOf(j)+"'";
              }
              else
              {
                R = (SingleRecord)sortArray.get(j);
                if(indexUpData.equals(""))
                  indexUpData = indexUpData + "'"+String.valueOf(R.index)+"'";
                else
                  indexUpData = indexUpData + ",'"+String.valueOf(R.index)+"'";
              }
              
            }
            //sort down
            String indexDownData = "";
            //modified by jimt 11/02/2006
            //Collections.sort(sortArray, new SingleRecordComparator(i, -1)); //original one
            Collections.sort(sortArray, new SingleRecordComparator(realIndex, -1)); 
            //modified end
            for (int j=0; j<sortArray.size(); j++)
            {
              //added for tree column sorting   
              if(columnTypes[i].equalsIgnoreCase("tree"))
              {
                if(indexDownData.equals(""))
                  indexDownData = indexDownData + "'"+String.valueOf(j)+"'";
                else
                  indexDownData = indexDownData + ",'"+String.valueOf(j)+"'";
              }
              else
              {
                R = (SingleRecord)sortArray.get(j);
                if(indexDownData.equals(""))
                  indexDownData = indexDownData + "'"+String.valueOf(R.index)+"'";
                else
                  indexDownData = indexDownData + ",'"+String.valueOf(R.index)+"'";
              }  
              
            }
            
            out.println ("  var "+name+"_sort_index"+String.valueOf(i)+"=[["+indexDownData+"], ["+indexUpData+"]]");
          }
      }
      
     
      out.println ("  var "+name+"_rows = new Array(); ");
      out.println ("  var "+name+"_style = '"+altRowStyle+"'; ");
      
      out.println ("  ");
      out.println ("  function "+name+"_initial_table() {");
       
            
      out.println ("  var tbody = document.getElementById('tbl_"+name+"').getElementsByTagName('TBODY')[0];");
      out.println ("  for (j=0;j<tbody.rows.length;j++) { "+name+"_rows[j] = tbody.rows[j]; }");
    
      out.println ("  }");
      
      out.println ("  ");
      out.println ("function "+name+"_sort_table(gridname, column, columnnum, rownum) {");
      out.println ("  var index;");
      out.println ("  var tbody = document.getElementById('tbl_'+gridname).getElementsByTagName(\"TBODY\")[0];");
      out.println ("  var span, ARROW; ");
      out.println ("  span = document.getElementById(gridname+'_sort_col'+column);");
      out.println ("  if (span.getAttribute(\"sortdir\") == 'down') {");
      out.println ("      ARROW = '&nbsp;&uarr;';");
      out.println ("      span.setAttribute('sortdir','up');");
      out.println ("      //alert('down');");
      out.println ("      for (i=0; i<rownum; i++) ");
      out.println ("      { ");
      out.println ("        index = eval(gridname + \"_sort_index\"+column+\"[0][\"+i+\"]\"); ");
      out.println ("        eval(\"tbody.appendChild(\"+ gridname+\"_rows[\"+index+\"])\");");
      out.println ("      }");
      out.println ("  } else { //alert('up');");
      out.println ("      ARROW = '&nbsp;&darr;';");
      out.println ("      span.setAttribute('sortdir','down');");
      out.println ("      ");
      out.println ("      for (i=0; i<rownum; i++) ");
      out.println ("      { ");
      out.println ("        index = eval(gridname + \"_sort_index\"+column+\"[1][\"+i+\"]\"); ");
      out.println ("        eval(\"tbody.appendChild(\"+ gridname+\"_rows[\"+index+\"])\");");
      out.println ("      }");
      out.println ("  }");
      out.println (" ");
      out.println ("  for (j=0; j<columnnum; j++) ");
      out.println ("  { ");
      out.println ("    try{");
      out.println ("      document.getElementById(gridname+'_sort_col'+j).innerHTML='&nbsp;&nbsp;';");
      out.println ("    }catch(error){ }");
      out.println ("  }");
      out.println ("  span.innerHTML = ARROW;");
      out.println ("  ");
      out.println ("  for (i=0; i<tbody.rows.length; i++)");
      out.println ("  { ");
      out.println ("    if(i%2 == 0)");
      out.println ("    { ");
      out.println ("      eval(\"tbody.rows[i].style.background = \"+ gridname+\"_style;\");");  
      out.println ("    }");
      out.println ("    else");
      out.println ("    {");
      out.println ("      eval(\"tbody.rows[i].style.background = ' ';\"); "); 
      out.println ("    }");
      out.println ("  }");
      out.println ("}");
    
      out.println ("  setTimeout('"+name+"_initial_table()', 200); ");
      
            
      out.println ("</script>");
    }
    catch(Exception e) {
    }
  }
  
  private void PrintOtherJavaScripts(JspWriter out)throws JspException{
    
    try {    
      out.println("<script type='text/javascript'>");  
      if(this.treeGrid)
      {
        out.println ("function clickTreeGridNode(tableid, rowid)");
        out.println ("  {");
        out.println ("    //note: status=1 means open, status=0 means close ");
        out.println ("    var bTrue = true;");
        out.println ("    //get row attribute");
        out.println ("    var row = document.getElementById(rowid);");
        out.println ("    var rowStatus = row.getAttribute('status');");
        out.println ("    if (rowStatus == null) //already open");
        out.println ("      bTrue = false;");
        out.println ("    else");
        out.println ("    {");
        out.println ("      if (rowStatus == '0') //already close");
        out.println ("        bTrue = true;");
        out.println ("      else  //already open");
        out.println ("        bTrue = false; ");
        out.println ("    }");
        out.println ("    ");
        out.println ("    if(bTrue)");
        out.println ("    {");
        out.println ("      try{");
        out.println ("      document.getElementById(rowid+'img').src = 'imgs/minus.gif';");
        out.println ("      document.getElementById(rowid+'icon').src = 'imgs/folder.gif';");
        out.println ("      }catch(e){}");
        out.println ("    }");
        out.println ("    else");
        out.println ("    {");
        out.println ("      try{");
        out.println ("      document.getElementById(rowid+'img').src = 'imgs/plus.gif';");
        out.println ("      document.getElementById(rowid+'icon').src = 'imgs/folder.gif';");
        out.println ("      }catch(e){}");
        out.println ("    }");
        out.println ("    ");
        out.println ("    showTreeGridChildNode(tableid, rowid, bTrue);");
        out.println ("    ");
        out.println ("    if(bTrue)");
        out.println ("      row.setAttribute('status','1');");
        out.println ("    else");
        out.println ("      row.setAttribute('status','0');");
        out.println ("  }");
        out.println ("  ");
        out.println ("  function showTreeGridChildNode(tableid, rowid, bTrue)");
        out.println ("  {");
        out.println ("    var tempId, elem = rowid + '_';");
        out.println ("    //var elems = document.getElementsByTagName('*'); // yes, wildcards do exist");
        out.println ("    var rows = eval(tableid+\".getElementsByTagName('TR')\")");
        out.println ("    for (var i=0; i<rows.length; i++) {");
        out.println ("      tempId = rows[i].id;");
        out.println ("      if(tempId!=null && tempId.length>=elem.length && tempId.substring(0,elem.length)==elem)");
        out.println ("      {");
        out.println ("        var temp = tempId.substring(elem.length, tempId.length); //alert(temp.indexOf('_'));");
        out.println ("        if(temp.indexOf('_')== -1)");
        out.println ("        {");
        out.println ("          var imgElem = document.getElementById(tempId + 'img');");
        out.println ("          if(bTrue)  //open");
        out.println ("          {");
        out.println ("            rows[i].style.display=(navigator.appName.indexOf(\"Microsoft\") > -1)?'inline':'table-row';");
        out.println ("            if(imgElem != null)");
        out.println ("            {");
        out.println ("              if (imgElem.src.indexOf('minus.gif') > -1)");
        out.println ("              {");
        out.println ("                showTreeGridChildNode(tableid, tempId, true);");
        out.println ("              }");
        out.println ("            }");
        out.println ("          }");
        out.println ("          else  //close");
        out.println ("          {");
        out.println ("            rows[i].style.display='none';");
        out.println ("            if(imgElem != null)");
        out.println ("            {");
        out.println ("              if (imgElem.src.indexOf('minus.gif') > -1) //if it is folder and already open, close his children");
        out.println ("              {");
        out.println ("                showTreeGridChildNode(tableid, tempId, false);");
        out.println ("              }");
        out.println ("            }");
        out.println ("          }");
        out.println ("        }");
        out.println ("      }");
        out.println ("    }");
        out.println ("    ");
        out.println ("  }");
      }
      
      out.println ("  var "+name+"_lastJGridRowClick = null;");
      out.println ("  var "+name+"_lastJGridRowClick_bgcolor = '';");
      out.println ("  function "+name+"_JGridRowClick(the){");
      out.println ("    if("+name+"_lastJGridRowClick) {");
      out.println ("      "+name+"_lastJGridRowClick.style.backgroundColor = "+name+"_lastJGridRowClick_bgcolor; ");
      out.println ("    }");
      out.println ("    "+name+"_lastJGridRowClick = the;");
      out.println ("    "+name+"_lastJGridRowClick_bgcolor = the.style.backgroundColor;");
      out.println ("    if(typeof(HighLiteRowColor)!='undefined') {");
      out.println ("      the.style.backgroundColor = HighLiteRowColor; ");
      out.println ("    } else {");
      out.println ("      the.style.backgroundColor = '#D8D8D8';");
      out.println ("    }");
      out.println ("  }");
      out.println ("  ");
      
      out.println ("  function "+name+"_changRowNum(the){");
//      out.println ("    if(the.value=='改变') {");
//      out.println ("      the.value='提交'; ");
//      out.println ("      document.getElementById('"+name+"_rowNum').removeAttribute('disabled'); ");
//      out.println ("    } else {");
      out.println ("      var value = document.getElementById('"+name+"_rowNum').value; ");
      out.println ("      window.location.href='" + pageName + name +"_NewRowNum='+value;");
//      out.println ("    }");
      out.println ("  }");
      out.println ("  ");
      
      out.println ("  function "+name+"_limitNumber(entry) {");
      out.println ("    var re = /^[0-9]*$/;");
      out.println ("    if (!re.test(entry.value)) {");
      out.println ("        entry.value = entry.value.replace(/[^0-9]/g, '');");
      out.println ("    }");
      out.println ("  }");
      
      out.println ("</script>");
    }
    catch(Exception e) {
    }
  
  }
  
  /**
   * Method called at end of tag.
   * @return EVAL_PAGE
   */
  public int doEndTag()
  {
    SingleRecord R;
    ArrayList sortArray = new ArrayList();

    if (resultSetArray == null) return EVAL_PAGE;
  
    int totalRecord = resultSetArray.size();
    //new adding for having totalSize
      if(totalSize > -1){
          totalRecord = totalSize;
      }
      //
    this.totalRowNumber = totalRecord;
    this.pageRowNumber = totalRecord;
        
    if (totalRecord > 0){
      String strNewRowNum = (String)pageContext.getSession().getAttribute(name + "_gridRows");
      if (strNewRowNum != null) {
        if(strNewRowNum.equals(""))
            gridRows = 0;
        else {
            gridRows = Integer.parseInt(strNewRowNum);
        }
     }
      if (gridRows < 0) bPrintNavigation = false;
      if (gridRows <= 0) gridRows = totalRecord;
      
      if(this.totalRowNumber < currPageNum * gridRows)
        this.pageRowNumber = totalRecord - (currPageNum - 1) * gridRows;
      else
        this.pageRowNumber =  gridRows;
          
      try {
        JspWriter out = pageContext.getOut();

        //get data type
        R = (SingleRecord)resultSetArray.get(0);
        String[] dataTypes = new String[this.totalColNumber];
        for(int col=0; col<this.totalColNumber; col++)
        {
          dataTypes[col] = R.getFieldDataType(col);
        }
        //print headings
        this.PrintLable(out, dataTypes);
        
        //new code begin
        int alternate = 0;
        int iIndex;
        for(iIndex=0; iIndex<resultSetArray.size(); iIndex++)
        {
          R = (SingleRecord)resultSetArray.get(iIndex);
          
          //Print out grid body
          //new adding for having totalSize
            if(totalSize > -1){
                PrintGridRows(out, alternate, R, iIndex);

                if (alternate==0){
                    alternate=1;
                }
                else if(alternate==1){
                    alternate=0;
                }

                //for client side sorting
                R.index = iIndex;
                sortArray.add(R);
                //      
            } else {
            //
                if ((iIndex >= (currPageNum - 1) * gridRows) && (iIndex < currPageNum * gridRows))
                {                
                    PrintGridRows(out, alternate, R, iIndex - (currPageNum - 1) * gridRows);

                    if (alternate==0){
                    alternate=1;
                    }
                    else if(alternate==1){
                    alternate=0;
                    }

                    //for client side sorting
                    R.index = iIndex - (currPageNum - 1) * gridRows;
                    sortArray.add(R);
                    //      
                }   
            }
            //
        }

        this.hideTotalRowColNumber(out);
        
        //print bottom navigation buttons
        this.PrintBottom(out);  
        
        boolean bsort = false;
        for (int i=0; i < this.captions.length; i++)
        {
            if (!this.sortFields[i].equals("")) 
            {
              bsort = true;
              break;
            }
        }
        
        if(bsort && !sortMethod.equalsIgnoreCase("server"))
        {
          this.PrintJavaScripts(out, sortArray);
        }
        
       // if(this.treeGrid)
        {
          this.PrintOtherJavaScripts(out);
        }
        
      }
      catch(Exception e) {
        System.out.println("Error "+  e.getMessage()  );
        e.printStackTrace();
      }
    }
    else{
      try {
        JspWriter out = pageContext.getOut();
        this.hideTotalRowColNumber(out);
        out.println("<center><span class='"+pageClass+"'>"+emptyText+"</span></center>");
      }
      catch(Exception e)
      {
        System.out.println("Error "+  e.getMessage()  );
        e.printStackTrace();
      }
    }
    return EVAL_PAGE;
  }


  public void setQueryString(String value)
  {
    queryString = value;
  }


  public String getQueryString()
  {
    return queryString;
  }


  public void setDataSource(String value)
  {
    dataSource = value;
  }

  public String getDataSource()
  {
    return dataSource;
  }


  public void setResultSetArray(ArrayList value)
  {
    resultSetArray = value;
  }
  
  public void setResultSetArray(List value)
  {
    resultSetArray = value;
  }



  public List getResultSetArray()
  {
    return resultSetArray;
  }


  public void setGridRows(int value)
  {
    gridRows = value;
  }


  public int getGridRows()
  {
    return gridRows;
  }

  public void setChangeRows(boolean value)
  {
    changeRows = value;
  }


  public boolean getChangeRows()
  {
    return changeRows;
  }
  
  public void setTotalSize(int value)
  {
    totalSize = value;
  }


  public int getTotalSize()
  {
    return totalSize;
  }
  

  public void setShowCaption(boolean value)
  {
    showCaption = value;
  }


  public boolean getShowCaption()
  {
    return showCaption;
  }


  public void setCellSpacing(String value)
  {
    cellSpacing = value;
  }
  public String getCellSpacing()
  {
    return cellSpacing;
  }
  
  public void setCellPadding(String value)
  {
    cellPadding = value;
  }
  public String getCellPadding()
  {
    return cellPadding;
  }
  
  public void setBgcolor(String value)
  {
    bgcolor = value;
  }
  public String getBgcolor()
  {
    return bgcolor;
  }
  
  public void setBorder(String value)
  {
    border = value;
  }
  public String getBorder()
  {
    return border;
  }
  
  public void setStyle(String value)
  {
    style = value;
  }
  public String getStyle()
  {
    return style;
  }

  public void setHeadClass(String value)
  {
    headClass = value;
  }


  public String getHeadClass()
  {
    return headClass;
  }


  public void setRowClass(String value)
  {
    rowClass = value;
  }


  public String getRowClass()
  {
    return rowClass;
  }

  public void setPageClass(String value)
  {
    pageClass = value;
  }

  public String getPageClass()
  {
    return pageClass;
  }

  public void setAltRowStyle(String value)
  {
    altRowStyle = value;
  }

  public String getAltRowStyle()
  {
    return altRowStyle;
  }

 

  public void setName(String value)
  {
    name = value;
  }


  public String getName()
  {
    return name;
  }
  
  public String getEmptyText(){
    return this.emptyText;
  }
  public void setEmptyText(String newEmptyText){
    this.emptyText = newEmptyText;
  }
  
  public String getPageType(){
    return this.pageType;
  }
  public void setPageType(String newPageType){
    this.pageType = newPageType;
  }

  public String getSortMethod(){
    return this.sortMethod;
  }
  public void setSortMethod(String newSortMethod){
    this.sortMethod = newSortMethod;
  }
  
  
}