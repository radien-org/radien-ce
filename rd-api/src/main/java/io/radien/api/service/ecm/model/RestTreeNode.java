package io.radien.api.service.ecm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;

/**
 * @author Marco Weiland
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "type", "data", "children" })
public class RestTreeNode implements TreeNode, Serializable {

    private static final String DEFAULT_TYPE = "default";

    private static final long serialVersionUID = 1L;

    @JsonProperty("type")
    private String type;

    @JsonProperty("data")
    private transient Object data;

    @JsonProperty("children")
    private List<TreeNode> children;

    @JsonIgnore
    private TreeNode parent;

    @JsonIgnore
    private boolean expanded;

    @JsonIgnore
    private boolean selected;

    @JsonIgnore
    private boolean selectable = true;

    @JsonIgnore
    private String rowKey;


    public RestTreeNode() {
        this.type = DEFAULT_TYPE;
        this.children = new ArrayList<>();
    }

    public RestTreeNode(Object data) {
        this.type = DEFAULT_TYPE;
        this.children = new ArrayList<>();
        this.data = data;
    }

    public RestTreeNode(Object data, TreeNode parent) {
        this.type = DEFAULT_TYPE;
        this.data = data;
        this.children = new ArrayList<>();
        this.parent = parent;
    }

    public RestTreeNode(String type, Object data, TreeNode parent) {
        this.type = type;
        this.data = data;
        this.children = new ArrayList<>();
        this.parent = parent;
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((rowKey == null) ? 0 : rowKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        RestTreeNode other = (RestTreeNode) obj;
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        }
        else if (!data.equals(other.data)) {
            return false;
        }

        if (rowKey == null) {
            return other.rowKey == null;
        }
        else return rowKey.equals(other.rowKey);

    }

    @Override
    public String toString() {
        if (data != null) {
            return data.toString();
        }
        else {
            return super.toString();
        }
    }



	@Override
	public JsonToken asToken() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberType numberType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isValueNode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isContainerNode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMissingNode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isArray() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isObject() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TreeNode get(String fieldName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeNode get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeNode path(String fieldName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeNode path(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<String> fieldNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeNode at(JsonPointer ptr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeNode at(String jsonPointerExpression) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonParser traverse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonParser traverse(ObjectCodec codec) {
		// TODO Auto-generated method stub
		return null;
	}
}
