package ${package}.model;

import javax.persistence.*;

/**
 * @author Rajesh Gavvala
 *
 */

@Entity
@Table(name = "${database-name}")
public class ${library-name} implements System${library-name} {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column
	private String message;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
