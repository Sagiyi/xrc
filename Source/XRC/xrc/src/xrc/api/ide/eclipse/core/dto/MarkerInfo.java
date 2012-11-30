package xrc.api.ide.eclipse.core.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * A Serializable DTO to wrap IMarker data for reading/writing 
 * 
 * @author Sagi Ifrah
 *
 */
public class MarkerInfo  implements Serializable{

	private static final long serialVersionUID = 8020125107038653199L;
	
	private String type;
	private Map<String, Serializable> attributes;

	public MarkerInfo(String type) {
		super();
		this.type = type;
	}
	
	public MarkerInfo(String type, Map<String, Serializable> attributes) {
		super();
		this.type = type;
		this.attributes = attributes;
	}

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, Serializable> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Serializable> attributes) {
		this.attributes = attributes;
	}

	//Delegated session from Map attributes
	
	public Set<Entry<String, Serializable>> entrySet() {
		return attributes.entrySet();
	}

	public Serializable get(Object key) {
		return attributes.get(key);
	}

	public Serializable put(String key, Serializable value) {
		return attributes.put(key, value);
	}

	public void putAll(Map<? extends String, ? extends Serializable> m) {
		attributes.putAll(m);
	}
	
	//hashCode & equals
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MarkerInfo other = (MarkerInfo) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}		
	
}

