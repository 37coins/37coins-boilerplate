package org.restnucleus.dao;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.validation.constraints.NotNull;

/**
 * an example implementation of a Model Class
 * 
 * @author johba
 */
@PersistenceCapable
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class Example extends Model {

	private static final long serialVersionUID = -792538125194459327L;

	// the email
	@Persistent
	@NotNull
	private String email;

	@Persistent(defaultFetchGroup = "true")
	private Example child;

	public Example getChild() {
		return child;
	}

	public Example setChild(Example child) {
		this.child = child;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public Example setEmail(String email) {
		this.email = email;
		return this;
	}

	public void update(Model newInstance) {
		Example n = (Example) newInstance;
		if (null != n.getEmail())
			this.setEmail(n.getEmail());
		if (null != n.getChild())
			this.setChild(n.getChild());
	}

}