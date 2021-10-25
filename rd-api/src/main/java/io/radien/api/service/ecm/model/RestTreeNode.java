/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 * Rest Tree Node class object for hierarchy
 *
 * @author Marco Weiland
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "type", "data", "children" })
public class RestTreeNode implements TreeNode, Serializable {

    private static final String DEFAULT_TYPE = "default";

    private static final long serialVersionUID = 9183863826098427270L;

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

    /**
     * Rest Tree NOde empty constructor
     */
    public RestTreeNode() {
        this.type = DEFAULT_TYPE;
        this.children = new ArrayList<>();
    }

    /**
     * Rest tree node object data constructor
     * @param data to be uploaded
     */
    public RestTreeNode(Object data) {
        this.type = DEFAULT_TYPE;
        this.children = new ArrayList<>();
        this.data = data;
    }

    /**
     * Rest tree node object data and tree node parent constructor
     * @param data to be uploaded
     * @param parent from the hierarchy
     */
    public RestTreeNode(Object data, TreeNode parent) {
        this.type = DEFAULT_TYPE;
        this.data = data;
        this.children = new ArrayList<>();
        this.parent = parent;
    }

    /**
     * Rest tree node type, data and parent constructor
     * @param type of the node
     * @param data to be uploaded
     * @param parent from the hierarchy
     */
    public RestTreeNode(String type, Object data, TreeNode parent) {
        this.type = type;
        this.data = data;
        this.children = new ArrayList<>();
        this.parent = parent;
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash tables such
     * as those provided by HashMap.
     * The general contract of hashCode is:
     * Whenever it is invoked on the same object more than once during an execution of a Java application,
     * the hashCode method must consistently return the same integer, provided no information used in equals
     * comparisons on the object is modified. This integer need not remain consistent from one execution of an
     * application to another execution of the same application.
     * If two objects are equal according to the equals(Object) method, then calling the hashCode method on each of
     * the two objects must produce the same integer result.
     * It is not required that if two objects are unequal according to the equals(java.lang.Object) method, then
     * calling the hashCode method on each of the two objects must produce distinct integer results. However, the
     * programmer should be aware that producing distinct integer results for unequal objects may improve the
     * performance of hash tables.
     * As much as is reasonably practical, the hashCode method defined by class Object does return distinct integers
     * for distinct objects. (This is typically implemented by converting the internal address of the object into an
     * integer, but this implementation technique is not required by the JavaTM programming language.)
     * @return hash code value for this object.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((rowKey == null) ? 0 : rowKey.hashCode());
        return result;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * The equals method implements an equivalence relation on non-null object references:
     *
     * It is reflexive: for any non-null reference value x, x.equals(x) should return true.
     * It is symmetric: for any non-null reference values x and y, x.equals(y) should return true if and only if
     * y.equals(x) returns true.
     * It is transitive: for any non-null reference values x, y, and z, if x.equals(y) returns true and y.equals(z)
     * returns true, then x.equals(z) should return true.
     * It is consistent: for any non-null reference values x and y, multiple invocations of x.equals(y) consistently
     * return true or consistently return false, provided no information used in equals comparisons on the objects
     * is modified.
     * For any non-null reference value x, x.equals(null) should return false.
     * The equals method for class Object implements the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values x and y, this method returns true if and only if x and y refer to the
     * same object (x == y has the value true).
     *
     * Note that it is generally necessary to override the hashCode method whenever this method is overridden, so
     * as to maintain the general contract for the hashCode method, which states that equal objects must have equal
     * hash codes.
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
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

    /**
     * This object (which is already a string!) is itself returned.
     * @return he string itself.
     */
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
