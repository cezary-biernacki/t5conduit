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
	
	/** Resource name (relative to {@link LessToCssTransformer} class) that contain Less compiler, by default a file included in the distribution is used. */
	public static final String LESS_COMPILER = "t5conduit.less-compiler";

	/** Resource name (relative to {@link LessToCssTransformer} class) that contains JavaScript code inserted after the compiler and provides interface between compiler and Java.
	 * By default file included in the distribution is used. */
	public static final String LESS_AFTER_COMPILER = "t5conduit.less-after";

	/** Resource name (relative to {@link LessToCssTransformer} class) that contains JavaScript code inserted before the compiler and provides initial settings for the compiler.
	 * By default file included in the distribution is used. */
	public static final String LESS_BEFORE_COMPILER = "t5conduit.less-before";

	/** Resource name (relative to {@link CoffeeToJsTransformer} class) that contain CoffeeScript compiler, by default a file included in the distribution is used. */
	public static final String COFFEE_COMPILER = "t5conduit.coffee-compiler";
}
