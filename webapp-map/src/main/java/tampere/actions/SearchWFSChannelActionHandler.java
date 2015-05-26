package tampere.actions;

import org.json.JSONArray;
import org.json.JSONObject;

import tampere.domain.WFSSearchChannelsConfiguration;
import tampere.helpers.SearchWFSChannelHelper;
import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.control.RestActionHandler;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.ConversionHelper;
import fi.nls.oskari.util.ResponseHelper;

@OskariActionRoute("SearchWFSChannel")
public class SearchWFSChannelActionHandler extends RestActionHandler {
	
	 private static final Logger log = LogFactory.getLogger(SearchWFSChannelActionHandler.class);
	 private static final String PARAM_ID = "id";
	 private static final String PARAM_WFS_ID = "wfsLayerId";
	 private static final String PARAM_TOPIC = "topic";
	 private static final String PARAM_DESC = "desc";
	 private static final String PARAM_PARAMS_FOR_SEARCH= "paramsForSearch";
     
     @Override
     public void handleGet(ActionParameters params) throws ActionException {
    	 // Only admin user
    	 params.requireAdminUser();
    	 
    	 try {
    		 ResponseHelper.writeResponse(params, SearchWFSChannelHelper.getChannels());
    	 } catch (Exception ex){
    		 log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(ex));    		 
    		 throw new ActionParamsException("Couldn't get WFS search channels");
    	 }
     }
     
     @Override
     public void handleDelete(ActionParameters params) throws ActionException {    	 
    	 // Only admin user
    	 params.requireAdminUser();
    	 
    	 int channelId = ConversionHelper.getInt(params.getRequiredParam(PARAM_ID), -1);
    	 
    	 try {
    		 if(channelId>0) {
    			 ResponseHelper.writeResponse(params, SearchWFSChannelHelper.delete(channelId));
    		 } else {
    			 throw new ActionParamsException("Couldn't delete WFS search channel");
    		 }
    	 } catch (Exception ex){
    		 log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(ex));    		 
    		 throw new ActionParamsException("Couldn't delete WFS search channel");
    	 }
     }
     
     @Override
     public void handlePost(ActionParameters params) throws ActionException {
    	 
    	 // Only admin user
    	 params.requireAdminUser();
       	 
	   	 try {
	   		WFSSearchChannelsConfiguration conf = new WFSSearchChannelsConfiguration();
	   		conf.setWFSLayerId(ConversionHelper.getInt(params.getRequiredParam(PARAM_WFS_ID), -1));
	   		conf.setTopic(new JSONObject(ConversionHelper.getString(params.getRequiredParam(PARAM_TOPIC), "")));
	   		conf.setDesc(new JSONObject(ConversionHelper.getString(params.getRequiredParam(PARAM_DESC),"")));   		
	   		conf.setParamsForSearch(new JSONArray(ConversionHelper.getString(params.getRequiredParam(PARAM_PARAMS_FOR_SEARCH),"")));
	   		conf.setId(ConversionHelper.getInt(params.getRequiredParam(PARAM_ID), -1));
	   		
	   		ResponseHelper.writeResponse(params, SearchWFSChannelHelper.update(conf));
	   	 } catch (Exception ex){
	   		 log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(ex));    		 
	   		 throw new ActionParamsException("Couldn't update WFS search channel");
	   	 }
     }
     
     @Override
     public void handlePut(ActionParameters params) throws ActionException {
    	 // Only admin user
    	 params.requireAdminUser();
   	 
	   	 try {
	   		WFSSearchChannelsConfiguration conf = new WFSSearchChannelsConfiguration();
	   		conf.setWFSLayerId(ConversionHelper.getInt(params.getRequiredParam(PARAM_WFS_ID), -1));
	   		conf.setTopic(new JSONObject(ConversionHelper.getString(params.getRequiredParam(PARAM_TOPIC), "")));
	   		conf.setDesc(new JSONObject(ConversionHelper.getString(params.getRequiredParam(PARAM_DESC),"")));   		
	   		conf.setParamsForSearch(new JSONArray(ConversionHelper.getString(params.getRequiredParam(PARAM_PARAMS_FOR_SEARCH),"")));

	   		ResponseHelper.writeResponse(params, SearchWFSChannelHelper.insert(conf));
	   	 } catch (Exception ex){
	   		 log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(ex));    		 
	   		 throw new ActionParamsException("Couldn't add WFS search channel");
	   	 }
     
     }

}
