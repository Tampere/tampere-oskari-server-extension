package tampere.search.channel;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.List;

import org.geotools.geojson.geom.GeometryJSON;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tampere.actions.SearchFromWFSChannelActionHandler;
import tampere.domain.WFSSearchChannelsConfiguration;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import fi.mml.portti.service.search.ChannelSearchResult;
import fi.mml.portti.service.search.SearchCriteria;
import fi.mml.portti.service.search.SearchResultItem;
import fi.nls.oskari.annotation.Oskari;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.geometry.WKTHelper;
import fi.nls.oskari.search.channel.SearchChannel;
import fi.nls.oskari.util.IOHelper;
import fi.nls.oskari.util.PropertyUtil;

@Oskari(WfsSearchChannel.ID)
public class WfsSearchChannel extends SearchChannel {

    /**
     * logger
     */
    private Logger log = LogFactory.getLogger(this.getClass());
    public static final String ID = "WFSSEARCH_CHANNEL";
    
    public static final String PARAM_GEOMETRY = "GEOMETRY";
    public static final String PARAM_WFS_SEARCH_CHANNEL_TYPE = "WFS_SEARCH_CHANNEL_TYPE";
    public static final String PARAM_WFS_SEARCH_CHANNEL_TITLE = "WFS_SEARCH_CHANNEL_TITLE";
    public static final String GT_GEOM_POINT = "POINT";
    public static final String GT_GEOM_LINESTRING = "LINESTRING";
    public static final String GT_GEOM_POLYGON = "POLYGON";
    public static final String GT_GEOM_MULTIPOINT = "MULTIPOINT";
    public static final String GT_GEOM_MULTILINESTRING = "MULTILINESTRING";
    public static final String GT_GEOM_MULTIPOLYGON = "MULTIPOLYGON";

    @Override
    public void init() {
        super.init();
    }

    /**
     * Returns the search raw results.
     *
     * @param searchCriteria Search criteria.
     * @return Result data in JSON format.
     * @throws Exception
     */
    private JSONArray getData(SearchCriteria searchCriteria) throws Exception {
        String searchStr = searchCriteria.getSearchString();
        log.debug("[WFSSEARCH] Search string: " + searchStr);
        List<WFSSearchChannelsConfiguration> channelsParams = (List<WFSSearchChannelsConfiguration>) searchCriteria.getParam(SearchFromWFSChannelActionHandler.PARAM_CHANNELS);
        JSONArray data = new JSONArray();
        String maxFeatures = PropertyUtil.get("search.channel.WFSSEARCH_CHANNEL.maxFeatures");
        
        for(int i=0;i<channelsParams.size();i++){
        	WFSSearchChannelsConfiguration channel = channelsParams.get(i);
        	JSONArray paramsJSONArray = new JSONArray();
        	
        	StringBuffer buf = new StringBuffer(channel.getUrl());
        	buf.append("service=WFS&version=" + channel.getVersion() +
                    "&request=GetFeature" +
                    "&typeName=" + channel.getLayerName() +
                    "&srsName=" + channel.getSrs() +
                    "&outputformat=json");
        	
        	if(maxFeatures != null && !maxFeatures.isEmpty()){
        		if(channel.getVersion().equals("1.1.0")){
        			buf.append("&maxFeatures=" +maxFeatures);
        		}else{
        			buf.append("&count=" +maxFeatures);
        		}
        	}
        	buf.append("&Filter=");
        	StringBuffer filter = new StringBuffer("<Filter>");
        	JSONArray params = channel.getParamsForSearch();
        	
        	if(channel.getIsAddress()){
        		filter.append("<And>");
        		String streetName = searchStr;
                String streetNumber = "";
                // find last word and if it is number then it must be street number?
                String lastWord = searchStr.substring(searchStr.lastIndexOf(" ") + 1);

                if (isStreetNumber(lastWord)) {
                    // override streetName without, street number
                    streetName = searchStr.substring(0, searchStr.lastIndexOf(" "));
                    log.debug("[tre] found streetnumber " + streetNumber);
                    streetNumber = lastWord;
                }
                
                filter.append("<PropertyIsLike wildCard='*' singleChar='.' escape='!' matchCase='false'>" +
        			"<PropertyName>"+params.getString(0)+"</PropertyName><Literal>"+ streetName +
        			"*</Literal></PropertyIsLike>"
        		);
                
                filter.append("<PropertyIsLike wildCard='*' singleChar='.' escape='!' matchCase='false'>" +
        			"<PropertyName>"+params.getString(1)+"</PropertyName><Literal>"+ streetNumber +
        			"*</Literal></PropertyIsLike>"
        		);
                
                paramsJSONArray.put(params.getString(0));
                paramsJSONArray.put(params.getString(1));
        		
        		filter.append("</And>");
        	} else {
        		
        		if(params.length()>1){
	        		filter.append("<Or>");
	        	}      	
	        	
	        	for(int j=0;j<params.length();j++){
	        		String param = params.getString(j);
	        		filter.append("<PropertyIsLike wildCard='*' singleChar='.' escape='!' matchCase='false'>" +
	        				"<PropertyName>"+param+"</PropertyName><Literal>*"+ searchStr +
	        				"*</Literal></PropertyIsLike>"
	        				);
	        		paramsJSONArray.put(param);
	        	}
	        	
	        	if(params.length()>1){
	        		filter.append("</Or>");
	        	}
        	
        	}
        	
        	filter.append("</Filter>");
        	String filterString = filter.toString();
        	filterString = URLEncoder.encode(filterString.trim(), "UTF-8");
        	
        	buf.append(filterString);
        	
        	if(params.length()>0) {
        		HttpURLConnection connection = getConnection(buf.toString());
        		if(channel.isAutenticated()) {
	        		String userPassword = channel.getUsername() + ":" + channel.getPassword();
	        		@SuppressWarnings("restriction")
					String encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
	        		connection.setRequestProperty("Authorization", "Basic " + encoding);
        		}
                String WFSData = IOHelper.readString(connection);
                //log.debug("[WFSSEARCH] WFSData: " + WFSData);
                JSONObject wfsJSON = new JSONObject(WFSData);
                wfsJSON.put(PARAM_WFS_SEARCH_CHANNEL_TYPE,channel.getTopic().getString(searchCriteria.getLocale()));
                wfsJSON.put(PARAM_WFS_SEARCH_CHANNEL_TITLE,paramsJSONArray);
                data.put(wfsJSON); 
        	}

        }

        return data;
    }
    
    /**
     * Returns the true if test contains numbers and/or a/b.
     *
     * @param searchCriteria Search criteria.
     * @return true if string can be set to street number field in wfs query.
     */
    private boolean isStreetNumber(String test) {
        log.debug("[tre] street number candidate: " + test);
        return test.matches("[0-9-a-b]+");
    }

    /**
     * Returns the channel search results.
     *
     * @param searchCriteria Search criteria.
     * @return Search results.
     */
    public ChannelSearchResult doSearch(SearchCriteria searchCriteria) {
        ChannelSearchResult searchResultList = new ChannelSearchResult();
        String queryStr = searchCriteria.getSearchString();
        log.debug("[WFSSEARCH] doSearch queryStr: " + queryStr);     
                
        try {
        	JSONArray datas = getData(searchCriteria);
        	
        	for (int j=0;j<datas.length();j++) {
                final JSONObject resp = datas.getJSONObject(j);
                log.debug("[WFSSEARCH] Response from server: " + resp);
                    
                JSONArray featuresArr = resp.getJSONArray("features");

                for (int i = 0; i < featuresArr.length(); i++) {
                    SearchResultItem item = new SearchResultItem();
                    JSONObject loopJSONObject = featuresArr.getJSONObject(i);
                    
                    item.setType(resp.getString(PARAM_WFS_SEARCH_CHANNEL_TYPE));
                    item.setTitle(getTitle(resp, loopJSONObject));

                    if(loopJSONObject.has("geometry")) {
	                    JSONObject featuresObj_geometry = loopJSONObject.getJSONObject("geometry");

	                    String geomType = featuresObj_geometry.getString("type").toUpperCase();
	                    GeometryJSON geom = new GeometryJSON(3);
	                    if (geomType.equals(GT_GEOM_POLYGON)) {
	                    	Polygon polygon = geom.readPolygon(featuresObj_geometry.toString());
	                    	item.addValue(PARAM_GEOMETRY, WKTHelper.getWKT(polygon));
	                    	item.setLat(Double.toString(polygon.getCentroid().getCoordinate().y));
	                    	item.setLon(Double.toString(polygon.getCentroid().getCoordinate().x));
	                    } else if (geomType.equals(GT_GEOM_LINESTRING)) {
	                        LineString lineGeom = geom.readLine(featuresObj_geometry.toString());
	                        item.addValue(PARAM_GEOMETRY, WKTHelper.getWKT(lineGeom));
	                        item.setLat(Double.toString(lineGeom.getCentroid().getCoordinate().y));
	                    	item.setLon(Double.toString(lineGeom.getCentroid().getCoordinate().x));
	                    } else if (geomType.equals(GT_GEOM_POINT)) {
	                        com.vividsolutions.jts.geom.Point pointGeom = geom.readPoint(featuresObj_geometry.toString());
	                        item.addValue(PARAM_GEOMETRY, WKTHelper.getWKT(pointGeom));
	                        item.setLat(Double.toString(pointGeom.getCentroid().getCoordinate().y));
	                    	item.setLon(Double.toString(pointGeom.getCentroid().getCoordinate().x));
	                    } else if (geomType.equals(GT_GEOM_MULTIPOLYGON)) {
	                    	MultiPolygon polygon = geom.readMultiPolygon(featuresObj_geometry.toString());
	                    	item.addValue(PARAM_GEOMETRY, WKTHelper.getWKT(polygon));
	                    	item.setLat(Double.toString(polygon.getCentroid().getCoordinate().y));
	                    	item.setLon(Double.toString(polygon.getCentroid().getCoordinate().x));
	                    } else if (geomType.equals(GT_GEOM_MULTILINESTRING)) {
	                        MultiLineString lineGeom = geom.readMultiLine(featuresObj_geometry.toString());
	                        item.addValue(PARAM_GEOMETRY, WKTHelper.getWKT(lineGeom));
	                        item.setLat(Double.toString(lineGeom.getCentroid().getCoordinate().y));
	                    	item.setLon(Double.toString(lineGeom.getCentroid().getCoordinate().x));
	                    } else if (geomType.equals(GT_GEOM_MULTIPOINT)) {
	                        MultiPoint pointGeom = geom.readMultiPoint(featuresObj_geometry.toString());
	                        item.addValue(PARAM_GEOMETRY, WKTHelper.getWKT(pointGeom));
	                        item.setLat(Double.toString(pointGeom.getCentroid().getCoordinate().y));
	                    	item.setLon(Double.toString(pointGeom.getCentroid().getCoordinate().x));
	                    }
                    }
                    
                    item.setVillage("Tampere");
                    item.setDescription("");
                    item.setLocationTypeCode("");

                    searchResultList.addItem(item);
                }
			}
        
        } catch (Exception e) {
            log.error(e, "[WFSSEARCH] Failed to search locations from register of WFSSearchChannel");
        }
        return searchResultList;
    }
    
    /**
     * Get title
     * @param resp
     * @param loopJSONObject
     * @return title
     */
    private String getTitle(JSONObject resp, JSONObject loopJSONObject){
    	StringBuffer buf = new StringBuffer();
    	JSONArray params;
		try {
			params = resp.getJSONArray(PARAM_WFS_SEARCH_CHANNEL_TITLE);
			JSONObject properties = loopJSONObject.getJSONObject("properties");
	    	
			for(int i=0;i<params.length();i++) {
	    		String param = params.getString(i);
	    		buf.append(properties.getString(param));
	    		if(i<params.length()-1) {
	    			buf.append(", ");
	    		}
	    	}
		} catch (JSONException e) {
			log.error(e, "[WFSSEARCH] Failed to get Title");
		}
    	return buf.toString();
    }
}