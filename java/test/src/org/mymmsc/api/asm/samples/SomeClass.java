package org.mymmsc.api.asm.samples;

public class SomeClass {
	private String name;
	 
    public void foo(String name) {
        this.setName(name);
    }

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
