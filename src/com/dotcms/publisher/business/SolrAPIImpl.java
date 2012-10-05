package com.dotcms.publisher.business;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dotcms.publisher.util.SolrUtil;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;

/**
 * Implement the SolrAPI abstract class methods
 * @author Oswaldo
 *
 */
public class SolrAPIImpl extends SolrAPI{

	private static SolrAPIImpl instance= null;
	private static int numOfTries = 10;
	public static SolrAPIImpl getInstance() {
		if(instance==null){
			instance = new SolrAPIImpl();
			try {
				numOfTries = 5; //TODO Put this value in a properties file
			} catch (NumberFormatException e) {
				Logger.debug(SolrAPIImpl.class, e.getMessage());
			}
		}	
		
		return instance;
	}
	protected SolrAPIImpl(){
		// Exists only to defeat instantiation.
	}
	
	private static final String MANDATORY_FIELDS= 
										"operation, "+
										"asset_identifier, "+ 
										"language_id, "+
										"entered_date, "+
										"in_error, "+ 
										"publish_date, "+ 
										"server_id, "+ 
										"type, "+ 
										"bundle_id";
	private static final String MANDATORY_PLACE_HOLDER = "?,?,?,?,?,?,?,?,?" ;
	
	//"last_results, "+ 
	//	"last_try,  "+
	//	"num_of_tries, "+ 
	
	private static final String PGINSERTSOLRSQL="insert into publishing_queue("+MANDATORY_FIELDS+") values("+MANDATORY_PLACE_HOLDER+")";
	private static final String MYINSERTSOLRSQL="insert into publishing_queue("+MANDATORY_FIELDS+") values("+MANDATORY_PLACE_HOLDER+")";
	private static final String MSINSERTSOLRSQL="insert into publishing_queue("+MANDATORY_FIELDS+") values("+MANDATORY_PLACE_HOLDER+")";
	private static final String OCLINSERTSOLRSQL="insert into publishing_queue("+MANDATORY_FIELDS+") values("+MANDATORY_PLACE_HOLDER+")";

	/**
	 * Include in the publishing_queue table the content to add or update in the Solr Index
	 * @param con Contentlet
	 * @throws DotSolrException 
	 */
	public void addContentToSolr(Contentlet con) throws DotSolrException {
		try{
			HibernateUtil.startTransaction();
			DotConnect dc = new DotConnect();
			if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)){
				/*Validate if the table doesn't exist then is created*/
				dc.setSQL(PGINSERTSOLRSQL);
				dc.addParam(SolrAPI.ADD_OR_UPDATE_SOLR_ELEMENT);
				dc.addObject(con.getIdentifier());
				dc.addObject(con.getLanguageId());
				dc.addParam(new Date());
				dc.addParam(DbConnectionFactory.getDBFalse());	//in error field
				
				//TODO How do I get new columns value?
				
				dc.loadResult();	
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
				dc.setSQL(MYINSERTSOLRSQL);
				dc.addParam(SolrAPI.ADD_OR_UPDATE_SOLR_ELEMENT);
				dc.addObject(con.getIdentifier());
				dc.addObject(con.getLanguageId());
				dc.addParam(new Date());
				dc.addParam(DbConnectionFactory.getDBFalse());	//in error field
				
				//TODO How do I get new columns value?	
				
				dc.loadResult();				
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
				dc.setSQL(MSINSERTSOLRSQL);
				dc.addParam(SolrAPI.ADD_OR_UPDATE_SOLR_ELEMENT);
				dc.addObject(con.getIdentifier());
				dc.addObject(con.getLanguageId());
				dc.addParam(new Date());
				dc.addParam(DbConnectionFactory.getDBFalse());	//in error field
				
				//TODO How do I get new columns value?	
				
				dc.loadResult();				
			}else{
				dc.setSQL(OCLINSERTSOLRSQL);
				dc.addParam(SolrAPI.ADD_OR_UPDATE_SOLR_ELEMENT);
				dc.addObject(con.getIdentifier());
				dc.addObject(con.getLanguageId());
				dc.addParam(new Date());
				dc.addParam(DbConnectionFactory.getDBFalse());	//in error field
				
				//TODO How do I get new columns value?	
								
				dc.loadResult();				
			}
			HibernateUtil.commitTransaction();			
		}catch(Exception e){

			try {
				HibernateUtil.rollbackTransaction();
			} catch (DotHibernateException e1) {
				Logger.debug(SolrAPIImpl.class,e.getMessage(),e1);
			}			
			Logger.debug(SolrAPIImpl.class,e.getMessage(),e);
			throw new DotSolrException("Unable to add " + con.getIdentifier() + " to solr table:" + e.getMessage(), e);
		}finally{
			DbConnectionFactory.closeConnection();
		}
	}

	private static final String PGDELETESOLRSQL= PGINSERTSOLRSQL;
	private static final String MYDELETESOLRSQL= MYINSERTSOLRSQL;
	private static final String MSDELETESOLRSQL= MSINSERTSOLRSQL;
	private static final String OCLDELETESOLRSQL= OCLINSERTSOLRSQL;
	/**
	 * Include in the publishing_queue table the content to remove in the Solr Index
	 * @param con Contentlet
	 * @throws DotSolrException
	 */
	public void removeContentFromSolr(Contentlet con) throws DotSolrException {
		try{
			HibernateUtil.startTransaction();
			DotConnect dc = new DotConnect();
			if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)){
				/*Validate if the table doesn't exist then is created*/
				dc.setSQL(PGDELETESOLRSQL);
				dc.addParam(SolrAPI.DELETE_SOLR_ELEMENT);
				dc.addObject(con.getIdentifier());
				dc.addObject(con.getLanguageId());
				dc.addParam(new Date());
				dc.addParam(DbConnectionFactory.getDBFalse());	//in error field
				
				//TODO How do I get new columns value?	
				
				dc.loadResult();
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
				dc.setSQL(MYDELETESOLRSQL);
				dc.addParam(SolrAPI.DELETE_SOLR_ELEMENT);
				dc.addObject(con.getIdentifier());
				dc.addObject(con.getLanguageId());
				dc.addParam(new Date());
				dc.addParam(DbConnectionFactory.getDBFalse());	//in error field
				
				//TODO How do I get new columns value?	
				
				dc.loadResult();					
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
				dc.setSQL(MSDELETESOLRSQL);
				dc.addParam(SolrAPI.DELETE_SOLR_ELEMENT);
				dc.addObject(con.getIdentifier());
				dc.addObject(con.getLanguageId());
				dc.addParam(new Date());
				dc.addParam(DbConnectionFactory.getDBFalse());	//in error field
				
				//TODO How do I get new columns value?	
				
				dc.loadResult();					
			}else{
				dc.setSQL(OCLDELETESOLRSQL);
				dc.addParam(SolrAPI.DELETE_SOLR_ELEMENT);
				dc.addObject(con.getIdentifier());
				dc.addObject(con.getLanguageId());
				dc.addParam(new Date());
				dc.addParam(DbConnectionFactory.getDBFalse());	//in error field
				
				//TODO How do I get new columns value?	
				
				dc.loadResult();
			}
			HibernateUtil.commitTransaction();
		}catch(Exception e){
			try {
				HibernateUtil.rollbackTransaction();
			} catch (DotHibernateException e1) {
				Logger.debug(SolrAPIImpl.class,e.getMessage(),e1);
			}
			Logger.debug(SolrAPIImpl.class,e.getMessage(),e);
			throw new DotSolrException("Unable to add " + con.getIdentifier() + " to solr table:" + e.getMessage(), e);
		}finally{
			DbConnectionFactory.closeConnection();
		}
	}
	
	public void removeContentFromSolr(String identifier, long languageId) throws DotSolrException {
		try{
			HibernateUtil.startTransaction();
			DotConnect dc = new DotConnect();
			if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)){
				/*Validate if the table doesn't exist then is created*/
				dc.setSQL(PGDELETESOLRSQL);
				dc.addParam(SolrAPI.DELETE_SOLR_ELEMENT);
				dc.addObject(identifier);
				dc.addObject(languageId);
				dc.addParam(new Date());
				dc.addParam(DbConnectionFactory.getDBFalse());	//in error field
				
				//TODO How do I get new columns value?	
				
				dc.loadResult();	
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
				dc.setSQL(MYDELETESOLRSQL);
				dc.addParam(SolrAPI.DELETE_SOLR_ELEMENT);
				dc.addObject(identifier);
				dc.addObject(languageId);
				dc.addParam(new Date());
				dc.addParam(DbConnectionFactory.getDBFalse());	//in error field
				
				//TODO How do I get new columns value?	
				
				dc.loadResult();					
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
				dc.setSQL(MSDELETESOLRSQL);
				dc.addParam(SolrAPI.DELETE_SOLR_ELEMENT);
				dc.addObject(identifier);
				dc.addObject(languageId);
				dc.addParam(new Date());
				dc.addParam(DbConnectionFactory.getDBFalse());	//in error field
				
				//TODO How do I get new columns value?	
				
				dc.loadResult();						
			}else{
				dc.setSQL(OCLDELETESOLRSQL);
				dc.addParam(SolrAPI.DELETE_SOLR_ELEMENT);
				dc.addObject(identifier);
				dc.addObject(languageId);
				dc.addParam(new Date());
				dc.addParam(DbConnectionFactory.getDBFalse());	//in error field
				
				//TODO How do I get new columns value?	
				
				dc.loadResult();
			}
			HibernateUtil.commitTransaction();
		}catch(Exception e){
			try {
				HibernateUtil.rollbackTransaction();
			} catch (DotHibernateException e1) {
				Logger.debug(SolrAPIImpl.class,e.getMessage(),e1);
			}
			Logger.debug(SolrAPIImpl.class,e.getMessage(),e);
			throw new DotSolrException("Unable to add " + identifier + " to solr table:" + e.getMessage(), e);
		}finally{
			DbConnectionFactory.closeConnection();
		}
	}

	private static final String PSGETENTRIESWITHERRORS="select * from publishing_queue where in_error = "+DbConnectionFactory.getDBTrue();
	private static final String MYGETENTRIESWITHERRORS="select * from publishing_queue where in_error = "+DbConnectionFactory.getDBTrue();
	private static final String MSGETENTRIESWITHERRORS="select * from publishing_queue where in_error = "+DbConnectionFactory.getDBTrue();
	private static final String OCLGETENTRIESWITHERRORS="select * from publishing_queue where in_error = "+DbConnectionFactory.getDBTrue();
	/**
	 * Get a list of all the elements in the publishing_queue table that could be processes because some error
	 * @return List<Map<String,Object>>
	 * @throws DotSolrException
	 */
	public List<Map<String,Object>> getQueueErrors() throws DotSolrException {
		try{
			DotConnect dc = new DotConnect();
			if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)){
				dc.setSQL(PSGETENTRIESWITHERRORS);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
				dc.setSQL(MYGETENTRIESWITHERRORS);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
				dc.setSQL(MSGETENTRIESWITHERRORS);
			}else{
				dc.setSQL(OCLGETENTRIESWITHERRORS);
			}
			return dc.loadObjectResults();
		}catch(Exception e){
			Logger.debug(SolrUtil.class,e.getMessage(),e);
			throw new DotSolrException("Unable to get list of solr elements with error:"+e.getMessage(), e);
		}finally{
			DbConnectionFactory.closeConnection();
		}
	}
	
	private static final String PSGETPAGINATEDENTRIESCOUNTERWITHERRORS="select count(*) as count from publishing_queue where in_error = "+DbConnectionFactory.getDBTrue();
	private static final String MYGETPAGINATEDENTRIESCOUNTERWITHERRORS="select count(*) as count from publishing_queue where in_error = "+DbConnectionFactory.getDBTrue();
	private static final String MSGETPAGINATEDENTRIESCOUNTERWITHERRORS="select count(*) as count from publishing_queue where in_error = "+DbConnectionFactory.getDBTrue();
	private static final String OCLGETPAGINATEDENTRIESCOUNTERWITHERRORS="select count(*) as count from publishing_queue where in_error = "+DbConnectionFactory.getDBTrue();
	/**
	 * Get the total of all the elements in the publishing_queue table that could be processes because some error
	 * @param condition WHERE condition
	 * @param orderBy ORDER BY condition
	 * @return List<Map<String,Object>>
	 * @throws DotSolrException
	 */
	public List<Map<String,Object>> getQueueErrorsCounter(String condition, String orderBy) throws DotSolrException {
		try{
			DotConnect dc = new DotConnect();
			String query = "";
			if(UtilMethods.isSet(condition)){
				query = " AND "+condition;
			}
			if(UtilMethods.isSet(orderBy)){
				query += " ORDER BY "+orderBy;
			}
			if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)){
				dc.setSQL(PSGETPAGINATEDENTRIESCOUNTERWITHERRORS+query);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
				dc.setSQL(MYGETPAGINATEDENTRIESCOUNTERWITHERRORS+query);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
				dc.setSQL(MSGETPAGINATEDENTRIESCOUNTERWITHERRORS+query);
			}else{
				dc.setSQL(OCLGETPAGINATEDENTRIESCOUNTERWITHERRORS+query);
			}
			return dc.loadObjectResults();
		}catch(Exception e){
			Logger.debug(SolrUtil.class,e.getMessage(),e);
			throw new DotSolrException("Unable to get list of solr elements with error:"+e.getMessage(), e);
		}finally{
			DbConnectionFactory.closeConnection();
		}
	}
	
	private static final String PSGETPAGINATEDENTRIESWITHERRORS="select * from publishing_queue where in_error = "+DbConnectionFactory.getDBTrue();
	private static final String MYGETPAGINATEDENTRIESWITHERRORS="select * from publishing_queue where in_error = "+DbConnectionFactory.getDBTrue();
	private static final String MSGETPAGINATEDENTRIESWITHERRORS="select * from publishing_queue where in_error = "+DbConnectionFactory.getDBTrue();
	private static final String OCLGETPAGINATEDENTRIESWITHERRORS="select * from publishing_queue where in_error = "+DbConnectionFactory.getDBTrue();
	/**
	 * Get a list of all the elements in the publishing_queue table that could be processes because some error
	 * @param condition WHERE condition
	 * @param orderBy ORDER BY condition
	 * @param offset first row to return
	 * @param limit max number of rows to return
	 * @return List<Map<String,Object>>
	 * @throws DotSolrException
	 */
	public List<Map<String,Object>> getQueueErrorsPaginated(String condition, String orderBy, String offset, String limit) throws DotSolrException {
		try{
			DotConnect dc = new DotConnect();
			String query = "";
			if(UtilMethods.isSet(condition)){
				query = " AND "+condition;
			}
			if(UtilMethods.isSet(orderBy)){
				query += " ORDER BY "+orderBy;
			}
			if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)){
				dc.setSQL(PSGETPAGINATEDENTRIESWITHERRORS+query);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
				dc.setSQL(MYGETPAGINATEDENTRIESWITHERRORS+query);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
				dc.setSQL(MSGETPAGINATEDENTRIESWITHERRORS+query);
			}else{
				dc.setSQL(OCLGETPAGINATEDENTRIESWITHERRORS+query);
			}
			dc.setStartRow(offset);
			dc.setMaxRows(limit);
			return dc.loadObjectResults();
		}catch(Exception e){
			Logger.debug(SolrUtil.class,e.getMessage(),e);
			throw new DotSolrException("Unable to get list of solr elements with error:"+e.getMessage(), e);
		}finally{
			DbConnectionFactory.closeConnection();
		}
	}


	private static final String PSGETASSETSTOINDEX="select * from publishing_queue where num_of_tries < "+numOfTries+" and operation in ("+SolrAPI.ADD_OR_UPDATE_SOLR_ELEMENT+","+SolrAPI.DELETE_SOLR_ELEMENT+") order by id asc";
	private static final String MYGETASSETSTOINDEX="select * from publishing_queue where num_of_tries < "+numOfTries+" and operation in ("+SolrAPI.ADD_OR_UPDATE_SOLR_ELEMENT+","+SolrAPI.DELETE_SOLR_ELEMENT+") order by id asc";
	private static final String MSGETASSETSTOINDEX="select * from publishing_queue where num_of_tries < "+numOfTries+" and operation in ("+SolrAPI.ADD_OR_UPDATE_SOLR_ELEMENT+","+SolrAPI.DELETE_SOLR_ELEMENT+") order by id asc";
	private static final String OCLGETASSETSTOINDEX="select * from publishing_queue where num_of_tries < "+numOfTries+" and operation in ("+SolrAPI.ADD_OR_UPDATE_SOLR_ELEMENT+","+SolrAPI.DELETE_SOLR_ELEMENT+") order by id asc";
	/**
	 * Get the Assets not processed yet to update the Solr index
	 * @return List<Map<String,Object>>
	 * @throws DotSolrException
	 */
	public List<Map<String,Object>> getSolrQueueContentletToProcess() throws DotSolrException{
		try{
			DotConnect dc = new DotConnect();
			if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)){
				dc.setSQL(PSGETASSETSTOINDEX);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
				dc.setSQL(MYGETASSETSTOINDEX);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
				dc.setSQL(MSGETASSETSTOINDEX);
			}else{
				dc.setSQL(OCLGETASSETSTOINDEX);
			}
			return dc.loadObjectResults();
		}catch(Exception e){
			Logger.debug(SolrUtil.class,e.getMessage(),e);
			throw new DotSolrException("Unable to get list of solr elements to process:"+e.getMessage(), e);
		}finally{
			DbConnectionFactory.closeConnection();
		}
	}
	
	private static final String PSGETQUEUEPAGINATEDASSETSCOUNTERTOINDEX="select count(*) as count from publishing_queue";
	private static final String MYGETQUEUEPAGINATEDASSETSCOUNTERTOINDEX="select count(*) as count from publishing_queue";
	private static final String MSGETQUEUEPAGINATEDASSETSCOUNTERTOINDEX="select count(*) as count from publishing_queue";
	private static final String OCLGETQUEUEPAGINATEDASSETSCOUNTERTOINDEX="select count(*) as count from publishing_queue";
	/**
	 * Get the total of All the Assets in the publishing_queue table paginated
	 * @param condition WHERE condition
	 * @param orderBy ORDER BY condition
	 * @return List<Map<String,Object>>
	 * @throws DotSolrException
	 */
	public List<Map<String,Object>> getSolrQueueContentletsCounter(String condition, String orderBy) throws DotSolrException{
		try{
			DotConnect dc = new DotConnect();
			String query = "";
			if(UtilMethods.isSet(condition)){
				query = " WHERE "+condition;
			}
			if(UtilMethods.isSet(orderBy)){
				query += " ORDER BY "+orderBy;
			}
			if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)){
				dc.setSQL(PSGETQUEUEPAGINATEDASSETSCOUNTERTOINDEX+query);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
				dc.setSQL(MYGETQUEUEPAGINATEDASSETSCOUNTERTOINDEX+query);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
				dc.setSQL(MSGETQUEUEPAGINATEDASSETSCOUNTERTOINDEX+query);
			}else{
				dc.setSQL(OCLGETQUEUEPAGINATEDASSETSCOUNTERTOINDEX+query);				
			}
			return dc.loadObjectResults();
		}catch(Exception e){
			Logger.debug(SolrUtil.class,e.getMessage(),e);
			throw new DotSolrException("Unable to get list of solr elements:"+e.getMessage(), e);
		}finally{
			DbConnectionFactory.closeConnection();
		}
	}
	
	private static final String PSGETQUEUEPAGINATEDASSETSTOINDEX="select * from publishing_queue";
	private static final String MYGETQUEUEPAGINATEDASSETSTOINDEX="select * from publishing_queue";
	private static final String MSGETQUEUEPAGINATEDASSETSTOINDEX="select * from publishing_queue";
	private static final String OCLGETQUEUEPAGINATEDASSETSTOINDEX="select * from publishing_queue";
	/**
	 * Get All the Assets in the publishing_queue table paginated
	 * @param condition WHERE condition
	 * @param orderBy ORDER BY condition
	 * @param offset first row to return
	 * @param limit max number of rows to return
	 * @return List<Map<String,Object>>
	 * @throws DotSolrException
	 */
	public List<Map<String,Object>> getSolrQueueContentletsPaginated(String condition, String orderBy, String offset, String limit) throws DotSolrException{
		try{
			DotConnect dc = new DotConnect();
			String query = "";
			if(UtilMethods.isSet(condition)){
				query = " WHERE "+condition;
			}
			if(UtilMethods.isSet(orderBy)){
				query += " ORDER BY "+orderBy;
			}
			if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)){
				dc.setSQL(PSGETQUEUEPAGINATEDASSETSTOINDEX+query);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
				dc.setSQL(MYGETQUEUEPAGINATEDASSETSTOINDEX+query);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
				dc.setSQL(MSGETQUEUEPAGINATEDASSETSTOINDEX+query);
			}else{
				dc.setSQL(OCLGETQUEUEPAGINATEDASSETSTOINDEX+query);				
			}
			dc.setStartRow(offset);
			dc.setMaxRows(limit);
			return dc.loadObjectResults();
		}catch(Exception e){
			Logger.debug(SolrUtil.class,e.getMessage(),e);
			throw new DotSolrException("Unable to get list of solr elements:"+e.getMessage(), e);
		}finally{
			DbConnectionFactory.closeConnection();
		}
	}
	
	private static final String PSGETPAGINATEDASSETSCOUNTERTOINDEX="select count(*) as count from publishing_queue where num_of_tries < "+numOfTries;
	private static final String MYGETPAGINATEDASSETSCOUNTERTOINDEX="select count(*) as count from publishing_queue where num_of_tries < "+numOfTries;
	private static final String MSGETPAGINATEDASSETSCOUNTERTOINDEX="select count(*) as count from publishing_queue where num_of_tries < "+numOfTries;
	private static final String OCLGETPAGINATEDASSETSCOUNTERTOINDEX="select count(*) as count from publishing_queue where num_of_tries < "+numOfTries;
	/**
	 * Get the total of Assets not processed yet to update the Solr index paginated
	 * @param condition WHERE condition
	 * @param orderBy ORDER BY condition
	 * @return List<Map<String,Object>>
	 * @throws DotSolrException
	 */
	public List<Map<String,Object>> getSolrQueueContentletToProcessCounter(String condition, String orderBy) throws DotSolrException{
		try{
			DotConnect dc = new DotConnect();
			String query = "";
			if(UtilMethods.isSet(condition)){
				query = " AND "+condition;
			}
			if(UtilMethods.isSet(orderBy)){
				query += " ORDER BY "+orderBy;
			}
			if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)){
				dc.setSQL(PSGETPAGINATEDASSETSCOUNTERTOINDEX+query);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
				dc.setSQL(MYGETPAGINATEDASSETSCOUNTERTOINDEX+query);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
				dc.setSQL(MSGETPAGINATEDASSETSCOUNTERTOINDEX+query);
			}else{
				dc.setSQL(OCLGETPAGINATEDASSETSCOUNTERTOINDEX+query);				
			}
			return dc.loadObjectResults();
		}catch(Exception e){
			Logger.debug(SolrUtil.class,e.getMessage(),e);
			throw new DotSolrException("Unable to get list of solr elements to process:"+e.getMessage(), e);
		}finally{
			DbConnectionFactory.closeConnection();
		}
	}
	
	private static final String PSGETPAGINATEDASSETSTOINDEX="select * from publishing_queue where num_of_tries < "+numOfTries;
	private static final String MYGETPAGINATEDASSETSTOINDEX="select * from publishing_queue where num_of_tries < "+numOfTries;
	private static final String MSGETPAGINATEDASSETSTOINDEX="select * from publishing_queue where num_of_tries < "+numOfTries;
	private static final String OCLGETPAGINATEDASSETSTOINDEX="select * from publishing_queue where num_of_tries < "+numOfTries;
	/**
	 * Get the Assets not processed yet to update the Solr index paginated
	 * @param condition WHERE condition
	 * @param orderBy ORDER BY condition
	 * @param offset first row to return
	 * @param limit max number of rows to return
	 * @return List<Map<String,Object>>
	 * @throws DotSolrException
	 */
	public List<Map<String,Object>> getSolrQueueContentletToProcessPaginated(String condition, String orderBy, String offset, String limit) throws DotSolrException{
		try{
			DotConnect dc = new DotConnect();
			String query = "";
			if(UtilMethods.isSet(condition)){
				query = " AND "+condition;
			}
			if(UtilMethods.isSet(orderBy)){
				query += " ORDER BY "+orderBy;
			}
			if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)){
				dc.setSQL(PSGETPAGINATEDASSETSTOINDEX+query);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
				dc.setSQL(MYGETPAGINATEDASSETSTOINDEX+query);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
				dc.setSQL(MSGETPAGINATEDASSETSTOINDEX+query);
			}else{
				dc.setSQL(OCLGETPAGINATEDASSETSTOINDEX+query);				
			}
			dc.setStartRow(offset);
			dc.setMaxRows(limit);
			return dc.loadObjectResults();
		}catch(Exception e){
			Logger.debug(SolrUtil.class,e.getMessage(),e);
			throw new DotSolrException("Unable to get list of solr elements to process:"+e.getMessage(), e);
		}finally{
			DbConnectionFactory.closeConnection();
		}
	}

	/**
	 * update element from publishing_queue table by id
	 */
	private static final String PSUPDATEELEMENTFROMSOLRQUEUESQL="UPDATE publishing_queue SET last_try=?, num_of_tries=?, in_error=?, last_results=? where id=?";
	private static final String MYUPDATEELEMENTFROMSOLRQUEUESQL="UPDATE publishing_queue SET last_try=?, num_of_tries=?, in_error=?, last_results=? where id=?";
	private static final String MSUPDATEELEMENTFROMSOLRQUEUESQL="UPDATE publishing_queue SET last_try=?, num_of_tries=?, in_error=?, last_results=? where id=?"; 
	private static final String OCLUPDATEELEMENTFROMSOLRQUEUESQL="UPDATE publishing_queue SET last_try=?, num_of_tries=?, in_error=?, last_results=? where id=?";
	/**
	 * update element from publishing_queue table by id
	 * @param id ID of the element in the publishing_queue
	 * @param next_try date of the next intent to execute the query
	 * @param in_error bolean indication if there was an error
	 * @param last_results error message
	 * @throws DotSolrException
	 */
	public void updateElementStatusFromSolrQueueTable(long id, Date last_try,int num_of_tries, boolean in_error,String last_results ) throws DotSolrException{
		try{
			HibernateUtil.startTransaction();
			DotConnect dc = new DotConnect();			
			if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)){
				/*Validate if the table doesn't exist then is created*/				
				dc.setSQL(PSUPDATEELEMENTFROMSOLRQUEUESQL);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
				dc.setSQL(MYUPDATEELEMENTFROMSOLRQUEUESQL);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
				dc.setSQL(MSUPDATEELEMENTFROMSOLRQUEUESQL);
			}else{
				dc.setSQL(OCLUPDATEELEMENTFROMSOLRQUEUESQL);
			}
			dc.addParam(last_try);
			dc.addParam(num_of_tries);
			dc.addParam(in_error);
			dc.addParam(last_results);
			dc.addParam(id);
			dc.loadResult();
			HibernateUtil.commitTransaction();
		}catch(Exception e){
			try {
				HibernateUtil.rollbackTransaction();
			} catch (DotHibernateException e1) {
				Logger.debug(SolrAPIImpl.class,e.getMessage(),e1);
			}
			Logger.debug(SolrUtil.class,e.getMessage(),e);
			throw new DotSolrException("Unable to update element "+id+" :"+e.getMessage(), e);
		}finally{
			DbConnectionFactory.closeConnection();
		}
	}

	/**
	 * Delete element from publishing_queue table by id
	 */
	private static final String PSDELETEELEMENTFROMSOLRQUEUESQL="DELETE FROM publishing_queue where id=?";
	private static final String MYDELETEELEMENTFROMSOLRQUEUESQL="DELETE FROM publishing_queue where id=?";
	private static final String MSDELETEELEMENTFROMSOLRQUEUESQL="DELETE FROM publishing_queue where id=?"; 
	private static final String OCLDELETEELEMENTFROMSOLRQUEUESQL="DELETE FROM publishing_queue where id=?";
	/**
	 * Delete element from publishing_queue table by id
	 * @param id ID of the element in the table
	 * @return boolean
	 * @throws DotSolrException
	 */
	public void deleteElementFromSolrQueueTable(long id) throws DotSolrException{
		try{
			HibernateUtil.startTransaction();
			DotConnect dc = new DotConnect();
			if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)){
				/*Validate if the table doesn't exist then is created*/				
				dc.setSQL(PSDELETEELEMENTFROMSOLRQUEUESQL);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
				dc.setSQL(MYDELETEELEMENTFROMSOLRQUEUESQL);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
				dc.setSQL(MSDELETEELEMENTFROMSOLRQUEUESQL);
			}else{
				dc.setSQL(OCLDELETEELEMENTFROMSOLRQUEUESQL);
			}
			dc.addParam(id);
			dc.loadResult();
			HibernateUtil.commitTransaction();
		}catch(Exception e){
			try {
				HibernateUtil.rollbackTransaction();
			} catch (DotHibernateException e1) {
				Logger.debug(SolrAPIImpl.class,e.getMessage(),e1);
			}
			Logger.debug(SolrUtil.class,e.getMessage(),e);
			throw new DotSolrException("Unable to delete element "+id+" :"+e.getMessage(), e);
		}finally{
			DbConnectionFactory.closeConnection();
		}
	}
	
	private static final String PSDELETEALLELEMENTFROMSOLRQUEUESQL="DELETE FROM publishing_queue";
	private static final String MYDELETEALLELEMENTFROMSOLRQUEUESQL="DELETE FROM publishing_queue";
	private static final String MSDELETEALLELEMENTFROMSOLRQUEUESQL="DELETE FROM publishing_queue"; 
	private static final String OCLDELETEALLELEMENTFROMSOLRQUEUESQL="DELETE FROM publishing_queue";
	/**
	 * Delete all elements from publishing_queue table
	 * @return boolean
	 */
	public void deleteAllElementsFromSolrQueueTable() throws DotSolrException{
		try{
			HibernateUtil.startTransaction();
			DotConnect dc = new DotConnect();
			if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)){
				/*Validate if the table doesn't exist then is created*/				
				dc.setSQL(PSDELETEALLELEMENTFROMSOLRQUEUESQL);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
				dc.setSQL(MYDELETEALLELEMENTFROMSOLRQUEUESQL);
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
				dc.setSQL(MSDELETEALLELEMENTFROMSOLRQUEUESQL);
			}else{
				dc.setSQL(OCLDELETEALLELEMENTFROMSOLRQUEUESQL);
			}
			dc.loadResult();
			HibernateUtil.commitTransaction();
		}catch(Exception e){
			try {
				HibernateUtil.rollbackTransaction();
			} catch (DotHibernateException e1) {
				Logger.debug(SolrAPIImpl.class,e.getMessage(),e1);
			}
			Logger.debug(SolrUtil.class,e.getMessage(),e);
			throw new DotSolrException("Unable to delete elements :"+e.getMessage(), e);
		}finally{
			DbConnectionFactory.closeConnection();
		}
	}

}