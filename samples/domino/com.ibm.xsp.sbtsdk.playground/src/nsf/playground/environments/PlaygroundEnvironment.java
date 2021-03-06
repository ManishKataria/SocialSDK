package nsf.playground.environments;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import nsf.playground.beans.DataAccessBean;
import nsf.playground.extension.Endpoints;

import com.ibm.commons.util.StringUtil;
import com.ibm.sbt.jslibrary.SBTEnvironment;
import com.ibm.sbt.opensocial.domino.config.DefaultContainerConfig;
import com.ibm.sbt.opensocial.domino.config.OpenSocialContainerConfig;
import com.ibm.sbt.opensocial.domino.container.ContainerExtPoint;
import com.ibm.sbt.opensocial.domino.container.ContainerExtPointException;
import com.ibm.sbt.opensocial.domino.oauth.DominoOAuth2Store;
import com.ibm.sbt.opensocial.domino.oauth.DominoOAuthStore;
import com.ibm.sbt.playground.extension.PlaygroundExtensionFactory;

/**
 * This is an extended environment class holding extra playground specific information
 * @author priand
 *
 */
public class PlaygroundEnvironment extends SBTEnvironment implements ContainerExtPoint {

	public static final String SESSION_PARAMETERS_MAP	 = "sessionParamsMap";
	
	public static PlaygroundEnvironment getCurrentEnvironment() {
		return getCurrentEnvironment(null);
	}
	public static PlaygroundEnvironment getCurrentEnvironment(String envName) {
		return DataAccessBean.get().getCurrentEnvironment(envName);
	}

	public static String getCurrentEnvironmentName() {
		return getCurrentEnvironmentName(null);
	}
	public static String getCurrentEnvironmentName(String envName) {
		return getCurrentEnvironment(envName).getName();
	}
	
	private static final class FieldMap extends HashMap<String, String> {
		private static final long serialVersionUID=1L;
		public String get(Object key) {
			return super.get(convertKey((String)key));
		}
		public String put(String key, String value) {
			return super.put(convertKey(key),value);
		}
		private static String convertKey(String o) {
			return ((String)o).toLowerCase();
		}
	}

	private String noteID;
	private String description;
	private boolean preferred;
	private String notesUrl;
	private OpenSocialContainerConfig containerConfig = new DefaultContainerConfig();
	
	
	private FieldMap fields = new FieldMap();
	private DominoOAuth2Store oauth2Store;
	private DominoOAuthStore oauthStore;
		
	public PlaygroundEnvironment() {
		this(null,null);
	}

	public PlaygroundEnvironment(String name) {
		this(name,null);
	}
	
	public PlaygroundEnvironment(String name, Property[] properties) {
		super(name,
			  null,
			  properties);
		setEndpointsArray(createEndpoints());
	}
	
	public Map<String,String> getFieldMap() {
		return fields;
	}
	public String getField(String name) {
		return fields.get(name);
	}
	public void putField(String name, String value) {
		fields.put(name,value);
	}
	
	private SBTEnvironment.Endpoint[] createEndpoints() {
		ArrayList<SBTEnvironment.Endpoint> endpoints = new ArrayList<SBTEnvironment.Endpoint>();

		List<Endpoints> envext = PlaygroundExtensionFactory.getExtensions(Endpoints.class);
		for(int ev=0; ev<envext.size(); ev++) {
			Endpoints e = envext.get(ev);
			String[] sp = StringUtil.splitString(e.getEndpointNames(), ',', true);
			for(int i=0; i<sp.length; i++) {
				if(StringUtil.isNotEmpty(sp)) {
					endpoints.add(new SBTEnvironment.Endpoint(sp[i],null));
				}
			}
		}
		return endpoints.toArray(new SBTEnvironment.Endpoint[endpoints.size()]);
	}

	
	public String getNoteID() {
		return noteID;
	}
	public void setNoteID(String noteID) {
		this.noteID = noteID;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isPreferred() {
		return preferred;
	}
	public void setPreferred(boolean preferred) {
		this.preferred = preferred;
	}

	public void prepareEndpoints() {
		List<Endpoints> endpoints = PlaygroundExtensionFactory.getExtensions(Endpoints.class);
		for(int i=0; i<endpoints.size(); i++) {
			endpoints.get(i).prepareEndpoints(this);
		}
	}
	
	
	public String getPropertyValueByName(String name) {
		FacesContext context = FacesContext.getCurrentInstance();
		if(context!=null) {
			Map<String,String> map = (Map<String,String>)context.getExternalContext().getSessionMap().get(SESSION_PARAMETERS_MAP);
			if(map!=null) {
				String value = map.get(name);
				if(value!=null) {
					return value;
				}
			}
		}
		return super.getPropertyValueByName(name);
	}

	public void pushSessionParams(String name, String value) {
		FacesContext context = FacesContext.getCurrentInstance();
		if(context!=null) {
			Map<String,String> map = (Map<String,String>)context.getExternalContext().getSessionMap().get(SESSION_PARAMETERS_MAP);
			if(map==null) {
				map = new HashMap<String, String>();
				context.getExternalContext().getSessionMap().put(SESSION_PARAMETERS_MAP, map);
			}
			if(StringUtil.isNotEmpty(value)) {
				map.put(name, value);
			}
		}
	}
	
	public void setNotesUrl(String url) {
		this.notesUrl = url;
	}
	
	@Override
	public String getId() throws ContainerExtPointException {
		try {
			return URLEncoder.encode(this.notesUrl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ContainerExtPointException(e);
		}
	}
	
	@Override
	public OpenSocialContainerConfig getContainerConfig() {
		return containerConfig;
	}
	
	@Override
	public DominoOAuthStore getContainerOAuthStore() {
		if(oauthStore == null) {
			oauthStore = new PlaygroundOAuthStore(this);
		}
		return oauthStore;
	}
	@Override
	public DominoOAuth2Store getContainerOAuth2Store() {
		if(oauth2Store == null) {
			oauth2Store = new PlaygroundOAuth2Store(this);
		}
		return oauth2Store;
	}

}
