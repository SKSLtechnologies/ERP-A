
.JAVA
========

package com.erp.corrdrwn.actions;
import java.io.PrintWriter;
import java.util.ArrayList;
import com.erp.corrdrwn.beans.FaxDataEntryFormBean;
import com.erp.corrdrwn.dao.CorrDrwnDao;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.apache.struts.action.Action;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForward;

/**
 *
 * @author COMPSOFT
 */
public class MOMAgendaDataSearchAction extends org.apache.struts.action.Action {
    
    /* forward name="success" path="" */
    //private final static String SUCCESS = "success";
    private String searchResult="";
    /**
     * This is the action called from the Struts framework.
     * @param mapping The ActionMapping used to select this instance.
     * @param form The optional ActionForm bean for this request.
     * @param request The HTTP Request we are processing.
     * @param response The HTTP Response we are processing.
     * @throws java.lang.Exception
     * @return
     **/
    
    public ActionForward execute(ActionMapping mapping, ActionForm  form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        FaxDataEntryFormBean bm1=(FaxDataEntryFormBean)form;
        CorrDrwnDao dao=new CorrDrwnDao();
        HttpSession session = request.getSession();
        String msg = "";
        PrintWriter out = response.getWriter();
        DataSource ds = getDataSource(request);
        String userid = (String) session.getAttribute("userid");
       
        ArrayList b = dao.MOMAgendaDataSearch(bm1, ds);

        if (!b.isEmpty()) {
            searchResult = "MOMDataViewSucc1";
            session.setAttribute("invbatchdata", b);
        } else {
            out.println("No Record Found");
            searchResult = null;
        }

        return mapping.findForward(searchResult);

    }
}



.JAVA as DAO
=============


    public ArrayList MOMAgendaDataSearch(FaxDataEntryFormBean bm1, DataSource ds) {
        Connection conn = null;
        ArrayList ar = new ArrayList();
        PreparedStatement st = null;
        String momdesc=null;
        ResultSet rs = null;
        try {
            System.out.println("...MOM Agenda......"+bm1.getMom_agndesc()+"   "+bm1.getMom_agncd());
            Connect con1 = new Connect();
            conn = con1.connect();
            momdesc=bm1.getMom_agndesc();
            if(momdesc.length()>2)
            {   
             st = conn.prepareStatement("select PROJCD,MOM_DATE,MOM_AGNCD,MOM_AGNDASL,MOM_AGNDESC,MOM_FILEPATH1 from adm_minutestran where projcd='" + bm1.getProjcd()+"' and MOM_AGNDESC like '%" + bm1.getMom_agndesc()+"%'");
            }
            else
            {
             st = conn.prepareStatement("select PROJCD,MOM_DATE,MOM_AGNCD,MOM_AGNDASL,MOM_AGNDESC,MOM_FILEPATH1 from adm_minutestran where projcd='" + bm1.getProjcd()+"' and mom_agncd='" + bm1.getMom_agncd()+"'");
            }
            rs = st.executeQuery();

            while (rs.next()) {
                bm1= new FaxDataEntryFormBean();
                bm1.setProjcd(rs.getString(1));
                bm1.setMom_date(rs.getString(2));
                bm1.setMom_agncd(rs.getString(3));
                bm1.setMom_agndasl(rs.getString(4));
                bm1.setMom_agndesc(rs.getString(5));
                bm1.setMom_filepath1(rs.getString(6));
                ar.add(bm1);
              System.out.println("......."+rs.getString(1)+"  "+rs.getString(2)+"  "+rs.getString(3)+"  "+rs.getString(5));
            }
           
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (Exception ex) {
            }
        }
        return ar;
    }
    


JSP FILE
=========


   <title>JSP Page</title>
    </head>
    <body>
        <center>
            <% ArrayList arr=(ArrayList)session.getAttribute("invbatchdata"); 
            if(arr.size()>0)
                {
            %>
           <table width="1050" border="0">
                <tr bgcolor="#6699FF">
                    
                    <td width="80" align="left">Project Code    .</td>
                    <td width="100" align="left">MOM Date    .</td>                    
                    <td width="56" align="left">MOM Code  .</td>
                    <td width="56" align="left">Agenda Sl .</td>
                    <td width="156" align="left">Item Description       .</td>
                    <td width="176" align="left">MOM File/Path          .</td>
                    <td width="120" align="center" >Update</td>
                    <td width="150" >Delete</td>
            </tr></table>
            <c:forEach var="item" items="${invbatchdata}">
                
                <html:form action="/WorkSchdInsert" onsubmit="return insertWorkSch(this);">
                    <table width="900" align="center">
                        <tr>
                            <td align="center">                    
                               <input type="text" id="projcd" name="projcd" value="${item.projcd}" readonly="true" size="4"/>  
                            </td>
                            <td>
                                <input type="text" id="mom_date" name="mom_date" value="${item.mom_date}" readonly="true" size="5"/>                   
                            </td>
                            <td>
                                <input type="text" id="mom_agncd" name="mom_agncd" value="${item.mom_agncd}" onblur="getMomPath(${item.mom_agncd});" readonly="true" size="12"/>                   
                            </td>
                            <td>
                                <input type="text" id="mom_agndasl" name="mom_agndasl" value="${item.mom_agndasl}" readonly="true" size="14"/>                   
                            </td>
                            <td>
                                <input type="text" id="mom_agndesc" name="mom_agndesc" value="${item.mom_agndesc}"  size="54"/>                   
                            </td>
                            <td align="right"> 
                                <input type="text" id="mom_filepath1" name="mom_filepath1" value="${item.mom_filepath1}"  size="56" onblur="getMomPath(${item.mom_agncd});" readonly="true"/>                   
                            </td>
                  <%--  <td>
                        <input type="text" id="batchno" name="batchno" value="${item.batchno}" readonly="true" size="8"/>                   
                  </td>  --%>  
                            <td width="76">
                              <input type="button" value="DISPLAY" name="display" onclick="return UpdateMomFilePath(this.form)" /> 
                            </td>  
                            
                            <td>&nbsp;</td>                                             
                            <td width="76">
                                <input type="button" value="UPDATE" name="update" onclick="return UpdateMomFilePath(this.form)" />
                            </td>
                           </tr></table>
                </html:form>
                
                
            </c:forEach>
            <%  }else{  %>
         NO RECORDS FOUND!!!!!   
<%  }  %>
        </center>
    </body>
</html>



