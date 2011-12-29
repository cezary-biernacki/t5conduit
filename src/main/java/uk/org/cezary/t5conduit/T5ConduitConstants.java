package uk.org.cezary.t5conduit;

/**
 * Constants used for configuring the module.
 * @author Cezary Biernacki
 *
 */
public final class T5ConduitConstants {
	private T5ConduitConstants() {}

	/** Name of configuration variable for configuring suffix (extension) of CoffeeScript files.
	 *  Default is "coffee". An empty value disables CoffeeScript support. */
	public static final String COFFEE_SUFFIX = "t5conduit.coffee-suffix";

	/** Name of configuration variable for configuring suffix (extension) of LessCSS files 
	 * Default is "less". An empty value disables CoffeeScript support. 
	 */
	public static final String LESS_SUFFIX = "t5conduit.less-suffix";

	/** 
	 * Name of configuration variable that contains a name of less variable that will 
	 * be expanded to path to the application context (e.g. "/asset/1.0-SNAPHOT/app/ctx/". 
	 * Default is "CTX_PATH". An empty value disables creating such variable. 
	 */
	public static final String LESS_CTX_PATH_VAR_NAME = "t5conduit.less-ctx-path-var-name";

}
