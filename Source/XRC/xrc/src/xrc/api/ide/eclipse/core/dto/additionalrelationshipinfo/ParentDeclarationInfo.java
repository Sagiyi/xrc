package xrc.api.ide.eclipse.core.dto.additionalrelationshipinfo;

public class ParentDeclarationInfo implements IAdditionalInfo{
	
	private static final long serialVersionUID = 1L;
	
	private String _aspect;
	private String _affectedType;
	private String _details;//implements, extends and whom
	private String _handle;
	
	public ParentDeclarationInfo(){
		super();
	}
	
	public ParentDeclarationInfo(String aspect, String affectedType, String details, String handle) {
		super();
		this._aspect = aspect;
		this._affectedType = affectedType;
		this._details = details;
		this._handle = handle;
	}
	
	public ParentDeclarationInfo(ParentDeclarationInfo parentDeclarationInfo) {
		this(parentDeclarationInfo.getAspect(),
				parentDeclarationInfo.getAffectedType(),
				parentDeclarationInfo.getDetails(),
				parentDeclarationInfo.getHandle());
	}
	
	public String getAspect() {
		return _aspect;
	}

	public void setAspect(String aspect) {
		this._aspect = aspect;
	}

	public String getAffectedType() {
		return _affectedType;
	}

	public void setAffectedType(String affectedType) {
		this._affectedType = affectedType;
	}

	public String getDetails() {
		return _details;
	}

	public void setDetails(String details) {
		this._details = details;
	}

	public String getHandle() {
		return _handle;
	}

	public void setHandle(String handle) {
		this._handle = handle;
	}

	@Override
	public String toString() {
		return "ParentDeclarationInfo [aspect=" + _aspect + ", affectedType="
				+ _affectedType + ", details=" + _details + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_affectedType == null) ? 0 : _affectedType.hashCode());
		result = prime * result + ((_aspect == null) ? 0 : _aspect.hashCode());
		result = prime * result
				+ ((_details == null) ? 0 : _details.hashCode());
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
		ParentDeclarationInfo other = (ParentDeclarationInfo) obj;
		if (_affectedType == null) {
			if (other._affectedType != null)
				return false;
		} else if (!_affectedType.equals(other._affectedType))
			return false;
		if (_aspect == null) {
			if (other._aspect != null)
				return false;
		} else if (!_aspect.equals(other._aspect))
			return false;
		if (_details == null) {
			if (other._details != null)
				return false;
		} else if (!_details.equals(other._details))
			return false;
		return true;
	}

	@Override
	public ParentDeclarationInfo clone() {
		// TODO Auto-generated method stub
		return new ParentDeclarationInfo(this);
	}
	
	
	
}
