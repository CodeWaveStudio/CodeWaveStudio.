package com.nmmc.property.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.print.attribute.standard.Media;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jxl.demo.XML;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.type.NullableType;
import org.jbpm.api.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.RangeValueIterator.Element;
import com.lowagie.text.Document;
import com.nmmc.common.model.Address;
import com.nmmc.common.model.CollectionCenter;
import com.nmmc.common.model.DocumentMaster;
import com.nmmc.common.model.NodeMaster;
import com.nmmc.common.model.Ward;
import com.nmmc.common.search.CollectionCenterSearch;
import com.nmmc.common.service.CollectionCenterService;
import com.nmmc.common.service.DocumentMasterService;
import com.nmmc.common.service.EmployeeService;
import com.nmmc.common.service.FinancialYearService;
import com.nmmc.common.service.NodeMasterService;
import com.nmmc.common.service.PlotService;
import com.nmmc.common.service.PlotTypeService;
import com.nmmc.common.service.SectorService;
import com.nmmc.common.service.WardService;
import com.nmmc.common.utils.CommonRequestUtils;
import com.nmmc.common.utils.CommonTokenUtils;
import com.nmmc.common.utils.CommonUtils;
import com.nmmc.common.utils.Constants;
import com.nmmc.common.utils.SendSMS;
import com.nmmc.common.utils.SessionUser;
import com.nmmc.dms.service.DmsDocumentCheckListService;
import com.nmmc.property.dao.impl.PropertyDAOImpl;
import com.nmmc.property.model.AmnestyRegistration;
import com.nmmc.property.model.Bill;
import com.nmmc.property.model.CloseProperty;
import com.nmmc.property.model.Collection;
import com.nmmc.property.model.CollectionBuffer;
import com.nmmc.property.model.CollectionDetails;
import com.nmmc.property.model.DemandCycle;
import com.nmmc.property.model.Ledger;
import com.nmmc.property.model.LedgerCalDetails;
import com.nmmc.property.model.LedgerDetails;
import com.nmmc.property.model.MobileNumberChange;
import com.nmmc.property.model.Owner;
import com.nmmc.property.model.Property;
import com.nmmc.property.model.PropertyAssessment;
import com.nmmc.property.model.PropertyDetails;
import com.nmmc.property.model.PropertyDocCheckList;
import com.nmmc.property.model.WardTransfer;
import com.nmmc.property.search.CollectionSearch;
import com.nmmc.property.search.OwnerSearch;
import com.nmmc.property.search.PropertySearch;
import com.nmmc.property.service.BillService;
import com.nmmc.property.service.BuildingTypeService;
import com.nmmc.property.service.CollectionBufferService;
import com.nmmc.property.service.CollectionDetailsService;
import com.nmmc.property.service.CollectionService;
import com.nmmc.property.service.DemandCycleService;
import com.nmmc.property.service.LedgerService;
import com.nmmc.property.service.OwnerGroupService;
import com.nmmc.property.service.OwnerService;
import com.nmmc.property.service.PropertyAssessmentService;
import com.nmmc.property.service.PropertyService;
import com.nmmc.property.service.SurveyPropertyService;
import com.nmmc.property.utils.CoreConstants;
//import com.nmmc.quartz.job.MailSenderBillJob;
import com.nmmc.workflow.service.WorkflowProcessDetailsService;
import com.nmmc.workflow.service.WorkflowService;

/**
 * <p>Title: PropertyController.java </p>

 * <p>Description: This is a  Property controller class for controlling Property related actions</p>

 * @see com.nmmc.property.model.Property

 * Copyright (c) 2008 for Mars Telecom India Pvt Ltd

 * @version: 1.0

 * @author : eGovernance development team <Mars Telecom Systems Pvt Ltd>
 *
 */

public class PropertyController extends MultiActionController implements InitializingBean
{

    private static String const_OrderBy = "OrderBy";

    private static String const_SortBy = "SortBy";

    private static Log log = LogFactory.getLog(PropertyController.class);

    private PropertyService propertyService;
    
    private SurveyPropertyService surveyPropertyService;

    private SectorService sectorService;

    private WardService wardService;

    private NodeMasterService nodeMasterService;

    private EmployeeService employeeService;
    
    private  CollectionDetailsService  collectionDetailsService;

    private DocumentMasterService documentMasterService;

    private DmsDocumentCheckListService dmsDocumentCheckListService;

    private OwnerService ownerService;
    
    private OwnerGroupService ownerGroupService;

    private PlotService plotService;

    private BuildingTypeService buildingTypeService;
    
    private BillService billService;
    
    private PropertyAssessmentService propertyAssessmentService;
    
    private LedgerService ledgerService;
    
    private DemandCycleService demandCycleService;
    
    private CollectionService collectionService;
    
    private WorkflowProcessDetailsService workflowProcessDetailsService;
    
    private WorkflowService workflowService;
    
    private RestTemplate restTemplate;
    
    private TaskService taskService;
    private CollectionBufferService collectionBufferService;
    private String DMS_ENTITY_NAME = CoreConstants.PROP_REGISTRATION_NEW;
	private String DMS_ENTITY_FOLDER_PATH = CoreConstants.PROP_REGISTRATION_NEW_PATH;
	 private static String txnid="123";
	 private FinancialYearService financialYearService;
    public FinancialYearService getFinancialYearService() {
		return financialYearService;
	}

	public void setFinancialYearService(FinancialYearService financialYearService) {
		this.financialYearService = financialYearService;
	}


	/**
     * @param propertyService
     *            sets the PropertyService object.
     */
	 
	 
	 private CollectionCenterService collectionCenterService;
	    
	    public CollectionCenterService getCollectionCenterService() {
			return collectionCenterService;
		}

		public void setCollectionCenterService(
				CollectionCenterService collectionCenterService) {
			this.collectionCenterService = collectionCenterService;
		}
		
		
	 
	 public CollectionDetailsService getCollectionDetailsService() {
			return collectionDetailsService;
		}

		public void setCollectionDetailsService(
				CollectionDetailsService collectionDetailsService) {
			this.collectionDetailsService = collectionDetailsService;
		}

	public CollectionBufferService getCollectionBufferService() {
			return collectionBufferService;
		}

		public void setCollectionBufferService(
				CollectionBufferService collectionBufferService) {
			this.collectionBufferService = collectionBufferService;
		}
		
		
    public void setPropertyService(PropertyService propertyService)
    {
	this.propertyService = propertyService;
    }

    /**
     * @param sectorService
     *            sets the PropertyService object.
     */
    public void setSectorService(SectorService sectorService)
    {
	this.sectorService = sectorService;
    }

    /**
     * @param wardService
     *            sets the PropertyService object.
     */
    public void setWardService(WardService wardService)
    {
	this.wardService = wardService;
    }

    /**
     * @param employeeService
     *            sets the PropertyService object.
     */
    public void setEmployeeService(EmployeeService employeeService)
    {
	this.employeeService = employeeService;
    }

    /**
     *
     * @param documentMasterService
     */
    public void setDocumentMasterService(DocumentMasterService documentMasterService) {
		this.documentMasterService = documentMasterService;
	}

	/**
	 *
	 * @param ownerService
	 */
    public void setOwnerService(OwnerService ownerService) {
		this.ownerService = ownerService;
	}

	public void setNodeMasterService(NodeMasterService nodeMasterService) {
		this.nodeMasterService = nodeMasterService;
	}

	public void setPlotService(PlotService plotService) {
		this.plotService = plotService;
	}

	public void setBuildingTypeService(BuildingTypeService buildingTypeService) {
		this.buildingTypeService = buildingTypeService;
	}

	public void setDmsDocumentCheckListService(
			DmsDocumentCheckListService dmsDocumentCheckListService) {
		this.dmsDocumentCheckListService = dmsDocumentCheckListService;
	}
	
	public void setBillService(BillService billService) {
		this.billService = billService;
	}

	public void setPropertyAssessmentService(
			PropertyAssessmentService propertyAssessmentService) {
		this.propertyAssessmentService = propertyAssessmentService;
	}

	public void setLedgerService(LedgerService ledgerService) {
		this.ledgerService = ledgerService;
	}

	public void setDemandCycleService(DemandCycleService demandCycleService) {
		this.demandCycleService = demandCycleService;
	}

	public void setCollectionService(CollectionService collectionService) {
		this.collectionService = collectionService;
	}

	public void setWorkflowProcessDetailsService(
			WorkflowProcessDetailsService workflowProcessDetailsService) {
		this.workflowProcessDetailsService = workflowProcessDetailsService;
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
	
	public void setOwnerGroupService(OwnerGroupService ownerGroupService) {
		this.ownerGroupService = ownerGroupService;
	}

	public void afterPropertiesSet() throws Exception
    {

    }

	
    public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
     * This method is to bind the date objects in the specifed format.
     *
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception
    {
	SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
	df.setLenient(true);
	binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(df, true));
    }

    /**
     *
     * @param request
     *            current HTTP request
     * @param response
     *            current HTTP response
     * @return a ModelAndView to render the response. This method is for saving
     *         Property entries to the database.
     */
    public ModelAndView saveProperty(HttpServletRequest request, HttpServletResponse response, Property property) throws ServletException
    {
		HttpSession session = request.getSession();
		if (log.isDebugEnabled()){
		    log.debug("Invoking saveProperty");
		}
		
		Long seq=propertyService.getPropertyLastSequnceNo();
		seq=(seq==0)?1l:seq+1;
		property.setProp_application_no(seq);
		property.setAssessmentrefno(property.getAssessmentrefno());
		property.setResidential_usage_percentage(property.getResidential_usage_percentage());
		property.setProperty_maintype(property.getProperty_maintype());
		property.setProperty_subtype(property.getProperty_subtype());
		if (property != null){

			 if (property.getOwner() != null || (request.getParameter("ownerId") != null &&
			    		request.getParameter("ownerId").length() > 0)){
			    	long ownerId = Long.parseLong(request.getParameter("ownerId"));
			    	property.setOwner(ownerService.get(ownerId));
			 }

			 if (property.getSubOwner() != null || (request.getParameter("subOwnerId") != null &&
			    		request.getParameter("subOwnerId").length() > 0)){
			    	long subOwnerId = Long.parseLong(request.getParameter("subOwnerId"));
			    	property.setSubOwner(ownerService.get(subOwnerId));
			 }

			if (property.getPlot() != null || (request.getParameter("plotId") != null &&
		    		request.getParameter("plotId").length() > 0)){
				long plotId = Long.parseLong(request.getParameter("plotId"));
				if (property.getPlot() != null && property.getPlot().getPlotId() > 0){
					plotId = property.getPlot().getPlotId();
			}
				property.setPlot(plotService.get(plotId));
		    }
			
			if (property.getWard() != null || (request.getParameter("wardId") != null &&
		    		request.getParameter("wardId").length() > 0)){
				long wardId = Long.parseLong(request.getParameter("wardId"));
				if (property.getWard() != null && property.getWard().getWardId() > 0){
					wardId = property.getWard().getWardId();
			}
				property.setWard(wardService.get(wardId));
		    }
			
			property.setSector(sectorService.get(1));
			property.setNode(nodeMasterService.get(1));
			
			/*if (property.getSector() != null || (request.getParameter("sectorId") != null &&
		    		request.getParameter("sectorId").length() > 0)){
				long sectorId = Long.parseLong(request.getParameter("sectorId"));
				if (property.getSector() != null && property.getSector().getSectorId() > 0){
					sectorId = property.getSector().getSectorId();
			}
				property.setSector(sectorService.get(sectorId));
		    }*/
			
			/*if (property.getNode() != null || (request.getParameter("nodeMasterId") != null &&
		    		request.getParameter("nodeMasterId").length() > 0)){
				long nodeMasterId = Long.parseLong(request.getParameter("nodeMasterId"));
				if (property.getNode() != null && property.getNode().getNodeMasterId() > 0){
					nodeMasterId = property.getNode().getNodeMasterId();
			}
				property.setNode(nodeMasterService.get(nodeMasterId));
		    }*/

			if (property.getBuildingType() != null || (request.getParameter("buildingTypeId") != null &&
		    		request.getParameter("buildingTypeId").length() > 0)){
				long buildingTypeId = Long.parseLong(request.getParameter("buildingTypeId"));
				if (property.getBuildingType() != null && property.getBuildingType().getBuildingTypeId() > 0){
					buildingTypeId = property.getBuildingType().getBuildingTypeId();
				}
				property.setBuildingType(buildingTypeService.get(buildingTypeId));
		    }

		    if (property.getOldPropWard() != null || (request.getParameter("oldPropWardId") != null
		    		&& request.getParameter("oldPropWardId").length() > 0)){
				long oldPropWardId = Long.parseLong(request.getParameter("oldPropWardId"));
				if (property.getOldPropWard() != null && property.getOldPropWard().getWardId() > 0){
				    oldPropWardId = property.getOldPropWard().getWardId();
				}
				property.setOldPropWard(wardService.get(oldPropWardId));
		    }

		    if (property.getInspector() != null || (request.getParameter("inspectorId") != null &&
		    		request.getParameter("inspectorId").length() > 0)){
		    	long inspectorId = Long.parseLong(request.getParameter("inspectorId"));
		    	if (property.getInspector() != null && property.getInspector().getEmployeeId() > 0){
		    		inspectorId = property.getInspector().getEmployeeId();
		    	}
		    	property.setInspector(employeeService.get(inspectorId));
		    }

		    Property propertyObj = null;

		    // Check for Duplicate Property
		    //TODO: Uncomment if you need to check duplicates and update the query in the PropertydaoImpl.checkForDuplicates
		   // propertyObj = propertyService.checkForDuplicates(property);
		    if (CommonTokenUtils.allowFormSubmit(request))
		    {
			if (propertyObj != null)
			{
			    session.setAttribute("message", "Property with the same Application Number already exists.");
			} else
			{
				//property.setOldPropertyAddress("Building : "+property.getBuilding()+",Unit : "+property.getUnit()+",Plot No : "+property.getPlot().getNumber()+",Sector : "+property.getSector().getName()+",Node : "+property.getNode().getName()+",Ward : "+property.getWard().getName());
				property.setOldPropertyAddress("Building : "+property.getBuilding()+",Unit : "+property.getUnit()+",Plot No : "+property.getPlot().getNumber()+",Ward : "+property.getWard().getName());
				property.setWorkFlowStatus(CoreConstants.WORK_FLOW_STATUS_CLOSED);
				System.out.println("Property="+property.getisamcProperty()+property.getStatus()+property.getIsAuthorizedProperty()+property.getWorkFlowStatus()+property.getIsPropertyAssessed()+property.getArea());
				property = propertyService.merge(property);
				
/*			    request.setAttribute(WorkflowConstants.WORKFLOW_REQUIRED, "true");
				request.setAttribute(WorkflowConstants.WORKFLOW_NAME, "Property_Registration");
				request.setAttribute(WorkflowConstants.WORKFLOW_ENTITYNAME, "Property_Registration");
				request.setAttribute(WorkflowConstants.WORKFLOW_ENTITYID, property.getPropertyId());
				
				WorkflowProcessDetails workflowProcessDetails = null;
				workflowProcessDetails = workflowProcessDetailsService.getWorkflowProcessDetailsByEntityDetails(property.getPropertyId(),"Property_Registration");
				
					if(workflowProcessDetails!=null){
						String processId = workflowProcessDetails.getProcessId();
					
						if (!workflowService.isProcessEnded(processId)){
							String taskId = workflowService.getCurrentTaskId(processId);
							Task task = null;
								if(taskId!=null)
								task = taskService.getTask(taskId);	
							
							SessionUser sessionUser = (SessionUser) session.getAttribute("SessionUser");
							
							if(task!=null && sessionUser.getUserName().equals(task.getAssignee()))
								request.setAttribute("userAccess",1);
							
							request.setAttribute("workFlowTask", task);
						}else{
							request.setAttribute("workFlowCompleted", "1");
							property.setWorkFlowStatus(CoreConstants.WORK_FLOW_STATUS_CLOSED);
							property = propertyService.merge(property);
						}
					}
*/				
				
			    //TODO: This as to be done only after completion of workflow. Need to move once workflow is integrated.
			    
				/*if(property.getCode() == null || property.getCode().trim().length()==0){
			    	property.setCode(generatePropertyCode(property));
			    	property = propertyService.merge(property);
			    }*/
				//property=genPropertyCodeAmc(property);
		    	property = propertyService.merge(property);
			    session.setAttribute("message", "Property details saved successfully");
			}
		    }
		}

		setDMSParameters(request,property);
		
		if(property.getPropertyId() > 0)
		{
			long wardId = property.getWard().getWardId();
			//long nodeId = property.getNode().getNodeMasterId();
	        //long sectorId = property.getSector().getSectorId();
			
			request.setAttribute("wardList", wardService.getAll());
			request.setAttribute("nodeMasterList", nodeMasterService.getAll());
			//request.setAttribute("sectorList", wardService.getSectorsOnWardAndNode(wardId,nodeId));
			//request.setAttribute("plotList", plotService.findByProperty("sector.sectorId", sectorId));
		} else
		{
			request.setAttribute("wardList", wardService.getAll());
			request.setAttribute("nodeMasterList", nodeMasterService.getAll());
		}

		request.setAttribute("employeeList", employeeService.getAll());
		request.setAttribute("buildingTypeList", buildingTypeService.getAll());
        
		if(request.getParameter("flag")!=null && request.getParameter("flag").equalsIgnoreCase("true"))
		{
			request.setAttribute("flag", "true");
			return new ModelAndView("managePropertyCreate", "property", property);
		}else
		{
			ModelAndView modelandView = new ModelAndView("manageProperty");
			modelandView.addObject("property", property);
			return modelandView;
		}
    }

    /**
     *
     * @param request
     *            current HTTP request
     * @param response
     *            current HTTP response
     * @return a ModelAndView to render the response. This method is for
     *         editing/creating Property entries
     */

    public ModelAndView editProperty(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
		if (log.isDebugEnabled())
		{
		    log.debug("Invoking editProperty");
		}

		Property property = null;
		String propertyId = request.getParameter("propertyId");

		if (StringUtils.isNotEmpty(propertyId)){
			
			property = propertyService.get(Long.parseLong(propertyId));
			
			request.setAttribute("plotList", plotService.getAllPlot());
			
/*			request.setAttribute(WorkflowConstants.WORKFLOW_REQUIRED, "true");
			request.setAttribute(WorkflowConstants.WORKFLOW_NAME, "Property_Registration");
			request.setAttribute(WorkflowConstants.WORKFLOW_ENTITYNAME, "Property_Registration");
			request.setAttribute(WorkflowConstants.WORKFLOW_ENTITYID, propertyId);
			
			property = propertyService.get(Long.parseLong(propertyId));
			
			WorkflowProcessDetails workflowProcessDetails = null;
			workflowProcessDetails = workflowProcessDetailsService.getWorkflowProcessDetailsByEntityDetails(property.getPropertyId(),"Property_Registration");
			
				if(workflowProcessDetails!=null){
					String processId = workflowProcessDetails.getProcessId();
				
					if (!workflowService.isProcessEnded(processId)){
						String taskId = workflowService.getCurrentTaskId(processId);
						Task task = null;
						if(taskId!=null)
						 task = taskService.getTask(taskId);	
						HttpSession session = request.getSession();
						SessionUser sessionUser = (SessionUser) session.getAttribute("SessionUser");
						
						if(task!=null && sessionUser.getUserName().equals(task.getAssignee()))
							request.setAttribute("userAccess",1);
						
						request.setAttribute("workFlowTask", task);
					}else{
						request.setAttribute("workFlowCompleted", "1");
					}
				}
*/
				
		//	long wardId = property.getPlot().getSector().getNodeMaster().getWard().getWardId();	
			long wardId = property.getWard().getWardId();
			long nodeId = property.getNode().getNodeMasterId();
	        long sectorId = property.getSector().getSectorId();
	        
			request.setAttribute("wardList", wardService.getAll());
			request.setAttribute("nodeMasterList", nodeMasterService.getAll());
			request.setAttribute("sectorList", wardService.getSectorsOnWardAndNode(wardId, nodeId));
			//request.setAttribute("plotList", plotService.findByProperty("sector.sectorId", sectorId));
			//request.setAttribute("plotList", plotService.getAll());
			
		} else
		{
		    property = new Property();
		    property.setStatus(Constants.ACTIVE);

			request.setAttribute("wardList", wardService.getAll());
			request.setAttribute("nodeMasterList", nodeMasterService.getAll());
		}
		setDMSParameters(request,property);
		//request.setAttribute("plotList", plotService.getAll());
		request.setAttribute("plotList", plotService.getAllPlot());
		request.setAttribute("employeeList", employeeService.getAll());
		request.setAttribute("buildingTypeList", buildingTypeService.getAll());
		request.setAttribute("propertyTypes", propertyService.getAllPropertyType());
		request.setAttribute("propertysubTypes", propertyService.getAllPropertySubType());
		System.out.println("Property Type="+propertyService.getAllPropertyType());
		System.out.println("Property sub Type="+propertyService.getAllPropertySubType());
		CommonTokenUtils.setNewToken(request);
		
		if(request.getParameter("flag")!=null && request.getParameter("flag").equalsIgnoreCase("true"))
		{
			request.setAttribute("flag", "true");
			return new ModelAndView("managePropertyCreate", "property", property);
		}
	    else
		    return new ModelAndView("manageProperty", "property", property);
		
    }

    /**
     *
     * @param request
     *            current HTTP request
     * @param response
     *            current HTTP response
     * @return a ModelAndView to render the response This method will display
     *         list of Property
     */

    public ModelAndView listProperty(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
		if (log.isDebugEnabled())
		{
	    	log.debug("Invoking listProperty");
		}
		PropertySearch searchOptions = new PropertySearch();
		HttpSession session = request.getSession();
		SessionUser sessionUser = (SessionUser) session.getAttribute("SessionUser");
		
		request.setAttribute("userId", sessionUser.getUserId());
		setSearchParameters(searchOptions, request);
		List<Property> PropertyList = null;

		try{
			long totalCount = 0;
			//if(searchOptions.isSearchParamSet() || (request.getParameter("searchParamSet") != null &&
					//request.getParameter("searchParamSet").equalsIgnoreCase("true"))){
				PropertyList = propertyService.getPropertyListBySearch(searchOptions);
				totalCount = propertyService.getPropertyCountBySearch(searchOptions);

				request.setAttribute("searchParamSet", true);
			//}
		    request.setAttribute("maximumPages", new Long(CommonUtils.getMaxPage(totalCount)));
		    request.setAttribute("totalCount", totalCount);
		} catch (Exception e)
		{
		   	log.error(e.getMessage());
		}

		//Check if the PropertyList is null
		if(PropertyList==null)
			PropertyList = new ArrayList<Property>();
		long wardId=0;
		long nodeId=0;
		long sectorId=0;
		
		if(request.getParameter("searchWardId")!=null && request.getParameter("searchWardId").length()>0)
		wardId=Long.parseLong(request.getParameter("searchWardId"));
		if(request.getParameter("searchNodeMasterId")!=null && request.getParameter("searchNodeMasterId").length()>0)
		nodeId=Long.parseLong(request.getParameter("searchNodeMasterId"));
		if(request.getParameter("searchSectorId")!=null && request.getParameter("searchSectorId").length()>0)
		sectorId=Long.parseLong(request.getParameter("searchSectorId"));
		
		if(wardId>0)
		{
		//	request.setAttribute("nodeList", nodeMasterService.findByProperty("ward.wardId", wardId));
			request.setAttribute("nodeList", nodeMasterService.getAll());
			
			if(wardId>0 && nodeId>0)
			request.setAttribute("sectorList", wardService.getSectorsOnWardAndNode(wardId,nodeId));
			if(sectorId>0)
			request.setAttribute("plotList", plotService.findByProperty("sector.sectorId", sectorId));
				
		}

	//	request.setAttribute("sectorList", sectorService.getAll());
		request.setAttribute("nodeList", nodeMasterService.getAll());
		request.setAttribute("wardList", wardService.getAll());
		request.setAttribute("inspectorList", employeeService.getAll());
		request.setAttribute("ownerGroupList", ownerGroupService.getAll());

		request.setAttribute("currentPage", searchOptions.getCurrentPage());
		request.setAttribute(const_OrderBy, searchOptions.getOrderBy());
		request.setAttribute(const_SortBy, searchOptions.getSortBy());
		request.setAttribute("SearchOptions", searchOptions);
		
		boolean linkFlag=linkEnable(sessionUser);
		    if(linkFlag)request.setAttribute("linkEnable","1");	    	
		    else
		    	request.setAttribute("linkFlag","0");
        
		if(request.getParameter("flag")!=null && request.getParameter("flag").equalsIgnoreCase("true"))
		{
			request.setAttribute("flag", "true");
			return new ModelAndView("listSearchProperty","PropertyList",PropertyList);
		}else
		    return new ModelAndView("listProperty","PropertyList",PropertyList);
    }

    /**
     *
     * @param request
     *            current HTTP request
     * @param response
     *            current HTTP response
     * @return a ModelAndView to render the response This method deletes a Property
     *         entry from database
     */

    public ModelAndView deleteProperty(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
	HttpSession session = request.getSession();
	if (log.isDebugEnabled())
	{
	    log.debug("Invoking deleteProperty");
	}
	String propertyId = request.getParameter("propertyId");
	if (StringUtils.isNotEmpty(propertyId))
	{
	    Property property = propertyService.get(Long.parseLong(propertyId));
	    if (property != null)
	    {
		try
		{
		    propertyService.delete(Long.parseLong(propertyId));
		    deleteDMSDocumentCheckListEntries(property);
		    session.setAttribute("message", "Property details deleted successfully");
		} catch (Exception exception)
		{
		    session.setAttribute("message", "Cannot delete this record as it is used in other page");
		    return editProperty(request, response);
		}
	    } else
	    {
		session.setAttribute("message", "The Property details you are trying to delete is already deleted.");
	    }
	}
	return listProperty(request, response);
    }

    /**
     * Generates Property Code.
     * @param property
     * @return
     */
    private String generatePropertyCode(Property property){

    	String propertyCode = null;
    	NodeMaster nodeMaster = property.getPlot().getSector().getNodeMaster();

    	//TODO: Need to replace property.getPropertyId() with a seq value.
    	StringBuffer propertyId = new StringBuffer("").append(property.getPropertyId() + 10);
    	StringBuffer propertyIdVal = propertyId;
    	int len = propertyIdVal.length();
    	long totalVal = 0;

    	while(len > 1 && len < 10){
    		for(int index=0; index < len; index++){
    			totalVal = totalVal + Long.parseLong(propertyIdVal.substring(index, index+1));
    		}
    		len = new StringBuffer("").append(totalVal).length();
    		propertyIdVal = new StringBuffer().append(totalVal);

    		if(len == 1){
    			propertyCode = nodeMaster.getCode() + StringUtils.leftPad(propertyId.toString(), 9, '0') + propertyIdVal;
    		}else{
    			totalVal = 0;
    		}
    	}

    	return propertyCode;
    }
       /**
     * Returns Documents check list.
     * @param property
     * @return
     */
    private List<PropertyDocCheckList> getSelectedPropertyDocCheckList(HttpServletRequest request, Property property){

    	List<PropertyDocCheckList> propertyDocCheckList = null;
    	List<PropertyDocCheckList> existingList = null;

    	if(property.getPropertyId() > 0){
    		existingList = propertyService.get(property.getPropertyId()).getPropertyDocCheckList();
    	}

    	String strDocCheckIds[] = null;
    	String selectedDocs = request.getParameter("selectedDocs");
    	try
    	{
	    	propertyDocCheckList = new ArrayList<PropertyDocCheckList>();
	    	List<DocumentMaster> documentMasterList = new ArrayList<DocumentMaster>();

	    	for(PropertyDocCheckList list : existingList){
	    		if(list != null){
    	    		if(selectedDocs.indexOf(""+list.getDocumentMaster().getDocumentMasterId()) != -1){
    	    			propertyDocCheckList.add(list);
    	    			documentMasterList.add(list.getDocumentMaster());
    	    		}
	    		}
	    	}
    	    if (selectedDocs != null && selectedDocs.length() > 0){
    	    	strDocCheckIds = selectedDocs.split(",");
		    }
    	    if (strDocCheckIds != null && strDocCheckIds.length > 0){
    	    	for (String id : strDocCheckIds) {
    	    		DocumentMaster documentMaster = documentMasterService.get(Long.parseLong(id));

    	    		if(!documentMasterList.contains(documentMaster)){
    	    			PropertyDocCheckList propDocCheckList = new PropertyDocCheckList();
    	    			propDocCheckList.setProperty(property);
    	    			propDocCheckList.setDocumentMaster(documentMaster);
    	    			propDocCheckList.setFlag(Constants.ACTIVE);
    	    			propertyDocCheckList.add(propDocCheckList);
    	    		}
				}
    	    }
    	}catch (Exception exception) {
			exception.printStackTrace();
			log.error(exception.getMessage());
		}
    	return propertyDocCheckList;
    }

    /**
     *
     * @param request
     *            current HTTP request
     * @param response
     *            current HTTP response
     * @return a ModelAndView to render the response. This method is for
     *         editing Property Owner details
     */

    public ModelAndView editPropertyOwner(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
		if (log.isDebugEnabled())
		{
		    log.debug("Invoking editPropertyOwner");
		}

		Property property = null;
		String propertyId = request.getParameter("propertyId");
		int ownerType = Integer.parseInt(request.getParameter("ownerType"));

		if (StringUtils.isNotEmpty(propertyId))
		{
		    property = propertyService.get(Long.parseLong(propertyId));
		}

		setOwnerSearchParameters(request);
		CommonTokenUtils.setNewToken(request);

		if(ownerType == CoreConstants.OWNER)
			return new ModelAndView("manageOwnerChange", "property", property);
		else
			return new ModelAndView("manageSubOwnerChange", "property", property);
    }

    /**
     *
     * @param request
     *            current HTTP request
     * @param response
     *            current HTTP response
     * @return a ModelAndView to render the response. This method is for changing
     *         Property entry to the database.
     */
    public ModelAndView savePropertyOwner(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
	HttpSession session = request.getSession();
	if (log.isDebugEnabled())
	{
	    log.debug("Invoking savePropertyOwner");
	}

	Property property = null;
	String propertyId = request.getParameter("propertyId");
	long ownerId=0, subOwnerId = 0 ;

	try
	{
		if (StringUtils.isNotEmpty(propertyId)){
		    property = propertyService.get(Long.parseLong(propertyId));
		}

		// Create a new record with the inActive Status.
		property.setStatus(Constants.INACTIVE);
		propertyService.cloneAndSave(property);

		property = propertyService.get(Long.parseLong(propertyId));

		// Set the new owner for the propertyAssessment Object
		if (request.getParameter("newOwnerId") != null && request.getParameter("newOwnerId").length() > 0)
		{
			ownerId = Long.parseLong(request.getParameter("newOwnerId"));
			property.setOwner(ownerService.get(ownerId));
		}

		// Set the new SubOwner for the propertyAssessment Object
		if (request.getParameter("newSubOwnerId") != null && request.getParameter("newSubOwnerId").length() > 0)
		{
			subOwnerId = Long.parseLong(request.getParameter("newSubOwnerId"));
			property.setSubOwner(ownerService.get(subOwnerId));
		}

		// Modify the owner / subOwner name in the existing record with its status Active.
		propertyService.save(property);
	}
	catch(Exception ex)
	{
		log.error(ex.getMessage());
		ex.printStackTrace();
	}

//	return listPropertyAssessment(request, response);
	if(ownerId != 0)
		return new ModelAndView("manageOwnerChange", "property", property);
	else
		return new ModelAndView("manageSubOwnerChange", "property", property);
    }

    /**
     *
     * @param searchOptions
     * @param request
     */
    private void setSearchParameters(PropertySearch searchOptions, HttpServletRequest request) {
    	long longCurrentPage = CommonUtils.checkPaginationAttributes(request);
		String orderBy = request.getParameter(const_OrderBy);
		String sortBy = request.getParameter(const_SortBy);

		// setting default order by on ownerId
		if (orderBy == null || orderBy.length() < 1) {
			orderBy = "propertyId";
			sortBy = "desc";
		}
        try {
          searchOptions.setCurrentPage(longCurrentPage);
          searchOptions.setOrderBy(orderBy);
          searchOptions.setSortBy(sortBy);

          searchOptions.setCode(request.getParameter("searchCode"));
          String searchInspectorId = request.getParameter("searchInspectorId");
          String searchNodeMasterId = request.getParameter("searchNodeMasterId");
          String searchSectorId = request.getParameter("searchSectorId");
          String searchWardId = request.getParameter("searchWardId");
          String searchPlotId = request.getParameter("searchPlotId");
          String searchOwnerGroupId = request.getParameter("searchOwnerGroupId");
          searchOptions.setBuilding(request.getParameter("searchBuilding"));
          searchOptions.setUnit(request.getParameter("searchUnit"));
          searchOptions.setOwnerFirstName(request.getParameter("searchFirstName"));
          searchOptions.setOwnerLastName(request.getParameter("searchLastName"));
          
          String searchOldPropWardId = request.getParameter("searchOldPropWardId");
          searchOptions.setOldPropSection(request.getParameter("searchSection"));
          searchOptions.setOldPropAcNo(request.getParameter("searchAcNo"));
          searchOptions.setOldPropAcSubNo(request.getParameter("searchAcSubNo"));

          if(searchOwnerGroupId != null && searchOwnerGroupId.length()>0)
        	  searchOptions.setOwnerGroupId(Long.parseLong(searchOwnerGroupId));          
          if(searchInspectorId != null && searchInspectorId.length()>0)
        	  searchOptions.setInspectorId(Long.parseLong(searchInspectorId));
          if(searchOldPropWardId != null && searchOldPropWardId.length()>0)
        	  searchOptions.setOldPropWardId(Long.parseLong(searchOldPropWardId));
          if(searchNodeMasterId != null && searchNodeMasterId.length()>0)
        	  searchOptions.setNodeMasterId(Long.parseLong(searchNodeMasterId));
          if(searchSectorId != null && searchSectorId.length()>0)
        	  searchOptions.setSectorId(Long.parseLong(searchSectorId));
          if(searchWardId != null && searchWardId.length()>0)
        	  searchOptions.setWardId(Long.parseLong(searchWardId));
          if(searchPlotId != null && searchPlotId.length()>0)
        	  searchOptions.setPlotId(Long.parseLong(searchPlotId));
          
          if(request.getParameter("flag")!=null && request.getParameter("flag").equalsIgnoreCase("true")){
        	  searchOptions.setWorkFlowStatus(CoreConstants.WORK_FLOW_STATUS_CLOSED);
          }

        } catch (Exception err) {
          log.error(err.getMessage());
        }
      }

    /**
     *
     * @param strName
     * @return
     */
    public List<Property> getPropertyList(String code)
    {
    	List<Property> propertyList = null;
    	PropertySearch propertySearch = new PropertySearch();
    	try{
        	// setting default order BY
    		propertySearch.setOrderBy("propertyId");
    		propertySearch.setSortBy("desc");
    		propertySearch.setCurrentPage(new Long(1));
    		propertySearch.setWorkFlowStatus(CoreConstants.WORK_FLOW_STATUS_CLOSED);

    		if(code!=null && code.trim().length()>0){
    			propertySearch.setCode(code);
    		}

    		propertyList = propertyService.getPropertyListBySearch(propertySearch);

    	}catch (Exception exception){
    		log.error(exception.getMessage());
		}

    	//Check if the usageTypeList is null
    	if(propertyList==null)
    		propertyList = new ArrayList<Property>();
    	return propertyList;
    }

    public Property checkValidProperty(String propertyCode)
    {
    	List<Property> propertyList;
		Property propertyObj = null;
    	try
    	{
    		propertyList = propertyService.validateProperty(propertyCode);
			if(propertyList != null && propertyList.size() > 0)
				propertyObj = propertyList.get(0);
	    	} catch(Exception ex) {
	    		ex.printStackTrace();
	    	}
    	return propertyObj;
    }

    public Property checkValidPropertyForOwnerChange(String propertyCode)
    {
    	List<Property> propertyList;
		Property propertyObj = null;
    	try
    	{
    		propertyList = propertyService.validateProperty(propertyCode);
			if(propertyList != null && propertyList.size() > 0)
				propertyObj = propertyList.get(0);
			//Setting owner object to propertyObj as there is rendering issue in jsp
			if(propertyObj != null && propertyObj.getOwner() != null){
				if(propertyObj.getOwner().getOwnerId()> 0){
					Owner owner = ownerService.get(propertyObj.getOwner().getOwnerId());
					propertyObj.setOwner(owner);
				}
			}
		} catch(Exception ex) {
	    		ex.printStackTrace();
	    	}
    	return propertyObj;
    }
    /**
     *
     * @param searchOptions
     * @param request
     */
    private void setOwnerSearchParameters(HttpServletRequest request) {
    	long longCurrentPage = CommonUtils.checkPaginationAttributes(request);
		String orderBy = request.getParameter(const_OrderBy);
		String sortBy = request.getParameter(const_SortBy);

		// setting default order by on ownerId
		if (orderBy == null || orderBy.length() < 1) {
			orderBy = "upper(firstName)";
			sortBy = "asc";
		}
        try {
        	List<Owner> OwnerList = null;
        	OwnerSearch searchOptions = new OwnerSearch();
        	searchOptions.setCurrentPage(longCurrentPage);
        	searchOptions.setOrderBy(orderBy);
        	searchOptions.setSortBy(sortBy);

        	searchOptions.setFirstName(request.getParameter("searchFirstName"));
        	searchOptions.setMiddleName(request.getParameter("searchMiddleName"));
        	searchOptions.setLastName(request.getParameter("searchLastName"));

        	long totalCount = 0;
        	if(searchOptions.isSearchParamSet()){
        		OwnerList = ownerService.getOwnerListBySearch(searchOptions);
        		totalCount = ownerService.getOwnerCountBySearch(searchOptions);
        	}

        	if(OwnerList==null)
        		OwnerList = new ArrayList<Owner>();

        	request.setAttribute("ownerList", OwnerList);
        	request.setAttribute("maximumPages", new Long(CommonUtils.getMaxPage(totalCount)));
        	request.setAttribute("totalCount", totalCount);

        	request.setAttribute("currentPage", searchOptions.getCurrentPage());
        	request.setAttribute(const_OrderBy, searchOptions.getOrderBy());
        	request.setAttribute(const_SortBy, searchOptions.getSortBy());
        	request.setAttribute("SearchOptions", searchOptions);

        } catch (Exception err) {
          log.error(err.getMessage());
        }
      }

    private void setDMSParameters(HttpServletRequest request,Object entity)
    {
    	Property property = (Property) entity;
    	long entityId = property.getPropertyId();
    	long wardId=0;
  //  	long wardId = property.getPlot().getSector().getNodeMaster().getWard().getWardId();
    	if(property.getWard() != null && property.getWard().getWardId()>0)
    	 wardId= property.getWard().getWardId();

    	String entityName = this.DMS_ENTITY_NAME;
    	String folderPath = this.DMS_ENTITY_FOLDER_PATH;
    	folderPath = folderPath.replace("[WARD_ID]", ""+wardId);
    	folderPath = folderPath.replace("[ENTITY_ID]", ""+entityId);

	    //DMS Integration related Info
	    request.setAttribute("DMS_WORKSPACE_NAME",Constants.WORKSPACE_NAME);
	    request.setAttribute("DMS_FOLDER_PATH",folderPath);
	    request.setAttribute("DMS_ENTITY_NAME",entityName);
	    request.setAttribute("DMS_ENTITY_ID",entityId);
	    request.setAttribute("DMS_UPLOAD_TYPE","DocumentChecklist");

	    List<DocumentMaster> documentMasterList = documentMasterService.getDocumentsByEntity(entityName, entityId);

		request.setAttribute("documentMasterList", documentMasterList);
    }

    private void deleteDMSDocumentCheckListEntries(Object entity)
    {

    	Property property = (Property) entity;
    	long entityId = property.getPropertyId();
    	String entityName = this.DMS_ENTITY_NAME;

    	dmsDocumentCheckListService.deleteByEntity(entityName,entityId);
    }
    
    public Property getPropertyCode(long propertyId){
    	Property property = null;
    	if(propertyId>0){
    		property = propertyService.get(propertyId);
    	}
    	return property;
    }
    
    public ModelAndView getPropertyDetailsAmnesty(HttpServletRequest request,HttpServletResponse response) throws ServletException, ParseException
    {
    	if(log.isDebugEnabled()){
    		log.debug("getPropertyDetails method called");
    	}
    	HttpSession session = request.getSession();
    	ModelAndView modelAndView=new ModelAndView("manageLedger");
    	DemandCycle demand=new DemandCycle();
    	PropertyDetails propertyDetails=null;
    	List<PropertyAssessment> assmntList=null;
    	String propertyCode=request.getParameter("searchCode");    	
    	
    	LedgerDetails ledgerDetails = null;
		long transRef = 0;
		int transType = 0;
		double dpcAmt=0;
		double totalBillAmt = 0;
		double totalAmtPaid = 0;
		String ledgerDate="";
		String collectionCeneter="";
		String remarks="";
		int recStatus=0;
    
    	if(propertyCode != null && propertyCode.length()==12)
    	{
    		Property property=propertyService.getPropertyByCode(propertyCode);
    		
    		if(property!=null)
    		{
    			propertyDetails=new PropertyDetails();
    			propertyDetails.setPropertyCode(propertyCode);
    			propertyDetails.setOwnerName(property.getOwner().getFullName());
    			
    			if(property.getSubOwner() != null && property.getSubOwner().getOwnerId()>0)
    			 propertyDetails.setSubOwnerName(property.getSubOwner().getFullName());
    			else
    			 propertyDetails.setSubOwnerName("-");
    			
    			propertyDetails.setPropertyAddress(property.getPropertyAddress());
    			propertyDetails.setCityAndState(property.getCitiAndState());
    			
    			PropertyAssessment propertyAssessment = propertyAssessmentService.getLatestAssessment(property.getPropertyId());
    			
    			if(propertyAssessment != null){
    			propertyDetails.setPropertyType(propertyAssessment.getUsage());
    			
    			String assmntListSql="select * from property_assessment where property_id="+property.getPropertyId()+" and status in (1,2) and work_flow_status="+CoreConstants.WORK_FLOW_STATUS_CLOSED+" order by to_date(assessment_date,'dd/mm/yyyy') ASC ";
    			//String assmntListSql="select * from property_assessment where property_id="+property.getPropertyId()+" and status="+Constants.ACTIVE+" order by to_date(assessment_date,'dd/mm/yyyy') DESC limit 1 ";
    			assmntList = propertyAssessmentService.getEntityListBySQLQuery(assmntListSql, null);
    			}
    			double outStandingAmt;
    			Bill bill;
    			if(property.getOwner().getOwnerGroup().getOwnerGroupId()==1002)
				{
    				bill = billService.getPropertyBillDuesAmount(propertyCode.toUpperCase(),CommonUtils.getCurrentStringDate());
				} else  {
					bill = billService.getPropertyBillDuesAmount(propertyCode.toUpperCase(),CommonUtils.getCurrentStringDate());
				}
    			if(bill!=null){
        			propertyDetails.setBalance(Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt()));
        			request.setAttribute("rebateAmount", Math.round(bill.getRebateAmt()));
        			request.setAttribute("amount", Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt())-Math.round(bill.getRebateAmt()));
        			
        			}
        			else{
        				propertyDetails.setBalance(0);
        				request.setAttribute("rebateAmount", 0);
            			request.setAttribute("amount",0);
        			}
        		
        		
        		List<Bill> billAmensty=billService.getPropertyBillDues(propertyCode.toUpperCase());
        		int month;
        		int tmp=0;
        		java.util.Date date= new Date();
        		Calendar cal = Calendar.getInstance();
        		cal.setTime(date);
        		tmp = cal.get(Calendar.MONTH);
        		month=tmp+1;
        		
        		//Oct 2019
        		/*if(month==10)
        		{
        			propertyDetails.setAmenestyBillAmount(billAmensty.get(0).getBillAmt());
        			propertyDetails.setAmenFDpc(billAmensty.get(0).getDpcAmt1());
        			propertyDetails.setAmenDpc(Math.round((billAmensty.get(0).getDpcAmt1()) * ((0.375))));
        			propertyDetails.setAmenWavedAmt(Math.round((billAmensty.get(0).getDpcAmt1()) - (billAmensty.get(0).getDpcAmt1() * (0.375))));
        		}
        		
        		if(month==12 || month==1)
        		{
        			propertyDetails.setAmenestyBillAmount(billAmensty.get(0).getBillAmt());
        			propertyDetails.setAmenFDpc(billAmensty.get(0).getDpcAmt1());
        			propertyDetails.setAmenDpc(Math.round((billAmensty.get(0).getDpcAmt1()) * ((0.375))));
        			propertyDetails.setAmenWavedAmt(Math.round((billAmensty.get(0).getDpcAmt1()) - (billAmensty.get(0).getDpcAmt1() * (0.37))));
        		}*/
        		
        		//Nov 2019
        		/*if(month==10)
        		{
        			propertyDetails.setAmenestyBillAmount1(billAmensty.get(1).getBillAmt());
        			propertyDetails.setAmenSDpc(billAmensty.get(1).getDpcAmt1());
        			propertyDetails.setAmenDpc1(Math.round(billAmensty.get(1).getDpcAmt1() * (0.375)));
        			propertyDetails.setAmenWavedAmt1(Math.round((billAmensty.get(1).getDpcAmt1()) - (billAmensty.get(1).getDpcAmt1() * (0.375))));
        		}
        		
        		if(month==12 || month==1)
        		{
        			propertyDetails.setAmenestyBillAmount1(billAmensty.get(1).getBillAmt());
        			propertyDetails.setAmenSDpc(billAmensty.get(0).getDpcAmt1());
        			propertyDetails.setAmenDpc1(Math.round(billAmensty.get(1).getDpcAmt1() * (0.375)));
        			propertyDetails.setAmenWavedAmt1(Math.round((billAmensty.get(1).getDpcAmt1()) - (billAmensty.get(1).getDpcAmt1() * (0.37))));
        		}*/
        		
        		//Dec 2019
        		/*if(month==10)
        		{
        			propertyDetails.setAmenestyBillAmount2(billAmensty.get(2).getBillAmt());
        			propertyDetails.setAmenTDpc(billAmensty.get(2).getDpcAmt1());
        			propertyDetails.setAmenDpc2(Math.round((billAmensty.get(2).getDpcAmt1()) * (0.375)));
        			propertyDetails.setAmenWavedAmt2(Math.round((billAmensty.get(2).getDpcAmt1()) - (billAmensty.get(1).getDpcAmt1() *(0.375))));
        		}
        		
        		if(month==12 || month==1)
        		{
        			propertyDetails.setAmenestyBillAmount2(billAmensty.get(2).getBillAmt());
        			propertyDetails.setAmenTDpc(billAmensty.get(2).getDpcAmt1());
        			propertyDetails.setAmenDpc2(Math.round(billAmensty.get(2).getDpcAmt1() * (0.375)));
        			propertyDetails.setAmenWavedAmt2(Math.round((billAmensty.get(2).getDpcAmt1()) - (billAmensty.get(2).getDpcAmt1() * (0.375))));
        		}*/
        		
        		//Jan2020
        		/*if(month==10)
        		{
        			propertyDetails.setAmenestyBillAmount3(billAmensty.get(3).getBillAmt());
        			propertyDetails.setAmenForDpc(billAmensty.get(3).getDpcAmt1());
        			propertyDetails.setAmenDpc3(Math.round(billAmensty.get(3).getDpcAmt1() * (0.375)));
        			propertyDetails.setAmenWavedAmt3(Math.round((billAmensty.get(3).getDpcAmt1()) - (billAmensty.get(3).getDpcAmt1() * (0.375))));
        		}
        		
        		if(month==12 || month==1)
        		{
        			propertyDetails.setAmenestyBillAmount3(billAmensty.get(3).getBillAmt());
        			propertyDetails.setAmenForDpc(billAmensty.get(3).getDpcAmt1());
        			propertyDetails.setAmenDpc3(Math.round(billAmensty.get(3).getDpcAmt1() * (0.37)));
        			propertyDetails.setAmenWavedAmt3(Math.round((billAmensty.get(3).getDpcAmt1()) - (billAmensty.get(3).getDpcAmt1() * (0.37))));
        		}*/
        		
    		}else{
        		session.setAttribute("message", "Property A/c No you have entered is invalid.");
        	}
    	
    	if(request.getParameter("flag") != null && request.getParameter("flag").equalsIgnoreCase("true")){
    		
    		List<LedgerDetails> ledgerDetailsList  = ledgerService.getLedgerDetailListBySQLQuery(propertyCode);            
    		modelAndView.addObject("ledgerDetailsList",ledgerDetailsList);	
    		/*//Start:Commented By VenkataRao M on 16072015
    		List<LedgerDetails> ledgerDetailsList = new ArrayList<LedgerDetails>();
    		List<Ledger> ledgerList  = ledgerService.getEntityListBySQLQuery("select * from ledger where upper(property_code)=upper('" + propertyCode + "') and rec_status in (1,-2) order by to_date(ledger_date,'dd/mm/yyyy') desc,group_id desc,to_number(trans_ref) desc,ledger_id desc", null);
            Ledger dupLedger = new Ledger();    		
    		if (ledgerList != null && ledgerList.size() > 0)
    		{
    			
    			for(Ledger ledger : ledgerList)
    			{
    				if(ledger.getTransType() == 1)
    				{
    					
						  if(transType == 2)
						  {
							  ledgerDetails= new LedgerDetails();
	    			   // 	  ledgerDetails.setTransRef(collectionCeneter+", R.No. "+ transRef);
							  ledgerDetails.setTransRef(collectionCeneter);
	    			    	  ledgerDetails.setCollectionAmount(totalBillAmt);
	    			    	  ledgerDetails.setBillAmount(totalBillAmt);
	    			    	  ledgerDetails.setDpcAmount(dpcAmt);
	    			    	  ledgerDetails.setLedgerAmount(totalAmtPaid);
	    			   //	  ledgerDetails.setStatus(dupLedger.getStatus());
	    			   // 	  ledgerDetails.setAdvanceAmount(dupLedger.getAdvanceAmount());
	    			    	  ledgerDetails.setLedgerDate(ledgerDate);
	    			    	  ledgerDetails.setTransType(CoreConstants.LEDGER_TRANS_TYPE_COLLECTION_LABEL);
	    			    	  ledgerDetails.setRemarks(remarks);
	    			    	  ledgerDetails.setRecStatus(recStatus);
	    			    	  
	    			    	  ledgerDetailsList.add(ledgerDetails);
						  }
							 
							 
						transRef=0;
						transType=0;						
						totalAmtPaid = 0;
						totalBillAmt = 0;
						dpcAmt=0;
						ledgerDate="";
						collectionCeneter="";
						transType=1;
						
						ledgerDetails= new LedgerDetails();
    					demand=demandCycleService.get(ledger.getDemandCycle().getDemandCycleId());
    					remarks=ledger.getRemarks();
						recStatus=ledger.getRecStatus();
    					ledgerDetails.setTransRef("Demand :"+ demand.getFromDate().substring(8, 10)+""+demand.getToDate().substring(8, 10));
    		 		    ledgerDetails.setDemandAmount(ledger.getBillAmount());
    		 		    ledgerDetails.setDpcAmount(ledger.getDpcAmount()); 
    		 		    ledgerDetails.setLedgerAmount(ledger.getLedgerAmount());
 			    	    ledgerDetails.setStatus(ledger.getStatus());
 			    	    ledgerDetails.setAdvanceAmount(ledger.getAdvanceAmount());
 			    	//  ledgerDetails.setTransRef(ledger.getTransRef());
 			    	    ledgerDetails.setLedgerDate(ledger.getLedgerDate());
 			    	    ledgerDetails.setRemarks(remarks);
			    		ledgerDetails.setRecStatus(recStatus);
			    		ledgerDetails.setTransType(CoreConstants.LEDGER_TRANS_TYPE_BILL_LABEL);
    		 		    
    		 		     ledgerDetailsList.add(ledgerDetails);
						 transRef=0;
						 transType=0;
						 remarks="";
						 recStatus=1;
    		 		     
    				}else
    			    if(ledger.getTransType() == 2)
    				{
    			    	
    			    	//Start:Added By VenkataRao M
    			    	SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
    			    	Date intialLedgerDate=null;
    			    	Date afterLedgerDate = null;
    			    	if(ledgerDate!="" && ledgerDate!=null)
    			    		intialLedgerDate = sdf.parse(ledgerDate);
    			    	if(ledger.getLedgerDate()!="" && ledger.getLedgerDate()!=null)
    			    		afterLedgerDate= sdf.parse(ledger.getLedgerDate());
    			    	//End:Added By VenkataRao M
    			    	
    			    	//if((transRef != 0 && transRef != Long.parseLong(ledger.getTransRef())) || (ledger.getGroupId()==0) )
						//{	
    			    	if(intialLedgerDate!=null && afterLedgerDate!=null)
    			    	{
	    			    		
	    			    	if(intialLedgerDate.compareTo(afterLedgerDate)!=0)
							{
    			    		ledgerDetails= new LedgerDetails();
    			   // 		ledgerDetails.setTransRef(collectionCeneter+", R.No. "+ transRef);
    			    		ledgerDetails.setTransRef(collectionCeneter);
    			    		ledgerDetails.setCollectionAmount(totalBillAmt);
    			    		ledgerDetails.setBillAmount(totalBillAmt);
    			    		ledgerDetails.setDpcAmount(dpcAmt);
    			    		ledgerDetails.setLedgerAmount(totalAmtPaid);
    			    //		ledgerDetails.setStatus(ledger.getStatus());
    			    //		ledgerDetails.setAdvanceAmount(ledger.getAdvanceAmount());
    			    		ledgerDetails.setLedgerDate(ledgerDate);
    			    		ledgerDetails.setRemarks(remarks);
    			    		ledgerDetails.setRecStatus(recStatus);
    			    		ledgerDetails.setTransType(CoreConstants.LEDGER_TRANS_TYPE_COLLECTION_LABEL);
    			    		
    			    		ledgerDetailsList.add(ledgerDetails);
    			    		
							totalBillAmt=0;
							totalAmtPaid=0;
							dpcAmt=0;
							transRef=0;
							ledgerDate="";
							collectionCeneter="";
							remarks="";
							recStatus=0;
						}
    			    }
						
    			    	//if(ledger.getGroupId()!=0)
						//{
	    			    	dupLedger = new Ledger(); 
				    		BeanUtils.copyProperties(ledger, dupLedger);
	    			    	transRef = Long.parseLong(ledger.getTransRef());
	    			    	ledgerDate = ledger.getLedgerDate();
	    			    	collectionCeneter=ledger.getCollectionCenter().getDescription();
							transType = 2;
							remarks=ledger.getRemarks();
							recStatus=ledger.getRecStatus();
							
								totalAmtPaid = totalAmtPaid + ledger.getLedgerAmount();
								totalBillAmt= totalBillAmt + ledger.getBillAmount() + ledger.getAdvanceAmount();							
								dpcAmt= dpcAmt + ledger.getDpcAmount();
						//}
							
    				}
    			}
    			if(transType == 2)
    			{
    				ledgerDetails= new LedgerDetails();
		    	//	ledgerDetails.setTransRef(collectionCeneter+", R.No. "+ transRef);
    				ledgerDetails.setTransRef(collectionCeneter);
		    		ledgerDetails.setCollectionAmount(totalBillAmt);
		    		ledgerDetails.setBillAmount(totalBillAmt);
		    		ledgerDetails.setDpcAmount(dpcAmt);
		    		ledgerDetails.setLedgerAmount(totalAmtPaid);
		    	//	ledgerDetails.setAdvanceAmount(ledger.getAdvanceAmount());
		    		ledgerDetails.setLedgerDate(ledgerDate);
		    		ledgerDetails.setTransType(CoreConstants.LEDGER_TRANS_TYPE_COLLECTION_LABEL);
		    		ledgerDetails.setRemarks(remarks);
		    		ledgerDetails.setRecStatus(recStatus);
		    		
		    		ledgerDetailsList.add(ledgerDetails);
    			}
    		}
    		modelAndView.addObject("ledgerDetailsList",ledgerDetailsList);	
    		//End:Commented By VenkataRao M on 16072015
*/
    	 }
    	}
    	
    	request.setAttribute("propertyCode", propertyCode);
        request.setAttribute("searchParamSet", "true");
    	modelAndView.addObject("propertyDetails",propertyDetails);
    	modelAndView.addObject("propertyAssessmentList",assmntList);
    	
    	return modelAndView;
    }  
    
    public ModelAndView getPropertyDetails(HttpServletRequest request,HttpServletResponse response) throws ServletException, ParseException
    {
    	if(log.isDebugEnabled()){
    		log.debug("getPropertyDetails method called");
    	}
    	HttpSession session = request.getSession();
 
		SessionUser sessionUser = (SessionUser) session.getAttribute("SessionUser");
		
    	ModelAndView modelAndView=new ModelAndView("manageLedger");
    	DemandCycle demand=new DemandCycle();
    	PropertyDetails propertyDetails=null;
    	List<PropertyAssessment> assmntList=null;
    	String propertyCode=request.getParameter("searchCode");    	
    	
    	LedgerDetails ledgerDetails = null;
		long transRef = 0;
		int transType = 0;
		double dpcAmt=0;
		double totalBillAmt = 0;
		double totalAmtPaid = 0;
		String ledgerDate="";
		String collectionCeneter="";
		String remarks="";
		int recStatus=0;
    
    	if(propertyCode != null)
    	{
    		Property property=propertyService.getPropertyByCode(propertyCode);
    		
    		if(property!=null)
    		{
    			propertyDetails=new PropertyDetails();
    			propertyDetails.setPropertyCode(propertyCode);
    			propertyDetails.setOwnerName(property.getOwner().getFullName());
    			
    			if(property.getSubOwner() != null && property.getSubOwner().getOwnerId()>0)
    			 propertyDetails.setSubOwnerName(property.getSubOwner().getFullName());
    			else
    			 propertyDetails.setSubOwnerName("-");
    			
    			//Setting property address to display
    			//propertyDetails.setPropertyAddress(property.getPropertyAddress());
    			//propertyDetails.setPropertyAddress(property.getOldPropertyAddress());
    			propertyDetails.setPropertyAddress(property.getOwner().getAddress().getArea());
    			propertyDetails.setCityAndState(property.getCitiAndState());
    			
    			PropertyAssessment propertyAssessment = propertyAssessmentService.getLatestAssessment(property.getPropertyId());
    			
    			if(propertyAssessment != null)
    			propertyDetails.setPropertyType(propertyAssessment.getUsage());
    			
    			String assmntListSql="select * from property_assessment where property_id="+property.getPropertyId()+" and status in (1,2) order by to_date(assessment_date,'dd/mm/yyyy') ASC ";
    			//String assmntListSql="select * from property_assessment where property_id="+property.getPropertyId()+" and status="+Constants.ACTIVE+" order by to_date(assessment_date,'dd/mm/yyyy') DESC limit 1 ";
    			assmntList = propertyAssessmentService.getEntityListBySQLQuery(assmntListSql, null);
    			
    			double outStandingAmt =0;
    			Bill bill=null;
    			if(property.getOwner().getOwnerGroup().getOwnerGroupId()==1002)
				{
    				bill = billService.getPropertyBillDuesAmount_Midc(propertyCode.toUpperCase(),CommonUtils.getCurrentStringDate());
				} else {
    			   bill = billService.getPropertyBillDuesAmount(propertyCode.toUpperCase(),CommonUtils.getCurrentStringDate());
				}
    			if(bill!=null){
        			propertyDetails.setBalance(Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt()));
        			request.setAttribute("rebateAmount", Math.round(bill.getRebateAmt()));
        			request.setAttribute("amount", Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt())-Math.round(bill.getRebateAmt()));
        			
        			}
        			else{
        				propertyDetails.setBalance(0);
        				request.setAttribute("rebateAmount", 0);
            			request.setAttribute("amount",0);
        			}
        		
    		}else{
        		session.setAttribute("message", "Property A/c No you have entered is invalid.");
        	}
    	
    	if(request.getParameter("flag") != null && request.getParameter("flag").equalsIgnoreCase("true")){
    		
    		List<LedgerDetails> ledgerDetailsList  = ledgerService.getLedgerDetailListBySQLQuery(propertyCode);            
    		modelAndView.addObject("ledgerDetailsList",ledgerDetailsList);	
    		/*//Start:Commented By VenkataRao M on 16072015
    		List<LedgerDetails> ledgerDetailsList = new ArrayList<LedgerDetails>();
    		List<Ledger> ledgerList  = ledgerService.getEntityListBySQLQuery("select * from ledger where upper(property_code)=upper('" + propertyCode + "') and rec_status in (1,-2) order by to_date(ledger_date,'dd/mm/yyyy') desc,group_id desc,to_number(trans_ref) desc,ledger_id desc", null);
            Ledger dupLedger = new Ledger();    		
    		if (ledgerList != null && ledgerList.size() > 0)
    		{
    			
    			for(Ledger ledger : ledgerList)
    			{
    				if(ledger.getTransType() == 1)
    				{
    					
						  if(transType == 2)
						  {
							  ledgerDetails= new LedgerDetails();
	    			   // 	  ledgerDetails.setTransRef(collectionCeneter+", R.No. "+ transRef);
							  ledgerDetails.setTransRef(collectionCeneter);
	    			    	  ledgerDetails.setCollectionAmount(totalBillAmt);
	    			    	  ledgerDetails.setBillAmount(totalBillAmt);
	    			    	  ledgerDetails.setDpcAmount(dpcAmt);
	    			    	  ledgerDetails.setLedgerAmount(totalAmtPaid);
	    			   //	  ledgerDetails.setStatus(dupLedger.getStatus());
	    			   // 	  ledgerDetails.setAdvanceAmount(dupLedger.getAdvanceAmount());
	    			    	  ledgerDetails.setLedgerDate(ledgerDate);
	    			    	  ledgerDetails.setTransType(CoreConstants.LEDGER_TRANS_TYPE_COLLECTION_LABEL);
	    			    	  ledgerDetails.setRemarks(remarks);
	    			    	  ledgerDetails.setRecStatus(recStatus);
	    			    	  
	    			    	  ledgerDetailsList.add(ledgerDetails);
						  }
							 
							 
						transRef=0;
						transType=0;						
						totalAmtPaid = 0;
						totalBillAmt = 0;
						dpcAmt=0;
						ledgerDate="";
						collectionCeneter="";
						transType=1;
						
						ledgerDetails= new LedgerDetails();
    					demand=demandCycleService.get(ledger.getDemandCycle().getDemandCycleId());
    					remarks=ledger.getRemarks();
						recStatus=ledger.getRecStatus();
    					ledgerDetails.setTransRef("Demand :"+ demand.getFromDate().substring(8, 10)+""+demand.getToDate().substring(8, 10));
    		 		    ledgerDetails.setDemandAmount(ledger.getBillAmount());
    		 		    ledgerDetails.setDpcAmount(ledger.getDpcAmount()); 
    		 		    ledgerDetails.setLedgerAmount(ledger.getLedgerAmount());
 			    	    ledgerDetails.setStatus(ledger.getStatus());
 			    	    ledgerDetails.setAdvanceAmount(ledger.getAdvanceAmount());
 			    	//  ledgerDetails.setTransRef(ledger.getTransRef());
 			    	    ledgerDetails.setLedgerDate(ledger.getLedgerDate());
 			    	    ledgerDetails.setRemarks(remarks);
			    		ledgerDetails.setRecStatus(recStatus);
			    		ledgerDetails.setTransType(CoreConstants.LEDGER_TRANS_TYPE_BILL_LABEL);
    		 		    
    		 		     ledgerDetailsList.add(ledgerDetails);
						 transRef=0;
						 transType=0;
						 remarks="";
						 recStatus=1;
    		 		     
    				}else
    			    if(ledger.getTransType() == 2)
    				{
    			    	
    			    	//Start:Added By VenkataRao M
    			    	SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
    			    	Date intialLedgerDate=null;
    			    	Date afterLedgerDate = null;
    			    	if(ledgerDate!="" && ledgerDate!=null)
    			    		intialLedgerDate = sdf.parse(ledgerDate);
    			    	if(ledger.getLedgerDate()!="" && ledger.getLedgerDate()!=null)
    			    		afterLedgerDate= sdf.parse(ledger.getLedgerDate());
    			    	//End:Added By VenkataRao M
    			    	
    			    	//if((transRef != 0 && transRef != Long.parseLong(ledger.getTransRef())) || (ledger.getGroupId()==0) )
						//{	
    			    	if(intialLedgerDate!=null && afterLedgerDate!=null)
    			    	{
	    			    		
	    			    	if(intialLedgerDate.compareTo(afterLedgerDate)!=0)
							{
    			    		ledgerDetails= new LedgerDetails();
    			   // 		ledgerDetails.setTransRef(collectionCeneter+", R.No. "+ transRef);
    			    		ledgerDetails.setTransRef(collectionCeneter);
    			    		ledgerDetails.setCollectionAmount(totalBillAmt);
    			    		ledgerDetails.setBillAmount(totalBillAmt);
    			    		ledgerDetails.setDpcAmount(dpcAmt);
    			    		ledgerDetails.setLedgerAmount(totalAmtPaid);
    			    //		ledgerDetails.setStatus(ledger.getStatus());
    			    //		ledgerDetails.setAdvanceAmount(ledger.getAdvanceAmount());
    			    		ledgerDetails.setLedgerDate(ledgerDate);
    			    		ledgerDetails.setRemarks(remarks);
    			    		ledgerDetails.setRecStatus(recStatus);
    			    		ledgerDetails.setTransType(CoreConstants.LEDGER_TRANS_TYPE_COLLECTION_LABEL);
    			    		
    			    		ledgerDetailsList.add(ledgerDetails);
    			    		
							totalBillAmt=0;
							totalAmtPaid=0;
							dpcAmt=0;
							transRef=0;
							ledgerDate="";
							collectionCeneter="";
							remarks="";
							recStatus=0;
						}
    			    }
						
    			    	//if(ledger.getGroupId()!=0)
						//{
	    			    	dupLedger = new Ledger(); 
				    		BeanUtils.copyProperties(ledger, dupLedger);
	    			    	transRef = Long.parseLong(ledger.getTransRef());
	    			    	ledgerDate = ledger.getLedgerDate();
	    			    	collectionCeneter=ledger.getCollectionCenter().getDescription();
							transType = 2;
							remarks=ledger.getRemarks();
							recStatus=ledger.getRecStatus();
							
								totalAmtPaid = totalAmtPaid + ledger.getLedgerAmount();
								totalBillAmt= totalBillAmt + ledger.getBillAmount() + ledger.getAdvanceAmount();							
								dpcAmt= dpcAmt + ledger.getDpcAmount();
						//}
							
    				}
    			}
    			if(transType == 2)
    			{
    				ledgerDetails= new LedgerDetails();
		    	//	ledgerDetails.setTransRef(collectionCeneter+", R.No. "+ transRef);
    				ledgerDetails.setTransRef(collectionCeneter);
		    		ledgerDetails.setCollectionAmount(totalBillAmt);
		    		ledgerDetails.setBillAmount(totalBillAmt);
		    		ledgerDetails.setDpcAmount(dpcAmt);
		    		ledgerDetails.setLedgerAmount(totalAmtPaid);
		    	//	ledgerDetails.setAdvanceAmount(ledger.getAdvanceAmount());
		    		ledgerDetails.setLedgerDate(ledgerDate);
		    		ledgerDetails.setTransType(CoreConstants.LEDGER_TRANS_TYPE_COLLECTION_LABEL);
		    		ledgerDetails.setRemarks(remarks);
		    		ledgerDetails.setRecStatus(recStatus);
		    		
		    		ledgerDetailsList.add(ledgerDetails);
    			}
    		}
    		modelAndView.addObject("ledgerDetailsList",ledgerDetailsList);	
    		//End:Commented By VenkataRao M on 16072015
       */
    	 }
    	}
    	
    /*	ArrayList<Object[]> list=collectionService.getos("","");
    	Object[] o=null;
    	String mFlag="";
    	boolean flag=false;
    	Bill bill=null;
    	
    	try{
    		
    	 if(list!=null){
    	for(int i=0;i<list.size();i++)
    	{
    		o=list.get(i);
    		
    		System.out.println("object 1:"+o[0]);
    		System.out.println("object 1:"+o[1]);
    		System.out.println("object 2:"+o[2].toString());
    		System.out.println("object 3:"+o[3]);   	
    		
    		if(o[0]!=null){
    		bill=billService.getPropertyBillDuesAmount(o[0].toString(),CommonUtils.getCurrentStringDate());
    		}
    	
    		String message="";
    		if(bill!=null){
    		if(bill.getBillAmt()>0){
    			 message ="Dear "+o[2]+", " +
    		           		" Your Property tax bill for Property no "+o[0]+" for the year 2022-2023 " +
    		           		 " is Rs "+ Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt()) +" .To check and pay your bill click on the link given below " +
    		           		 " https://aurangabadmahapalika.org:8443/TaxCollection/pg/property/getPropertyPgWebApi " +
    		                 " You may pay your bill online or at the nearest Citizen Facilitation Centre (CFC) Regards, Aurangabad Municipal Corporation";
    		}}
    	        
    	    
    	      String messageMarathi=" प्रिय "+o[2]+", तुमचे 2022-2023 या वर्षाचे मालमत्ता क्रमांक "+o[0]+" साठीचे मालमत्ता कराचे देयक "+ Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt()) +" रुपये आहे.तुमचे देयक तपासण्यासाठी तसेच देयकाचा भरणा करण्यासाठी खालील लिंकवर क्लिक करा https://aurangabadmahapalika.org:8443/TaxCollection/pg/property/getPropertyPgWebApi आपले देयक ऑनलाइन किंवा जवळच्या नागरिक सुविधा केंद्रांवर भरू शकता.औरंगाबाद महानगरपालिका";
    	      String message="Dear भाडेकरु शाखा व्यवस्थापक , Your Property tax bill for Property no "+o[0]+" for the year 2022-2023 is Rs "+ o[1] +".To check and pay your bill click on the link given below https://aurangabadmahapalika.org:8443/TaxCollection/pg/property/getPropertyPgWebApi You may pay your bill online or at the nearest Citizen Facilitation Centre (CFC)";
  			
    	       String message=" प्रिय "+o[2]+", तुमचे 2022-2023 या वर्ााचेमालमत्ता क्रमाांक "+o[0]+" साठीचेमालमत्ता किाचेदेयक "+
                           " "+ o[1] +" रुपयेआहे.तुमचेदेयक तपासण्यासाठी तसेच देयकाचा भिणा किण्यासाठी खालील प्रलांकवि क्लिक किा https://aurangabadmahapalika.org:8443/TaxCollection/pg/property/getPropertyPgWebApi आपलेदेयक ऑनलाइन प्रकां वा जवळच्य";
           
    	   
    	      System.out.println("message body ::"+message);
    	  
    	      if((o[1]!=null && o[2]!=null && (bill!=null) && o[3]!=null)){
    	    	 if(o[3].toString().length()==10 && (bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt()>0)){
  				flag=SendSMS.SendsSMStoApplicantNew(o[3].toString(),message);
    	    	 }
    	     }
  				 if(flag)
  				{
  					mFlag="success";
  				}
  				
  	    		System.out.println("Message Sent "+ mFlag +"fully**********"+o[3].toString()+"******* End ***"+ o[0]);
    	 }
    	  
    	}
    	 } catch (Exception e){
    		 e.printStackTrace();
    	 }*/
    	
    	request.setAttribute("propertyCode",propertyCode);
        request.setAttribute("searchParamSet", "true");
        modelAndView.addObject("propertyDetails", propertyDetails);
        modelAndView.addObject("propertyAssessmentList", assmntList);
        request.setAttribute("userId", sessionUser.getUserId());
        
    	return modelAndView;
    }  
  
    
    public ModelAndView getPropertyLedgerDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException
	{
	
		if (log.isDebugEnabled())
		{
	    	log.debug("Invoking getPropertyLedgerDetails");
		}	
		
    	HttpSession session = request.getSession();		
		ModelAndView modelAndView=new ModelAndView("manageLedgerDetails");
		String propertyCode = request.getParameter("searchCode");
		
    	if(propertyCode != null && propertyCode.length()==12)
    	{
    		Property property=propertyService.getPropertyByCode(propertyCode);
    		PropertyDetails propertyDetails=null;
    		if(property!=null)
    		{
    			propertyDetails=new PropertyDetails();
    			propertyDetails.setPropertyCode(propertyCode);
       			
    			double outStandingAmt =0;
    			Bill bill=null;
    			if(property.getOwner().getOwnerGroup().getOwnerGroupId()==1002)
				{
    				bill = billService.getPropertyBillDuesAmount_Midc(propertyCode.toUpperCase(),CommonUtils.getCurrentStringDate());
				} else 
				{
       			    bill = billService.getPropertyBillDuesAmount(propertyCode.toUpperCase(),CommonUtils.getCurrentStringDate());
				}
    			if(bill!=null){
        			propertyDetails.setBalance(Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt()));
        			request.setAttribute("rebateAmount", Math.round(bill.getRebateAmt()));
        			request.setAttribute("amount", Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt())-Math.round(bill.getRebateAmt()));
        			
        			}
        			else{
        				propertyDetails.setBalance(0);
        				request.setAttribute("rebateAmount", 0);
            			request.setAttribute("amount",0);
        			}
           		
           		modelAndView.addObject("propertyDetails",propertyDetails);   
           		LinkedHashMap<String, Object> finalMap = collectionService.getLedgerCalculationDetails(property);
           		LinkedHashMap<String, List<LedgerCalDetails>> demandMap = (LinkedHashMap<String, List<LedgerCalDetails>>)finalMap.get("demandMap");
           		
           		double finalDPC = 0;
           		if(finalMap.get("finalDPC") != null)
           			finalDPC = (Double)finalMap.get("finalDPC");
           		request.setAttribute("demandMap", demandMap);
           		request.setAttribute("finalDPC", finalDPC);
    		}
    	}else{
    		session.setAttribute("message", "Property A/c No you have entered is invalid.");
    	}   	
    	request.setAttribute("propertyCode", propertyCode);    	

    	return modelAndView;
	}    

    public boolean linkEnable(SessionUser sessionUser)
    {
 	   String roleIds=sessionUser.getRoleIds();
 	    boolean roleIdCheck=false;
 	    if( roleIds.contains(","))
 	    {
 	    	for (String retval: roleIds.split(",")){
 	    		if(retval.equals("390000035"))
 	    		{
 	    			roleIdCheck=true;	
 	    		}
 	            
 	         }
 	    	
 	    }
 	    else
 	    {	    	
 	    	if(roleIds.equals("390000035"))roleIdCheck=true;
 	    }
 	    
 	   return roleIdCheck;
 	   
    }
    
    @SuppressWarnings("unchecked")
	public <T> T checkValidAmnestyProperty(String propertyCode)
    {
    	List<Property> propertyList;
		Property propertyObj = null;
    	try
    	{
    		propertyObj = propertyService.getPropertyByCode(propertyCode);
    	    AmnestyRegistration	registerdAmnesty = collectionService.getA(propertyObj
					.getPropertyId());
    	    if(registerdAmnesty!=null && registerdAmnesty.getStatus()==1){
    	    	
    	    	String msg="Please Unregister Amnesty!!";
    	    	return (T)msg;
    	    }
    	    
    		propertyList = propertyService.validateProperty(propertyCode);
    		
			if(propertyList != null && propertyList.size() > 0)
				propertyObj = propertyList.get(0);
			return (T)propertyObj;
	    	} catch(Exception ex) {
	    		ex.printStackTrace();
	    	}
    	return null;
    }
    
    
    public ModelAndView getPropertyDetailspg(HttpServletRequest request,
			HttpServletResponse response) throws ServletException,
			ParseException {

		return new ModelAndView("index");
	}

    public ModelAndView printFailOnlineWebReceipt(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, ParseException
        {
            if(log.isDebugEnabled())
                log.debug("getPropertyDetails method called");
            HttpSession session = request.getSession();
            Map parameterMap = request.getParameterMap();
            Iterator i = parameterMap.keySet().iterator();
            String txnid1 = "";
            String propertyCode = "";
            Double amount = Double.valueOf(0.0D);
            String status = "";
            CollectionBuffer collectionBuffer = new CollectionBuffer();
            while(i.hasNext()) 
            {
                String key = (String)i.next();
                String value = ((String[])parameterMap.get(key))[0];
                if(key.equalsIgnoreCase("txnid"))
                    txnid1 = value;
                else
                if(key.equalsIgnoreCase("udf1"))
                    propertyCode = value;
                else
                if(key.equalsIgnoreCase("amount"))
                    amount = Double.valueOf(Double.parseDouble(value));
                else
                if(key.equalsIgnoreCase("status"))
                    status = value;
            }
            System.out.println((new StringBuilder()).append(amount).append(status).append(propertyCode).append("txnd1 is").append(txnid1).toString());
            CollectionBuffer cbf = new CollectionBuffer();
            Collection collection = new Collection();
            Property property = propertyService.getPropertyByCode(propertyCode);
            collectionBuffer.setCollectionBufferId(0L);
            DateFormat nmmc_dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            collectionBuffer.setCollectionDate(nmmc_dateFormat.format(new Date()));
            collectionBuffer.setPaymentMode(Integer.valueOf(3));
            collectionBuffer.setAmount(amount.doubleValue());
            collectionBuffer.setCollectionCenter(collectionCenterService.get(1L));
            collectionBuffer.setProperty(property);
            collectionBuffer.setStatus(Integer.valueOf(-1));
            collectionBuffer.setRefNumber(txnid1);
            if(property != null)
            {
                collectionBuffer = collectionBufferService.findByPropertyName("uniqPgId", txnid1);
                collection.setAmount(collectionBuffer.getAmount());
                collection.setProperty(property);
                collection.setCollectionDate(collectionBuffer.getCollectionDate());
                collection.setCollectionCenter(collectionBuffer.getCollectionCenter());
                collection.setPaymentMode(collectionBuffer.getPaymentMode());
                collection.setBank(collectionBuffer.getBank());
                collection.setBranch(collectionBuffer.getBranch());
                collection.setChequeOrDDNumber(collectionBuffer.getChequeOrDDNumber());
                collection.setCreatedBy(collectionBuffer.getCreatedBy());
                collection.setReceiptNo(collectionBuffer.getReceiptNo());
                collection.setStatus(Integer.valueOf(2));
                collection.setRefNumber(collectionBuffer.getRefNumber());
            }
            Bill bill = billService.getPropertyBillDuesAmount(propertyCode.toUpperCase(), CommonUtils.getCurrentStringDate());
            request.setAttribute("rebateAmount", Long.valueOf(Math.round(bill.getRebateAmt())));
            request.setAttribute("amount", Long.valueOf(Math.round((bill.getBillAmt() + bill.getDpcAmt1()) - bill.getAdvanceAmt()) - Math.round(bill.getRebateAmt())));
            request.setAttribute("sut", Long.valueOf(Math.round(bill.getRebateAmt())));
            request.setAttribute("previousamt", Long.valueOf(Math.round(bill.getPrevOsAmt())));
            request.setAttribute("currentamt", Long.valueOf(Math.round(bill.getBillAmt())));
            ModelAndView modelAndView = new ModelAndView("manageLedgerPgWeb");
            modelAndView.addObject("collection", collectionBuffer);
            return modelAndView;
        }
    
    
    public ModelAndView printOnlineWebReceipt(HttpServletRequest request,
			HttpServletResponse response) throws ServletException,
			ParseException {
     	
    	if(log.isDebugEnabled()){
    		log.debug("getPropertyDetails method called");
    	}
    	
    	
    	ModelAndView modelAndView = new ModelAndView();
    	HttpSession session = request.getSession();
    	Map<String, String[]> parameterMap = request.getParameterMap();
    	Iterator i = parameterMap.keySet().iterator();
    	String txnid1="";
    	String propertyCode="";
    	Double amount=0.0;
    	String status="";
    	String bankref="";
    	String responsehash = "";
    	boolean webflag=false;
    	
    	CollectionBuffer collectionBuffer = new CollectionBuffer();
    	Double amt1 = 0.0;
    	
    	while (i.hasNext()) {
    		String key = (String) i.next();
    		String value = ((String[]) parameterMap.get( key ))[ 0 ];
    		
    		if(key.equalsIgnoreCase("txnid")){
    			txnid1 = value;
    			
    		}
    		else if(key.equalsIgnoreCase("udf1")){
    			propertyCode = value;
    			
    		}
    		else if(key.equalsIgnoreCase("amount")){
    			amount =Double.parseDouble(value);
    			
    		}
    		else if(key.equalsIgnoreCase("status")){
    			status = value;
    			
    		}

    		 else if (key.equalsIgnoreCase("mihpayid")) {
    				bankref = value;

    			} else if (key.equalsIgnoreCase("hash")) {
    				responsehash = value;

    			}
    	}
    	
    		/*//
    		
    		String hashSequence ="SALT|status||||||udf5|udf4|udf3|udf2|udf1|email|firstname|productinfo|am ount|txnid|key";
    		
    		Map<String, String[]> params = request.getParameterMap();
    		Map<String, String> mapResult = new HashMap<String,String>();
    		Iterator paramNames = params.keySet().iterator();
    		
    		 while(paramNames.hasNext()) 
    		{
    		        String paramName = (String)paramNames.next();
    		        String paramValue = ((String[]) params.get(paramName))[ 0 ];
    		        
    		        mapResult.put(paramName,paramValue);
    		}
    		 String key = "7rnFly";
    		 String salt = "pjVQAWpA";
    		 
    		 String[] hashVarSeq=hashSequence.split("\\|");
    		 String retHashSeq=salt+'|';
    		     for(String part : hashVarSeq)
    		     {
    		         retHashSeq= ((params.get(part)==null))?retHashSeq.concat(""):retHashSeq.concat(mapResult.get(part));
    		         retHashSeq=retHashSeq.concat("|");
    		     }
    	
    	
    	retHashSeq = retHashSeq.substring(0,retHashSeq.length()-1);
    	System.out.println("retHashSeq:"+retHashSeq);
    	
    	String hash = hashCal("SHA-512", retHashSeq);
    	
    	System.out.println("hash :"+hash);
    	*/
    	
    	//hash code
    	
    	
    			String hashSequence ="status||||||udf5|udf4|udf3|udf2|udf1|email|firstname|productinfo|amount|txnid";
    			//String hashSequence ="key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5|||||salt";
    			
    			Map<String, String[]> params = request.getParameterMap();
    			Map<String, String> mapResult = new HashMap<String,String>();
    			Iterator paramNames = params.keySet().iterator();
    			
    			 while(paramNames.hasNext()) 
    			{
    			        String paramName = (String)paramNames.next();
    			        //System.out.println("Response param name--> :"+paramName);
    			        
    			        String paramValue = ((String[]) params.get(paramName))[ 0 ];
    			        //System.out.println("Response param Value---> :"+paramValue);
    			        
    			        mapResult.put(paramName,paramValue);
    			}
    			/* String key = "7rnFly";
    			 String salt = "pjVQAWpA";*/
    			 
    			 String key ="4B0F9V";
    			 String salt ="rKVJtlqlxH4skKAqAyi2xWjKBCpeFZZx";
    					 
    			 String[] hashVarSeq=hashSequence.split("\\|");
    			 String retHashSeq=salt+'|';
    			     for(String part : hashVarSeq)
    			     {
    			         retHashSeq= ((params.get(part)==null))?retHashSeq.concat(""):retHashSeq.concat(mapResult.get(part));
    			         retHashSeq=retHashSeq.concat("|");
    			     }


    		retHashSeq = retHashSeq.substring(0,retHashSeq.length()-1);
    		retHashSeq=retHashSeq+"|"+key;
    		//System.out.println("retHashSeq:"+retHashSeq);

    		String hash = hashCal("SHA-512", retHashSeq);

    			System.out.println("calculated hash form contr=&&&&&&&&&&&"+hash);
    			System.out.println("Res HashCode from Bank =&&&&&&&&"+responsehash);
    			
    			//hash code end
    	
    	
    	    Property property = propertyService.getPropertyByCode(propertyCode);	

    	   DateFormat nmmc_dateFormat = new SimpleDateFormat(Constants.DATE_UI_FORMAT);

    	//collectionBuffer.setCollectionBufferId(0);
    	/*collectionBuffer.setCollectionDate(nmmc_dateFormat.format(new Date()));
    	collectionBuffer.setPaymentMode(3);
    	
    	collectionBuffer.setAmount(amount);
    	collectionBuffer.setCollectionCenter(collectionCenterService.get(1));
    	collectionBuffer.setProperty(property);
    	 collectionBuffer.setStatus(3);
    	 collectionBuffer.setRefNumber(txnid1);*/ 
    	   
    	   CollectionBuffer cbref = collectionBufferService.findByPropertyName("refNumber", bankref);
    	   
    	   if(cbref!=null){
    		   status="false";
    	   }
    	   
    	if (propertyCode != null && (status.equals("success")))  {
    		List<CollectionBuffer> collectionBuffer1 = null;

    		if (txnid1 != null) {
    			String UniqPgid = txnid1;
    			collectionBuffer1 = collectionBufferService	.getBufferPgData(UniqPgid);
    		}
    		if(CollectionUtils.isNotEmpty(collectionBuffer1)){
    		 amt1 = collectionBuffer1.get(0).getAmount();
    		System.out.println("amout@@@=" + amt1);
    		}
    		if (amt1.equals(amount) && amount!=null) {
    			collectionBuffer.setAmount(amount);
    			System.out.println("amout@@@=" + amt1);

    			String connum = collectionBuffer1.get(0).getProperty().getCode();
    			if (connum.equals(propertyCode) && propertyCode!=null) {
    				System.out.println("amout@@@=" + connum);
    				collectionBuffer.setProperty(property);

    				String uniqid = collectionBuffer1.get(0).getUniqPgId();
    				if (uniqid.equals(txnid1) && txnid1!=null ) {
    					
    					if(hash.equals(responsehash)){
    						
    					System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@---"+collectionBuffer1.get(0).getCollectionCenter().getCollectionCenterId());
    						long CollectionBufferId=collectionBuffer1.get(0).getCollectionBufferId();
    						collectionBuffer.setCollectionBufferId(CollectionBufferId);
    						collectionBuffer.setUniqPgId(txnid1);
    			            collectionBuffer.setPaymentMode(3);
    						collectionBuffer.setCollectionCenter(collectionBuffer1.get(0).getCollectionCenter());
    						collectionBuffer.setCollectionDate(nmmc_dateFormat.format(new Date()));
    		                collectionBuffer.setStatus(0);
    						collectionBuffer.setAmount(amount);
    					    collectionBuffer.setRefNumber(txnid1);
    					    collectionBuffer.setRebateAmount(collectionBuffer1.get(0).getRebateAmount());
    					    collectionBuffer = collectionBufferService.merge(collectionBuffer);
    					   
    						 
    						
    						

    						Integer responsepg=getPgStatus(txnid1);
 							if(collectionBuffer.getCollectionBufferId()>0 && (responsepg>0) ){
 								webflag=true;
    							} else {
    									//modelAndView.setViewName("portalFailCollectionAcknowledgement");
    								//session.setAttribute("message", "your transaction has been declined ! Please Try Again.");
    								}
    								
    						}
    											 
    						 
    					     if ((collectionBuffer.getCollectionBufferId() > 0) && (webflag)) {
    					    		if(status.equals("success")){
    					    			 collectionBuffer.setStatus(0);
    					    			 } else {
    					    				 collectionBuffer.setStatus(-1); 
    					    			 }
    					    			 collectionBuffer.setRefNumber(bankref);
    					    			 //collectionBuffer=collectionBufferService.merge(collectionBuffer);
    					    			 
    					    			
    					    			 com.nmmc.property.model.Collection collection=new com.nmmc.property.model.Collection();
    					    	            
    					    				
    					    				collection.setAmount(collectionBuffer.getAmount());
    					    				collection.setProperty(property);;
    					    				collection.setCollectionDate(collectionBuffer.getCollectionDate());
    					    				collection.setCollectionCenter(collectionBuffer.getCollectionCenter());
    					    				collection.setPaymentMode(collectionBuffer.getPaymentMode());
    					    				collection.setBank(collectionBuffer.getBank());
    					    				collection.setBranch(collectionBuffer.getBranch());
    					    				collection.setChequeOrDDNumber(collectionBuffer.getChequeOrDDNumber());
    					    				collection.setCreatedBy(collectionBuffer.getCreatedBy());
    					    				collection.setReceiptNo(collectionBuffer.getReceiptNo());
    					    				collection.setStatus(2);
    					    				collection.setRefNumber(collectionBuffer.getRefNumber());
    					    				Bill bill = billService.getPropertyBillDuesAmount(propertyCode.toUpperCase(), CommonUtils.getCurrentStringDate());
    					    	            request.setAttribute("rebateAmount", Long.valueOf(Math.round(bill.getRebateAmt())));
    					    	            request.setAttribute("amount", Long.valueOf(Math.round((bill.getBillAmt() + bill.getDpcAmt1()) - bill.getAdvanceAmt()) - Math.round(bill.getRebateAmt())));
    					    	            request.setAttribute("sut", Long.valueOf(Math.round(bill.getRebateAmt())));
    					    	            request.setAttribute("previousamt", Long.valueOf(Math.round(bill.getPrevOsAmt())));
    					    	            request.setAttribute("currentamt", Long.valueOf(Math.round(bill.getBillAmt())));
    					    	           
    					    				   
    					    				
    					    			    Long collectionId=collectionService.saveCollectionFunction(collection);
    					    			    if(collectionId>0){
    					    			    	collectionBuffer.setStatus(3);
    					    			    	collectionBuffer=collectionBufferService.merge(collectionBuffer);
    					    			    	try{
    					    						if(property.getOwner()!=null && property.getOwner().getAddress()!=null 
    					    								&& property.getOwner().getAddress().getMobileNumber()!=null){
    					    							String eMobile=property.getOwner().getAddress().getMobileNumber();
    					    							if((eMobile.length()==10))
    					    							{
    					    								System.out
																	.println("sfsdg################"+eMobile);
    					    								String link = "https://tinyurl.com/yyz464ej?collectioId="+collectionId;
    	    					    						String mobileString =
    	    					    								
    	    					    								" Property Tax payment amount of Rs.  " + collectionBuffer.getAmount() +
    	    					    								" received for Property No. " + property.getCode() +
    	    					    								" To download the receipt click on the following link" + link +
    	    					    								" Regards,AMCORP"; 
    					    								
    					    											    //http://smsatm.net/v3/api.php?username=ASCDCL&apikey=c01f32640f54e44f7660&senderid=AMCGOV&templateid=1707164879816155540		
    					    						boolean flag1=SendSMS.sendSingleSMS("1707166943840528676","ASCDCL",eMobile,mobileString);
    					    							}
    					    						}
    					    						} catch (Exception e) {
    					    							//collectionRestDTO.setResponseCode(CoreConstants.WEBSERVICE_RESPONSECODE_FAILURE);
    					    							//collectionRestDTO.setResponseMessage("Collection not saved : "+e.getMessage());
    					    							e.printStackTrace();
    					    						}
    					    			    	System.out.println("collection saved successfully for property");
    					    			    } else {
    											//modelAndView.setViewName("portalFailCollectionAcknowledgement");
    					    			    	//session.setAttribute("message", "your transaction has been declined ! Please Try Again.");
    										}
    					    			 			
    					    		}
    					    		
    					
    					} else {
    						//modelAndView.setViewName("portalFailCollectionAcknowledgement");
    						//session.setAttribute("message", "your transaction has been declined ! Please Try Again.");
    					}
    					
    					
    				} else {
    					//modelAndView.setViewName("portalFailCollectionAcknowledgement");
    					//.setAttribute("message", "your transaction has been declined ! Please Try Again.");
    				}
    				
    				modelAndView.addObject("collection",collectionBuffer);
    				modelAndView.setViewName("portalCollectionAcknowledgement");

    			} else {
    				//session.setAttribute("message", "your transaction has been declined ! Please Try Again.");
    				//modelAndView.setViewName("portalFailCollectionAcknowledgement");
    				
    			}
    		}
    	//}	


    		return modelAndView;
	}
    
    public ModelAndView printOnlinePortalReceipt(HttpServletRequest request,
			HttpServletResponse response) throws ServletException,
			ParseException {
    	
	if(log.isDebugEnabled()){
		log.debug("getPropertyDetails method called");
	}
	HttpSession session = request.getSession();
	Map<String, String[]> parameterMap = request.getParameterMap();
	Iterator i = parameterMap.keySet().iterator();
	String txnid1="";
	String propertyCode="";
	Double amount=0.0;
	String status="";
	String bankref="";
	CollectionBuffer collectionBuffer = new CollectionBuffer();
	while (i.hasNext()) {
		String key = (String) i.next();
		String value = ((String[]) parameterMap.get( key ))[ 0 ];
		if(key.equalsIgnoreCase("txnid")){
			txnid1 = value;
			
		}
		else if(key.equalsIgnoreCase("udf1")){
			propertyCode = value;
			
		}
		else if(key.equalsIgnoreCase("amount")){
			amount =Double.parseDouble(value);
			
		}
		else if(key.equalsIgnoreCase("status")){
			status = value;
			
		}
		
		 else if (key.equalsIgnoreCase("bank_ref_num")) {
				bankref = value;

			}
	}
	System.out.println(amount+status+propertyCode+"txnd1 is"+txnid1);
	CollectionBuffer cbf = new CollectionBuffer();
	Property property = propertyService.getPropertyByCode(propertyCode);	
	/*collectionBuffer.setCollectionBufferId(0);
    DateFormat nmmc_dateFormat = new SimpleDateFormat(Constants.DATE_UI_FORMAT);
	collectionBuffer.setCollectionDate(nmmc_dateFormat.format(new Date()));
	collectionBuffer.setPaymentMode(3);
	
	collectionBuffer.setAmount(amount);
	collectionBuffer.setCollectionCenter(collectionCenterService.get(1));
	collectionBuffer.setProperty(property);
	 collectionBuffer.setStatus(3);
	 collectionBuffer.setRefNumber(txnid1); 
	
	//collectionBuffer.setDpcWaiveOffAmount(collectionRestDTO.getDpcWaiveOffAmount());
	
     collectionBuffer=collectionBufferService.merge(collectionBuffer);
*/
	
	
	if(property != null)
	{		
		
		collectionBuffer = collectionBufferService.findByPropertyName("ref_no", txnid1);
		
		System.out.println("collectionBuffer object is"+collectionBuffer.toString());
		if(status.equals("success")){
		 collectionBuffer.setStatus(CoreConstants.COLLECTION_BUFFER_STATUS_VERIFIED_FROM_PORTAL);
		 } else {
			 collectionBuffer.setStatus(-1); 
		 }
		 collectionBuffer.setRefNumber(bankref);
		 collectionBuffer=collectionBufferService.merge(collectionBuffer);
		 
		
		 com.nmmc.property.model.Collection collection=new com.nmmc.property.model.Collection();
            
			
			collection.setAmount(collectionBuffer.getAmount());
			collection.setProperty(property);;
			collection.setCollectionDate(collectionBuffer.getCollectionDate());
			collection.setCollectionCenter(collectionBuffer.getCollectionCenter());
			collection.setPaymentMode(collectionBuffer.getPaymentMode());
			collection.setBank(collectionBuffer.getBank());
			collection.setBranch(collectionBuffer.getBranch());
			collection.setChequeOrDDNumber(collectionBuffer.getChequeOrDDNumber());
			collection.setCreatedBy(collectionBuffer.getCreatedBy());
			collection.setReceiptNo(collectionBuffer.getReceiptNo());
			collection.setStatus(2);
			collection.setRefNumber(collectionBuffer.getRefNumber());
			
			   
			//collectionService.save(collection);
			
		    Long	collectionId=collectionService.saveCollectionFunction(collection);
		    if(collectionId>0){
		    	try{
					if(property.getOwner()!=null && property.getOwner().getAddress()!=null 
							&& property.getOwner().getAddress().getMobileNumber()!=null){
						String eMobile=property.getOwner().getAddress().getMobileNumber();
						if((eMobile.length()==10))
						{
							String link = "https://tinyurl.com/yyz464ej?collectioId="+collectionId;
    						String mobileString =
    								
    								" Property Tax payment amount of Rs.  " + collectionBuffer.getAmount() +
    								" received for Property No. " + property.getCode() +
    								" To download the receipt click on the following link" + link +
    								" Regards,AMCORP"; 
    								
    											    //http://smsatm.net/v3/api.php?username=ASCDCL&apikey=c01f32640f54e44f7660&senderid=AMCGOV&templateid=1707164879816155540		
    						boolean flag1=SendSMS.sendSingleSMS("1707166943840528676","ASCDCL",eMobile,mobileString);
						}
					}
					} catch (Exception e) {
						//collectionRestDTO.setResponseCode(CoreConstants.WEBSERVICE_RESPONSECODE_FAILURE);
						//collectionRestDTO.setResponseMessage("Collection not saved : "+e.getMessage());
						e.printStackTrace();
					}
		    	System.out.println("collection saved successfully for property");
		    }
		 			
	}
	
	ModelAndView modelAndView = new ModelAndView("portalCollectionAcknowledgementWeb");
		modelAndView.addObject("collection",collectionBuffer);
		
		return modelAndView;
	
	}

    
    public ModelAndView printFailOnlineReceipt(HttpServletRequest request,
			HttpServletResponse response) throws ServletException,
			ParseException {
    	
	if(log.isDebugEnabled()){
		log.debug("getPropertyDetails method called");
	}
	HttpSession session = request.getSession();
	Map<String, String[]> parameterMap = request.getParameterMap();
	Iterator i = parameterMap.keySet().iterator();
	String txnid1="";
	String propertyCode="";
	Double amount=0.0;
	String status="";
	CollectionBuffer collectionBuffer = new CollectionBuffer();
	while (i.hasNext()) {
		String key = (String) i.next();
		String value = ((String[]) parameterMap.get( key ))[ 0 ];
		if(key.equalsIgnoreCase("txnid")){
			txnid1 = value;
			
		}
		else if(key.equalsIgnoreCase("udf1")){
			propertyCode = value;
			
		}
		else if(key.equalsIgnoreCase("amount")){
			amount =Double.parseDouble(value);
			
		}
		else if(key.equalsIgnoreCase("status")){
			status = value;
			
		}
	}
	System.out.println(amount+status+propertyCode+"txnd1 is"+txnid1);
	
	CollectionBuffer cbf = new CollectionBuffer();
	Collection collection= new Collection();
	Property property = propertyService.getPropertyByCode(propertyCode);	
	collectionBuffer.setCollectionBufferId(0);
    DateFormat nmmc_dateFormat = new SimpleDateFormat(Constants.DATE_UI_FORMAT);
	collectionBuffer.setCollectionDate(nmmc_dateFormat.format(new Date()));
	collectionBuffer.setPaymentMode(3);
	
	collectionBuffer.setAmount(amount);
	collectionBuffer.setCollectionCenter(collectionCenterService.get(1));
	collectionBuffer.setProperty(property);
	 collectionBuffer.setStatus(-1);
	 collectionBuffer.setRefNumber(txnid1); 
	
	//collectionBuffer.setDpcWaiveOffAmount(collectionRestDTO.getDpcWaiveOffAmount());
	
     ////collectionBuffer=collectionBufferService.merge(collectionBuffer);

	//collectionBuffer = collectionBufferService.findByPropertyName("refNumber", txnid1);
	
	if(property != null)
	{
		
		collectionBuffer = collectionBufferService.findByPropertyName("uniqPgId", txnid1);
		
			
			collection.setAmount(collectionBuffer.getAmount());
			collection.setProperty(property);;
			collection.setCollectionDate(collectionBuffer.getCollectionDate());
			collection.setCollectionCenter(collectionBuffer.getCollectionCenter());
			collection.setPaymentMode(collectionBuffer.getPaymentMode());
			 collection.setBank(collectionBuffer.getBank());
			collection.setBranch(collectionBuffer.getBranch());
			collection.setChequeOrDDNumber(collectionBuffer.getChequeOrDDNumber());
			collection.setCreatedBy(collectionBuffer.getCreatedBy());
			collection.setReceiptNo(collectionBuffer.getReceiptNo());
			collection.setStatus(2);
			collection.setRefNumber(collectionBuffer.getRefNumber());
	
	}
	
	ModelAndView modelAndView = new ModelAndView("manageLedgerPg");
		modelAndView.addObject("collection",collectionBuffer);
		
		return modelAndView;
	
	}
    
    //For failed Payment--old changed on 3-02-2022
    
    public ModelAndView printOnlineReceipt_old(HttpServletRequest request,
			HttpServletResponse response) throws ServletException,
			ParseException {
    	
	if(log.isDebugEnabled()){
		log.debug("getPropertyDetails method called");
	}
	
	
	ModelAndView modelAndView = new ModelAndView();
	HttpSession session = request.getSession();
	Map<String, String[]> parameterMap = request.getParameterMap();
	Iterator i = parameterMap.keySet().iterator();
	String txnid1="";
	String propertyCode="";
	Double amount=0.0;
	String status="";
	    String bankref="";
	CollectionBuffer collectionBuffer = new CollectionBuffer();
	Double amt1 = 0.0;
	while (i.hasNext()) {
		String key = (String) i.next();
		String value = ((String[]) parameterMap.get( key ))[ 0 ];
		if(key.equalsIgnoreCase("txnid")){
			txnid1 = value;
			
		}
		else if(key.equalsIgnoreCase("udf1")){
			propertyCode = value;
			
		}
		else if(key.equalsIgnoreCase("amount")){
			amount =Double.parseDouble(value);
			
		}
		else if(key.equalsIgnoreCase("status")){
			status = value;
			
		}

		 else if (key.equalsIgnoreCase("mihpayid")) {
				bankref = value;

			} 
	}
	
	Property property = propertyService.getPropertyByCode(propertyCode);	

	   DateFormat nmmc_dateFormat = new SimpleDateFormat(Constants.DATE_UI_FORMAT);

	//collectionBuffer.setCollectionBufferId(0);
	/*collectionBuffer.setCollectionDate(nmmc_dateFormat.format(new Date()));
	collectionBuffer.setPaymentMode(3);
	
	collectionBuffer.setAmount(amount);
	collectionBuffer.setCollectionCenter(collectionCenterService.get(1));
	collectionBuffer.setProperty(property);
	 collectionBuffer.setStatus(3);
	 collectionBuffer.setRefNumber(txnid1);*/ 
	   
	   CollectionBuffer cbref = collectionBufferService.findByPropertyName("refNumber", bankref);
	   
	   if(cbref!=null){
		   status="false";
	   }
	   
	if (propertyCode != null && (status.equals("success")))  {
		List<CollectionBuffer> collectionBuffer1 = null;

		if (txnid1 != null) {
			String UniqPgid = txnid1;
			collectionBuffer1 = collectionBufferService	.getBufferPgData(UniqPgid);
		}
		if(CollectionUtils.isNotEmpty(collectionBuffer1)){
		 amt1 = collectionBuffer1.get(0).getAmount();
		System.out.println("amout@@@=" + amt1);
		}
		if (amt1.equals(amount) && amount!=null) {
			collectionBuffer.setAmount(amount);
			System.out.println("amout@@@=" + amt1);

			String connum = collectionBuffer1.get(0).getProperty().getCode();
			if (connum.equals(propertyCode) && propertyCode!=null) {
				System.out.println("amout@@@=" + connum);
				collectionBuffer.setProperty(property);

				String uniqid = collectionBuffer1.get(0).getUniqPgId();
				if (uniqid.equals(txnid1) && txnid1!=null ) {
					String UniqPgid1 = collectionBuffer1.get(0)
							.getUniqPgId();
					//UniqPgid1 = hashCal("SHA-256", UniqPgid1).substring(0,
							//20);
					//txnid1 = hashCal("SHA-256", txnid1).substring(0, 20);
					//System.out.println("hashcode Data basesame***********="
							//+ UniqPgid1);
					//System.out
							//.println("hashcode  from Bank same***********="
									//+ txnid1);
					//System.out.println("hashcode of transation" + txnid1);
					//if (UniqPgid1.equals(txnid1)) {
						System.out.println("hashcode same***********");
						long CollectionBufferId=collectionBuffer1.get(0).getCollectionBufferId();
						collectionBuffer.setCollectionBufferId(CollectionBufferId);
						
						//collectionBuffer.setBillDueDate(nmmc_dateFormat.format(new Date()));
						//collectionBuffer.setReferenceNumber(txnid1);
						collectionBuffer.setUniqPgId(txnid1);
						//collectionBuffer.setPaymentType(3);
						collectionBuffer.setPaymentMode(3);
						//collectionBuffer.setChequeNumber("123");
						collectionBuffer.setCollectionCenter(collectionCenterService.get(1));// 1
						collectionBuffer.setCollectionDate(nmmc_dateFormat.format(new Date()));
						
						collectionBuffer.setStatus(3);
						collectionBuffer.setAmount(amount);
						 collectionBuffer.setRefNumber(txnid1);
						collectionBuffer = collectionBufferService.merge(collectionBuffer);
					     collectionBuffer=collectionBufferService.merge(collectionBuffer);
					     if (collectionBuffer.getCollectionBufferId() > 0) {
					    		if(status.equals("success")){
					    			 collectionBuffer.setStatus(CoreConstants.COLLECTION_BUFFER_STATUS_VERIFIED_FROM_PORTAL);
					    			 } else {
					    				 collectionBuffer.setStatus(0); 
					    			 }
					    			 collectionBuffer.setRefNumber(bankref);
					    			 collectionBuffer=collectionBufferService.merge(collectionBuffer);
					    			 
					    			
					    			 com.nmmc.property.model.Collection collection=new com.nmmc.property.model.Collection();
					    	            
					    				
					    				collection.setAmount(collectionBuffer.getAmount());
					    				collection.setProperty(property);;
					    				collection.setCollectionDate(collectionBuffer.getCollectionDate());
					    				collection.setCollectionCenter(collectionBuffer.getCollectionCenter());
					    				collection.setPaymentMode(collectionBuffer.getPaymentMode());
					    				collection.setBank(collectionBuffer.getBank());
					    				collection.setBranch(collectionBuffer.getBranch());
					    				collection.setChequeOrDDNumber(collectionBuffer.getChequeOrDDNumber());
					    				collection.setCreatedBy(collectionBuffer.getCreatedBy());
					    				collection.setReceiptNo(collectionBuffer.getReceiptNo());
					    				collection.setStatus(2);
					    				collection.setRefNumber(collectionBuffer.getRefNumber());
					    				
					    				   
					    				
					    			    Long	collectionId=collectionService.saveCollectionFunction(collection);
					    			    if(collectionId>0){
					    			    	try{
					    						if(property.getOwner()!=null && property.getOwner().getAddress()!=null 
					    								&& property.getOwner().getAddress().getMobileNumber()!=null){
					    							String eMobile=property.getOwner().getAddress().getMobileNumber();
					    							if((eMobile.length()==10))
					    							{
					    								String link = "https://tinyurl.com/yyz464ej?"+collectionId;
	    					    						String mobileString =
	    					    								
	    					    								" Property Tax payment amount of Rs.  " + collectionBuffer.getAmount() +
	    					    								" received for Property No. " + property.getCode() +
	    					    								" To download the receipt click on the following link" + link +
	    					    								" Regards,AMCORP"; 
	    					    								
	    					    											    //http://smsatm.net/v3/api.php?username=ASCDCL&apikey=c01f32640f54e44f7660&senderid=AMCGOV&templateid=1707164879816155540		
	    					    						boolean flag1=SendSMS.sendSingleSMS("1707166943840528676","ASCDCL",eMobile,mobileString);
					    							}
					    						}
					    						} catch (Exception e) {
					    							//collectionRestDTO.setResponseCode(CoreConstants.WEBSERVICE_RESPONSECODE_FAILURE);
					    							//collectionRestDTO.setResponseMessage("Collection not saved : "+e.getMessage());
					    							e.printStackTrace();
					    						}
					    			    	System.out.println("collection saved successfully for property");
					    			    }
					    			 			
					    		}
					    		
					
					//}
					
					
				}
				
				modelAndView.addObject("collection",collectionBuffer);
				modelAndView.setViewName("portalCollectionAcknowledgement");

			} else {

				modelAndView.setViewName("portalFailCollectionAcknowledgement");
				
			}
		}
	}

	

	
    
	

		return modelAndView;
	
	}

    

    public ModelAndView getPropertyOs(HttpServletRequest request,
			HttpServletResponse response) throws ServletException,
			ParseException {
    	
    	
    	PropertyDetails propertyDetails = new PropertyDetails();
    	HttpSession session = request.getSession();

		ModelAndView modelAndView = new ModelAndView();

    	
    	String propertyCode = request.getParameter("searchCode");
    	Property property = propertyService.getPropertyByCode(propertyCode);
    	if (propertyCode != null && propertyCode.length() == 12) {
			property = propertyService.getPropertyByCode(propertyCode);

			if (property == null) {

				modelAndView.setViewName("amount");
				session.setAttribute("message",
						"Property A/c No you have entered is invalid.");
			}

			if (property != null) {
				
				propertyDetails.setPropertyCode(propertyCode);
				propertyDetails.setOwnerName(property.getOwner().getFullName());

				if (property.getSubOwner() != null
						&& property.getSubOwner().getOwnerId() > 0)
					propertyDetails.setSubOwnerName(property.getSubOwner()
							.getFullName());
				else
					propertyDetails.setSubOwnerName("-");

				propertyDetails.setPropertyAddress(property
						.getPropertyAddress());
				propertyDetails.setCityAndState(property.getCitiAndState());
				
				Bill bill =null;
				bill = billService.getPropertyBillDuesAmount(propertyCode,CommonUtils.getCurrentStringDate());
				if(bill!=null){
	    			propertyDetails.setBalance(Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt()));
	    			request.setAttribute("rebateAmount", Math.round(bill.getRebateAmt()));
	    			request.setAttribute("amount", Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt())-Math.round(bill.getRebateAmt()));
	    			
	    			}
	    			else{
					propertyDetails.setBalance(0);
	    				request.setAttribute("rebateAmount", 0);
	        			request.setAttribute("amount",0);
	    			}

	

			}}
    	
    	modelAndView.addObject("propertyDetails", propertyDetails);
    	modelAndView.setViewName("amount");
    	
		return modelAndView;
	}
   /* Web_View Property
    * DATE 12-Jun- 22
    * With New
    * Recipt*/
    public ModelAndView getPropertyPgWebApi(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, ParseException
        {   if(log.isDebugEnabled())
            log.debug("getProperty Details  method called");
        HttpSession session = request.getSession();
        ModelAndView modelAndView = new ModelAndView("manageLedgerPgWeb");
        PropertyDetails propertyDetails = null;
        Bill bill=null;
        String propertyCode = request.getParameter("searchCode");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String wardId = request.getParameter("wardId");
        //saveConsumerDetails(bank_Propertycode, bank_Amt, bank_Rndm);
       
        
        List<Property> plist = null;
        List<PropertyDetails> pdetailslist = new ArrayList();
        if(!StringUtils.isBlank(firstName) || !StringUtils.isBlank(lastName) || !StringUtils.isBlank(propertyCode))
        {
            plist = propertyService.getPropertyListQuery(wardId, firstName, lastName, propertyCode);
            if(plist.size() == 0){
                request.setAttribute("message", "no results found");
            }
            else if (plist.size() > 100) {
				request.setAttribute("message",
						"search result cannot exceeed 100 results");

			}
        }
        if(CollectionUtils.isNotEmpty(plist))
        {
            for(Iterator iterator = plist.iterator(); iterator.hasNext(); pdetailslist.add(propertyDetails))
            {
                Property prop = (Property)iterator.next();
                propertyDetails = new PropertyDetails();
                propertyDetails.setPropertyCode(prop.getCode());
                propertyDetails.setOwnerName(prop.getOwner().getFullName());
                if(prop.getSubOwner() != null && prop.getSubOwner().getOwnerId() > 0L)
                    propertyDetails.setSubOwnerName(prop.getSubOwner().getFullName());
                else
                    propertyDetails.setSubOwnerName("-");
                propertyDetails.setPropertyAddress(prop.getOwner().getAddress().getArea());
                propertyDetails.setCityAndState(prop.getCitiAndState());
                if(prop.getOwner().getOwnerGroup().getOwnerGroupId() == 1002L)
                    propertyDetails.setBalance(billService.getPropertyBillDuesAmount_Midc(propertyDetails.getPropertyCode().toUpperCase()));
                else
                	 propertyDetails.setBalance(billService.getPropertyBillDuesAmount(propertyDetails.getPropertyCode().toUpperCase()));
                bill=billService.getPropertyBillDuesAmount(propertyDetails.getPropertyCode().toUpperCase(),CommonUtils.getCurrentStringDate());
                if(bill!=null){

                	propertyDetails.setBalance(billService.getPropertyBillDuesAmount(propertyDetails.getPropertyCode().toUpperCase()));
    				bill=billService.getPropertyBillDuesAmount(propertyDetails.getPropertyCode().toUpperCase(),CommonUtils.getCurrentStringDate());
    				//Double genralTax=bill.getBillDetails()
    				
    				
    				propertyDetails.setBalance(Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt()));
    				
    				//syj-01-07-2023 for online rebate 5%
    				//request.setAttribute("rebateAmount", (Math.round(bill.getRebateAmt())+Math.round((bill.getRebateAmt()*5)/8)));
    				request.setAttribute("rebateAmount", Math.round(bill.getRebateAmt()));
    				
    				//syj-01-07-2023 for online rebate 5%
    				//long rebateAmount=Math.round(bill.getRebateAmt())+Math.round((bill.getRebateAmt()*5)/8);
        			
    				long rebateAmount=Math.round(bill.getRebateAmt());
    				
        			System.out.println("adavnce:"+Math.round(bill.getRebateAmt()));
        			
        			//syj-01-07-2023 for online rebate 5%
        			//long amountafterrebate = Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt())-Math.round(bill.getRebateAmt()+(Math.round((bill.getRebateAmt()*5)/8)));
        			//request.setAttribute("amount", Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt())-Math.round(bill.getRebateAmt()+Math.round((bill.getRebateAmt()*5)/8)));;
        			
        			long amountafterrebate = Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt())-Math.round(bill.getRebateAmt());
        			request.setAttribute("amount", Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt())-Math.round(bill.getRebateAmt()));
        			
        			request.setAttribute("payamout",amountafterrebate);
        			
        			if(prop.getOwner().getFullName()!=null && prop.getOwner().getFullName()!=""){
        				request.setAttribute("owner",prop.getOwner().getFullName());
        				 
        			} else {
        				request.setAttribute("owner","None");
        			}
        			String mobile=prop.getOwner().getAddress().getMobileNumber();
        			System.out.println("mobile***********"+mobile);
        			if(mobile!=null && StringUtils.isNotEmpty(mobile) && StringUtils.isNotBlank(mobile) && mobile!=""){
        				request.setAttribute("mobile",prop.getOwner().getAddress().getMobileNumber());
        			} else {
        				request.setAttribute("mobile","None");
        			}
        			String email=prop.getOwner().getAddress().getEmailId();
        			if(email!=null &&  StringUtils.isNotEmpty(email) && StringUtils.isNotBlank(email)){
        				request.setAttribute("email",prop.getOwner().getAddress().getEmailId());
        			} else {
        				request.setAttribute("email","None");
        			}
             
                }
            }

        }
        Random rand = new Random();
        String rndm = (new StringBuilder(String.valueOf(Integer.toString(rand.nextInt())))).append(System.currentTimeMillis() / 1000L).toString();
        txnid = hashCal("SHA-256", rndm).substring(0, 20);
        request.setAttribute("wardList", wardService.getAll());
        request.setAttribute("txnid", txnid);
        request.setAttribute("propertyCode", propertyCode);
        request.setAttribute("firstName", firstName);
        request.setAttribute("lastName", lastName);
        request.setAttribute("searchParamSet", "true");
        modelAndView.addObject("propertydetailslist", pdetailslist);

       
        String paybleAmount = request.getParameter("Payamt");
		String paybleConectionNumber = request.getParameter("connectionNumber");
		String randomnum = request.getParameter("randomnum");
		String rebate= request.getParameter("rebateAmount");
		System.out.println("cvdsfvgdfbdfgfbfg@@@@@@="+rebate);
		
		
		 if(!StringUtils.isBlank(paybleAmount)){

			 double amountafterrebate=Double.valueOf(paybleAmount)-Double.valueOf(rebate);
						 request.setAttribute("paybleAmount", amountafterrebate);  
						 request.setAttribute("paybleConectionNumber",paybleConectionNumber);
						 request.setAttribute("randomnum", randomnum.trim());
			            Property prop = propertyService.getPropertyByCode(paybleConectionNumber);
			            CollectionBuffer collectionBuffer = new CollectionBuffer();
			            DateFormat nmmc_dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			            System.out.println("serch save buffer+++++++++++++");
			            collectionBuffer.setCollectionBufferId(0L);
			            nmmc_dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			            collectionBuffer.setCollectionDate(nmmc_dateFormat.format(new Date()));
			            collectionBuffer.setPaymentMode(Integer.valueOf(3));
			            collectionBuffer.setAmount(amountafterrebate);
			            long WardId=prop.getWard().getWardId();
			            String pgCollectionId=String.valueOf(WardId);
			           
			            List<CollectionCenter> CollectionCenterList = collectionCenterService.findByProperty("pgcollectionid", pgCollectionId);
			            
			            long collectionCenterId=CollectionCenterList.get(0).getCollectionCenterId();
			            CollectionCenter center=collectionCenterService.get(collectionCenterId);
			            collectionBuffer.setCollectionCenter(center);
			            collectionBuffer.setProperty(prop);
			            System.out.println((new StringBuilder("Save Property Controller@@@@@@=")).append(collectionBuffer.getProperty().getCode()).toString());
			            System.out.println((new StringBuilder("Save Property Controller@@@@@@=")).append(collectionBuffer.getProperty().getPropertyId()).toString());
			            collectionBuffer.setStatus(Integer.valueOf(-1));
			            collectionBuffer.setUniqPgId(randomnum);
			            collectionBuffer.setRebateAmount(Double.parseDouble(rebate));
			            
			            collectionBuffer = collectionBufferService.merge(collectionBuffer);
        }
       
            return modelAndView;
        }

       



       
    private  String hashCal(String type, String str) {
        byte[] hashseq = str.getBytes();
        StringBuffer hexString = new StringBuffer();
        try {
        	System.out.println("in hashcal method");
            MessageDigest algorithm = MessageDigest.getInstance(type);
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }

            
        } catch (NoSuchAlgorithmException nsae) {
        }
        return hexString.toString();
    }
	
    public ModelAndView getPropertyPgApi(HttpServletRequest request,HttpServletResponse response) throws ServletException, ParseException
    {
    	if(log.isDebugEnabled()){
    		log.debug("getProperty Details  method called");
    	}
    	HttpSession session = request.getSession();
    	ModelAndView modelAndView=new ModelAndView("manageLedgerPg");
    	PropertyDetails propertyDetails=null;
    	String propertyCode=request.getParameter("searchCode");    	
    	String firstName=request.getParameter("firstName");
    	String lastName=request.getParameter("lastName");
    	String wardId=request.getParameter("wardId");
    	CollectionBuffer collectionBuffer=new CollectionBuffer();
    	
    	
		List<Property> plist=null;
		List<PropertyDetails> pdetailslist=new ArrayList<PropertyDetails>();
		 
		
		
		    if(!StringUtils.isBlank(firstName) || !StringUtils.isBlank(lastName)   || !StringUtils.isBlank(propertyCode)){
    		
			     plist=propertyService.getPropertyListQuery( wardId, firstName, lastName,propertyCode);
    		
    		
    		
			    if(CollectionUtils.isEmpty(plist)){
    			
    			request.setAttribute("message", "no results found");
    			
    		 }
    		 
    		
    	}
    	
		      //String status=getPgStatus("b217a92b04238773bd9");
		     
		     //System.out.println("status pg XXXXX:"+status);
		     
      if(CollectionUtils.isNotEmpty(plist)){
    	  double amountafterrebate=0.0;
    	for(Property prop:plist){
    		propertyDetails=new PropertyDetails();
    		propertyDetails.setPropertyCode(prop.getCode());
			propertyDetails.setOwnerName(prop.getOwner().getFullName());
			if(prop.getSubOwner() != null && prop.getSubOwner().getOwnerId()>0){
			 propertyDetails.setSubOwnerName(prop.getSubOwner().getFullName());
			}else{
			 propertyDetails.setSubOwnerName("-");
			}
			propertyDetails.setPropertyAddress(prop.getOwner().getAddress().getArea());
			propertyDetails.setCityAndState(prop.getCitiAndState());
			if(prop.getOwner().getOwnerGroup().getOwnerGroupId()==1002)
			{
				propertyDetails.setBalance(billService.getPropertyBillDuesAmount_Midc(propertyCode.toUpperCase()));
			} else {
				propertyDetails.setBalance(billService.getPropertyBillDuesAmount(propertyCode.toUpperCase()));
				Bill bill=billService.getPropertyBillDuesAmount(propertyCode.toUpperCase(),CommonUtils.getCurrentStringDate());
				
				
				propertyDetails.setBalance(Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt()));
    			
				//syj-commntted for 5% online rebate
				//request.setAttribute("rebateAmount", Math.round(bill.getRebateAmt()+Math.round((bill.getRebateAmt()*5)/8)));
    			 
				request.setAttribute("rebateAmount", Math.round(bill.getRebateAmt()));
    			
				//syj-commntted for 5% online rebate
				//collectionBuffer.setRebateAmount(bill.getRebateAmt()+Math.round((bill.getRebateAmt()*5)/8));
				collectionBuffer.setRebateAmount(bill.getRebateAmt());
				
				//long amountafterrebate = Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt())-Math.round(bill.getRebateAmt()+(Math.round(bill.getRebateAmt()/2)));
    			
    			
    			System.out.println("adavnce:"+Math.round(bill.getRebateAmt()));
    			
    			//syj-commntted for 5% online rebate
    			//amountafterrebate = Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt())-Math.round(bill.getRebateAmt()+Math.round((bill.getRebateAmt()*5)/8));
    			//request.setAttribute("amount", Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt())-Math.round(bill.getRebateAmt()+Math.round((bill.getRebateAmt()*5)/8)));
    			
    			amountafterrebate = Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt())-Math.round(bill.getRebateAmt());
    			request.setAttribute("amount", Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt())-Math.round(bill.getRebateAmt()));
    			
    			if(prop.getOwner().getFullName()!=null && prop.getOwner().getFullName()!=""){
    				request.setAttribute("owner",prop.getOwner().getFullName());
    				 
    			} else {
    				request.setAttribute("owner","None");
    			}
    			String mobile=prop.getOwner().getAddress().getMobileNumber();
    			System.out.println("mobile***********"+mobile);
    			if(mobile!=null && StringUtils.isNotEmpty(mobile) && StringUtils.isNotBlank(mobile) && mobile!=""){
    				request.setAttribute("mobile",prop.getOwner().getAddress().getMobileNumber());
    			} else {
    				request.setAttribute("mobile","None");
    			}
    			String email=prop.getOwner().getAddress().getEmailId();
    			if(email!=null &&  StringUtils.isNotEmpty(email) && StringUtils.isNotBlank(email)){
    				request.setAttribute("email",prop.getOwner().getAddress().getEmailId());
    			} else {
    				request.setAttribute("email","None");
    			}
         
    			   			   			
			}
			pdetailslist.add(propertyDetails);
			
			Random rand = new Random();
		     String rndm = Integer.toString(rand.nextInt()) + (System.currentTimeMillis() / 1000L);
		     txnid = hashCal("SHA-256", rndm).substring(0, 20);
		     
		     System.out.println("balance:"+billService.getPropertyBillDuesAmount(propertyCode.toUpperCase()));
		     
		     
			collectionBuffer.setCollectionBufferId(0);
		    DateFormat nmmc_dateFormat = new SimpleDateFormat(Constants.DATE_UI_FORMAT);
		  	collectionBuffer.setCollectionDate(nmmc_dateFormat.format(new Date()));
		  	collectionBuffer.setPaymentMode(3);
		  	
		  	collectionBuffer.setAmount(amountafterrebate);
		  	 CollectionCenterSearch searchCollectionCenter=new CollectionCenterSearch();
		  	     long WardId=prop.getWard().getWardId();
	            String pgCollectionId=String.valueOf(WardId);
	            System.out.println("pgCollectionId---------------------------"+pgCollectionId);
	            List<CollectionCenter> CollectionCenterList = collectionCenterService.findByProperty("pgcollectionid", pgCollectionId);
	            
	            long collectionCenterId=CollectionCenterList.get(0).getCollectionCenterId();
	            CollectionCenter center=collectionCenterService.get(collectionCenterId);
	            collectionBuffer.setCollectionCenter(center);
		  	    collectionBuffer.setProperty(prop);
		  	    collectionBuffer.setStatus(-1);
		  	   
		  	    collectionBuffer.setUniqPgId(txnid);
		  	
		  	//collectionBuffer.setDpcWaiveOffAmount(collectionRestDTO.getDpcWaiveOffAmount());
		  	
		       collectionBuffer=collectionBufferService.merge(collectionBuffer);

    		}
    	  }
    		
      
    	
	    	    
		   request.setAttribute("wardId",wardId);
	       request.setAttribute("txnid", txnid);
	     request.setAttribute("propertyCode", propertyCode);
        request.setAttribute("firstName", firstName);
        
        request.setAttribute("txnid", txnid);
        System.out.println("in hashcal method 12345:"+txnid);
	
        request.setAttribute("searchParamSet", "true");
         modelAndView.addObject("propertyDetails",propertyDetails);
        
      
    	return modelAndView;
    } 
    
    
    public ModelAndView getPropertyPgApi_bk(HttpServletRequest request,HttpServletResponse response) throws ServletException, ParseException
    {
    	if(log.isDebugEnabled()){
    		log.debug("getProperty Details  method called");
    	}
    	HttpSession session = request.getSession();
    	ModelAndView modelAndView=new ModelAndView("manageLedgerPg");
    	PropertyDetails propertyDetails=null;
    	String propertyCode=request.getParameter("searchCode");    	
    	String firstName=request.getParameter("firstName");
    	String lastName=request.getParameter("lastName");
    	String wardId=request.getParameter("wardId");
    	int sectorId=0;
    	
    	
		List<Property> plist=null;
		List<PropertyDetails> pdetailslist=new ArrayList<PropertyDetails>();
		 
		
				
	  
		    if(!StringUtils.isBlank(firstName) || !StringUtils.isBlank(lastName)   || !StringUtils.isBlank(propertyCode) || !StringUtils.isBlank(wardId)){
    		
			     plist=propertyService.getPropertyListQuery(wardId, firstName, lastName,propertyCode);
    		
    		
    		if(plist!=null){
    		if(plist.size()==0){
    			
    			request.setAttribute("message", "no results found");
    			
    		 }
    		 
    		}
    	}
    	
    
      if(CollectionUtils.isNotEmpty(plist)){
    	for(Property prop:plist){
    		propertyDetails=new PropertyDetails();
    		propertyDetails.setPropertyCode(prop.getCode());
			propertyDetails.setOwnerName(prop.getOwner().getFullName());
			if(prop.getSubOwner() != null && prop.getSubOwner().getOwnerId()>0){
			 propertyDetails.setSubOwnerName(prop.getSubOwner().getFullName());
			}else{
			 propertyDetails.setSubOwnerName("-");
			}
			propertyDetails.setPropertyAddress(prop.getPropertyAddress());
			propertyDetails.setCityAndState(prop.getCitiAndState());
			if(prop.getOwner().getOwnerGroup().getOwnerGroupId()==1002)
			{
				propertyDetails.setBalance(billService.getPropertyBillDuesAmount_Midc(propertyCode.toUpperCase()));
			} else {
				propertyDetails.setBalance(billService.getPropertyBillDuesAmount(propertyCode.toUpperCase()));
			}
			pdetailslist.add(propertyDetails);
    		}
    	  }
    		
    	
      
    			
		Random rand = new Random();
	     String rndm = Integer.toString(rand.nextInt()) + (System.currentTimeMillis() / 1000L);
	     txnid = hashCal("SHA-256", rndm).substring(0, 20);
	    
		   request.setAttribute("wardId",wardId);
         request.setAttribute("txnid", txnid);
	   request.setAttribute("propertyCode", propertyCode);
        request.setAttribute("firstName", firstName);
        request.setAttribute("lastName", lastName);
       
	
        request.setAttribute("searchParamSet", "true");
        modelAndView.addObject("propertyDetails",pdetailslist);
        
      
    	return modelAndView;
    }
    
    
    
    
    
    
    public ModelAndView printOnlineReceipt(HttpServletRequest request,
			HttpServletResponse response) throws ServletException,
			ParseException {
     	
    	if(log.isDebugEnabled()){
    		log.debug("getPropertyDetails method called");
    	}
    	
    	
    	ModelAndView modelAndView = new ModelAndView();
    	HttpSession session = request.getSession();
    	Map<String, String[]> parameterMap = request.getParameterMap();
    	Iterator i = parameterMap.keySet().iterator();
    	String txnid1="";
    	String propertyCode="";
    	Double amount=0.0;
    	String status="";
    	String bankref="";
    	String responsehash = "";
    	boolean webflag=false;
    	
    	CollectionBuffer collectionBuffer = new CollectionBuffer();
    	Double amt1 = 0.0;
    	
    	while (i.hasNext()) {
    		String key = (String) i.next();
    		String value = ((String[]) parameterMap.get( key ))[ 0 ];
    		
    		if(key.equalsIgnoreCase("txnid")){
    			txnid1 = value;
    			
    		}
    		else if(key.equalsIgnoreCase("udf1")){
    			propertyCode = value;
    			
    		}
    		else if(key.equalsIgnoreCase("amount")){
    			amount =Double.parseDouble(value);
    			
    		}
    		else if(key.equalsIgnoreCase("status")){
    			status = value;
    			
    		}

    		 else if (key.equalsIgnoreCase("mihpayid")) {
    				bankref = value;

    			} else if (key.equalsIgnoreCase("hash")) {
    				responsehash = value;

    			}
    	}
    	
    		/*//
    		
    		String hashSequence ="SALT|status||||||udf5|udf4|udf3|udf2|udf1|email|firstname|productinfo|am ount|txnid|key";
    		
    		Map<String, String[]> params = request.getParameterMap();
    		Map<String, String> mapResult = new HashMap<String,String>();
    		Iterator paramNames = params.keySet().iterator();
    		
    		 while(paramNames.hasNext()) 
    		{
    		        String paramName = (String)paramNames.next();
    		        String paramValue = ((String[]) params.get(paramName))[ 0 ];
    		        
    		        mapResult.put(paramName,paramValue);
    		}
    		 String key = "7rnFly";
    		 String salt = "pjVQAWpA";
    		 
    		 String[] hashVarSeq=hashSequence.split("\\|");
    		 String retHashSeq=salt+'|';
    		     for(String part : hashVarSeq)
    		     {
    		         retHashSeq= ((params.get(part)==null))?retHashSeq.concat(""):retHashSeq.concat(mapResult.get(part));
    		         retHashSeq=retHashSeq.concat("|");
    		     }
    	
    	
    	retHashSeq = retHashSeq.substring(0,retHashSeq.length()-1);
    	System.out.println("retHashSeq:"+retHashSeq);
    	
    	String hash = hashCal("SHA-512", retHashSeq);
    	
    	System.out.println("hash :"+hash);
    	*/
    	
    	//hash code
    	
    	
    			String hashSequence ="status||||||udf5|udf4|udf3|udf2|udf1|email|firstname|productinfo|amount|txnid";
    			//String hashSequence ="key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5|||||salt";
    			
    			Map<String, String[]> params = request.getParameterMap();
    			Map<String, String> mapResult = new HashMap<String,String>();
    			Iterator paramNames = params.keySet().iterator();
    			
    			 while(paramNames.hasNext()) 
    			{
    			        String paramName = (String)paramNames.next();
    			        //System.out.println("Response param name--> :"+paramName);
    			        
    			        String paramValue = ((String[]) params.get(paramName))[ 0 ];
    			        //System.out.println("Response param Value---> :"+paramValue);
    			        
    			        mapResult.put(paramName,paramValue);
    			}
    			/*String key = "7rnFly";
    			 String salt = "pjVQAWpA";*/
    			 
    			 String key ="4B0F9V";
    			 String salt ="rKVJtlqlxH4skKAqAyi2xWjKBCpeFZZx";
    					 
    			 String[] hashVarSeq=hashSequence.split("\\|");
    			 String retHashSeq=salt+'|';
    			     for(String part : hashVarSeq)
    			     {
    			         retHashSeq= ((params.get(part)==null))?retHashSeq.concat(""):retHashSeq.concat(mapResult.get(part));
    			         retHashSeq=retHashSeq.concat("|");
    			     }


    		retHashSeq = retHashSeq.substring(0,retHashSeq.length()-1);
    		retHashSeq=retHashSeq+"|"+key;
    		//System.out.println("retHashSeq:"+retHashSeq);

    		String hash = hashCal("SHA-512", retHashSeq);

    			System.out.println("calculated hash form contr=&&&&&&&&&&&"+hash);
    			System.out.println("Res HashCode from Bank =&&&&&&&&"+responsehash);
    			
    			//hash code end
    	
    	
    	    Property property = propertyService.getPropertyByCode(propertyCode);	

    	   DateFormat nmmc_dateFormat = new SimpleDateFormat(Constants.DATE_UI_FORMAT);

    	//collectionBuffer.setCollectionBufferId(0);
    	/*collectionBuffer.setCollectionDate(nmmc_dateFormat.format(new Date()));
    	collectionBuffer.setPaymentMode(3);
    	
    	collectionBuffer.setAmount(amount);
    	collectionBuffer.setCollectionCenter(collectionCenterService.get(1));
    	collectionBuffer.setProperty(property);
    	 collectionBuffer.setStatus(3);
    	 collectionBuffer.setRefNumber(txnid1);*/ 
    	   
    	   CollectionBuffer cbref = collectionBufferService.findByPropertyName("refNumber", bankref);
    	   
    	   if(cbref!=null){
    		   status="false";
    	   }
    	   
    	if (propertyCode != null && (status.equals("success")))  {
    		List<CollectionBuffer> collectionBuffer1 = null;

    		if (txnid1 != null) {
    			String UniqPgid = txnid1;
    			collectionBuffer1 = collectionBufferService	.getBufferPgData(UniqPgid);
    		}
    		if(CollectionUtils.isNotEmpty(collectionBuffer1)){
    		 amt1 = collectionBuffer1.get(0).getAmount();
    		System.out.println("amout@@@=" + amt1);
    		}
    		if (amt1.equals(amount) && amount!=null) {
    			collectionBuffer.setAmount(amount);
    			System.out.println("amout@@@=" + amt1);

    			String connum = collectionBuffer1.get(0).getProperty().getCode();
    			if (connum.equals(propertyCode) && propertyCode!=null) {
    				System.out.println("amout@@@=" + connum);
    				collectionBuffer.setProperty(property);

    				String uniqid = collectionBuffer1.get(0).getUniqPgId();
    				if (uniqid.equals(txnid1) && txnid1!=null ) {
    					
    					if(hash.equals(responsehash)){
    						
    					
    						long CollectionBufferId=collectionBuffer1.get(0).getCollectionBufferId();
    						collectionBuffer.setCollectionBufferId(CollectionBufferId);
    						
    						//collectionBuffer.setBillDueDate(nmmc_dateFormat.format(new Date()));
    						//collectionBuffer.setReferenceNumber(txnid1);
    						collectionBuffer.setUniqPgId(txnid1);
    						//collectionBuffer.setPaymentType(3);
    						collectionBuffer.setPaymentMode(3);
    						//collectionBuffer.setChequeNumber("123");
    						collectionBuffer.setCollectionCenter(collectionBuffer1.get(0).getCollectionCenter());
    						collectionBuffer.setCollectionDate(nmmc_dateFormat.format(new Date()));
    						collectionBuffer.setRebateAmount(collectionBuffer1.get(0).getRebateAmount());
    						collectionBuffer.setStatus(0);
    						collectionBuffer.setAmount(amount);
    						 collectionBuffer.setRefNumber(txnid1);
    						 collectionBuffer = collectionBufferService.merge(collectionBuffer);
    					    // collectionBuffer=collectionBufferService.merge(collectionBuffer);
    						 
    						 Integer responsepg=getPgStatus(txnid1);
    						 //Integer responsepg=1;
    							if(collectionBuffer.getCollectionBufferId()>0 && (responsepg>0)){
    								webflag=true;
    							} else {
    									modelAndView.setViewName("portalFailCollectionAcknowledgement");
    								}
    								
    						
    											 
    					}
    					    		
    					    
    											 
    							 
       					     if ((collectionBuffer.getCollectionBufferId() > 0) && (webflag)) {
       					    		if(status.equals("success")){
       					    			 collectionBuffer.setStatus(0);
       					    			 } else {
       					    				 collectionBuffer.setStatus(-1); 
       					    			 }
    					    			 collectionBuffer.setRefNumber(bankref);
    					    			 //collectionBuffer=collectionBufferService.merge(collectionBuffer);
    					    			 
    					    			
    					    			 com.nmmc.property.model.Collection collection=new com.nmmc.property.model.Collection();
    					    	            
    					    				
    					    				collection.setAmount(collectionBuffer.getAmount());
    					    				collection.setProperty(property);;
    					    				collection.setCollectionDate(collectionBuffer.getCollectionDate());
    					    				collection.setCollectionCenter(collectionBuffer.getCollectionCenter());
    					    				collection.setPaymentMode(collectionBuffer.getPaymentMode());
    					    				collection.setBank(collectionBuffer.getBank());
    					    				collection.setBranch(collectionBuffer.getBranch());
    					    				collection.setChequeOrDDNumber(collectionBuffer.getChequeOrDDNumber());
    					    				collection.setCreatedBy(collectionBuffer.getCreatedBy());
    					    				collection.setReceiptNo(collectionBuffer.getReceiptNo());
    					    				collection.setStatus(2);
    					    				collection.setRefNumber(collectionBuffer.getRefNumber());
    					    				Bill bill = billService.getPropertyBillDuesAmount(propertyCode.toUpperCase(), CommonUtils.getCurrentStringDate());
    					    	            request.setAttribute("rebateAmount", Long.valueOf(Math.round(bill.getRebateAmt())));
    					    	            request.setAttribute("amount", Long.valueOf(Math.round((bill.getBillAmt() + bill.getDpcAmt1()) - bill.getAdvanceAmt()) - Math.round(bill.getRebateAmt())));
    					    	            request.setAttribute("sut", Long.valueOf(Math.round(bill.getRebateAmt())));
    					    	            request.setAttribute("previousamt", Long.valueOf(Math.round(bill.getPrevOsAmt())));
    					    	            request.setAttribute("currentamt", Long.valueOf(Math.round(bill.getBillAmt())));
    					    	           
    					    				   
    					    				
    					    			    Long collectionId=collectionService.saveCollectionFunction(collection);
    					    			    if(collectionId>0){
    					    			    	collectionBuffer.setStatus(3);
    					    			    	collectionBuffer=collectionBufferService.merge(collectionBuffer);
    					    			    	try{
    					    						if(property.getOwner()!=null && property.getOwner().getAddress()!=null 
    					    								&& property.getOwner().getAddress().getMobileNumber()!=null){
    					    							String eMobile=property.getOwner().getAddress().getMobileNumber();
    					    							if((eMobile.length()==10))
    					    							{
    					    								String link = "https://tinyurl.com/yyz464ej?"+collectionId;
    	    					    						String mobileString =
    	    					    								
    	    					    								" Property Tax payment amount of Rs.  " + collectionBuffer.getAmount() +
    	    					    								" received for Property No. " + property.getCode() +
    	    					    								" To download the receipt click on the following link" + link +
    	    					    								" Regards,AMCORP"; 
    	    					    								
    	    					    											    //http://smsatm.net/v3/api.php?username=ASCDCL&apikey=c01f32640f54e44f7660&senderid=AMCGOV&templateid=1707164879816155540		
    	    					    						boolean flag1=SendSMS.sendSingleSMS("1707166943840528676","ASCDCL",eMobile,mobileString);
    					    							}
    					    						}
    					    						} catch (Exception e) {
    					    							//collectionRestDTO.setResponseCode(CoreConstants.WEBSERVICE_RESPONSECODE_FAILURE);
    					    							//collectionRestDTO.setResponseMessage("Collection not saved : "+e.getMessage());
    					    							e.printStackTrace();
    					    						}
    					    			    	System.out.println("collection saved successfully for property");
    					    			    } else {
    											//modelAndView.setViewName("portalFailCollectionAcknowledgement");
    					    			    	//session.setAttribute("message", "your transaction has been declined ! Please Try Again.");
    										}
    					    			 			
    					    		}
    					    		
    					
    					} else {
    						//modelAndView.setViewName("portalFailCollectionAcknowledgement");
    					//	session.setAttribute("message", "your transaction has been declined ! Please Try Again.");
    					}
    					
    					
    				} else {
    					//modelAndView.setViewName("portalFailCollectionAcknowledgement");
    					//session.setAttribute("message", "your transaction has been declined ! Please Try Again.");
    				}
    				
    				modelAndView.addObject("collection",collectionBuffer);
    				modelAndView.setViewName("portalCollectionAcknowledgement");

    			} else {
    				//session.setAttribute("message", "your transaction has been declined ! Please Try Again.");
    				//modelAndView.setViewName("portalFailCollectionAcknowledgement");
    				
    			}
    		}
    	//}	


    		return modelAndView;
	
	}
    
    
    public ModelAndView printAcknowledgementLadger(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	   if (log.isDebugEnabled())
    		{
    	    	log.debug("Invoking printAcknowledgement");
    		}	
    		String collectionId = request.getParameter("collectioId");
    		System.out.println("${collection.createdBy}@@@@@@@@@@@@="+collectionId);
    		String printCollectionProcessId = request.getParameter("collectioId");
    	    
    	    Collection collection = null;
    	    
    	    List<CollectionDetails> collectionDetails=null;
    	    
    	    
    	    
    	    //CollectionBuffer collectionBuffer=null;
    	    if(collectionId!=null && collectionId.length()>0){
    	    try{
    	    	collection  = collectionService.get(Long.parseLong(collectionId));
                 String Date=collection.getCollectionDate();
    	    	
    	    	int year=Integer.parseInt(Date.substring(6,10));
    	    	System.out.println("Date_____________________________"+year);
    	    	
    	    	int month=Integer.parseInt(Date.substring(3,5));
    	    	
    	    	System.out.println("month_____________________________"+month);
    	    	
    	    	
    	    	int day=Integer.parseInt(Date.substring(0,2));
    	    	
    	    	System.out.println("day_____________________________"+day);
    	    	if(year==2017 && month >=04  )
    	    	{
    	    		request.setAttribute("fy", "2017-18");
    	    	}
    	    	
    	    	if(year==2018 && month >=04  )
    	    	{
    	    		request.setAttribute("fy", "2018-19");
    	    	}
    	    	
    	    	if(year==2019 && month >=04  )
    	    	{
    	    		request.setAttribute("fy", "2019-20");
    	    	}
    	    	
    	    	if(year==2020 && month >=04  )
    	    	{
    	    		request.setAttribute("fy", "2020-21");
    	    	}
    	    	
    	    	if(year==2021 && month >=04  )
    	    	{
    	    		request.setAttribute("fy", "2021-222");
    	    	}
    	    	if(year==2022 && month >=04  )
    	    	{
    	    		request.setAttribute("fy", "2022-23");
    	    	}
    	    	if(year==2023 && month >=04  )
    	    	{
    	    		request.setAttribute("fy", "2023-24");
    	    	}
    	    	
 	    	
    	    	
    	    	collectionDetails= collectionDetailsService.findByProperty("collection", collectionId);
    	    	
    				Bill bill = null;
    		    	Property property=null;
    		    	try
    		    	{
    		    		property = propertyService.getPropertyByCode(collection.getProperty().getCode());
    		    		System.out.println("Name @@@@@@="+property.getCode());
    			    	if(property != null)
    			    	{
    			    		if(property.getOwner().getOwnerGroup().getOwnerGroupId()==1002)
    						{
    			    			bill = billService.getPropertyBillDuesAmount_Midc(collection.getProperty().getCode(), CommonUtils.getCurrentStringDate());
    						} else
    						{
    							if(property.getStatus()==9){
    				    				bill = billService.getPropertyBillDuesAmount_39_14(collection.getProperty().getCode(),CommonUtils.getCurrentStringDate());
    				    				
    				    			//}
    				    		} else {
    				    			 
    			    		       bill = billService.getPropertyBillDuesAmount(collection.getProperty().getCode(), CommonUtils.getCurrentStringDate());
    				    		}
    						}
    			    	}
    			    	if(request.getParameter("eMobile")!=null){
							String eMobile=request.getParameter("eMobile");
							System.out.println("Mob --------------------="+eMobile);
							if((eMobile.length()==10))
							{
								String link = "https://tinyurl.com/yyz464ej?collectioId="+collectionId;
	    						String mobileString =
	    								
	    								" Property Tax payment amount of Rs.  " + collection.getAmount() +
	    								" received for Property No. " + property.getCode() +
	    								" To download the receipt click on the following link" + link +
	    								" Regards,AMCORP"; 
								
											    //http://smsatm.net/v3/api.php?username=ASCDCL&apikey=c01f32640f54e44f7660&senderid=AMCGOV&templateid=1707164879816155540		
						boolean flag1=SendSMS.sendSingleSMS("1707166943840528676","ASCDCL",eMobile,mobileString);
							}
    			    	}
    			    	
    			    	

    		    	} catch (Exception e)
    				{
    					e.printStackTrace();
    			    	log.error(e.getMessage());
    				}

                     
    		    	 System.out.println("printCollectionProcessId id : "+printCollectionProcessId);
    		    	List previous_outsatnding=collectionService.getDemandAmount(Long.parseLong(printCollectionProcessId));
    		    	System.out.println("collection id : prev os"+previous_outsatnding);
    		    	List current_demand_adjusted=collectionService.getTotalDemandAmount(Long.parseLong(printCollectionProcessId));
    		    	System.out.println("collection id : current adjusted"+current_demand_adjusted);
    		    	
    		    	double outStandingAmt = billService.getPropertyBillDuesAmount(collection.getProperty().getCode().toUpperCase());
    				
    		    	System.out.println("collection id : current os"+outStandingAmt);
    		    	
    
    		    	request.setAttribute("previous_outsatnding", previous_outsatnding.toString().replace("[", "").replace("]", ""));
    		    	request.setAttribute("current_demand_adjusted", current_demand_adjusted.toString().replace("[", "").replace("]", ""));
    				request.setAttribute("outStandingAmt", outStandingAmt);
    				
    				request.setAttribute("advAmt", bill.getAdvanceAmt());
    				request.setAttribute("rebatAmt", bill.getRebateAmt());
    				request.setAttribute("dpc3Amt", bill.getDpcAmt3());
    				request.setAttribute("billAmt", bill.getBillAmt());
    				request.setAttribute("dpcAmt1", bill.getDpcAmt1());
    				request.setAttribute("advAmt", bill.getAdvanceAmt());
    				request.setAttribute("total",(bill.getDpcAmt1()+bill.getDpcAmt1())-bill.getAdvanceAmt());

    	    	
    	    	}catch(Exception e){
    	    		e.printStackTrace();
    	    	}
    	}  	   
    		return new ModelAndView("printCollectionLedAcknowledgement","collection",collection);
    	 //   return new ModelAndView("manageCollection","collection",collection);
    }
    
    

public ModelAndView getProcess(HttpServletRequest request,HttpServletResponse response) throws ServletException, ParseException
{
	if(log.isDebugEnabled()){
		log.debug("Info About Reverse os calculation method called");
	}
	  HttpSession session = request.getSession();
	Property property =null;
	int result=0;
	String propertyCode= request.getParameter("searchCode");
	System.out.println("Property Code:-"+propertyCode);
	if(propertyCode != null)
	{
		property=propertyService.getPropertyByCode(propertyCode);
		
		long propertyId=property.getPropertyId();
		
		System.out.println("Property id:-"+propertyId);
		
		result=billService.getProcess(propertyId);
		
		if(result <= 0)
		{
			
				 session.setAttribute("message", "Process not completed may be old Demand's issue or no prev demands found/paid colleciton in 2022-2023/");
				request.setAttribute("flag", 1);
			}
		else{
			session.setAttribute("message", "Processed...please check cfc outstanding for payment submition");
			
		}
	}
	
	
	return new ModelAndView("manageLedger");
	
}



    
    
    public Integer getPgStatus(String txnNo){
    	
    	Integer finalFlag=0;
		
    	try
    	{   		
    		String userName="amcadmin";
    		String userPWD="wspassword";
    		String status="";
    		//UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, userPWD);
    		//CommonsClientHttpRequestFactory factory = (CommonsClientHttpRequestFactory)restTemplate.getRequestFactory();
            //org.apache.commons.httpclient.HttpClient client = factory.getHttpClient();
            //client.getState().setCredentials(AuthScope.ANY, credentials);
    		  //String restUrl = "http://amc.aurangabadmahapalika.org:8586/License/ws/get/paymentpayuProperty/"+txnNo;
    		String restUrl = "http://10.30.40.129:8586/License/ws/get/paymentpayuProperty/"+txnNo;

    		try {
    			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    			String result = restTemplate.getForObject(restUrl, String.class);
    			//System.out.println("result :"+result.get);

    			DocumentBuilderFactory factory =
    					DocumentBuilderFactory.newInstance();
    					DocumentBuilder builder = factory.newDocumentBuilder();
    					StringBuilder xmlStringBuilder = new StringBuilder();
    					xmlStringBuilder.append(result);
    					ByteArrayInputStream input = new ByteArrayInputStream(
    					   xmlStringBuilder.toString().getBytes("UTF-8"));
    					org.w3c.dom.Document doc = builder.parse(input);
    					org.w3c.dom.Element root = doc.getDocumentElement();
    					String  pgstatus =root.getElementsByTagName("responseCode").item(0).getTextContent();
    					System.out.println("pgesult::"+pgstatus);
		       

    					if(pgstatus.equals("1")){
		                	  finalFlag=1;
		                  }
    			
    			
    		} catch (Exception exception) {
    			exception.printStackTrace();
    			log.error(exception.getMessage());
    		}
    	
    	} catch (Exception e)
		{
			e.printStackTrace();
	    	log.error(e.getMessage());
		}
    		return finalFlag;
	}
    
    
   /* public ModelAndView getPropertyDetailsForPortal(HttpServletRequest request,HttpServletResponse response) throws ServletException, ParseException
    {
    	if(log.isDebugEnabled()){
    		log.debug("getPropertyDetails method called");
    	}
    	HttpSession session = request.getSession();
    	ModelAndView modelAndView=new ModelAndView();
    	System.out.println("FLpgTypeAGE-------------------------="+request.getParameter("pgType"));
    	System.out.println("flag-------------------------="+request.getParameter("flag"));
    	
    	
    	int pgType=Integer.parseInt(request.getParameter("pgType"));
    	
    	if(pgType==1){
    	modelAndView=new ModelAndView("manageDownloadReceipt");
    	}
    	else
    	{
    		modelAndView=new ModelAndView("manageLedgerPgWeb");
    	}
    	DemandCycle demand=new DemandCycle();
    	PropertyDetails propertyDetails=null;
    	List<PropertyAssessment> assmntList=null;
    	String propertyCode=request.getParameter("searchCode");    	
    	
    	LedgerDetails ledgerDetails = null;
		long transRef = 0;
		int transType = 0;
		double dpcAmt=0;
		double totalBillAmt = 0;
		double totalAmtPaid = 0;
		String ledgerDate="";
		String collectionCeneter="";
		String remarks="";
		int recStatus=0;
    
    	if(propertyCode != null)
    	{
    		Property property=propertyService.getPropertyByCode(propertyCode);
    		
    		if(property!=null)
    		{
    			propertyDetails=new PropertyDetails();
    			propertyDetails.setPropertyCode(propertyCode);
    			propertyDetails.setOwnerName(property.getOwner().getFullName());
    			
    			if(property.getSubOwner() != null && property.getSubOwner().getOwnerId()>0)
    			 propertyDetails.setSubOwnerName(property.getSubOwner().getFullName());
    			else
    			 propertyDetails.setSubOwnerName("-");
    			
    			
    			propertyDetails.setPropertyAddress(property.getOldPropertyAddress());
    			propertyDetails.setCityAndState(property.getCitiAndState());
    			
    			PropertyAssessment propertyAssessment = propertyAssessmentService.getLatestAssessment(property.getPropertyId());
    			
    			if(propertyAssessment != null)
    			propertyDetails.setPropertyType(propertyAssessment.getUsage());
    			
    			String assmntListSql="select * from property_assessment where property_id="+property.getPropertyId()+" and status in (1,2) order by to_date(assessment_date,'dd/mm/yyyy') ASC ";
    			
    			assmntList = propertyAssessmentService.getEntityListBySQLQuery(assmntListSql, null);
    			
    			double outStandingAmt =0;
    			Bill bill=null;
    			if(property.getOwner().getOwnerGroup().getOwnerGroupId()==1002)
				{
    				bill = billService.getPropertyBillDuesAmount_Midc(propertyCode.toUpperCase(),CommonUtils.getCurrentStringDate());
				} else {
    			   bill = billService.getPropertyBillDuesAmount(propertyCode.toUpperCase(),CommonUtils.getCurrentStringDate());
				}
    			if(bill!=null){
        			propertyDetails.setBalance(Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt()));
        			request.setAttribute("rebateAmount", Math.round(bill.getRebateAmt()));
        			request.setAttribute("amount", Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt())-Math.round(bill.getRebateAmt()));
        			
        			}
        			else{
        				propertyDetails.setBalance(0);
        				request.setAttribute("rebateAmount", 0);
            			request.setAttribute("amount",0);
        			}
        		
    		}else{
        		session.setAttribute("message", "Property A/c No you have entered is invalid.");
        	}
    	
    	if(request.getParameter("flag") != null && request.getParameter("flag").equalsIgnoreCase("true")){
    		
    		List<LedgerDetails> ledgerDetailsList  = ledgerService.getLedgerDetailListBySQLQuery(propertyCode);            
    		modelAndView.addObject("ledgerDetailsList",ledgerDetailsList);	
    		
    	 }
    	}
    	
    	request.setAttribute("propertyCode", propertyCode);
        request.setAttribute("searchParamSet", "true");
    	modelAndView.addObject("propertyDetails",propertyDetails);
    	modelAndView.addObject("propertyAssessmentList",assmntList);
    	request.setAttribute("demandCycleList", demandCycleService.getAll());
		request.setAttribute("nodeMasterList", nodeMasterService.getAll());
		request.setAttribute("wardList", wardService.getAll());
		
		request.setAttribute("plotList", plotService.getAll());
		request.setAttribute("ownerGroupList", ownerGroupService.getAll());
		request.setAttribute("flag", 0);
		
		request.setAttribute("financialList", financialYearService.getAll());
    	
    	return modelAndView;
    }  
  
    
    
    
  */  
    
  public ModelAndView getPropertyDetailsForPortal(HttpServletRequest request,HttpServletResponse response) throws ServletException, ParseException
    {
    	if(log.isDebugEnabled()){
    		log.debug("getPropertyDetails method called");
    	}
    	HttpSession session = request.getSession();
    	ModelAndView modelAndView=new ModelAndView();
    	System.out.println("FLpgTypeAGE-------------------------="+request.getParameter("pgType"));
    	System.out.println("flag-------------------------="+request.getParameter("flag"));
    	
    	
    	int pgType=Integer.parseInt(request.getParameter("pgType"));
    	
    	if(pgType==1){
    	modelAndView=new ModelAndView("manageDownloadReceipt");
    	}
    	else
    	{
    		modelAndView=new ModelAndView("manageLedgerPgWeb");
    	}
    	DemandCycle demand=new DemandCycle();
    	PropertyDetails propertyDetails=null;
    	List<PropertyAssessment> assmntList=null;
    	String propertyCode=request.getParameter("searchCode");
    	
    	System.out.println("searchCode________________________"+propertyCode);
    	
    	
    	CollectionSearch searchOptions=new CollectionSearch();
		setCollectionSearchParameters(searchOptions,request);
		searchOptions.setStatus(1);
	    String orderBy = "to_date(collectionDate,'" + Constants.DATE_FORMAT + "')";
	    String  sortBy = "desc";
	    searchOptions.setOrderBy(orderBy);
	    searchOptions.setSortBy(sortBy);
		searchOptions.setCode(propertyCode);
		
	//	List<Collection> CollectionList = collectionService.getCollectionList(longCurrentPage,orderBy,sortBy);
		List<Collection> CollectionList = collectionService.getCollectionListBySearch(searchOptions);
		try
		{
			long totalCount = collectionService.getCollectionCountBySearch(searchOptions);
	        request.setAttribute("maximumPages", new Long(CommonUtils.getMaxPage(totalCount)));
	        request.setAttribute("totalCount", totalCount);
		} catch (Exception e)
		{
			e.printStackTrace();
	    	log.error(e.getMessage());
		}

	//Check if the CollectionList is null
	if(CollectionList==null)
		CollectionList = new ArrayList<Collection>();

	request.setAttribute("currentPage", searchOptions.getCurrentPage());
	request.setAttribute(const_OrderBy, searchOptions.getOrderBy());
	request.setAttribute(const_SortBy, searchOptions.getSortBy());
	request.setAttribute("SearchOptions", searchOptions);
	//request.setAttribute("collectionCenterList", collectionCenterService.getAll());
	//return new ModelAndView("listCollection","CollectionList",CollectionList);
		
    
	
	
	    request.setAttribute("propertyCode", propertyCode);
        request.setAttribute("searchParamSet", "true");
    	modelAndView.addObject("propertyDetails",propertyDetails);
    	modelAndView.addObject("propertyAssessmentList",assmntList);
    	request.setAttribute("demandCycleList", demandCycleService.getAll());
		request.setAttribute("nodeMasterList", nodeMasterService.getAll());
		request.setAttribute("wardList", wardService.getAll());
		
		request.setAttribute("plotList", plotService.getAll());
		request.setAttribute("ownerGroupList", ownerGroupService.getAll());
		request.setAttribute("flag", 0);
		
		request.setAttribute("financialList", financialYearService.getAll());
		modelAndView.addObject("listCollection",CollectionList);
    	
    	return modelAndView;
    }  
  
    
    
    public ModelAndView printOnlineReceipt_testing(HttpServletRequest request,
			HttpServletResponse response) throws ServletException,
			ParseException {
    	
	if(log.isDebugEnabled()){
		log.debug("getPropertyDetails method called");
	}
	
	
	ModelAndView modelAndView = new ModelAndView();
	HttpSession session = request.getSession();
	Map<String, String[]> parameterMap = request.getParameterMap();
	Iterator i = parameterMap.keySet().iterator();
	String txnid1="";
	String propertyCode="";
	Double amount=0.0;
	String status="";
	String bankref="";
	String responsehash = "";
	boolean webflag=false;
	
	CollectionBuffer collectionBuffer = new CollectionBuffer();
	Double amt1 = 0.0;
	
	while (i.hasNext()) {
		String key = (String) i.next();
		String value = ((String[]) parameterMap.get( key ))[ 0 ];
		
		if(key.equalsIgnoreCase("txnid")){
			txnid1 = value;
			
		}
		else if(key.equalsIgnoreCase("udf1")){
			propertyCode = value;
			
		}
		else if(key.equalsIgnoreCase("amount")){
			amount =Double.parseDouble(value);
			
		}
		else if(key.equalsIgnoreCase("status")){
			status = value;
			
		}

		 else if (key.equalsIgnoreCase("mihpayid")) {
				bankref = value;

			} else if (key.equalsIgnoreCase("hash")) {
				responsehash = value;

			}
	}
	
		/*//
		
		String hashSequence ="SALT|status||||||udf5|udf4|udf3|udf2|udf1|email|firstname|productinfo|am ount|txnid|key";
		
		Map<String, String[]> params = request.getParameterMap();
		Map<String, String> mapResult = new HashMap<String,String>();
		Iterator paramNames = params.keySet().iterator();
		
		 while(paramNames.hasNext()) 
		{
		        String paramName = (String)paramNames.next();
		        String paramValue = ((String[]) params.get(paramName))[ 0 ];
		        
		        mapResult.put(paramName,paramValue);
		}
		 String key = "7rnFly";
		 String salt = "pjVQAWpA";
		 
		 String[] hashVarSeq=hashSequence.split("\\|");
		 String retHashSeq=salt+'|';
		     for(String part : hashVarSeq)
		     {
		         retHashSeq= ((params.get(part)==null))?retHashSeq.concat(""):retHashSeq.concat(mapResult.get(part));
		         retHashSeq=retHashSeq.concat("|");
		     }
	
	
	retHashSeq = retHashSeq.substring(0,retHashSeq.length()-1);
	System.out.println("retHashSeq:"+retHashSeq);
	
	String hash = hashCal("SHA-512", retHashSeq);
	
	System.out.println("hash :"+hash);
	*/
	
	//hash code
	
	
			String hashSequence ="status||||||udf5|udf4|udf3|udf2|udf1|email|firstname|productinfo|amount|txnid";
			//String hashSequence ="key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5|||||salt";
			
			Map<String, String[]> params = request.getParameterMap();
			Map<String, String> mapResult = new HashMap<String,String>();
			Iterator paramNames = params.keySet().iterator();
			
			 while(paramNames.hasNext()) 
			{
			        String paramName = (String)paramNames.next();
			        //System.out.println("Response param name--> :"+paramName);
			        
			        String paramValue = ((String[]) params.get(paramName))[ 0 ];
			        //System.out.println("Response param Value---> :"+paramValue);
			        
			        mapResult.put(paramName,paramValue);
			}
			 String key = "7rnFly";
			 String salt = "pjVQAWpA";
			 
			 //String key ="4B0F9V";
			 //String salt ="rKVJtlqlxH4skKAqAyi2xWjKBCpeFZZx";
					 
			 String[] hashVarSeq=hashSequence.split("\\|");
			 String retHashSeq=salt+'|';
			     for(String part : hashVarSeq)
			     {
			         retHashSeq= ((params.get(part)==null))?retHashSeq.concat(""):retHashSeq.concat(mapResult.get(part));
			         retHashSeq=retHashSeq.concat("|");
			     }


		retHashSeq = retHashSeq.substring(0,retHashSeq.length()-1);
		retHashSeq=retHashSeq+"|"+key;
		//System.out.println("retHashSeq:"+retHashSeq);

		String hash = hashCal("SHA-512", retHashSeq);

			System.out.println("calculated hash form contr=&&&&&&&&&&&"+hash);
			System.out.println("Res HashCode from Bank =&&&&&&&&"+responsehash);
			
			//hash code end
	
	
	    Property property = propertyService.getPropertyByCode(propertyCode);	

	   DateFormat nmmc_dateFormat = new SimpleDateFormat(Constants.DATE_UI_FORMAT);

	//collectionBuffer.setCollectionBufferId(0);
	/*collectionBuffer.setCollectionDate(nmmc_dateFormat.format(new Date()));
	collectionBuffer.setPaymentMode(3);
	
	collectionBuffer.setAmount(amount);
	collectionBuffer.setCollectionCenter(collectionCenterService.get(1));
	collectionBuffer.setProperty(property);
	 collectionBuffer.setStatus(3);
	 collectionBuffer.setRefNumber(txnid1);*/ 
	   
	   CollectionBuffer cbref = collectionBufferService.findByPropertyName("refNumber", bankref);
	   
	   if(cbref!=null){
		   status="false";
	   }
	   
	if (propertyCode != null && (status.equals("success")))  {
		List<CollectionBuffer> collectionBuffer1 = null;

		if (txnid1 != null) {
			String UniqPgid = txnid1;
			collectionBuffer1 = collectionBufferService	.getBufferPgData(UniqPgid);
		}
		if(CollectionUtils.isNotEmpty(collectionBuffer1)){
		 amt1 = collectionBuffer1.get(0).getAmount();
		System.out.println("amout@@@=" + amt1);
		}
		if (amt1.equals(amount) && amount!=null) {
			collectionBuffer.setAmount(amount);
			System.out.println("amout@@@=" + amt1);

			String connum = collectionBuffer1.get(0).getProperty().getCode();
			if (connum.equals(propertyCode) && propertyCode!=null) {
				System.out.println("amout@@@=" + connum);
				collectionBuffer.setProperty(property);

				String uniqid = collectionBuffer1.get(0).getUniqPgId();
				if (uniqid.equals(txnid1) && txnid1!=null ) {
					
					if(hash.equals(responsehash)){
						
					
						long CollectionBufferId=collectionBuffer1.get(0).getCollectionBufferId();
						collectionBuffer.setCollectionBufferId(CollectionBufferId);
						
						//collectionBuffer.setBillDueDate(nmmc_dateFormat.format(new Date()));
						//collectionBuffer.setReferenceNumber(txnid1);
						collectionBuffer.setUniqPgId(txnid1);
						//collectionBuffer.setPaymentType(3);
						collectionBuffer.setPaymentMode(3);
						//collectionBuffer.setChequeNumber("123");
						collectionBuffer.setCollectionCenter(collectionCenterService.get(1));// 1
						collectionBuffer.setCollectionDate(nmmc_dateFormat.format(new Date()));
						
						collectionBuffer.setStatus(0);
						collectionBuffer.setAmount(amount);
						 collectionBuffer.setRefNumber(txnid1);
						 collectionBuffer = collectionBufferService.merge(collectionBuffer);
					    // collectionBuffer=collectionBufferService.merge(collectionBuffer);
						 
						 //Integer responsepg=getPgStatus(txnid1);
						 //Integer responsepg=1;
							if(collectionBuffer.getCollectionBufferId()>0 /*&& (responsepg>0)*/){
								webflag=true;
							} else {
									modelAndView.setViewName("portalFailCollectionAcknowledgement");
								}
								
						}
											 
						 
					     if ((collectionBuffer.getCollectionBufferId() > 0) && (webflag)) {
					    		if(status.equals("success")){
					    			 collectionBuffer.setStatus(0);
					    			 } else {
					    				 collectionBuffer.setStatus(-1); 
					    			 }
					    		
					    		
					    			 collectionBuffer.setRefNumber(bankref);
					    			 //collectionBuffer=collectionBufferService.merge(collectionBuffer);
					    			 
					    			
					    			 com.nmmc.property.model.Collection collection=new com.nmmc.property.model.Collection();
					    	            
					    				
					    				collection.setAmount(collectionBuffer.getAmount());
					    				collection.setProperty(property);;
					    				collection.setCollectionDate(collectionBuffer.getCollectionDate());
					    				collection.setCollectionCenter(collectionBuffer.getCollectionCenter());
					    				collection.setPaymentMode(collectionBuffer.getPaymentMode());
					    				collection.setBank(collectionBuffer.getBank());
					    				collection.setBranch(collectionBuffer.getBranch());
					    				collection.setChequeOrDDNumber(collectionBuffer.getChequeOrDDNumber());
					    				collection.setCreatedBy(collectionBuffer.getCreatedBy());
					    				collection.setReceiptNo(collectionBuffer.getReceiptNo());
					    				collection.setStatus(2);
					    				collection.setRefNumber(collectionBuffer.getRefNumber());
					    				
					    				   
					    				
					    			    Long collectionId=collectionService.saveCollectionFunction(collection);
					    			    if(collectionId>0){
					    			    	collectionBuffer.setStatus(3);
					    			    	collectionBuffer=collectionBufferService.merge(collectionBuffer);
					    			    	/*try{
					    						if(property.getOwner()!=null && property.getOwner().getAddress()!=null 
					    								&& property.getOwner().getAddress().getMobileNumber()!=null){
					    							String eMobile=property.getOwner().getAddress().getMobileNumber();
					    							if((eMobile.length()==10))
					    							{
					    						String mobileString =
					    								
					    								" Property Tax payment amount of Rs.  " + collectionBuffer.getAmount() +
					    								" received for Property No. " + property.getCode() +
					    								" Regards,AMCORP"; 
					    								
					    											    //http://smsatm.net/v3/api.php?username=ASCDCL&apikey=c01f32640f54e44f7660&senderid=AMCGOV&templateid=1707164879816155540		
					    						boolean flag1=SendSMS.sendSingleSMS("1707164422560186390","ASCDCL",eMobile,mobileString);
					    							}
					    						}
					    						} catch (Exception e) {
					    							//collectionRestDTO.setResponseCode(CoreConstants.WEBSERVICE_RESPONSECODE_FAILURE);
					    							//collectionRestDTO.setResponseMessage("Collection not saved : "+e.getMessage());
					    							e.printStackTrace();
					    						}
					    			    	*/System.out.println("collection saved successfully for property");
					    			    } else {
											modelAndView.setViewName("portalFailCollectionAcknowledgement");
										}
					    			 			
					    		} else {

									modelAndView.setViewName("portalFailCollectionAcknowledgement");
									
								}
					    		
					
					} else {
						modelAndView.setViewName("portalFailCollectionAcknowledgement");
					}
					
					
				} else {
					modelAndView.setViewName("portalFailCollectionAcknowledgement");
				}
				
				modelAndView.addObject("collection",collectionBuffer);
				modelAndView.setViewName("portalCollectionAcknowledgement");

			} else {

				modelAndView.setViewName("portalFailCollectionAcknowledgement");
				
			}
		}
	//}	


		return modelAndView;
	
	}
    

    private Property genPropertyCodeAmc(Property property){
       	
      	 String wardId =String.valueOf(property.getWard().getWardId());
      	//System.out.println("Wardid@@@@@@@@@@@="+wardId);
  	    String propertyCode = null;
  	   String propertyCodeNew="";
  	   String wardName="";
  		try {
  			propertyCode = propertyService.getlatestNewPropertyCode(wardId);
  			//System.out.println("connNumber from@@@@@ db"+propertyCode);
  			} catch (Exception e) {
  			// TODO Auto-generated catch block			
  		}
  	    //String connectionNumber =  String.valueOf(connNumber);
  		
  	    //Date date = new Date(); 
  	    //String year = String.valueOf(date.getYear()+1900);
  		 //if(connNumber.length()==9|| connNumber!=null){
  	    if(propertyCode.length()==9){
  	    	
  	    	wardName=propertyCode.substring(0,2);
  	    	//connectionNumbernew =connNumber.substring(2);
  	    	//connectionNumbernew=wardName+Integer.parseInt(connNumber .substring(2)) + 1;
  	    	
  	    	String s = propertyCode.substring(2);
  	        Integer i = Integer.parseInt(s);
  	        i++;
  	        s = String.format("%0" + s.length() + "d", i);
  	    	
  	    	//System.out.println("wardName @@@@="+wardName);
  	    	//System.out.println("subId @@@@="+s);
  	    	propertyCodeNew=wardName+s;
  	    	
  	    	//System.out.println("connectionNumbernew @@@@="+propertyCodeNew);
  	    	//System.out.println("Temp @@@@@@@@@@="+temp);
  	    	
  	    	
  	    	
  	    	//connectionNumber = String.valueOf(connNumber+1);
  	    	property.setCode((propertyCodeNew));	    	
  	    }else{
  	    	
  	    	String incrimentNo="";
  			if(wardId.equals("1")){
  				incrimentNo=incrimentNo+"A";
  				
  			} else if(wardId.equals("2")){
  				incrimentNo=incrimentNo+"B";
  				
  			} else if(wardId.equals("3")){
  				incrimentNo=incrimentNo+"C";
  				
  			} else if(wardId.equals("4")){
  				incrimentNo=incrimentNo+"D";
  				
  			} else if(wardId.equals("5")){
  				incrimentNo=incrimentNo+"E";
  				
  			} else if(wardId.equals("6")){
  				incrimentNo=incrimentNo+"F";
  				
  			} else if(wardId.equals("9")){
  				incrimentNo=incrimentNo+"I";
  				
  			}  else if(wardId.equals("7")){
  				incrimentNo=incrimentNo+"G";
  				
  			} else if(wardId.equals("8")){
  				incrimentNo=incrimentNo+"H";
  				
  			}
  		
  			propertyCodeNew = incrimentNo+"00000001";
  	    	property.setCode(propertyCodeNew);	    	
  	    }
  	    
  	    
      	return property;
      }  

/*public ModelAndView listPropertyBillCloud(HttpServletRequest request,HttpServletResponse response) throws ServletException, ParseException, IOException{
    	
    	Integer finalFlag=0;
    	String newUrl="";
    	boolean redirect = false;
   			
    	String restUrl = "https://app.billcloud.in/webapi/qr/nmmc/NMMC/PTAX/NA/BE0001938587";

		URL obj=null;
		try {
			obj = new URL(restUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		
		int status=0;
		try {
			status = conn.getResponseCode();
			System.out.println("status-->"+status);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(status != HttpURLConnection.HTTP_OK) 
		{
    		if((status == HttpURLConnection.HTTP_MOVED_TEMP)
    				|| (status == HttpURLConnection.HTTP_MOVED_PERM) 
    				|| (status == HttpURLConnection.HTTP_SEE_OTHER)
    				|| (status == 307)) 
    		{
    			redirect=true;
    		}
    		
    	}

		if(redirect) 
		{
			newUrl = conn.getHeaderField("Location");
		} 
    		System.out.println("newUrl--->>"+newUrl);
    	} catch (Exception exception) 
    		{
    			exception.printStackTrace();
    			log.error(exception.getMessage());
    		}
    	
    	}catch (Exception e)
		{
			e.printStackTrace();
	    	log.error(e.getMessage());
		}
    		return new ModelAndView("listProperty");
	
}  
*/   
    
    
    public ModelAndView searchPropertyForChangeMobNum(HttpServletRequest request, HttpServletResponse response ) throws ServletException
   	{
       	log.debug("Invoking manageCloseProperty");
       	if (log.isDebugEnabled())
   		{
   	    	log.debug("Invoking listProperty");
   		}
       	
   		PropertySearch searchOptions = new PropertySearch();
   		HttpSession session = request.getSession();
   		SessionUser sessionUser = (SessionUser) session.getAttribute("SessionUser");
   		
   		request.setAttribute("userId", sessionUser.getUserId());
   		setSearchParameters(searchOptions, request);
   		List<Property> PropertyList = null;
   		Property property=new Property();
   		if(request.getParameter("propertyCode")!=null){
   		try{
   			
   				PropertyList = propertyService.getPropertyListBySearch(searchOptions);
   				long propertyId =PropertyList.get(0).getPropertyId();
   				property= propertyService.getPropertyByPropertyId(propertyId);
   				if(property.getPropertyId()==0 ){
   					session.setAttribute("message", "No Property found ! Try again");
   					
   				}
   				request.setAttribute("searchParamSet", true);
   		
   		} catch (Exception e)
   		{
   		   	log.error(e.getMessage());
   		}

   		
   				
       	}
   		request.setAttribute("wardList", wardService.getAll());
   		return new ModelAndView("manageCloseProperty","property",property);
       	
            
        }

    
   
    
        
    
    
    
    
    public ModelAndView listUpdatedMobNum(HttpServletRequest request, HttpServletResponse response ) throws ServletException
   	{
		if (log.isDebugEnabled())
		{
	    	log.debug("Invoking listProperty");
		}
		PropertySearch searchOptions = new PropertySearch();
		HttpSession session = request.getSession();
		SessionUser sessionUser = (SessionUser) session.getAttribute("SessionUser");
		
		request.setAttribute("userId", sessionUser.getUserId());
		setSearchParameters(searchOptions, request);
		List<Property> PropertyList = null;

		try{
			long totalCount = 0;
			//if(searchOptions.isSearchParamSet() || (request.getParameter("searchParamSet") != null &&
					//request.getParameter("searchParamSet").equalsIgnoreCase("true"))){
				PropertyList = propertyService.getPropertyListBySearch(searchOptions);
				totalCount = propertyService.getPropertyCountBySearch(searchOptions);

				request.setAttribute("searchParamSet", true);
			//}
		    request.setAttribute("maximumPages", new Long(CommonUtils.getMaxPage(totalCount)));
		    request.setAttribute("totalCount", totalCount);
		} catch (Exception e)
		{
		   	log.error(e.getMessage());
		}

		//Check if the PropertyList is null
		if(PropertyList==null)
			PropertyList = new ArrayList<Property>();
		long wardId=0;
		long nodeId=0;
		long sectorId=0;
		
		if(request.getParameter("searchWardId")!=null && request.getParameter("searchWardId").length()>0)
		wardId=Long.parseLong(request.getParameter("searchWardId"));
		if(request.getParameter("searchNodeMasterId")!=null && request.getParameter("searchNodeMasterId").length()>0)
		nodeId=Long.parseLong(request.getParameter("searchNodeMasterId"));
		if(request.getParameter("searchSectorId")!=null && request.getParameter("searchSectorId").length()>0)
		sectorId=Long.parseLong(request.getParameter("searchSectorId"));
		
		if(wardId>0)
		{
		//	request.setAttribute("nodeList", nodeMasterService.findByProperty("ward.wardId", wardId));
			request.setAttribute("nodeList", nodeMasterService.getAll());
			
			if(wardId>0 && nodeId>0)
			request.setAttribute("sectorList", wardService.getSectorsOnWardAndNode(wardId,nodeId));
			if(sectorId>0)
			request.setAttribute("plotList", plotService.findByProperty("sector.sectorId", sectorId));
				
		}

	//	request.setAttribute("sectorList", sectorService.getAll());
		request.setAttribute("nodeList", nodeMasterService.getAll());
		request.setAttribute("wardList", wardService.getAll());
		request.setAttribute("inspectorList", employeeService.getAll());
		request.setAttribute("ownerGroupList", ownerGroupService.getAll());

		request.setAttribute("currentPage", searchOptions.getCurrentPage());
		request.setAttribute(const_OrderBy, searchOptions.getOrderBy());
		request.setAttribute(const_SortBy, searchOptions.getSortBy());
		request.setAttribute("SearchOptions", searchOptions);
		
		boolean linkFlag=linkEnable(sessionUser);
		    if(linkFlag)request.setAttribute("linkEnable","1");	    	
		    else
		    	request.setAttribute("linkFlag","0");
        
		if(request.getParameter("flag")!=null && request.getParameter("flag").equalsIgnoreCase("true"))
		{
			request.setAttribute("flag", "true");
			return new ModelAndView("listSearchProperty","PropertyList",PropertyList);
		}else
		    return new ModelAndView("listUpdatedMobNum","PropertyList",PropertyList);
   		
   	
            
        }
    
    
    
    
    public ModelAndView getPropertyReceipt(HttpServletRequest request,HttpServletResponse response) throws ServletException, ParseException
    {
    	if(log.isDebugEnabled()){
    		log.debug("getProperty Details  method called");
    	}
    	HttpSession session = request.getSession();
    	ModelAndView modelAndView=new ModelAndView("manageDownloadReceipt");
    	PropertyDetails propertyDetails=null;
    	String propertyCode=request.getParameter("searchCode");    	
    	String firstName=request.getParameter("firstName");
    	String lastName=request.getParameter("lastName");
    	String wardId=request.getParameter("wardId");
    	CollectionBuffer collectionBuffer=new CollectionBuffer();
    	
    	
		List<Property> plist=null;
		List<PropertyDetails> pdetailslist=new ArrayList<PropertyDetails>();
		 
		
		
		    if(!StringUtils.isBlank(firstName) || !StringUtils.isBlank(lastName)   || !StringUtils.isBlank(propertyCode)){
    		
			     plist=propertyService.getPropertyListQuery( wardId, firstName, lastName,propertyCode);
    		
    		
    		
			    if(CollectionUtils.isEmpty(plist)){
    			
    			request.setAttribute("message", "no results found");
    			
    		 }
    		 
    		
    	}
    	
		      //String status=getPgStatus("b217a92b04238773bd9");
		     
		     //System.out.println("status pg XXXXX:"+status);
		     
      if(CollectionUtils.isNotEmpty(plist)){
    	  double amountafterrebate=0.0;
    	for(Property prop:plist){
    		propertyDetails=new PropertyDetails();
    		propertyDetails.setPropertyCode(prop.getCode());
			propertyDetails.setOwnerName(prop.getOwner().getFullName());
			if(prop.getSubOwner() != null && prop.getSubOwner().getOwnerId()>0){
			 propertyDetails.setSubOwnerName(prop.getSubOwner().getFullName());
			}else{
			 propertyDetails.setSubOwnerName("-");
			}
			propertyDetails.setPropertyAddress(prop.getPropertyAddress());
			propertyDetails.setCityAndState(prop.getCitiAndState());
			if(prop.getOwner().getOwnerGroup().getOwnerGroupId()==1002)
			{
				propertyDetails.setBalance(billService.getPropertyBillDuesAmount_Midc(propertyCode.toUpperCase()));
			} else {
				propertyDetails.setBalance(billService.getPropertyBillDuesAmount(propertyCode.toUpperCase()));
				Bill bill=billService.getPropertyBillDuesAmount(propertyCode.toUpperCase(),CommonUtils.getCurrentStringDate());
				
				
				propertyDetails.setBalance(Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt()));
    			request.setAttribute("rebateAmount", Math.round(bill.getRebateAmt()));
    			System.out.println("adavnce:"+Math.round(bill.getRebateAmt()));
    			amountafterrebate = Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt())-Math.round(bill.getRebateAmt());
    			request.setAttribute("amount", Math.round(bill.getBillAmt()+bill.getDpcAmt1()-bill.getAdvanceAmt())-Math.round(bill.getRebateAmt()));
    			
    			if(prop.getOwner().getFullName()!=null && prop.getOwner().getFullName()!=""){
    				request.setAttribute("owner",prop.getOwner().getFullName());
    				 
    			} else {
    				request.setAttribute("owner","None");
    			}
    			
    			if(prop.getOwner().getAddress().getMobileNumber()!=null && prop.getOwner().getAddress().getMobileNumber()!=""){
    				request.setAttribute("mobile",prop.getOwner().getAddress().getMobileNumber());
    			} else {
    				request.setAttribute("mobile","None");
    			}
    			
    			if(prop.getOwner().getAddress().getEmailId()!=null && prop.getOwner().getAddress().getEmailId()!=""){
    				request.setAttribute("email",prop.getOwner().getAddress().getEmailId());
    			} else {
    				request.setAttribute("email","None");
    			}
         
    			   			   			
			}
			pdetailslist.add(propertyDetails);
			
		   

    		}
    	  }
       request.setAttribute("wardId",wardId);
       request.setAttribute("txnid", txnid);
	   request.setAttribute("propertyCode", propertyCode);
       request.setAttribute("firstName", firstName);
       request.setAttribute("txnid", txnid);
       System.out.println("in hashcal method 12345:"+txnid);
       request.setAttribute("searchParamSet", "true");
         modelAndView.addObject("propertyDetails",propertyDetails);
      	return modelAndView;
    } 
    
    private void setCollectionSearchParameters(CollectionSearch searchOptions,HttpServletRequest request)
    {
    	if(log.isDebugEnabled()){
    		log.debug("setSearchParameters Method is called");
    	}

    	try{
 	    	HttpSession session = request.getSession();
 		    SessionUser sessionUser = (SessionUser)session.getAttribute("SessionUser");
 		    long wardId = sessionUser.getWardId();
 		    long collectionCenterId = sessionUser.getCollectionCenterId();
    		
    		    long longCurrentPage = CommonUtils.checkPaginationAttributes(request);
 			String orderBy = request.getParameter(const_OrderBy);
 			String sortBy = request.getParameter(const_SortBy);

 			// setting default order by on collectionId
 			if (orderBy == null || orderBy.length() < 1) {
 				orderBy = "collectionId";
 				sortBy = "desc";
 			}
 				searchOptions.setCurrentPage(longCurrentPage);
 				searchOptions.setOrderBy(orderBy);
 				searchOptions.setSortBy(sortBy);

 				String fromDate = CommonRequestUtils.getStringParameter(request, "fromDate");
 				String toDate = CommonRequestUtils.getStringParameter(request, "toDate");
 				double fromAmount=0;
 				double toAmount=0;
 				
 				if(request.getParameter("fromAmount")!= null && request.getParameter("toAmount")!= null)
 				{
 				   fromAmount=CommonRequestUtils.getDoubleParameter(request, "fromAmount");
 				   toAmount=CommonRequestUtils.getDoubleParameter(request, "toAmount");
 				}

 				if ((fromDate == null || fromDate.length() == 0) && (toDate != null && toDate.length() > 0)) {
 					fromDate = toDate;
 				} else if ((toDate == null || toDate.length() == 0) && (fromDate != null && fromDate.length() > 0)) {
 					toDate = fromDate;

 				} else if ((fromDate != null && toDate != null) && (fromDate.length() > 0 && toDate.length() > 0) && CommonUtils.getFormattedDate(fromDate).after(CommonUtils.getFormattedDate(toDate))) {
 					String tempVal = fromDate;
 					fromDate = toDate;
 					toDate = tempVal;
 				}
 				
 				if(fromAmount > 0 && toAmount == 0)
 				{
 					toAmount = fromAmount;
 				}else if(fromAmount == 0 && toAmount > 0)
 				{
 					fromAmount = toAmount;
 				}else if((fromAmount > 0 && toAmount > 0) && (fromAmount > toAmount))
 				{
 					double temp=fromAmount;
 					fromAmount=toAmount;
 					toAmount=temp;
 				}
 				searchOptions.setCode(request.getParameter("searchCode"));
 				searchOptions.setFromDate(fromDate);
 				searchOptions.setToDate(toDate);
 				searchOptions.setFromAmount(fromAmount);
 				searchOptions.setToAmount(toAmount);
 				searchOptions.setStatus(0);				
 				
 				searchOptions.setRefNumber(request.getParameter("searchRefNumber"));
 				searchOptions.setReceiptNo(request.getParameter("searchReceiptNumber"));
 				if(request.getParameter("searchPaymentMode")!=null && request.getParameter("searchPaymentMode").length()>0)
 					searchOptions.setPaymentMode(Integer.parseInt(request.getParameter("searchPaymentMode")));

 				
 				if(wardId > 0){					
 					List<Ward> wardList = new ArrayList<Ward>();
 					wardList.add(wardService.get(wardId));
 					
 					if(collectionCenterId > 0){
 						
 						searchOptions.setCollectionCenterId(collectionCenterId);						
 						List<CollectionCenter> collectionCenterList = new ArrayList<CollectionCenter>();						
 						collectionCenterList.add(collectionCenterService.get(collectionCenterId));						
 						request.setAttribute("collectionCenterList", collectionCenterList);			
 						
 					}else{
 						searchOptions.setWardId(wardId);
 						request.setAttribute("collectionCenterList", collectionCenterService.findByProperty("ward.wardId", wardId));
 					}
 					searchOptions.setWardId(wardId);
 					request.setAttribute("wardList", wardList);
 				}else{
 					request.setAttribute("wardList", wardService.getAll());
 				}
 				
 				if(request.getParameter("searchWard")!=null && request.getParameter("searchWard").length()>0){
 					searchOptions.setWardId(Integer.parseInt(request.getParameter("searchWard")));
 					request.setAttribute("collectionCenterList", collectionCenterService.findByProperty("ward.wardId", searchOptions.getWardId()));
 				}
 				if(request.getParameter("searchCollectionCenter")!=null && request.getParameter("searchCollectionCenter").length()>0)
 					searchOptions.setCollectionCenterId(Integer.parseInt(request.getParameter("searchCollectionCenter")));
 				

       }catch (Exception err) {
 		log.error(err.getMessage());
 	  }
    }


   
}
