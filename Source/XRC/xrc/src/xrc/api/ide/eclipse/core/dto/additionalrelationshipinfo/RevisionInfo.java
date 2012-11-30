package xrc.api.ide.eclipse.core.dto.additionalrelationshipinfo;

public class RevisionInfo implements IAdditionalInfo{
	
	private static final long serialVersionUID = 1L;
	
	private Long _revisionId;
	private Boolean _isDirty;	

	public RevisionInfo() {
		super();		
	}
	
	public RevisionInfo(Long revisionId, Boolean isDirty) {
		super();
		this._revisionId = revisionId;
		this._isDirty = isDirty;
	}
	
	public RevisionInfo(Long revisionId) {
		super();
		this._revisionId = revisionId;
	}

	public RevisionInfo(RevisionInfo revisionInfo){
		this(revisionInfo.getRevisionId(), revisionInfo.isDirty());
	}
	
	public Long getRevisionId() {
		return _revisionId;
	}
		
	public void setRevisionId(Long revisionId) {
		this._revisionId = revisionId;
	}
	public Boolean isDirty() {
		return _isDirty;
	}
	public void setDirty(Boolean isDirty) {
		this._isDirty = isDirty;
	}

	@Override
	public String toString() {
		return "RevisionInfo [revisionId=" + _revisionId + ", isDirty="
				+ _isDirty + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_isDirty == null) ? 0 : _isDirty.hashCode());
		result = prime * result
				+ ((_revisionId == null) ? 0 : _revisionId.hashCode());
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
		RevisionInfo other = (RevisionInfo) obj;
		if (_isDirty == null) {
			if (other._isDirty != null)
				return false;
		} else if (!_isDirty.equals(other._isDirty))
			return false;
		if (_revisionId == null) {
			if (other._revisionId != null)
				return false;
		} else if (!_revisionId.equals(other._revisionId))
			return false;
		return true;
	}

	public RevisionInfo clone(){
		return new RevisionInfo(this);
	}
}
